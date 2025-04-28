package com.samsung.iug.ui.screenmirror

import com.samsung.iug.adb.GetAdb
import com.samsung.iug.adb.getScrcpy
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

object ScreenMirror {
    val panel = JPanel(BorderLayout())
    private val imageLabel = JLabel("Not connect", SwingConstants.CENTER)
    val adbPath = GetAdb().absolutePath
    private var mouseListener: MouseAdapter? = null
    private var isStream = false

    init {
        panel.add(imageLabel, BorderLayout.CENTER)
    }

    fun getDeviceScreenSizeDdmlib(device: String): Pair<Int, Int> {
        val process = Runtime.getRuntime().exec("$adbPath -s $device shell wm size")
        val reader = process.inputStream.bufferedReader()
        val output = reader.readText()
        val match = Regex("Physical size: (\\d+)x(\\d+)").find(output)
        if (match != null) {
            val (width, height) = match.destructured
            return Pair(width.toInt(), height.toInt())
        }
        throw RuntimeException("Cannot get screen size from device")
    }

    fun mapToDevice(panelX: Int, panelY: Int, device: String): Pair<Int, Int> {
        val (deviceWidth, deviceHeight) = getDeviceScreenSizeDdmlib(device)
        val imageIcon = imageLabel.icon as? ImageIcon ?: return Pair(0, 0)

        val imageWidth = imageIcon.iconWidth
        val imageHeight = imageIcon.iconHeight

        val labelWidth = imageLabel.width
        val labelHeight = imageLabel.height

        val offsetX = (labelWidth - imageWidth)/2
        val offsetY = (labelHeight - imageHeight)/2

        val relativeX = panelX - offsetX
        val relativeY = panelY - offsetY

        if (relativeX < 0 || relativeY < 0 || relativeX > imageWidth || relativeY > imageHeight) {
            return Pair(0, 0)
        }

        val scaleX = deviceWidth / imageWidth.toDouble()
        val scaleY = deviceHeight / imageHeight.toDouble()

        val rx = (relativeX * scaleX).toInt()
        val ry = (relativeY * scaleY).toInt()

        return Pair(rx, ry)
    }

    fun startStream(device: String) {
        isStream = true
        imageLabel.text = "Connecting..."
        Thread { startScrcpyAndStream(device) }.start()
    }

    fun stopStream(device: String) {
        isStream = false
        SwingUtilities.invokeLater {
            imageLabel.icon = null
            imageLabel.text = "Not connect"
            imageLabel.revalidate()
            imageLabel.repaint()
        }
        ProcessBuilder(adbPath, "-s", device, "forward", "--remove", "tcp:27183").start().waitFor()
        ProcessBuilder(adbPath, "-s", device, "shell", "pkill", "-f", "scrcpy").start().waitFor()
    }

    fun startControl(device: String) {
        mouseListener = object : MouseAdapter() {
            private var lastX = -1
            private var lastY = -1

            override fun mousePressed(e: MouseEvent) {
                val (x, y) = mapToDevice(e.x, e.y, device)
                lastX = x
                lastY = y
            }

            override fun mouseReleased(e: MouseEvent) {
                val (x, y) = mapToDevice(e.x, e.y, device)
                val endX = x
                val endY = y

                val isTap = (Math.abs(lastX - endX) < 10) && (Math.abs(lastY - endY) < 10)
                val duration = if (isTap) 1 else 300
                Runtime.getRuntime().exec("$adbPath -s $device shell input swipe $lastX $lastY $endX $endY $duration")
            }
        }
        imageLabel.addMouseListener(mouseListener!!)
    }

    fun stopControl() {
        mouseListener?.let {
            imageLabel.removeMouseListener(it)
            mouseListener = null
        }
    }

    private fun startScrcpyAndStream(device: String) {
        try {
            val localPath = getScrcpy().absolutePath
            val serverPath = "/data/local/tmp/scrcpy-server-v3.2.jar"

            ProcessBuilder(adbPath, "-s", device, "forward", "--remove", "tcp:27183").start().waitFor()
            ProcessBuilder(adbPath, "-s", device, "shell", "pkill", "-f", "scrcpy").start().waitFor()
            ProcessBuilder(adbPath, "-s", device, "push", localPath, serverPath).start().waitFor()
            ProcessBuilder(adbPath, "-s", device, "forward", "tcp:27183", "localabstract:scrcpy").start().waitFor()


            val command = listOf(
                adbPath,
                "-s",
                device,
                "shell",
                "CLASSPATH=$serverPath",
                "app_process", "/", "com.genymobile.scrcpy.Server",
                "3.2",
                "tunnel_forward=true",
                "audio=false",
                "control=false",
                "cleanup=false",
                "raw_stream=true",
                "max_size=600"
            )

            ProcessBuilder(command).redirectErrorStream(true).start()

            Thread.sleep(2000)
            val grabber = FFmpegFrameGrabber("tcp://127.0.0.1:27183").apply {
                format = "h264"
                start()
            }

            val converter = Java2DFrameConverter()
            imageLabel.text = null
            try {
                while (isStream) {
                    try {
                        val frame = grabber.grabImage()
                        if (frame != null) {
                            val bufferedImage = converter.convert(frame)

                            SwingUtilities.invokeLater {
                                if (!isStream) {
                                    return@invokeLater
                                }
                                imageLabel.icon = ImageIcon(bufferedImage)
                                imageLabel.setSize(bufferedImage.width, bufferedImage.height)
                                imageLabel.revalidate()
                                imageLabel.repaint()
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        break
                    }
                }
            } finally {
                try {
                    grabber.stop()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
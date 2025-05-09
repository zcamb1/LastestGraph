package com.samsung.iug.ui.mirror

import com.samsung.iug.device.getAdb
import com.samsung.iug.ui.graph.GraphUI
import com.samsung.iug.ui.graph.ViewNode
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.concurrent.fixedRateTimer
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.awt.BasicStroke
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class ScreenMirror(): JPanel(BorderLayout()) {
    val adbPath = getAdb().absolutePath
    private val imagePanel = ImagePanel()

    init {
        add(imagePanel, BorderLayout.CENTER)
        fixedRateTimer("adb-screencap", initialDelay = 1000, period = 500) {
            val bounds = parseClickableNodes()
            val img = captureScreen()
            if (img != null) {
                SwingUtilities.invokeLater {
                    imagePanel.image = img
                    imagePanel.clickableAreas = bounds
                    imagePanel.revalidate()
                    imagePanel.repaint()
                }
            }
        }
    }

    private fun captureScreen() : BufferedImage? {
        return try {
            val process = ProcessBuilder(adbPath, "exec-out", "screencap", "-p").start()

            val inputStream = process.inputStream
            val output = inputStream.readBytes()

            ImageIO.read(output.inputStream())
        } catch (e: Exception) {
            println("Lỗi khi chụp màn hình: ${e.message}")
            null
        }
    }

    private fun parseClickableNodes(): List<Node> {
        return try {
            Runtime.getRuntime().exec("$adbPath shell uiautomator dump /sdcard/window_dump.xml").waitFor()
            Runtime.getRuntime().exec("$adbPath pull /sdcard/window_dump.xml").waitFor()

            val xmlFile = File("window_dump.xml")
            val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val doc: Document = docBuilder.parse(xmlFile)
            val nodeList = doc.getElementsByTagName("node")

            val rootNode = nodeList.item(0) as Element
            ViewNode.currentScreen = rootNode.getAttribute("package")

            val boundsList = mutableListOf<Node>()

            for (i in 0 until nodeList.length) {
                val node = nodeList.item(i) as Element

                val className = node.getAttribute("class")
                val skipClasses = listOf(
                    "android.widget.LinearLayout",
                    "android.view.ViewGroup"
                )

                val packageName = node.getAttribute("package")
                val skipPackage = listOf(
                    "com.google.android.googlequicksearchbox",
                )

                if (node.getAttribute("clickable") == "true" && className !in skipClasses && packageName !in skipPackage) {
                    val boundsStr = node.getAttribute("bounds")
                    val match = Regex("\\[(\\d+),(\\d+)]\\[(\\d+),(\\d+)]").find(boundsStr)
                    match?.let {
                        val (x1, y1, x2, y2) = it.destructured
                        boundsList.add(
                            Node(
                                node.getAttribute("class"),
                                node.getAttribute("resource-id"),
                                node.getAttribute("text"),
                                Rectangle(x1.toInt(), y1.toInt(), x2.toInt() - x1.toInt(), y2.toInt() - y1.toInt()),
                                node.getAttribute("content-desc")
                            )
                        )
                    }
                }
            }
            boundsList
        } catch (e: Exception) {
            println("Lỗi khi phân tích XML: ${e.message}")
            emptyList()
        }
    }
}

class ImagePanel: JPanel() {
    var image: BufferedImage? = null
    var clickableAreas: List<Node> = emptyList()
    var selectedRect: Rectangle? = null
    var selectedNode: Node? = null

    init {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val clickX = e.x * 5
                val clickY = e.y * 5

                selectedNode = clickableAreas.find { it.bounds.contains(clickX, clickY) }
                selectedRect = selectedNode?.bounds

                if (selectedRect != null) {
                    ViewNode.screenId = ViewNode.currentScreen
                    ViewNode.className = selectedNode!!.className
                    ViewNode.resourceId = selectedNode!!.resourceId
                    ViewNode.text = selectedNode!!.text
                    ViewNode.bounds = selectedNode!!.bounds
                    ViewNode.contentDescription = selectedNode!!.contentDescription

                    println("Clicked inside a clickable area: $selectedRect")
                } else {
                    println("Clicked outside clickable areas.")
                }

                repaint()
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        image?.let {
            val g2d = g as Graphics2D
            val scaleWidth = (it.width / 5)
            val scaleHeight = (it.height / 5)

            val x = (width - scaleWidth) / 2
            val y = (height - scaleHeight) / 2

            g2d.drawImage(it, x, y, scaleWidth, scaleHeight, null)

            g2d.color = Color.YELLOW
            g2d.stroke = BasicStroke(2f)
            for (rect in clickableAreas) {
                val x1 = (rect.bounds.x / 5) + x
                val y1 = (rect.bounds.y / 5) + y
                val w = (rect.bounds.width / 5)
                val h = (rect.bounds.width / 5)

                if (rect.bounds == selectedRect) {
                    g2d.color = Color(255, 0, 0, 60)
                    g2d.stroke = BasicStroke(2f)
                    g2d.fillOval(x1, y1, w, h)

                    g2d.color = Color.RED
                    g2d.stroke = BasicStroke(2f)
                    g2d.drawOval(x1, y1, w, h)
                } else {
                    g2d.color = Color.YELLOW
                    g2d.stroke = BasicStroke(2f)
                    g2d.drawOval(x1, y1, w, h)
                }
            }
            revalidate()
            repaint()
            GraphUI.repaintLayered()
        }
    }

    override fun getPreferredSize(): Dimension {
        image?.let {
            return Dimension(it.width / 5, it.height / 5)
        }
        return super.getPreferredSize()
    }
}

data class Node (
    var className: String = "",
    var resourceId: String = "",
    var text: String = "",
    var bounds: Rectangle = Rectangle(0, 0, 0, 0),
    var contentDescription: String = ""
)
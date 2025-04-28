package com.samsung.iug.utils

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener
import com.android.ddmlib.IDevice
import com.samsung.iug.adb.GetAdb
import com.samsung.iug.service.Log
import java.awt.TextField
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import javax.swing.JLabel
import javax.swing.JTextField
import javax.swing.SwingUtilities

object AdbManager {
    private var activityProcess: Process? = null
    private var activityThread: Thread? = null

    fun startListeningActivityChanges(deviceIdSelected: String, screenIdTF: JTextField, packageTF: JTextField) {
        try {
            Runtime.getRuntime().exec("adb logcat -c")

            val process = Runtime.getRuntime().exec(
                arrayOf(
                    GetAdb().absolutePath, "-s", deviceIdSelected, "logcat", "ActivityTaskManager:I", "*:S"
                )
            )
            activityProcess = process
            val reader = BufferedReader(InputStreamReader(process.inputStream))

            val thread = Thread {
                var line: String?
                println("start listening activity changes")
                try {
                    while (reader.readLine().also { line = it } != null && !Thread.currentThread().isInterrupted) {
                        line?.let {
                            val pattern = Regex("""ActivityRecord\{[^}]*u0\s+([^/]+)(?:/([^ ]+))?""")
                            val isFound = updatePackageAndScreenByPattern(pattern, it, screenIdTF, packageTF)

                            if (!isFound) {
                                val displayRegex = Regex("""Displayed\s+(\S+)/(\S+)""")
                                updatePackageAndScreenByPattern(displayRegex, it, screenIdTF, packageTF)
                            }
                        }
                        println("=========")
                    }
                } catch (e: Exception) {
                    if (e !is InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
            activityThread = thread
            thread.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopListeningActivityChanges() {
        activityThread?.interrupt()
        activityThread = null

        activityProcess?.destroy()
        activityProcess = null
    }

    private fun updatePackageAndScreenByPattern(
        pattern: Regex,
        data: String,
        screenIdTF: JTextField,
        packageTF: JTextField
    ): Boolean {
        var isFound = false
        val matchResult = pattern.find(data)
        if (matchResult != null) {
            val (packageName, activity) = matchResult.destructured
            val screenId = if (activity.startsWith(".")) {
                "$packageName$activity"
            } else {
                activity
            }
            SwingUtilities.invokeLater {
                println("=========")
                println("packagehi \npackageName: $packageName\nscreenId: $screenId")
                if (packageName.isNotBlank()) {
                    screenIdTF.text = packageName
                    isFound = true
                }
                if (screenId.isNotBlank()) {
                    packageTF.text = screenId
                    isFound = true
                }
            }
        }
        return isFound
    }

    fun getTopResumedActivity(deviceIdSelected: String, screenIdTF: JTextField, packageTF: JTextField) {
        try {
            val process = ProcessBuilder(
                GetAdb().absolutePath,
                "-s",
                deviceIdSelected,
                "shell",
                "dumpsys",
                "activity",
                "activities"
            )
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            println("Result output get activity: $output")
            println("=====================================")

            val line = output.lines().find { it.contains("topResumedActivity") } ?: return
            println("Result filter get activity: $line")

            val pattern = Regex("""topResumedActivity=ActivityRecord\{\S+ \S+ (\S+)/(\S+)""")
            updatePackageAndScreenByPattern(pattern, output, screenIdTF, packageTF)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTopResumedActivity(deviceIdSelected: String): Pair<String, String>? {
        return try {
            val process = ProcessBuilder(
                GetAdb().absolutePath,
                "-s",
                deviceIdSelected,
                "shell",
                "dumpsys",
                "activity",
                "activities"
            )
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            println("Result output get activity: $output")
            println("=====================================")

            val line = output.lines().find { it.contains("topResumedActivity") } ?: return null
            println("Result filter get activity: $line")

            val pattern = Regex("""topResumedActivity=ActivityRecord\{\S+ \S+ (\S+)/(\S+)""")
            val match = pattern.find(output)
            println("Result match groups get activity: ${match?.groups}")
            val packageName = match?.groups?.get(1)?.value ?: ""
            val activityName = match?.groups?.get(2)?.value ?: ""
            val screenId = if (activityName.startsWith(".")) {
                "$packageName$activityName"
            } else {
                activityName
            }

            packageName to screenId
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // background thread
    fun initializeAdb() {
        Thread {
            Log.i(TAG, "AndroidDebugBridge: start init")
            AndroidDebugBridge.initIfNeeded(false)

            val adb = AndroidDebugBridge.createBridge(GetAdb().absolutePath, true)
            if (adb != null) {
                AndroidDebugBridge.addDeviceChangeListener(object : IDeviceChangeListener {
                    override fun deviceConnected(device: IDevice) {
                        val modelName = device.getProperty("ro.product.model") ?: "Unknown"
                        Log.i(TAG, "Device connected: $modelName")
                        println(getTopResumedActivity(device.serialNumber))
                    }

                    override fun deviceDisconnected(device: IDevice) {
                        val modelName = device.getProperty("ro.product.model") ?: "Unknown"
                        Log.i(TAG, "Device disconnected: $modelName")
                    }

                    override fun deviceChanged(device: IDevice, changeMask: Int) {
                        val modelName = device.getProperty("ro.product.model") ?: "Unknown"

                        when (device.state) {
                            IDevice.DeviceState.ONLINE -> {
                                Log.i(TAG, "Device connected: $modelName")
                                println(getTopResumedActivity(device.serialNumber))
                            }

                            IDevice.DeviceState.OFFLINE,
                            IDevice.DeviceState.UNAUTHORIZED,
                            IDevice.DeviceState.DISCONNECTED -> {
                                Log.i(TAG, "Device disconnected: $modelName")
                            }

                            else -> {
                                Log.i(TAG, "Device is changed: $modelName")
                            }
                        }

                    }
                })

                Log.i(TAG, "AndroidDebugBridge: initialized successfully")
            } else {
                Log.i(TAG, "AndroidDebugBridge: failed to initialize")
            }
        }.start()
    }

    private const val TAG = "AdbManager"
}
package com.samsung.iug.utils

object DeviceManager {
    fun isAdbDeviceConnected(): Boolean {
        return try {
            val process = ProcessBuilder("adb", "devices").start()
            val output = process.inputStream.bufferedReader().readText()
            val connectedDevices = output.lines()
                .filter { it.isNotBlank() && it.contains("\tdevice") }

            connectedDevices.isNotEmpty()
        } catch (ex: Exception) {
            false
        }
    }
}
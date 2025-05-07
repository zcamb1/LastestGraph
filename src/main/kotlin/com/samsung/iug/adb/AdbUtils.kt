package com.samsung.iug.adb

fun getSizeScreen(device: String) : Pair<Int, Int> {
    val adbPath = GetAdb().absolutePath
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
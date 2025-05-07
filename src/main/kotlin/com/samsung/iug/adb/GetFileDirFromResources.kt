package com.samsung.iug.adb

import java.io.File
import java.io.FileOutputStream

fun GetAdb (): File {
    val resourcePath = "/file/adb.exe"
    val tempRoot = System.getProperty("java.io.tmpdir")
    val extractedDir = File(tempRoot, "iugplugin")

    if (!extractedDir.exists()) {
        extractedDir.mkdirs()
    }

    val adbFile = File(extractedDir, "adb.exe")

    if (!adbFile.exists()) {
        val inputStream = object {}.javaClass.getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("Cannot find $resourcePath")

        inputStream.use { input ->
            FileOutputStream(adbFile).use { output ->
                input.copyTo(output)
            }
        }

        adbFile.deleteOnExit()
    }

    return adbFile
}

fun getScrcpy(): File {
    val resourcePath = "/file/scrcpy-server-v3.2"
    val tempRoot = System.getProperty("java.io.tmpdir")
    val extractedDir = File(tempRoot, "iugplugin")

    if (!extractedDir.exists()) {
        extractedDir.mkdirs()
    }

    val scrcpyFile = File(extractedDir, "scrcpy-server-v3.2")

    if (!scrcpyFile.exists()) {
        val inputStream = object {}.javaClass.getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("Cannot find $resourcePath")

        inputStream.use { input ->
            FileOutputStream(scrcpyFile).use { output ->
                input.copyTo(output)
            }
        }

        scrcpyFile.deleteOnExit()
    }

    return scrcpyFile
}
package com.samsung.iug.utils

import java.io.File
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.swing.JOptionPane

object ScrcpyLauncher {
    fun launchScrcpy(deviceIdSelected: String = "") {
        try {
            val scrcpyExe = prepareScrcpy()
            if (!scrcpyExe.exists()) {
                JOptionPane.showMessageDialog(null, "scrcpy.exe not found")
                return
            }

            val command = mutableListOf(scrcpyExe.absolutePath)
            if (deviceIdSelected.isNotBlank()) {
                command.addAll(listOf("-s", deviceIdSelected))
            }

            ProcessBuilder(command)
                .start()

        } catch (e: Exception) {
            JOptionPane.showMessageDialog(null, "Run error scrcpy: ${e.message}")
        }
    }

    private fun prepareScrcpy(): File {
        val tempRoot = System.getProperty("java.io.tmpdir")
        val extractedDir = File(tempRoot, "0a_scrcpy_bundle")

        if (!File(extractedDir, "scrcpy.exe").exists()) {
            extractedDir.mkdirs()

            val zipInputStream = javaClass.getResourceAsStream("/bin/scrcpy.zip")
                ?: throw IOException("scrcpy.zip resources not found")

            val zipFile = File.createTempFile("scrcpy", ".zip")
            zipFile.deleteOnExit()
            zipInputStream.use { input ->
                zipFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            unzip(zipFile, extractedDir)
        }

        return File(extractedDir, "scrcpy.exe")
    }


    fun unzip(zipFile: File, outputDir: File) {
        ZipInputStream(zipFile.inputStream()).use { zis ->
            var entry: ZipEntry? = zis.nextEntry
            while (entry != null) {
                val outFile = File(outputDir, entry.name)
                if (entry.isDirectory) {
                    outFile.mkdirs()
                } else {
                    outFile.parentFile.mkdirs()
                    outFile.outputStream().use { os -> zis.copyTo(os) }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
    }
}
package com.samsung.iug.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object FileStorage {
    private const val MAX_RECENT = 10
    private val gson = Gson()

    private val recentFile: File by lazy {
        val tempDir = System.getProperty("java.io.tmpdir")
        File(tempDir, "iug_recent_projects.json").apply {
            println(this.absolutePath)
        }
    }

    private var recentPaths: MutableList<String> = loadRecentListFromDisk()
    var currentFile: File? = null

    fun addRecent(path: String) {
        recentPaths.remove(path)
        recentPaths.add(0, path)
        if (recentPaths.size > MAX_RECENT) {
            recentPaths = recentPaths.take(MAX_RECENT).toMutableList()
        }
        saveRecentListToDisk()
    }

    fun getAllRecent(): List<String> = recentPaths.toList()

    fun removeRecent(path: String) {
        recentPaths.remove(path)
    }

    private fun loadRecentListFromDisk(): MutableList<String> {
        return try {
            if (!recentFile.exists()) return mutableListOf()
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(recentFile.readText(), type) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    private fun saveRecentListToDisk() {
        try {
            recentFile.writeText(gson.toJson(recentPaths))
        } catch (_: Exception) {
        }
    }

    fun openProject(filePath: String): Boolean {
        val file = File(filePath)
        if (!file.exists()) return false
        currentFile = file
        addRecent(filePath)
        return true
    }
}
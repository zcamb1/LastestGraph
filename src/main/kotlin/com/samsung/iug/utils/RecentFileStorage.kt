package com.samsung.iug.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object RecentFileStorage {
    private const val MAX_RECENT = 10
    private val gson = Gson()

    private val recentFile: File by lazy {
        val tempDir = System.getProperty("java.io.tmpdir")
        File(tempDir, "iug_recent_projects.json").apply {
            println(this.absolutePath)
        }
    }

    private var recentPaths: MutableList<String> = loadFromDisk()

    // TODO: gọi khi tạo project mới và mở project cũ (nếu prj cũ k tồn tại gọi remove)
    fun add(path: String) {
        recentPaths.remove(path)
        recentPaths.add(0, path)
        if (recentPaths.size > MAX_RECENT) {
            recentPaths = recentPaths.take(MAX_RECENT).toMutableList()
        }
        saveToDisk()
    }

    // TODO gọi mỗi khi mở màn open recent guide
    fun getAll(): List<String> = recentPaths.toList()

    // TODO: prj cũ k tồn tại
    fun remove(path: String){
        recentPaths.remove(path)
    }

    private fun loadFromDisk(): MutableList<String> {
        return try {
            if (!recentFile.exists()) return mutableListOf()
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(recentFile.readText(), type) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    private fun saveToDisk() {
        try {
            recentFile.writeText(gson.toJson(recentPaths))
        } catch (_: Exception) {}
    }
}
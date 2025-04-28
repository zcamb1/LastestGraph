package com.samsung.iug.utils

import java.io.File
import com.google.gson.GsonBuilder
object JsonHelper {
    val gson = GsonBuilder().setPrettyPrinting().create()


    inline fun <reified T> readJson(filePath: String): T {
        val json = File(filePath).readText(Charsets.UTF_8)
        return gson.fromJson(json, T::class.java)
    }

    inline fun <reified T> fromJson(json: String): T {
        return gson.fromJson(json, T::class.java)
    }

    fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }

    fun writeJson(filePath: String, obj: Any) {
        val json = gson.toJson(obj)
        File(filePath).writeText(json, Charsets.UTF_8)
    }
}
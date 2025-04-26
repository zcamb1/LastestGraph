package com.samsung.iug.utils

import java.io.File
import com.google.gson.Gson

object JsonHelper {
    public val gson = Gson()

    inline fun <reified T> readJson(filePath: String): T {
        val json = File(filePath).readText(Charsets.UTF_8)
        return gson.fromJson(json, T::class.java)
    }

    fun writeJson(filePath: String, obj: Any) {
        val json = gson.toJson(obj)
        File(filePath).writeText(json, Charsets.UTF_8)
    }
}
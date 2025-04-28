package com.samsung.iug.utils

import java.io.File
import com.google.gson.GsonBuilder
import com.samsung.iug.model.Rule

object JsonHelper {
    val gson = GsonBuilder().setPrettyPrinting().create()

    inline fun <reified T> fromJson(json: String): T {
        return gson.fromJson(json, T::class.java)
    }

    fun writeJsonToFile(filePath: String, obj: Any) {
        val json = gson.toJson(obj)
        File(filePath).writeText(json, Charsets.UTF_8)
    }

    fun exportRuleToJsonFile(rule: Rule, file: File): Pair<Boolean, String?> {
        return try {
            writeJsonToFile(file.absolutePath, rule)
            Pair(true, null)
        } catch (e: Exception) {
            Pair(false, "Error exporting rule: ${e.message}")
        }
    }
}
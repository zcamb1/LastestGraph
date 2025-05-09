package com.samsung.iug.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.samsung.iug.graph.Node
import java.io.File

object JsonHelper {
    val gson = GsonBuilder().setPrettyPrinting().create()

    inline fun <reified T> fromJson(json: String): T {
        return gson.fromJson(json, T::class.java)
    }

    fun writeJsonToFile(filePath: String, obj: Any) {
        val json = gson.toJson(obj)
        File(filePath).writeText(json, Charsets.UTF_8)
    }

    fun exportRuleToJsonFile(rule: List<Node>, file: File): Pair<Boolean, String?> {
        return try {
            writeJsonToFile(file.absolutePath, rule)
            Pair(true, file.absolutePath)
        } catch (e: Exception) {
            Pair(false, "Error exporting rule: ${e.message}")
        }
    }

    fun parseRuleFromJson(file: File):List<Node>{
        val jsonStr = file.readText()
        return gson.fromJson(jsonStr, object : TypeToken<List<Node>>() {}.type)
    }

    fun parseRuleFromJson(jsonObject: JsonObject): List<Node> {
        return fromJson<List<Node>>(jsonObject.toString())
    }
}

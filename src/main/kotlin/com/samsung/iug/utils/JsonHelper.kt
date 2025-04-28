package com.samsung.iug.utils

import java.io.File
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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

    fun parseRulesFromFile(file: File): List<Rule> {
        val jsonStr = file.readText()
        val jsonElement = JsonParser.parseString(jsonStr)
        val jsonObject = jsonElement.asJsonObject

        return when {
            jsonObject.has("stepRules") -> {
                val rulesArray = jsonObject.getAsJsonArray("stepRules")
                List(rulesArray.size()) { i ->
                    parseRuleFromJson(rulesArray.get(i).asJsonObject)
                }
            }
            else -> listOf(parseRuleFromJson(jsonObject))
        }
    }

    fun parseRuleFromJson(jsonObject: JsonObject): Rule {
        val rule = fromJson<Rule>(jsonObject.toString())
        rule.steps.forEach { step ->
            if (step.nextStepIds == null) step.nextStepIds = mutableListOf()
        }
        return rule
    }
}
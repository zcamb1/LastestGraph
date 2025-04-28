package com.samsung.iug.utils

import com.samsung.iug.model.Rule
import java.io.File
import com.google.gson.JsonObject
import com.google.gson.JsonParser


class RuleParser {
    private val gson = JsonHelper.gson

    fun parseRule(jsonStr: String): Rule {
        return RuleJsonParser.parseRuleFromJson(jsonStr)
    }

    fun parseRulesFromFile(file: File): List<Rule> {
        val jsonStr = file.readText()
        val jsonElement = JsonParser.parseString(jsonStr)
        val jsonObject = jsonElement.asJsonObject

        return when {
            jsonObject.has("stepRules") -> {
                val rulesArray = jsonObject.getAsJsonArray("stepRules")
                List(rulesArray.size()) { i ->
                    RuleJsonParser.parseRuleFromJson(rulesArray.get(i).asJsonObject)
                }
            }
            else -> listOf(RuleJsonParser.parseRuleFromJson(jsonObject))
        }
    }

    fun serializeRule(rule: Rule): String = RuleJsonSerializer.serializeRule(rule)

    fun exportRuleToJsonFile(rule: Rule, file: File): Pair<Boolean, String?> {
        return try {
            JsonHelper.writeJson(file.absolutePath, rule)
            Pair(true, null)
        } catch (e: Exception) {
            Pair(false, "Error exporting rule: ${e.message}")
        }
    }

    fun exportRulesToJsonFile(rules: List<Rule>, file: File): Pair<Boolean, String?> {
        return try {
            val rootObject = JsonObject()
            val rulesArray = gson.toJsonTree(rules).asJsonArray
            rootObject.add("stepRules", rulesArray)

            file.writeText(gson.toJson(rootObject))
            Pair(true, null)
        } catch (e: Exception) {
            Pair(false, "Error exporting rules: ${e.message}")
        }
    }
}

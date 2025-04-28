package com.samsung.iug.utils

import com.samsung.iug.model.Rule

object RuleJsonSerializer {
    /**
     * Serialize a Rule object to a JSON string
     */
    fun serializeRule(rule: Rule): String {
        return JsonHelper.toJson(rule)
    }
}

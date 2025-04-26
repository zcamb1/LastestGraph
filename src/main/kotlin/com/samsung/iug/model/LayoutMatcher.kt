package com.samsung.iug.model

data class LayoutMatcher(
    val matchTarget: String,
    val matchOperand: String,
    val matchCriteria: String = "",
    val highlightType: String = "",
    val transitionCondition: String = ""
) 
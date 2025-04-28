package com.samsung.iug.model

data class LayoutMatcher(
    val matchTarget: String,
    val matchOperand: String,
    val matchCriteria: String? = null,
    val highlightType: String? = null,
    val transitionCondition: String? = null
)
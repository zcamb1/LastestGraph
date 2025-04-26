package com.samsung.iug.model

data class Step(
    var id: String,
    var screenId: String,
    var guideContent: String,
    val layoutMatchers: List<LayoutMatcher> = mutableListOf(),
    val nextStepIds: MutableList<String> = mutableListOf(),
    val screenMatcher: String? = null,
    val transitionCondition: String? = null,
    var isSubStep: Boolean = false
) {
    fun hasChildren(): Boolean = nextStepIds.isNotEmpty()

    fun addNextStep(stepId: String) {
        if (!nextStepIds.contains(stepId)) {
            nextStepIds.add(stepId)
        }
    }

    fun removeNextStep(stepId: String) {
        nextStepIds.remove(stepId)
    }

    override fun toString(): String {
        return "Step(id='$id', nextStepIds='$nextStepIds')"
    }
} 
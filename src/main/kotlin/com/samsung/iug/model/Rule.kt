package com.samsung.iug.model

data class Rule(
    val id: String,
    val ruleSpecVersion: Int,
    val ruleVersion: Int,
    val targetAppPackages: List<AppPackage>,
    val landingUri: String,
    val utterances: List<String>,
    val preConditions: List<String>,
    val steps: MutableList<Step>,
) {
    private fun getStepById(id: String): Step? {
        return steps.find { it.id == id }
    }

    fun addStep(step: Step) {
        // todo: check id already exists
        steps.add(step)
    }

    fun removeStep(stepId: String): Boolean {
        val step = getStepById(stepId) ?: return false

        if (step.hasChildren()) {
            return false
        }

        steps.forEach { s ->
            s.nextStepIds.remove(stepId)
        }

        return steps.removeIf { it.id == stepId }
    }

    fun updateStepId(oldId: String, newId: String): Boolean {
        val step = getStepById(oldId) ?: return false

        steps.forEach { s ->
            val index = s.nextStepIds.indexOf(oldId)
            if (index >= 0) {
                s.nextStepIds[index] = newId
            }
        }

        step.id = newId
        return true
    }
}
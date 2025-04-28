package com.samsung.iug.ui.rulemaker

import com.mxgraph.view.mxGraph
import com.samsung.iug.model.Rule
import com.samsung.iug.model.Step

/**
 * StepCellFactory handles creating and styling nodes (steps)
 * in the graph based on their role (main step, sub-step, start, end).
 */
class StepCellFactory(
    private val graph: mxGraph,
    private val stepToCellMap: MutableMap<String, Any>,
    private val cellToStepMap: MutableMap<Any, Step>,
    private val currentRuleProvider: () -> Rule?
) {

    // Constants for different node styles
    private val MAIN_STEP_STYLE = "mainStep"
    private val SUB_STEP_STYLE = "subStep"
    private val EDGE_STYLE = "edge"
    private val ACTIVE_STEP_STYLE = "activeStep"
    private val START_STEP_STYLE = "startStep"
    private val END_STEP_STYLE = "endStep"

    /**
     * Create a graph cell (node) for a given step with appropriate styling.
     */
    fun createStepCell(step: Step): Any {
        val parent = graph.defaultParent

        // Determine the style based on the step's characteristics
        val style = determineStepStyle(step)

        // Format the step label
        val label = formatStepLabel(step)

        // Calculate appropriate width based on label length
        val width = calculateWidthForText(label)

        // Set a standard height
        val height = 45.0

        // Final style: no word wrapping, single line, small font
        val finalStyle = style + ";wordWrap=false;whiteSpace=nowrap;overflow=hidden;fontSize=12;"

        val cell = graph.insertVertex(
            parent, step.id, label,
            0.0, 0.0, width, height, finalStyle
        )

        // Map the cell to the step and vice versa
        cellToStepMap[cell] = step
        stepToCellMap[step.id] = cell

        return cell
    }

    /**
     * Determine the appropriate style for a step node.
     */
    private fun determineStepStyle(step: Step): String {
        val isFirstStep = isStartStep(step)
        val isEndStep = isEndStep(step)

        return when {
            isFirstStep -> START_STEP_STYLE
            isEndStep -> END_STEP_STYLE
            step.isSubStep -> SUB_STEP_STYLE
            else -> MAIN_STEP_STYLE
        }
    }

    /**
     * Check if a step is a start step (entry point).
     */
     fun isStartStep(step: Step): Boolean {
        val rule = currentRuleProvider() ?: return false
        return !rule.steps.any { otherStep -> otherStep.nextStepIds.contains(step.id) }
    }

    /**
     * Check if a step is an end step (no outgoing connections).
     */
     fun isEndStep(step: Step): Boolean {
        return step.nextStepIds.isEmpty()
    }

    /**
     * Format the display label for a step node.
     */
    private fun formatStepLabel(step: Step): String {
        return step.id
    }

    /**
     * Calculate an appropriate width for a node based on text length.
     */
    private fun calculateWidthForText(text: String): Double {
        val textLength = text.length
        return when {
            textLength < 12 -> 140.0
            textLength < 18 -> 170.0
            else -> 220.0
        }
    }
}


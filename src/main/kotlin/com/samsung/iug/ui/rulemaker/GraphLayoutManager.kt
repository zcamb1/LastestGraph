package com.samsung.iug.ui.rulemaker

import com.mxgraph.model.mxGeometry
import com.mxgraph.swing.mxGraphComponent
import com.mxgraph.view.mxGraph
import com.samsung.iug.model.Rule
import com.samsung.iug.model.Step
import java.util.*

/**
 * GraphLayoutManager handles layout logic for arranging nodes in the graph,
 * including positioning main steps, sub-steps, and adjusting vertical alignments.
 */
class GraphLayoutManager(
    private val graph: mxGraph,
    private val graphComponent: mxGraphComponent,
    private val stepToCellMap: Map<String, Any>,
    private val cellToStepMap: Map<Any, Step>,
    private val logger: (String) -> Unit
) {
    private var mainFlowPath = listOf<String>()

    /**
     * Apply a custom layout based on the main flow of the rule.
     */
    fun applyCustomMainFlowLayout(rule: Rule) {
        if (rule.steps.isEmpty()) return

        mainFlowPath = identifyMainFlowPath(rule)

        graph.model.beginUpdate()
        try {
            logger("Applying custom column-based layout")
            logger("Main flow path: ${mainFlowPath.joinToString(" -> ")}")

            positionMainFlow(mainFlowPath)
            ensureVerticalEdgesAligned(rule)
        } finally {
            graph.model.endUpdate()
        }
    }

    /**
     * Position main flow nodes horizontally with equal spacing.
     */
    private fun positionMainFlow(mainFlowPath: List<String>) {
        val xSpacing = 500.0
        val mainFlowY = 200.0

        logger("Positioning main flow nodes horizontally")

        var currentX = 350.0
        for ((index, nodeId) in mainFlowPath.withIndex()) {
            val cell = stepToCellMap[nodeId] ?: continue
            val geo = graph.getCellGeometry(cell)
            if (geo != null) {
                val newGeo = geo.clone() as mxGeometry
                newGeo.x = index * xSpacing + 100
                newGeo.y = mainFlowY
                graph.model.setGeometry(cell, newGeo)
                logger("Positioned main node $nodeId at x=${newGeo.x}, y=${newGeo.y}")
            }
        }

        positionSubNodes()
    }

    /**
     * Position sub-nodes vertically around their parent main steps.
     */
    private fun positionSubNodes() {
        val mainFlowY = 200.0
        val ySpacingAbove = 100.0
        val ySpacingBelow = 100.0

        val processedSubNodes = mutableSetOf<String>()
        val parentToSubNodesMap = mutableMapOf<String, List<String>>()

        for ((parentId, cell) in stepToCellMap) {
            val step = cellToStepMap[cell] ?: continue
            val subNodeIds = step.nextStepIds.filter { id ->
                val subStep = cellToStepMap[stepToCellMap[id] ?: return@filter false]
                subStep?.isSubStep == true
            }
            if (subNodeIds.isNotEmpty()) {
                parentToSubNodesMap[parentId] = subNodeIds
            }
        }

        for ((parentId, subNodeIds) in parentToSubNodesMap) {
            val parentCell = stepToCellMap[parentId] ?: continue
            val parentGeo = graph.getCellGeometry(parentCell) ?: continue

            for ((index, subNodeId) in subNodeIds.withIndex()) {
                if (processedSubNodes.contains(subNodeId)) continue
                val subCell = stepToCellMap[subNodeId] ?: continue
                val subGeo = graph.getCellGeometry(subCell)
                if (subGeo != null) {
                    val newGeo = subGeo.clone() as mxGeometry
                    newGeo.x = parentGeo.x + parentGeo.width + 120

                    if (index % 2 == 0) {
                        val slotIndex = index / 2
                        val yOffset = (slotIndex + 1) * ySpacingAbove
                        newGeo.y = parentGeo.y - yOffset
                    } else {
                        val slotIndex = index / 2
                        val yOffset = (slotIndex + 1) * ySpacingBelow
                        newGeo.y = parentGeo.y + yOffset
                    }
                    graph.model.setGeometry(subCell, newGeo)
                    processedSubNodes.add(subNodeId)
                    logger("Positioned sub-node $subNodeId relative to parent $parentId at x=${newGeo.x}, y=${newGeo.y}")
                }
            }
        }
    }

    /**
     * Align vertical connections between nodes properly.
     */
    private fun ensureVerticalEdgesAligned(rule: Rule) {
        rule.steps.forEach { step ->
            val sourceCell = stepToCellMap[step.id] ?: return@forEach
            val sourceGeo = graph.getCellGeometry(sourceCell) ?: return@forEach
            val sourceCenterX = sourceGeo.x + sourceGeo.width / 2

            step.nextStepIds.forEach { nextStepId ->
                val targetCell = stepToCellMap[nextStepId] ?: return@forEach
                val targetGeo = graph.getCellGeometry(targetCell) ?: return@forEach

                val yDiff = kotlin.math.abs(sourceGeo.y - targetGeo.y)
                val xDiff = kotlin.math.abs(sourceGeo.x - targetGeo.x)

                if (yDiff > 70 && xDiff < 100) {
                    logger("Found vertical connection: ${step.id} -> $nextStepId")

                    val targetCenterX = targetGeo.x + targetGeo.width / 2
                    val newTargetX = sourceCenterX - targetGeo.width / 2

                    if (kotlin.math.abs(sourceCenterX - targetCenterX) > 2) {
                        logger("Adjusting ${nextStepId} X position from ${targetGeo.x} to $newTargetX to align with ${step.id}")
                        val newGeo = targetGeo.clone() as mxGeometry
                        newGeo.x = newTargetX
                        graph.model.setGeometry(targetCell, newGeo)
                    }
                }
            }
        }
    }

    /**
     * Identify the main flow path from start to end nodes.
     */
    private fun identifyMainFlowPath(rule: Rule): List<String> {
        logger("Identifying main flow path for column-based layout")

        val mainNodes = rule.steps.filter { !it.isSubStep }.map { it.id }
        val startNodes = findStartNodes(rule)
        if (startNodes.isEmpty()) {
            logger("No start nodes found, using all main nodes")
            return mainNodes
        }

        val startNodeId = startNodes[0]
        logger("Using start node: $startNodeId")

        val visited = mutableSetOf<String>()
        val result = mutableListOf<String>()

        result.add(startNodeId)
        visited.add(startNodeId)

        val queue = ArrayDeque<String>()
        queue.add(startNodeId)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val currentStep = rule.steps.find { it.id == current } ?: continue

            for (nextId in currentStep.nextStepIds) {
                val nextStep = rule.steps.find { it.id == nextId } ?: continue
                if (!nextStep.isSubStep && nextId !in visited) {
                    visited.add(nextId)
                    result.add(nextId)
                    queue.add(nextId)
                }
            }
        }

        val missingMainNodes = mainNodes.filter { it !in visited }
        if (missingMainNodes.isNotEmpty()) {
            logger("Some main nodes not reachable from start node: ${missingMainNodes.joinToString()}")
            result.addAll(missingMainNodes)
        }

        logger("Main flow path: ${result.joinToString(" -> ")}")
        return result
    }

    /**
     * Find all start nodes (nodes not referenced by others).
     */
    private fun findStartNodes(rule: Rule): List<String> {
        val allReferencedIds = rule.steps.flatMap { it.nextStepIds }.toSet()
        val startNodes = rule.steps.filter { it.id !in allReferencedIds }.map { it.id }

        logger("Found ${startNodes.size} start nodes: ${startNodes.joinToString()}")
        return startNodes
    }

    /**
     * Find all end nodes (nodes with no outgoing connections).
     */
    private fun findEndNodes(rule: Rule): List<String> {
        val endNodes = rule.steps.filter { it.nextStepIds.isEmpty() }.map { it.id }
        logger("Found ${endNodes.size} end nodes: ${endNodes.joinToString()}")
        return endNodes
    }
}

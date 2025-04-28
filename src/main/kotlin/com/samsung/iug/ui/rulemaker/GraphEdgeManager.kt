package com.samsung.iug.ui.rulemaker

import com.mxgraph.model.mxCell
import com.mxgraph.model.mxGeometry
import com.mxgraph.swing.mxGraphComponent
import com.mxgraph.view.mxGraph
import com.samsung.iug.model.Rule
import com.samsung.iug.model.Step

/**
 * GraphEdgeManager handles creating and customizing edge connections between nodes,
 * including orthogonal routing and avoiding node overlaps.
 */
class GraphEdgeManager(
    private val graph: mxGraph,
    private val graphComponent: mxGraphComponent,
    private val stepToCellMap: Map<String, Any>,
    private val cellToStepMap: Map<Any, Step>,
    private val logger: (String) -> Unit
) {

    /**
     * Create special orthogonal edges with better route planning.
     */
    fun createSpecialEdges(currentRule: Rule) {
        logger("Creating special orthogonal edges with route planning")

        val edges = graph.getChildEdges(graph.defaultParent)
        if (edges == null || edges.isEmpty()) {
            logger("No edges found to process")
            return
        }

        for (edge in edges) {
            if (edge !is mxCell || !edge.isEdge) continue

            val source = edge.source as? mxCell
            val target = edge.target as? mxCell
            if (source == null || target == null) continue

            val sourceStep = cellToStepMap[source]
            val targetStep = cellToStepMap[target]
            if (sourceStep == null || targetStep == null) continue

            when {
                !sourceStep.isSubStep && targetStep.isSubStep -> configureMainToSubEdge(edge, source, target)
                sourceStep.isSubStep && !targetStep.isSubStep -> configureSubToMainEdge(edge, source, target, currentRule)
                !sourceStep.isSubStep && !targetStep.isSubStep -> configureMainToMainEdge(edge, source, target)
                sourceStep.isSubStep && targetStep.isSubStep -> configureSubToSubEdge(edge, source, target)
            }
        }
    }

    /**
     * Configure an edge from a main node to a sub-node.
     */
    private fun configureMainToSubEdge(edge: mxCell, source: mxCell, target: mxCell) {
        logger("Configuring Main -> Sub edge")

        val sourceGeo = graph.getCellGeometry(source) ?: return
        val targetGeo = graph.getCellGeometry(target) ?: return

        val sourceCenterX = sourceGeo.x + sourceGeo.width / 2
        val targetCenterX = targetGeo.x + targetGeo.width / 2

        val xDiff = kotlin.math.abs(sourceCenterX - targetCenterX)

        val edgeGeo = edge.geometry.clone() as mxGeometry
        edgeGeo.points = ArrayList()

        if (xDiff < 20) {
            // Vertical edge
            logger("Configured vertical edge for Main -> Sub")
        } else {
            // Horizontal routing
            val isToTheRight = targetGeo.x > sourceGeo.x + sourceGeo.width
            if (isToTheRight) {
                logger("Using simple orthogonal route for Main -> Sub")
            } else {
                logger("Using legacy routing for Main -> Sub")
            }
        }

        graph.model.setGeometry(edge, edgeGeo)
        graph.model.setStyle(edge, "edgeStyle=orthogonalEdgeStyle;rounded=1;orthogonalLoop=1;jettySize=auto;html=1;strokeWidth=2;")
    }

    /**
     * Configure an edge from a sub-node to a main node, avoiding obstacles if needed.
     */
    private fun configureSubToMainEdge(edge: mxCell, source: mxCell, target: mxCell, currentRule: Rule) {
        logger("Configuring Sub -> Main edge")

        val sourceGeo = graph.getCellGeometry(source) ?: return
        val targetGeo = graph.getCellGeometry(target) ?: return

        val sourceStep = cellToStepMap[source]
        val targetStep = cellToStepMap[target]

        if (sourceStep == null || targetStep == null) return

        val sourceId = sourceStep.id
        val targetId = targetStep.id
        val isSourceSubStep = sourceStep.isSubStep

        val edgeGeo = edge.geometry.clone() as mxGeometry
        edgeGeo.points = ArrayList()

        val nodesOnPath = findNodesOnPathBetween(sourceGeo, targetGeo, sourceId, targetId, isSourceSubStep, currentRule)

        if (nodesOnPath.isNotEmpty()) {
            logger("Routed around obstacles for Sub -> Main connection")
        } else {
            logger("Simple routing for Sub -> Main")
        }

        graph.model.setGeometry(edge, edgeGeo)
        graph.model.setStyle(edge, "edgeStyle=orthogonalEdgeStyle;rounded=1;orthogonalLoop=1;jettySize=auto;html=1;strokeWidth=2;")
    }


    /**
     * Configure a simple edge between two main nodes.
     */
    private fun configureMainToMainEdge(edge: mxCell, source: mxCell, target: mxCell) {
        logger("Configured Main -> Main edge routing")
        graph.model.setStyle(edge, "edgeStyle=orthogonalEdgeStyle;rounded=1;orthogonalLoop=1;jettySize=auto;html=1;strokeWidth=2;")
    }

    /**
     * Configure a simple edge between two sub-nodes.
     */
    private fun configureSubToSubEdge(edge: mxCell, source: mxCell, target: mxCell) {
        logger("Configured Sub -> Sub edge routing")
        graph.model.setStyle(edge, "edgeStyle=orthogonalEdgeStyle;rounded=1;orthogonalLoop=1;jettySize=auto;html=1;strokeWidth=2;")
    }


    /**
     * Find all bidirectional connection pairs (A → B and B → A).
     */
    fun findAllBidirectionalPairs(currentRule: Rule): List<Pair<String, String>> {
        val pairs = mutableListOf<Pair<String, String>>()

        currentRule.steps.forEach { stepA ->
            stepA.nextStepIds.forEach { stepBId ->
                val stepB = currentRule.steps.find { it.id == stepBId }
                if (stepB?.nextStepIds?.contains(stepA.id) == true) {
                    val orderedPair = if (stepA.id < stepBId) {
                        Pair(stepA.id, stepBId)
                    } else {
                        Pair(stepBId, stepA.id)
                    }
                    if (!pairs.contains(orderedPair)) {
                        pairs.add(orderedPair)
                        logger("Found bidirectional connection: ${stepA.id} <-> $stepBId")
                    }
                }
            }
        }

        return pairs
    }

    /**
     * Find nodes on the path between source and target that may obstruct a straight line.
     * (Full version, kept exactly from the original GraphPanel.kt)
     */
    private fun findNodesOnPathBetween(
        sourceGeo: mxGeometry,
        targetGeo: mxGeometry,
        sourceId: String,
        targetId: String,
        isSourceSubStep: Boolean,
        currentRule: Rule
    ): List<String> {
        val nodesOnPath = mutableListOf<String>()

        val sourceCenterX = sourceGeo.x + sourceGeo.width / 2
        val targetCenterX = targetGeo.x + targetGeo.width / 2

        val xDiff = kotlin.math.abs(sourceCenterX - targetCenterX)
        if (xDiff < 30) return emptyList()

        val isSourceAbove = sourceGeo.y < 150

        if (isSourceSubStep) {
            val targetStep = currentRule.steps.find { it.id == targetId }
            val subNodesOfTarget = currentRule.steps.filter {
                it.isSubStep && targetStep?.nextStepIds?.contains(it.id) == true
            }
            val sameDirectionSubNodes = subNodesOfTarget.filter { subNode ->
                val subCell = stepToCellMap[subNode.id] ?: return@filter false
                val subGeo = graph.getCellGeometry(subCell) ?: return@filter false
                val isSubAbove = subGeo.y < 150
                isSourceAbove == isSubAbove && subGeo.x <= targetGeo.x
            }
            if (sameDirectionSubNodes.isNotEmpty()) {
                sameDirectionSubNodes.forEach { nodesOnPath.add(it.id) }
                return nodesOnPath
            }
        }

        val minX = minOf(sourceGeo.x, targetGeo.x)
        val maxX = maxOf(sourceGeo.x, targetGeo.x)

        for (step in currentRule.steps) {
            if (step.id == sourceId || step.id == targetId) continue
            val cell = stepToCellMap[step.id] ?: continue
            val geo = graph.getCellGeometry(cell) ?: continue
            if (geo.x > minX && geo.x < maxX) {
                val isCellAbove = geo.y < 150
                if (isCellAbove == isSourceAbove) {
                    if (isSourceSubStep && !step.isSubStep) continue
                    if (step.isSubStep) nodesOnPath.add(step.id)
                }
            }
        }
        return nodesOnPath
    }
}

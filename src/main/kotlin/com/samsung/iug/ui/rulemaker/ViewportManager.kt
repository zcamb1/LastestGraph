package com.samsung.iug.ui.rulemaker

import com.intellij.openapi.diagnostic.Logger
import com.mxgraph.model.mxGeometry
import com.mxgraph.swing.mxGraphComponent
import com.mxgraph.view.mxGraph
import com.mxgraph.util.mxPoint

/**
 * ViewportManager ensures that all nodes in the graph are visible
 * by translating the graph if necessary (e.g., when nodes have negative coordinates).
 */
class ViewportManager(
    private val graph: mxGraph,
    private val graphComponent: mxGraphComponent,
    private val stepToCellMap: Map<String, Any>,
    private val logger: Logger
) {

    /**
     * Ensure all nodes are visible within the graph's viewport
     * by shifting nodes and updating edge control points if necessary.
     */
    fun ensureNodesVisible() {
        logger.info("Ensuring all nodes are visible in the viewport")

        var minX = Double.MAX_VALUE
        var minY = Double.MAX_VALUE
        var maxX = Double.MIN_VALUE
        var maxY = Double.MIN_VALUE

        for (cell in stepToCellMap.values) {
            val geo = graph.getCellGeometry(cell) ?: continue
            minX = minOf(minX, geo.x)
            minY = minOf(minY, geo.y)
            maxX = maxOf(maxX, geo.x + geo.width)
            maxY = maxOf(maxY, geo.y + geo.height)
        }

        logger.info("Graph bounds: minX=$minX, minY=$minY, maxX=$maxX, maxY=$maxY")

        var needsTranslation = false
        var translateX = 0.0
        var translateY = 0.0

        if (minX < 0) {
            translateX = kotlin.math.abs(minX) + 50
            needsTranslation = true
            logger.info("Need to translate X by $translateX")
        }

        if (minY < 0) {
            translateY = kotlin.math.abs(minY) + 50
            needsTranslation = true
            logger.info("Need to translate Y by $translateY")
        }

        if (needsTranslation) {
            logger.info("Translating graph by X=$translateX, Y=$translateY")

            graph.model.beginUpdate()
            try {
                for (cell in stepToCellMap.values) {
                    val geo = graph.getCellGeometry(cell) ?: continue
                    val newGeo = geo.clone() as mxGeometry
                    newGeo.x += translateX
                    newGeo.y += translateY
                    graph.model.setGeometry(cell, newGeo)
                }

                for (edge in graph.getChildEdges(graph.defaultParent)) {
                    val geo = graph.getCellGeometry(edge) ?: continue
                    if (geo.points != null && geo.points.isNotEmpty()) {
                        val newGeo = geo.clone() as mxGeometry
                        val newPoints = ArrayList<mxPoint>()
                        for (point in geo.points) {
                            newPoints.add(mxPoint(point.x + translateX, point.y + translateY))
                        }
                        newGeo.points = newPoints
                        graph.model.setGeometry(edge, newGeo)
                    }
                }
            } finally {
                graph.model.endUpdate()
            }

            logger.info("Translation complete")
        }

        graphComponent.zoomAndCenter()
    }
}
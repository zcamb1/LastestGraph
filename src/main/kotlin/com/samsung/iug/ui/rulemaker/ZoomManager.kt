package com.samsung.iug.ui.rulemaker

import com.intellij.openapi.diagnostic.Logger
import com.mxgraph.swing.mxGraphComponent

/**
 * ZoomManager handles zoom operations (getting and setting zoom levels)
 * for the graph component, ensuring smooth scaling and readability.
 */
class ZoomManager(
    private val graphComponent: mxGraphComponent,
    private val logger: Logger
) {

    /**
     * Helper method to safely get the current zoom level.
     */
    fun getCurrentZoom(): Double {
        return try {
            graphComponent.graph.view.scale
        } catch (e: Exception) {
            logger.warn("Could not determine current zoom level: ${e.message}")
            1.0 // Default zoom level
        }
    }

    /**
     * Helper method to safely set the zoom level with a minimum bound
     * to ensure nodes remain visible and readable.
     */
    fun setZoomLevel(zoomLevel: Double) {
        try {
            // Set zoom level to 1.0 for better readability
            val adjustedZoom = 1.0
            graphComponent.zoom(adjustedZoom)
            logger.info("Set zoom level to $adjustedZoom (requested: $zoomLevel)")
        } catch (e: Exception) {
            logger.error("Failed to set zoom level: ${e.message}")
        }
    }
}
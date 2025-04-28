package com.samsung.iug.ui.rulemaker

import com.mxgraph.util.mxConstants
import com.mxgraph.view.mxGraph
import java.util.*

object GraphStyles {
    fun setupStylesheet(graph: mxGraph, defaultEdgeColor: String) {
        val stylesheet = graph.stylesheet

        // Helper function để tạo style node
        fun nodeStyle(
            fill: String,
            stroke: String,
            font: Int = 12,
            width: Double = 1.5
        ): HashMap<String, Any> {
            val map = HashMap<String, Any>()
            map[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE
            map[mxConstants.STYLE_PERIMETER] = mxConstants.PERIMETER_RECTANGLE
            map[mxConstants.STYLE_ROUNDED] = true
            map[mxConstants.STYLE_ARCSIZE] = 20
            map[mxConstants.STYLE_FONTSIZE] = font
            map[mxConstants.STYLE_FONTCOLOR] = "#000000"
            map[mxConstants.STYLE_FILLCOLOR] = fill
            map[mxConstants.STYLE_STROKECOLOR] = stroke
            map[mxConstants.STYLE_STROKEWIDTH] = width
            map[mxConstants.STYLE_SHADOW] = true
            map[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER
            map[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE
            map["wordWrap"] = "true"
            return map
        }

        // Đăng ký các style cho node
        stylesheet.putCellStyle("mainStep", nodeStyle("#D4E7FF", "#7EA6E0"))
        stylesheet.putCellStyle("subStep", nodeStyle("#F0F0F0", "#B0B0B0", font = 11))
        stylesheet.putCellStyle("startStep", nodeStyle("#A5D6A7", "#2E7D32", width = 2.0))
        stylesheet.putCellStyle("endStep", nodeStyle("#FFD2D2", "#FF9999"))
        stylesheet.putCellStyle("activeStep", nodeStyle("#FFE2B8", "#FFA940", width = 2.0))

        // Edge style
        val edgeStyle = HashMap<String, Any>().apply {
            put(mxConstants.STYLE_STROKECOLOR, defaultEdgeColor)
            put(mxConstants.STYLE_STROKEWIDTH, 2.0)
            put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC)
            put(mxConstants.STYLE_FONTSIZE, 11)
            put(mxConstants.STYLE_OPACITY, 100)
            put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ORTHOGONAL)
            put(mxConstants.STYLE_ROUNDED, true)
            put(mxConstants.STYLE_ARCSIZE, 15)
            put(mxConstants.STYLE_ENDSIZE, 12.0)
        }
        stylesheet.putCellStyle("edge", edgeStyle)
    }
}

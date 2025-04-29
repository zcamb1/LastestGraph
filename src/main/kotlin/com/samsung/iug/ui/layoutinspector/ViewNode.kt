package com.samsung.iug.ui.layoutinspector

import java.awt.Rectangle

data class ViewNode(
    val className: String,
    val resourceId: String,
    val text: String,
    val bounds: Rectangle,
    val children: List<ViewNode> = emptyList()
) {
    override fun toString(): String {
        val idPart = if (resourceId.isNotBlank()) "id=$resourceId" else ""
        val textPart = if (text.isNotBlank()) "text=$text" else ""
        return "$className [$idPart $textPart]".trim()
    }

    fun flatten(): List<ViewNode> {
        return listOf(this) + children.flatMap { it.flatten() }
    }

    fun findById(id: String): ViewNode? {
        if (this.resourceId == id) return this
        return children.firstNotNullOfOrNull { it.findById(id) }
    }
}
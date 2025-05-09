package com.samsung.iug.ui.graph

import java.awt.Rectangle

class Node (
    var id: String,
    var x: Int = 0,
    var y: Int = 0,
    val children: MutableList<Node> = mutableListOf(),
    var level: Int = 0,
    var point: Int = 0,
    var parent: Node? = null,
    var screenId: String = "",
    var className: String = "",
    var resourceId: String = "",
    var text: String = "",
    var bounds: Rectangle = Rectangle(0, 0, 0, 0),
    var contentDescription: String = "",
    var guildContent: String = ""
)

class Edge (
    val from: Node,
    val to: Node
)

object listNode {
    val listNode = mutableListOf<Node>()
}

object listEdge {
    val listEdge = mutableListOf<Edge>()
}
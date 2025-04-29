package com.samsung.iug.ui.layoutinspector

import javax.swing.tree.DefaultMutableTreeNode

class TreeNodeAdapter(private val node: ViewNode) : DefaultMutableTreeNode(node.className) {
    init {
        for (child in node.children) {
            add(TreeNodeAdapter(child))
        }
    }

    fun getViewNode(): ViewNode = node
}
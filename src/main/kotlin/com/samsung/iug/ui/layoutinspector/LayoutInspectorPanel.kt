package com.samsung.iug.ui.layoutinspector

import java.awt.BorderLayout
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.border.TitledBorder
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

class LayoutInspectorPanel : JPanel(BorderLayout()) {

    private val tree = JTree()
    private var rootNode: ViewNode? = null
    private var selectedNodeId: String? = null
    var onNodeSelected: ((ViewNode) -> Unit)? = null

    init {
        border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Layout Inspector Panel",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            null
        )

        tree.model = DefaultTreeModel(DefaultMutableTreeNode("No layout loaded"))
        add(JScrollPane(tree), BorderLayout.CENTER)

        tree.addTreeSelectionListener { e ->
            val selectedPath = e.path
            val selectedNode = selectedPath?.lastPathComponent as? TreeNodeAdapter
            val viewNode = selectedNode?.getViewNode()
            viewNode?.let {
                onNodeSelected?.invoke(it)
            }
        }
    }

    fun updateTree(nodes: List<ViewNode>) {
        if (nodes.isEmpty()) {
            rootNode= null
            tree.model = DefaultTreeModel(DefaultMutableTreeNode("No layout available"))
            return
        }

        rootNode = nodes[0]
        val rootTreeNode = TreeNodeAdapter(rootNode!!)
        val model = DefaultTreeModel(rootTreeNode)
        tree.model = model
        tree.updateUI()
        expandAll()

        selectedNodeId?.let { id ->
            selectNodeById(id)
        }
    }

    fun getAllNodes(): List<ViewNode> {
        return rootNode?.flatten() ?: emptyList()
    }

    fun clearTree() {
        tree.model = DefaultTreeModel(DefaultMutableTreeNode("No layout loaded"))
    }

    fun selectNodeById(id: String) {
        selectedNodeId = id
        val target = rootNode?.findById(id) ?: return
        val pathList = findPathToNode(target, tree.model.root as DefaultMutableTreeNode)
        if (pathList != null) {
            val path = TreePath(pathList.toTypedArray())
            tree.selectionPath = path
            tree.scrollPathToVisible(path)
        }
    }

    private fun findPathToNode(
        targetNode: ViewNode,
        currentTreeNode: DefaultMutableTreeNode
    ): List<Any>? {
        if (currentTreeNode is TreeNodeAdapter && currentTreeNode.getViewNode().resourceId == targetNode.resourceId) {
            return listOf(currentTreeNode)
        }

        for (i in 0 until currentTreeNode.childCount) {
            val child = currentTreeNode.getChildAt(i) as DefaultMutableTreeNode
            val path = findPathToNode(targetNode, child)
            if (path != null) {
                return listOf(currentTreeNode) + path
            }
        }

        return null
    }

    private fun expandAll() {
        for (i in 0 until tree.rowCount) {
            tree.expandRow(i)
        }
    }

    fun scrollToViewNode(node: ViewNode) {
        selectNodeById(node.resourceId)
    }
}
//package com.samsung.iug.ui
//
//import com.mxgraph.model.mxCell
//import com.mxgraph.model.mxGeometry
//import com.mxgraph.swing.mxGraphComponent
//import com.mxgraph.view.mxGraph
//import com.samsung.iug.model.Step
//import javax.swing.JPopupMenu
//import javax.swing.JMenuItem
//import javax.swing.JOptionPane
//
///**
// * ContextMenuManager handles right-click context menus for graph panel,
// * including operations on steps like edit, add, remove, swap, and layout.
// */
//class ContextMenuManager(
//        private val graph: mxGraph,
//        private val graphComponent: mxGraphComponent,
//        private val stepToCellMap: MutableMap<String, Any>,
//        private val cellToStepMap: MutableMap<Any, Step>,
//        private val onStepSelected: (Step) -> Unit,
//        private val onAddSubStep: (Step) -> Unit,
//        private val onAddStep: (Step?, mxCell?, mxGeometry?) -> Unit,
//        private val onRemoveStep: (Step) -> Boolean,
//        private val onSwapNode: (Step, String) -> Unit,
//        private val applyLayout: () -> Unit
//) {
//
//    /**
//     * Show context menu when right-clicking on a step node.
//     */
//    fun showContextMenu(x: Int, y: Int, step: Step) {
//        val popup = JPopupMenu()
//
//        // Edit Step
//        val editItem = JMenuItem("Edit Step")
//        editItem.addActionListener { onStepSelected(step) }
//        popup.add(editItem)
//
//        if (!step.isSubStep) {
//            // Add Sub-Step
//            val addSubStepItem = JMenuItem("Add Sub-Step")
//            addSubStepItem.addActionListener { onAddSubStep(step) }
//            popup.add(addSubStepItem)
//
//            // Add Next Step
//            val addNextStepItem = JMenuItem("Add Next Step")
//            addNextStepItem.addActionListener {
//                val parentCell = stepToCellMap[step.id] as? mxCell
//                val parentGeo = parentCell?.let { graph.getCellGeometry(it) }
//                onAddStep(step, parentCell, parentGeo)
//            }
//            popup.add(addNextStepItem)
//        }
//
//        // Remove Step (only if no children)
//        val removeItem = JMenuItem("Remove Step")
//        removeItem.isEnabled = !step.hasChildren()
//        removeItem.addActionListener {
//            if (onRemoveStep(step)) {
//                applyLayout()
//            }
//        }
//        popup.add(removeItem)
//
//        // Swap Node
//        val swapItem = JMenuItem("Swap Node")
//        swapItem.addActionListener {
//            val swapId = JOptionPane.showInputDialog(
//                    null,
//                    "Enter ID of node to swap with:",
//                    "Swap Node",
//                    JOptionPane.QUESTION_MESSAGE
//            )
//            if (swapId != null && swapId.isNotBlank()) {
//                onSwapNode(step, swapId.trim())
//            }
//        }
//        popup.add(swapItem)
//
//        // Layout menu
//        popup.addSeparator()
//        val layoutItem = JMenuItem("Rearrange Layout")
//        layoutItem.addActionListener { applyLayout() }
//        popup.add(layoutItem)
//
//        popup.show(graphComponent.graphControl, x, y)
//    }
//
//    /**
//     * Show context menu when right-clicking on empty space.
//     */
//    fun showEmptySpaceContextMenu(x: Int, y: Int) {
//        val popup = JPopupMenu()
//
//        val addStepItem = JMenuItem("Add New Step")
//        addStepItem.addActionListener { onAddStep(null, null, null) }
//        popup.add(addStepItem)
//
//        popup.addSeparator()
//        val layoutItem = JMenuItem("Rearrange Layout")
//        layoutItem.addActionListener { applyLayout() }
//        popup.add(layoutItem)
//
//        popup.show(graphComponent.graphControl, x, y)
//    }
//}

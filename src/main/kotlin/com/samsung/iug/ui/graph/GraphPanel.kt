package com.samsung.iug.ui.graph

import com.mxgraph.model.mxCell
import com.mxgraph.swing.mxGraphComponent
import com.mxgraph.util.mxConstants
import com.mxgraph.view.mxGraph
import java.awt.*
import java.awt.event.*
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants
import javax.swing.SwingUtilities

/**
 * Graph panel using JGraphX (mxGraph) library
 * Manages the graph visualization of the IUG workflow
 */
object GraphPanel : JPanel() {
    // JGraphX components
    private val graph = mxGraph()
    private val graphComponent: mxGraphComponent
    private val parent = graph.defaultParent
    
    // Track hovered cells
    private var currentHoverCell: Any? = null
    private var hoveredEdge: mxCell? = null
    
    // Track action dots
    private var dotCells = mutableListOf<mxCell>()
    
    // Style constants - moved outside companion object since object can't have companion
    const val NODE_WIDTH = 280.0
    const val NODE_HEIGHT = 110.0
    const val NODE_STYLE = "userQueryNode"
    const val ADD_BUTTON_STYLE = "addButton"
    
    /**
     * Get the graphComponent for use in other UI components
     */
    fun getGraphComponent(): mxGraphComponent {
        return graphComponent
    }
    
    /**
     * Get the graph instance
     */
    fun getGraph(): mxGraph {
        return graph
    }
    
    init {
        layout = BorderLayout()
        isOpaque = false
        
        // Basic graph configuration
        graph.isAllowDanglingEdges = false
        graph.isAllowLoops = false
        graph.isCellsEditable = false
        graph.isCellsResizable = false
        graph.setCellsDisconnectable(false)
        graph.setConnectableEdges(false)
        graph.setCellsBendable(false)
        
        // Configure HTML labels to render properly
        graph.isHtmlLabels = true

        // Apply all styles using CustomGraphRenderer
        CustomGraphRenderer.applyStyles(graph)
        
        // Add styles for action dots
        configureActionDotStyles()
        
        // Create a basic graph component
        graphComponent = mxGraphComponent(graph)

        // Configure graph component for transparency
        graphComponent.isAntiAlias = true
        graphComponent.background = Color(0, 0, 0, 0) // Transparent background
        graphComponent.foreground = Color.WHITE
        graphComponent.isFocusable = false
        graphComponent.border = null
        
        // Make all components transparent
        graphComponent.isOpaque = false
        graphComponent.viewport.isOpaque = false
        
        // Turn off grid to use parent panel's grid
        graphComponent.isGridVisible = false
        
        // Remove scrollbars - let the parent container handle scrolling
        graphComponent.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        graphComponent.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
        
        // Ensure the content area takes the full available size
        graphComponent.setPreferredSize(Dimension(2000, 2000)) // Large enough to show all content
        
        // Allow the control panel to take as much space as needed
        graphComponent.graphControl.setMinimumSize(Dimension(0, 0))
        graphComponent.graphControl.setPreferredSize(null)
        
        // Add edge context menu
        setupEdgeContextMenu()
        
        // Setup mouse listeners
        setupGraphListeners()
        
        // Add resize listener to ensure graph scaling works properly
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                // Update graph component size when panel is resized
                graphComponent.setSize(width, height)
                graphComponent.viewport.setViewSize(Dimension(width, height))
                
                // Ensure viewport sees all content
                graphComponent.zoom(1.0) // Reset zoom to ensure proper sizing
            }
        })
        
        // Insert initial utterance node
        graph.model.beginUpdate()
        try {
            // Create user query node
            val initialNode = createUtteranceNode("What does the user ask?")
            
            // Create add button for initial node using AddButtonConnector
            AddButtonConnector.createAddButtonForNode(graph, parent, initialNode)
        } finally {
            graph.refresh()
            graphComponent.repaint()
            graph.model.endUpdate()
        }
        
        // Add the graph component to this panel - using a constraint that expands it fully
        add(graphComponent, BorderLayout.CENTER)
    }
    
    /**
     * Set up edge context menu for deleting connections
     */
    private fun setupEdgeContextMenu() {
        // Add mouse listener for right clicks on edges
        graphComponent.graphControl.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    val cell = graphComponent.getCellAt(e.x, e.y) as? mxCell
                    if (cell != null && cell.isEdge) {
                        // Store the edge for deleting
                        hoveredEdge = cell
                        
                        // Show confirmation dialog
                        showDeleteConnectionDialog(cell)
                        
                        // Debug output
                        println("Delete connection dialog shown for edge: ${cell.id}")
                    }
                }
            }
        })
        
        // Add mouse motion listener to track hovered edge
        graphComponent.graphControl.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                val cell = graphComponent.getCellAt(e.x, e.y) as? mxCell
                hoveredEdge = if (cell != null && cell.isEdge) cell else null
            }
        })
    }
    
    /**
     * Show a dialog asking whether to delete the connection
     */
    private fun showDeleteConnectionDialog(edge: mxCell) {
        // Get source and target nodes
        val sourceNode = edge.source as? mxCell
        val targetNode = edge.target as? mxCell
        
        // Build description of the connection
        val connectionDescription = buildConnectionDescription(sourceNode, targetNode)
        
        // Show confirmation dialog
        val options = arrayOf("Delete", "Cancel")
        val result = JOptionPane.showOptionDialog(
            this,
            "Delete connection $connectionDescription?",
            "Delete Connection",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[1] // Default option is Cancel
        )
        
        // If user clicked Delete
        if (result == 0) {
            deleteEdge(edge)
        }
    }
    
    /**
     * Build a description of the connection for the dialog
     */
    private fun buildConnectionDescription(sourceNode: mxCell?, targetNode: mxCell?): String {
        // Extract node IDs or labels
        val sourceId = extractNodeId(sourceNode?.value?.toString() ?: "")
        val targetId = extractNodeId(targetNode?.value?.toString() ?: "")
        
        return "from \"$sourceId\" to \"$targetId\""
    }
    
    /**
     * Extract a readable ID from node's HTML content
     */
    private fun extractNodeId(htmlContent: String): String {
        // Handle empty content
        if (htmlContent.isEmpty()) return "Unknown"
        
        // Check if this is the User Query node
        if (htmlContent.contains("User Query")) {
            return "User Query"
        }
        
        // For regular nodes, extract Step ID from the HTML content
        val textPattern = "<tr><td[^>]*>(.*?)</td></tr>\\s*<tr><td[^>]*>(.*?)</td></tr>".toRegex(RegexOption.DOT_MATCHES_ALL)
        val match = textPattern.find(htmlContent)
        
        // The second captured group should be the Step ID
        return match?.groupValues?.getOrNull(2)?.trim() ?: "Unknown"
    }
    
    /**
     * Delete an edge from the graph
     */
    private fun deleteEdge(edge: mxCell) {
        graph.model.beginUpdate()
        try {
            // Remove the edge
            graph.removeCells(arrayOf(edge))
            
            // Refresh the graph
            graph.refresh()
        } finally {
            graph.model.endUpdate()
        }
    }
    
    /**
     * Setup listeners for graph events
     */
    private fun setupGraphListeners() {
        // Mouse motion listener for hover effects
        graphComponent.graphControl.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                handleNodeHover(e)
            }
        })
        
        // Mouse listener for clicks and for clearing dots when mouse exits
        graphComponent.graphControl.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2) {
                    val cell = graphComponent.getCellAt(e.x, e.y) as? mxCell
                    if (cell != null && !cell.isEdge && !AddButtonConnector.isAddButton(graph, cell)) {
                        // Show edit dialog using the extracted NodeEditDialog
                        NodeEditDialog.showEditDialog(this@GraphPanel, cell, graph)
                    }
                } else if (e.clickCount == 1) {
                    val cell = graphComponent.getCellAt(e.x, e.y) as? mxCell
                    if (cell != null) {
                        // Xử lý khi click vào action dots
                        if (dotCells.contains(cell)) {
                            handleActionDotClick(cell, e)
                        } 
                        // Xử lý khi click vào nút dấu cộng
                        else if (AddButtonConnector.isAddButton(graph, cell)) {
                            AddButtonConnector.handleAddButtonClick(this@GraphPanel, graph, cell)
                        }
                    }
                }
            }
            
            override fun mouseExited(e: MouseEvent) {
                // Clear dots when mouse exits the component
                clearDragDots()
                currentHoverCell = null
            }
        })
    }
    
    /**
     * Handle hover over nodes to show or hide action dots
     */
    private fun handleNodeHover(e: MouseEvent) {
        val cell = graphComponent.getCellAt(e.x, e.y) as? mxCell
        
        // If hovering over a dot, keep the dots visible
        if (cell != null && dotCells.contains(cell)) {
            return
        }
        
        // Check if the cell is a node we should react to (not edge, not add button)
        val isHoveringOverNode = cell != null && 
                             !cell.isEdge && 
                             !AddButtonConnector.isAddButton(graph, cell) &&
                             !dotCells.contains(cell)
        
        // Determine if we need to clear existing dots
        var shouldClearDots = false
        
        // If moving to a different cell or empty space
        if (cell != currentHoverCell) {
            // If we were hovering over a node before, check if we're still in the dot area
            if (currentHoverCell != null) {
                // Safely capture the current hover cell in a local variable
                val hoverCell = currentHoverCell
                
                // If the current position is not in a dot area, clear the dots
                val inDotArea = if (hoverCell != null) isInDotArea(hoverCell, e.point) else false
                if (!inDotArea && !dotCells.contains(cell)) {
                    shouldClearDots = true
                }
            } else {
                // We weren't hovering over anything before, so just clear
                shouldClearDots = true
            }
        }
        
        // Clear dots if needed
        if (shouldClearDots) {
            clearDragDots()
            currentHoverCell = null
        }
        
        // Create new dots if hovering over a node
        if (isHoveringOverNode && cell != currentHoverCell) {
            currentHoverCell = cell
            cell?.let { createActionDots(it) }
        }
    }
    
    /**
     * Create action dots (edit and copy) when hovering over a node
     */
    private fun createActionDots(cell: mxCell) {
        graph.model.beginUpdate()
        try {
            val cellGeometry = graph.getCellGeometry(cell)
            val x = cellGeometry.x
            val y = cellGeometry.y + cellGeometry.height + 10 // Position dots below the node
            
            // Determine if this is a step node (green border) or query node (purple border)
            val isStepNode = cell.style.contains("stepNode")
            
            // Create edit dot
            createEditDot(cell, x, y, isStepNode)
            
            // Create copy dot
            createCopyDot(cell, x + 35, y, isStepNode)
            
            // Create trash bin icon to the right of the other dots
            createTrashIcon(cell, x + 70, y, isStepNode)
        } finally {
            graph.model.endUpdate()
        }
    }
    
    /**
     * Create trash bin icon
     */
    private fun createTrashIcon(cell: mxCell, x: Double, y: Double, isStepNode: Boolean) {
        val trashDotStyle = if (isStepNode) "trashGreenDot" else "trashDot"
        val iconColor = if (isStepNode) "#4ade80" else "#6366F1"
        
        // Create HTML content with trash bin icon
        val trashIconHtml = """
            <div style="display:flex;justify-content:center;align-items:center;width:100%;height:100%;padding-bottom:5px;">
                <span style="font-size:16px;color:${iconColor};">🗑️</span>
            </div>
        """.trimIndent()
        
        // Create trash dot
        val trashDot = graph.insertVertex(
            parent, null, trashIconHtml,
            x, y, 20.0, 20.0, trashDotStyle + ";html=1"
        )

        dotCells.add(trashDot as mxCell)
    }
    
    /**
     * Create edit dot with a pen icon
     */
    private fun createEditDot(cell: mxCell, x: Double, y: Double, isStepNode: Boolean) {
        val editDotStyle = if (isStepNode) "editGreenDot" else "editDot"
        val iconColor = if (isStepNode) "#4ade80" else "#6366F1"
        
        // Create HTML content with enhanced styling for the edit icon
        val editIconHtml = """
            <div style="display:flex;justify-content:center;align-items:center;width:100%;height:100%;padding-bottom:10px;">
                <span style="font-size:14px;color:${iconColor};">✎</span>
            </div>
        """.trimIndent()
        
        // Apply HTML style directly in the style string
        val editDot = graph.insertVertex(
            parent, null, editIconHtml,
            x + 5, y, 20.0, 20.0, editDotStyle + ";html=1"
        )

        dotCells.add(editDot as mxCell)
    }
    
    /**
     * Create copy dot with a copy icon
     */
    private fun createCopyDot(cell: mxCell, x: Double, y: Double, isStepNode: Boolean) {
        val copyDotStyle = if (isStepNode) "copyGreenDot" else "copyDot"
        val iconColor = if (isStepNode) "#4ade80" else "#6366F1"
        
        // Create HTML content with enhanced styling for the copy icon
        val copyIconHtml = """
            <div style="display:flex;justify-content:center;align-items:center;width:100%;height:100%;padding-bottom:10px;">
                <span style="font-size:12px;color:${iconColor};">⧉</span>
            </div>
        """.trimIndent()
        
        // Create copy dot
        val copyDot = graph.insertVertex(
            parent, null, copyIconHtml,
            x, y, 20.0, 20.0, copyDotStyle + ";html=1"
        )

        dotCells.add(copyDot as mxCell)
    }
    
    /**
     * Handle clicks on action dots
     */
    private fun handleActionDotClick(cell: mxCell, e: MouseEvent) {
        val style = cell.style ?: ""
        
        // Check if the clicked cell is an edit dot
        if (style.contains("editDot") || style.contains("editGreenDot")) {
            // Find the parent node for this dot
            val parentNode = findParentNodeForDot(cell)
            if (parentNode != null) {
                NodeEditDialog.showEditDialog(this, parentNode, graph)
            }
        }
        // Check if the clicked cell is a copy dot
        else if (style.contains("copyDot") || style.contains("copyGreenDot")) {
            // Copy functionality would be implemented here
            // Currently disabled as per requirements
        }
        // Check if the clicked cell is a trash dot
        else if (style.contains("trashDot") || style.contains("trashGreenDot")) {
            // Find the parent node for this dot
            val parentNode = findParentNodeForDot(cell)
            if (parentNode != null) {
                deleteNode(parentNode)
            }
        }
    }
    
    /**
     * Delete a node from the graph
     */
    private fun deleteNode(node: mxCell) {
        graph.model.beginUpdate()
        try {
            // Find all connected edges
            val allEdges = graph.getEdges(node)
            
            // Find any add buttons that might be connected
            val connectedCells = allEdges.mapNotNull { 
                val edge = it as mxCell
                // Get the opposite end of each edge
                if (edge.source == node) edge.target else edge.source
            }.filter { 
                // Filter for add buttons
                AddButtonConnector.isAddButton(graph, it) 
            }.toTypedArray()
            
            // Remove the add buttons
            if (connectedCells.isNotEmpty()) {
                graph.removeCells(connectedCells)
            }
            
            // Now remove the edges
            if (allEdges.isNotEmpty()) {
                graph.removeCells(allEdges)
            }
            
            // Remove the node itself
            graph.removeCells(arrayOf(node))
            
            // Clear any dots
            clearDragDots()
            
            // Refresh the graph
            graph.refresh()
        } finally {
            graph.model.endUpdate()
        }
    }
    
    /**
     * Check if a point is in the dot area below a node
     */
    private fun isInDotArea(node: Any, point: Point): Boolean {
        val cellGeometry = graph.getCellGeometry(node)
        if (cellGeometry != null) {
            // Calculate the dot area below the node
            val dotArea = Rectangle(
                (cellGeometry.x).toInt(),
                (cellGeometry.y + cellGeometry.height).toInt(),
                cellGeometry.width.toInt(),
                40 // Height of dot area
            )
            
            // Convert point to graph coordinates
            val graphPoint = Point(point)
            
            return dotArea.contains(graphPoint.x, graphPoint.y)
        }
        return false
    }
    
    /**
     * Find the parent node for an action dot
     */
    private fun findParentNodeForDot(dotCell: mxCell): mxCell? {
        // Get all vertices in the graph
        val vertices = graph.getChildVertices(graph.defaultParent)
        
        // Find the node that is geometrically related to this dot
        return vertices.filterIsInstance<mxCell>()
            .filter { it != dotCell && !dotCells.contains(it) }
            .minByOrNull { 
                val dotGeom = graph.getCellGeometry(dotCell)
                val cellGeom = graph.getCellGeometry(it)
                
                // Calculate distance - handling both dots below and above the node
                val dx = Math.abs(dotGeom.x - (cellGeom.x + cellGeom.width / 2))
                val dyBelow = Math.abs(dotGeom.y - (cellGeom.y + cellGeom.height))
                val dyAbove = Math.abs(dotGeom.y - cellGeom.y)
                val dy = Math.min(dyBelow, dyAbove)
                
                // Return a combined distance measure
                dx + dy
            }
    }
    
    /**
     * Configure styles for action dots
     */
    private fun configureActionDotStyles() {
        val stylesheet = graph.stylesheet
        
        // Common properties for action dots
        val commonProps = HashMap<String, Any>()
        commonProps[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_ELLIPSE
        commonProps[mxConstants.STYLE_FILLCOLOR] = "#1E1E1E" // Dark background
        commonProps[mxConstants.STYLE_STROKEWIDTH] = 2
        commonProps[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE
        commonProps[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER
        
        // Create styles with different colors
        createActionDotStyle(stylesheet, "editDot", "#6366F1", commonProps) // Purple edit
        createActionDotStyle(stylesheet, "editGreenDot", "#4ade80", commonProps) // Green edit
        createActionDotStyle(stylesheet, "copyDot", "#6366F1", commonProps) // Purple copy
        createActionDotStyle(stylesheet, "copyGreenDot", "#4ade80", commonProps) // Green copy
        createActionDotStyle(stylesheet, "trashDot", "#6366F1", commonProps) // Purple trash
        createActionDotStyle(stylesheet, "trashGreenDot", "#4ade80", commonProps) // Green trash
    }
    
    /**
     * Create action dot style with specific color
     */
    private fun createActionDotStyle(stylesheet: com.mxgraph.view.mxStylesheet, styleName: String, color: String, baseProps: HashMap<String, Any>) {
        val style = HashMap<String, Any>(baseProps) // Clone the base properties
        style[mxConstants.STYLE_STROKECOLOR] = color
        style[mxConstants.STYLE_FONTCOLOR] = color
        stylesheet.putCellStyle(styleName, style)
    }
    
    /**
     * Clear all action dots
     */
    private fun clearDragDots() {
        if (dotCells.isNotEmpty()) {
            graph.removeCells(dotCells.toTypedArray())
            dotCells.clear()
        }
    }
    
    /**
     * Create a node for user utterance
     */
    private fun createUtteranceNode(text: String): mxCell {
        // Create HTML content for the cell
        val htmlContent = NodeEditDialog.createQueryNodeHtml(text)
        
        // Insert vertex with HTML content
        val cell = graph.insertVertex(
            parent, null, htmlContent, 
            50.0, 50.0, // x, y position
            NODE_WIDTH, NODE_HEIGHT, // width, height
            "defaultVertex" // style with label positioning
        ) as mxCell
        
        return cell
    }
    
    /**
     * Add a new node to the graph with add button connected to it
     */
    fun addNewNode(text: String, x: Double, y: Double): mxCell {
        graph.model.beginUpdate()
        try {
            // Create the new node
            val cell = graph.insertVertex(
                parent, null, NodeEditDialog.createStepNodeHtml(text, text),
                x, y, NODE_WIDTH, NODE_HEIGHT, "stepNode"
            ) as mxCell
            
            // No longer add "+" buttons to subsequent nodes
            // AddButtonConnector.createAddButtonForNode(graph, parent, cell)
            
            return cell
        } finally {
            graph.model.endUpdate()
        }
    }
    
    override fun paintComponent(g: Graphics) {
        // Ensure we're not painting over the grid
        if (isOpaque) {
            isOpaque = false
        }
        
        super.paintComponent(g)
        // Don't paint our own background to ensure transparency
    }
    
    // Force the panel to take up all available space
    override fun getPreferredSize(): Dimension {
        val parentComponent = parent as? Component
        val parentSize = parentComponent?.getSize() ?: super.getPreferredSize()
        return Dimension(parentSize.width, parentSize.height)
    }
}
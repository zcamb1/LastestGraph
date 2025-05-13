package com.samsung.iug.ui.graph

import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder
import com.mxgraph.model.mxCell
import com.mxgraph.model.mxGeometry
import com.mxgraph.view.mxGraph
import com.samsung.iug.ui.graph.NodeEditDialog

/**
 * UI for adding nodes to the graph
 * Allows entering previous and next node step IDs
 */
class SimpleAddNodeUI : JPanel() {
    private val stepIdField = JTextField(15)
    private val guideContentField = JTextArea(4, 15)
    private val previousNodeField = JTextField(15)
    private val nextNodeField = JTextField(15)
    private val cancelButton = JButton("Cancel")
    private val addButton = JButton("Add Node")

    // Reference to the clicked add button cell
    private var sourceButtonCell: mxCell? = null

    // Constants for node spacing
    private val VERTICAL_SPACING = 50.0 // Increased vertical spacing between nodes

    init {
        layout = BorderLayout(10, 10)
        border = EmptyBorder(20, 20, 20, 20)
        background = Color(30, 30, 30) // Dark background

        // Create title
        val titleLabel = JLabel("Add New Node")
        titleLabel.foreground = Color.WHITE
        titleLabel.font = Font("Arial", Font.BOLD, 16)
        titleLabel.horizontalAlignment = SwingConstants.CENTER

        // Create form panel
        val formPanel = JPanel(GridBagLayout())
        formPanel.background = Color(30, 30, 30) // Dark background
        val gbc = GridBagConstraints().apply {
            insets = Insets(5, 5, 5, 5)
            fill = GridBagConstraints.HORIZONTAL
        }

        // Step ID field
        val stepIdLabel = JLabel("Step ID (Node Name):")
        stepIdLabel.foreground = Color.WHITE
        gbc.gridx = 0
        gbc.gridy = 0
        formPanel.add(stepIdLabel, gbc)

        stepIdField.apply {
            font = Font("Arial", Font.PLAIN, 14)
            text = "Step Name" // Default value
        }
        gbc.gridx = 0
        gbc.gridy = 1
        formPanel.add(stepIdField, gbc)

        // Guide content field
        val guideContentLabel = JLabel("Guide Content:")
        guideContentLabel.foreground = Color.WHITE
        gbc.gridx = 0
        gbc.gridy = 2
        formPanel.add(guideContentLabel, gbc)

        guideContentField.apply {
            font = Font("Arial", Font.PLAIN, 14)
            text = "What does the user ask?" // Default value
            lineWrap = true
            wrapStyleWord = true
        }
        val scrollPane = JScrollPane(guideContentField)
        gbc.gridx = 0
        gbc.gridy = 3
        formPanel.add(scrollPane, gbc)

        // Previous node selection - now a text field
        val previousNodeLabel = JLabel("Previous Step ID:")
        previousNodeLabel.foreground = Color.WHITE
        gbc.gridx = 0
        gbc.gridy = 4
        formPanel.add(previousNodeLabel, gbc)

        previousNodeField.apply {
            font = Font("Arial", Font.PLAIN, 14)
            text = "User Query" // Default previous node
        }
        gbc.gridx = 0
        gbc.gridy = 5
        formPanel.add(previousNodeField, gbc)

        // Next node selection - now a text field (optional)
        val nextNodeLabel = JLabel("Next Step ID: (Optional)")
        nextNodeLabel.foreground = Color.WHITE
        gbc.gridx = 0
        gbc.gridy = 6
        formPanel.add(nextNodeLabel, gbc)

        nextNodeField.apply {
            font = Font("Arial", Font.PLAIN, 14)
            text = "" // Empty by default
        }
        gbc.gridx = 0
        gbc.gridy = 7
        formPanel.add(nextNodeField, gbc)

        // Buttons panel
        val buttonsPanel = JPanel(FlowLayout(FlowLayout.CENTER, 10, 0))
        buttonsPanel.background = Color(30, 30, 30) // Dark background

        cancelButton.apply {
            font = Font("Arial", Font.PLAIN, 14)
            preferredSize = Dimension(120, 30)
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addActionListener { closeUI() }
        }

        addButton.apply {
            font = Font("Arial", Font.BOLD, 14)
            preferredSize = Dimension(120, 30)
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addActionListener { addNewNode() }
        }

        buttonsPanel.add(cancelButton)
        buttonsPanel.add(addButton)

        // Add components to main panel
        add(titleLabel, BorderLayout.NORTH)
        add(formPanel, BorderLayout.CENTER)
        add(buttonsPanel, BorderLayout.SOUTH)

        // Set preferred size
        preferredSize = Dimension(350, 400)
    }

    /**
     * Set the add button cell that was clicked
     *
     * @param cell The add button cell that was clicked
     */
    fun setSourceButtonCell(cell: mxCell) {
        sourceButtonCell = cell

        // When source button is set, get the corresponding node's ID
        val graph = GraphPanel.getGraph()
        val sourceNode = AddButtonConnector.findParentNodeForAddButton(graph, cell)

        if (sourceNode != null) {
            val stepId = extractStepIdFromCell(sourceNode.value?.toString() ?: "")
            previousNodeField.text = stepId
        }
    }

    /**
     * Check if a source button cell has been set
     */
    fun hasSourceButtonCell(): Boolean {
        return sourceButtonCell != null
    }

    /**
     * Close the UI
     */
    private fun closeUI() {
        GraphUI.closeNode()
    }

    /**
     * Add a new node with the entered data
     */
    private fun addNewNode() {
        // Get values from fields
        val stepId = stepIdField.text.trim()
        val guideContent = guideContentField.text.trim()
        val previousNodeId = previousNodeField.text.trim()
        val nextNodeId = nextNodeField.text.trim()

        // Validate inputs
        if (stepId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Step ID", "Validation Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        if (guideContent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Guide Content", "Validation Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        // Validate previous node is specified
        if (previousNodeId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please specify a Previous Step ID", "Validation Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        val graph = GraphPanel.getGraph()

        // Find the actual cell objects
        val previousCell = findCellByStepId(graph, previousNodeId)
        val nextCell = if (nextNodeId.isNotEmpty()) findCellByStepId(graph, nextNodeId) else null

        if (previousCell == null) {
            JOptionPane.showMessageDialog(this, "Previous node with ID '$previousNodeId' not found", "Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        if (nextNodeId.isNotEmpty() && nextCell == null) {
            JOptionPane.showMessageDialog(this, "Next node with ID '$nextNodeId' not found", "Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        // Check if we still have the source button reference (legacy mode)
        if (sourceButtonCell != null) {
            addNodeLegacyMode(stepId, guideContent)
            return
        }

        // New mode: based on previous/next selection
        graph.model.beginUpdate()
        try {
            // Case 1: Only previous node is selected
            if (nextCell == null) {
                // Find all outgoing edges from previous node
                val outgoingEdges = graph.getOutgoingEdges(previousCell)
                val childCells = outgoingEdges.map { (it as mxCell).target }.filterIsInstance<mxCell>()

                // Calculate position based on children
                val nodeWidth = GraphPanel.NODE_WIDTH
                val nodeHeight = GraphPanel.NODE_HEIGHT
                val prevGeom = graph.getCellGeometry(previousCell)

                // Default position - to the right of previous node
                var newX = prevGeom.x + prevGeom.width + AddButtonConnector.HORIZONTAL_SPACING / 2
                var newY = prevGeom.y

                // If the previous node already has children, adjust X position
                if (childCells.isNotEmpty()) {
                    // Use the same X position as the first child
                    val firstChildGeom = graph.getCellGeometry(childCells.first())
                    newX = firstChildGeom.x

                    // Find the bottom-most child to place new node below it with spacing
                    val bottomMostChild = childCells.maxByOrNull {
                        graph.getCellGeometry(it).y + graph.getCellGeometry(it).height
                    }

                    if (bottomMostChild != null) {
                        val bottomChildGeom = graph.getCellGeometry(bottomMostChild)
                        newY = bottomChildGeom.y + bottomChildGeom.height + VERTICAL_SPACING
                    }
                }

                // Get all nodes in the graph to check for potential overlaps
                val allCells = graph.getChildCells(graph.defaultParent)
                    .filterIsInstance<mxCell>()
                    .filter { !it.isEdge && !AddButtonConnector.isAddButton(graph, it) }

                // Define the potential bounding box for the new node
                val potentialBounds = Rectangle(
                    newX.toInt(), newY.toInt(),
                    nodeWidth.toInt(), nodeHeight.toInt()
                )

                // Check for overlaps with any existing node
                var hasOverlaps = true

                while (hasOverlaps) {
                    hasOverlaps = false

                    for (cell in allCells) {
                        val cellGeom = graph.getCellGeometry(cell)
                        val cellBounds = Rectangle(
                            cellGeom.x.toInt(), cellGeom.y.toInt(),
                            cellGeom.width.toInt(), cellGeom.height.toInt()
                        )

                        // Check if the cells would overlap
                        if (potentialBounds.intersects(cellBounds)) {
                            // If we would overlap, move our node down below the current cell
                            hasOverlaps = true
                            potentialBounds.y = (cellGeom.y + cellGeom.height + VERTICAL_SPACING).toInt()
                            newY = potentialBounds.y.toDouble()
                            break
                        }
                    }
                }

                // Create the new node at the non-overlapping position
                val newNode = graph.insertVertex(
                    graph.defaultParent, null,
                    NodeEditDialog.createStepNodeHtml(guideContent, stepId),
                    newX, newY, nodeWidth, nodeHeight, "stepNode"
                ) as mxCell

                // Connect from previous to new node
                graph.insertEdge(
                    graph.defaultParent, null, "",
                    previousCell, newNode, "stepEdge"
                )
            }
            // Case 2: Both previous and next nodes selected
            else {
                // Find the edge between previous and next, if it exists
                val edgesToRemove = findEdgesBetween(graph, previousCell, nextCell)

                // Get geometry of both cells
                val prevGeom = graph.getCellGeometry(previousCell)
                val nextGeom = graph.getCellGeometry(nextCell)

                // Get node dimensions
                val nodeWidth = GraphPanel.NODE_WIDTH
                val nodeHeight = GraphPanel.NODE_HEIGHT

                // Step 1: Place the new node at the position of the next node
                val newNode = graph.insertVertex(
                    graph.defaultParent, null,
                    NodeEditDialog.createStepNodeHtml(guideContent, stepId),
                    nextGeom.x, nextGeom.y, nodeWidth, nodeHeight, "stepNode"
                ) as mxCell

                // Step 2: Move the next node to the right
                // Create a translated geometry for the next node
                val translatedGeom = nextGeom.clone() as mxGeometry
                translatedGeom.x = translatedGeom.x + nodeWidth + AddButtonConnector.HORIZONTAL_SPACING / 2

                // Update the geometry of the next node
                graph.model.setGeometry(nextCell, translatedGeom)

                // Remove direct edge between previous and next
                if (edgesToRemove.isNotEmpty()) {
                    graph.removeCells(edgesToRemove.toTypedArray())
                }

                // Connect previous -> new -> next
                graph.insertEdge(
                    graph.defaultParent, null, "",
                    previousCell, newNode, "stepEdge"
                )

                graph.insertEdge(
                    graph.defaultParent, null, "",
                    newNode, nextCell, "stepEdge"
                )

                // Refresh the graph to update positions
                graph.refresh()
            }
        } finally {
            graph.model.endUpdate()
        }

        // Close the UI
        closeUI()
    }

    /**
     * Legacy mode for adding nodes - used when clicking on a "+" button
     */
    private fun addNodeLegacyMode(stepId: String, guideContent: String) {
        val graph = GraphPanel.getGraph()
        val sourceNode = AddButtonConnector.findParentNodeForAddButton(graph, sourceButtonCell!!)

        if (sourceNode != null) {
            // Get the geometry of the button that was clicked
            val buttonGeometry = graph.getCellGeometry(sourceButtonCell)
            val sourceNodeGeometry = graph.getCellGeometry(sourceNode)

            if (buttonGeometry != null && sourceNodeGeometry != null) {
                // Begin update to batch all graph changes
                graph.model.beginUpdate()
                try {
                    // First, remove all edges connected to the add button
                    val edges = graph.getEdges(sourceButtonCell)
                    if (edges != null && edges.isNotEmpty()) {
                        graph.removeCells(edges)
                    }

                    // Then remove the add button itself
                    graph.removeCells(arrayOf(sourceButtonCell))

                    // Get node dimensions
                    val nodeWidth = GraphPanel.NODE_WIDTH
                    val nodeHeight = GraphPanel.NODE_HEIGHT

                    // Calculate better position for the new node to avoid overlap
                    // Place it to the right of the source node with proper spacing
                    val newNodeX = sourceNodeGeometry.x + nodeWidth + AddButtonConnector.HORIZONTAL_SPACING / 2

                    // Keep it at the same Y level as the source node
                    val newNodeY = sourceNodeGeometry.y

                    // Create a new node at the calculated position
                    val newNode = graph.insertVertex(
                        graph.defaultParent, null, NodeEditDialog.createStepNodeHtml(guideContent, stepId),
                        newNodeX, newNodeY, nodeWidth, nodeHeight, "stepNode"
                    ) as mxCell

                    // Create a connection (edge) between the source node and the new node
                    graph.insertEdge(
                        graph.defaultParent, null, "",
                        sourceNode, newNode,
                        "stepEdge"
                    )

                    // Refresh the graph
                    graph.refresh()
                } finally {
                    graph.model.endUpdate()
                }
            } else {
                // Fallback if geometry can't be determined
                GraphPanel.addNewNode(stepId, 100.0, 100.0)
            }
        } else {
            // Fallback if source node not found
            GraphPanel.addNewNode(stepId, 100.0, 100.0)
        }

        // Clear the source button reference
        sourceButtonCell = null

        // Close the UI
        closeUI()
    }

    /**
     * Helper methods
     */

    /**
     * Find a cell by its step ID
     */
    private fun findCellByStepId(graph: mxGraph, stepId: String): mxCell? {
        // Special handling for User Query
        if (stepId.equals("User Query", ignoreCase = true)) {
            val parent = graph.defaultParent
            val childVertices = graph.getChildVertices(parent)

            // Look for the first node containing "User Query" text
            return childVertices
                .filterIsInstance<mxCell>()
                .find { cell ->
                    val cellValue = cell.value?.toString() ?: ""
                    cellValue.contains("User Query")
                }
        }

        // Standard search for other nodes
        val parent = graph.defaultParent
        val childVertices = graph.getChildVertices(parent)

        return childVertices
            .filterIsInstance<mxCell>()
            .find { cell ->
                val cellValue = cell.value?.toString() ?: ""
                cellValue.contains(stepId)
            }
    }

    /**
     * Find edges between two cells
     */
    private fun findEdgesBetween(graph: mxGraph, source: mxCell, target: mxCell): List<mxCell> {
        val edges = graph.getEdges(source)
        return edges
            .filterIsInstance<mxCell>()
            .filter { it.target == target }
    }

    /**
     * Extract step ID from HTML cell content
     */
    private fun extractStepIdFromCell(htmlContent: String): String {
        // Check if this is the User Query node
        if (htmlContent.contains("User Query")) {
            return "User Query"
        }

        // For regular nodes, extract Step ID from the HTML content
        val textPattern = "<tr><td[^>]*>(.*?)</td></tr>\\s*<tr><td[^>]*>(.*?)</td></tr>".toRegex(RegexOption.DOT_MATCHES_ALL)
        val match = textPattern.find(htmlContent)

        // The second captured group should be the Step ID
        return match?.groupValues?.getOrNull(2)?.trim() ?: ""
    }
} 
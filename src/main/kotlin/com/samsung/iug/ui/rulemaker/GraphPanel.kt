package com.samsung.iug.ui.rulemaker

import com.samsung.iug.model.Rule
import com.samsung.iug.model.Step
import com.intellij.openapi.diagnostic.Logger
import com.intellij.ui.JBColor
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout
import com.mxgraph.model.mxCell
import com.mxgraph.model.mxGeometry
import com.mxgraph.swing.mxGraphComponent
import com.mxgraph.util.mxConstants
import com.mxgraph.view.mxGraph
import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

/**
 * Panel for displaying and interacting with the rule steps graph.
 */
class GraphPanel(
    private val onStepSelected: (Step) -> Unit,
    private val onAddStep: (Step?, mxCell?, mxGeometry?) -> Unit,
    private val onAddSubStep: (Step) -> Unit,
    private val onRemoveStep: (Step) -> Boolean,
    private val onSwapNode: (Step, String) -> Unit
) : JPanel(BorderLayout()) {

    private val LOG = Logger.getInstance(GraphPanel::class.java)

    private val graph = mxGraph()
    private val graphComponent = mxGraphComponent(graph)

    private var currentRule: Rule? = null
    private val cellToStepMap = mutableMapOf<Any, Step>()
    private val stepToCellMap = mutableMapOf<String, Any>()

    // Biến lưu zoom level hiện tại
    private var userZoomLevel: Double = 0.9  // Mặc định là 0.9 - scale nhỏ hơn một chút

    // Constants for styling
    private val MAIN_STEP_STYLE = "mainStep"
    private val SUB_STEP_STYLE = "subStep"
    private val EDGE_STYLE = "edge"
    private val ACTIVE_STEP_STYLE = "activeStep"
    private val START_STEP_STYLE = "startStep"
    private val END_STEP_STYLE = "endStep"

    // Default edge color - can be overridden when rule is loaded
    private var defaultEdgeColor = "#b1b1b1" // Default dark gray color

    // Store main flow path for layout
    private var mainFlowPath = listOf<String>()

    private val layoutManager = GraphLayoutManager(graph, graphComponent, stepToCellMap, cellToStepMap) { LOG.info(it) }

    // Manages edge (connection) creation and routing between nodes in the graph
    private val edgeManager = GraphEdgeManager(graph, graphComponent, stepToCellMap, cellToStepMap) { LOG.info(it) }

    //Manager right-click context menu for nodes and empty space
    private val contextMenuManager = ContextMenuManager(
        graph,
        graphComponent,
        stepToCellMap,
        cellToStepMap,
        onStepSelected = { step -> onStepSelected(step) },
        onAddSubStep = { step -> onAddSubStep(step) },
        onAddStep = { step, cell, geo -> onAddStep(step, cell, geo) },
        onRemoveStep = { step -> onRemoveStep(step) },
        onSwapNode = { step, targetId -> onSwapNode(step, targetId) },
        applyLayout = { applyLayout() }
    )

    // Manages getting and setting zoom levels for the graph component
    private val zoomManager = ZoomManager(graphComponent, LOG)

    // Manages viewport translation to ensure all nodes are visible
    private val viewportManager = ViewportManager(graph, graphComponent, stepToCellMap, LOG)

    // Manages creating and styling step nodes (cells) in the graph
    private val stepCellFactory = StepCellFactory(graph, stepToCellMap, cellToStepMap) { currentRule }

    init {
        // Configure graph settings
        graph.isAllowDanglingEdges = false
        graph.isAllowLoops = true
        graph.isCellsEditable = false
        graph.isCellsResizable = false
        graph.isCellsMovable = true
        graph.isDisconnectOnMove = false

        // Set default edge style to orthogonal
        graph.stylesheet.defaultEdgeStyle[mxConstants.STYLE_EDGE] = mxConstants.EDGESTYLE_ORTHOGONAL
        graph.stylesheet.defaultEdgeStyle[mxConstants.STYLE_ROUNDED] = true
        graph.stylesheet.defaultEdgeStyle[mxConstants.STYLE_ARCSIZE] = 15

        // Configure graph component
        graphComponent.connectionHandler.isEnabled = false
        graphComponent.setToolTips(true)


        // Set anti-aliasing for better rendering
        graphComponent.setAntiAlias(true)
        graphComponent.setTextAntiAlias(true)
        graphComponent.verticalScrollBar.unitIncrement = 7
        graphComponent.horizontalScrollBar.unitIncrement = 7
        // Set background color
        graphComponent.setBackground(JBColor(Color(250, 250, 250), Color(60, 63, 65)))

        // Increase grid size for more spacing
        graph.gridSize = 50

        // Configure viewport to show negative coordinates
        graphComponent.viewport.setViewPosition(java.awt.Point(0, 200))

        GraphStyles.setupStylesheet(graph, defaultEdgeColor)

        // Create panel for controls
        val controlPanel = JPanel(FlowLayout(FlowLayout.LEFT))


        // Add components to panel
        add(controlPanel, BorderLayout.NORTH)
        add(graphComponent, BorderLayout.CENTER)

        // Add listener to update coordinates when cells are moved
        graph.addListener("cellsMoved") { sender, evt ->
            // updateCoordinateDisplay() call removed
        }

        // Add mouse listener for selecting cells
        graphComponent.graphControl.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    val cell = graphComponent.getCellAt(e.x, e.y)
                    if (cell != null && cell is mxCell && !cell.isEdge) {
                        val step = cellToStepMap[cell]
                        if (step != null) {
                            onStepSelected(step)

                            // Remove coordinate display dialog when clicking on a node
                        }
                    }
                }
            }

            override fun mouseClicked(e: MouseEvent) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    val cell = graphComponent.getCellAt(e.x, e.y)
                    if (cell != null && cell is mxCell && !cell.isEdge) {
                        val step = cellToStepMap[cell]
                        if (step != null) {
                            contextMenuManager.showContextMenu(e.x, e.y, step)
                        }
                    } else {
                        // Show context menu for empty space
                        contextMenuManager.showEmptySpaceContextMenu(e.x, e.y)
                    }
                }
            }
        })
    }


    /**
     * Display a rule in the graph panel.
     */
    fun displayRule(rule: Rule) {
        // Lưu ý: Tất cả việc điều chỉnh zoom KHÔNG được thực hiện trực tiếp ở đây
        // mà được tập trung ở phương thức applyLayout() để tránh việc reset zoom nhiều lần

        // Lưu lại zoom level hiện tại trước khi load file mới
        val currentZoom = zoomManager.getCurrentZoom()

        currentRule = rule
        cellToStepMap.clear()
        stepToCellMap.clear()

        // Check if rule has custom edge color defined
        if (rule.edgeColor != null && rule.edgeColor.isNotEmpty()) {
            defaultEdgeColor = rule.edgeColor
            LOG.info("Using edge color from rule: $defaultEdgeColor")
            // Re-setup stylesheet with new color
            GraphStyles.setupStylesheet(graph, defaultEdgeColor)
        }

        // Create the graph in a single transaction with improved positioning
        graph.model.beginUpdate()
        try {
            graph.removeCells(graph.getChildCells(graph.defaultParent))

            // First create all nodes with appropriate styles
            for (step in rule.steps) {
                stepCellFactory.createStepCell(step)
            }

            // Create all edges
            createAllEdges(rule)

        } finally {
            graph.model.endUpdate()
        }

        // Apply layout in a separate transaction
        graph.model.beginUpdate()
        try {
            // Apply our custom layout logic
            layoutManager.applyCustomMainFlowLayout(rule)


            // Apply special treatments for edges to avoid crossing nodes
            edgeManager.createSpecialEdges(currentRule!!)

            // Translate graph and ensure all nodes are visible
            viewportManager.ensureNodesVisible()

        } finally {
            graph.model.endUpdate()
        }

        // Check if edges were created
        val edgeCount = countEdges()
        LOG.info("After layout, graph has ${rule.steps.size} nodes and $edgeCount edges")

        // If no edges, try a direct approach
        if (edgeCount == 0 && hasExpectedConnections(rule)) {
            JOptionPane.showMessageDialog(
                this,
                "Warning: Could not create connections automatically. " +
                        "Connections exist in the data but are not visible in the graph.",
                "Connection Issue",
                JOptionPane.WARNING_MESSAGE
            )
        }

        // Khôi phục zoom level sau khi đã load xong file
        zoomManager.setZoomLevel(currentZoom)
    }

    /**
     * Manually create ALL edges in the rule at once with appropriate styling
     * based on the relationship between nodes.
     */
    private fun createAllEdges(rule: Rule) {
        var edgesCreated = 0

        // Clear any existing edges first to prevent duplicates
        val existingEdges = graph.getChildEdges(graph.defaultParent)
        if (existingEdges != null && existingEdges.isNotEmpty()) {
            graph.removeCells(existingEdges)
        }

        // Find all bidirectional pairs (nodes that connect to each other)
        val bidirectionalPairs = edgeManager.findAllBidirectionalPairs(currentRule!!)
        LOG.info("Found ${bidirectionalPairs.size} bidirectional pairs: ${bidirectionalPairs.joinToString()}")

        // Identify start nodes for special edge styling
        val startNodes = rule.steps.filter { step -> stepCellFactory.isStartStep(step) }.map { it.id }
        LOG.info("Found start nodes: ${startNodes.joinToString()}")

        // Create all edges based on nextStepIds
        for (step in rule.steps) {
            val sourceCell = stepToCellMap[step.id]
            if (sourceCell == null) {
                LOG.error("Source cell not found for step ${step.id}")
                continue
            }

            // Check if source is a main step (not a sub-step)
            val isMainStep = !step.isSubStep
            val isSourceStartNode = startNodes.contains(step.id)

            for (nextStepId in step.nextStepIds) {
                val targetCell = stepToCellMap[nextStepId]
                if (targetCell == null) {
                    LOG.error("Target cell not found for step $nextStepId")
                    continue
                }

                // Get target step info
                val targetStep = rule.steps.find { it.id == nextStepId }
                if (targetStep == null) {
                    LOG.error("Target step not found for ID $nextStepId")
                    continue
                }

                // Check if target is a main step
                val isTargetMainStep = !targetStep.isSubStep

                // Use consistent EDGE_STYLE for all connections
                // to follow the rule: "Avoid making the connectors too colorful"
                val edgeStyle = EDGE_STYLE

                // Create the edge with appropriate style
                edgesCreated++

                // Log connection information - but don't change color
                if (isSourceStartNode) {
                    LOG.info("Created edge from START node: ${step.id} → $nextStepId")
                } else if (isMainStep && isTargetMainStep) {
                    LOG.info("Created main-to-main edge: ${step.id} → $nextStepId")
                } else {
                    LOG.info("Created edge: ${step.id} → $nextStepId")
                }
            }
        }

        LOG.info("Created $edgesCreated edges based on rule data")
    }



    fun setCellGeometry(cell: mxCell?, geo: mxGeometry) {
        graph.model.setGeometry(cell, geo)
    }

    /**
     * Get the cell object for a given step ID
     */
    fun getCellForStep(stepId: String): mxCell? {
        return stepToCellMap[stepId] as? mxCell
    }

    /**
     * Get the geometry for a given cell
     */
    fun getCellGeometry(cell: mxCell?): mxGeometry? {
        return cell?.let { graph.getCellGeometry(it) }
    }


    /**
     * Refresh the graph display based on the current rule.
     */
    fun refreshGraph() {
        currentRule?.let { displayRule(it) }

        // Extra check to ensure all nodes are visible after refresh
        viewportManager.ensureNodesVisible()
    }


    /**
     * Count the number of edges in the graph
     */
    private fun countEdges(): Int {
        val edges = graph.getChildEdges(graph.defaultParent)
        return edges?.size ?: 0
    }

    /**
     * Check if the rule has any expected connections
     */
    private fun hasExpectedConnections(rule: Rule): Boolean {
        return rule.steps.any { it.nextStepIds.isNotEmpty() }
    }

    /**
     * Public method to apply layout when needed.
     */
    fun applyLayout() {
        graph.model.beginUpdate()
        try {
            // Get the current rule and apply the layout
            currentRule?.let {
                layoutManager.applyCustomMainFlowLayout(it)
            } ?: {
                // Apply a simple layout if no rule is available
                val layout = mxHierarchicalLayout(graph, SwingConstants.WEST)
                layout.execute(graph.defaultParent)
            }()

            // Center the graph in the view
            graphComponent.zoomAndCenter()

            // Khôi phục zoom level hiện tại của người dùng thay vì reset
            zoomManager.setZoomLevel(userZoomLevel)

        } finally {
            graph.model.endUpdate()
        }
    }


    /**
     * Reconstruct the path from start to end using the parent map.
     */
    private fun reconstructPath(parentMap: Map<String, String>, startNodeId: String, endNodeId: String): List<String> {
        val path = mutableListOf<String>()
        var currentId = endNodeId

        // Work backwards from end to start
        while (currentId != startNodeId) {
            path.add(0, currentId)
            currentId = parentMap[currentId] ?: break
        }

        // Add the start node
        path.add(0, startNodeId)

        return path
    }
}
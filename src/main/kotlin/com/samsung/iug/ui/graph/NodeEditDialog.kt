package com.samsung.iug.ui.graph

import com.mxgraph.model.mxCell
import com.mxgraph.view.mxGraph
import com.samsung.iug.graph.listNode
import java.awt.*
import javax.swing.*
import javax.swing.border.LineBorder

/**
 * Dialog for editing nodes in the graph
 */
object NodeEditDialog {

    /**
     * Show edit dialog for the given cell
     *
     * @param parent The parent component (GraphPanel)
     * @param cell The cell to edit
     * @param graph The mxGraph instance
     * @param updateCallback Optional callback after update
     */
    fun showEditDialog(parent: JPanel, cell: mxCell, graph: mxGraph, updateCallback: (() -> Unit)? = null) {
        // Get current content
        val content = cell.value.toString()

        // Determine if this is a step node or user query node
        val isStepNode = cell.style.contains("stepNode")

        // Extract node contents
        val (guideText, stepId) = extractNodeContents(cell)

        // Create edit dialog similar to EditUtteranceDialog
        val window = SwingUtilities.getWindowAncestor(parent)
        val dialogTitle = if (isStepNode) "Edit Step" else "Edit User Query"
        val dialog = JDialog(window as Frame, dialogTitle, true)
        dialog.layout = BorderLayout()
        dialog.isUndecorated = true
        dialog.background = Color(0, 0, 0, 0)

        // Create a rounded panel like in EditUtteranceDialog
        val panel = createRoundedPanel()
        panel.layout = GridBagLayout()

        // Create text area for guide content
        val textArea = JTextArea(guideText)
        textArea.foreground = Color.WHITE
        textArea.background = Color(25, 25, 25)
        textArea.caretColor = Color.WHITE
        textArea.font = Font("Arial", Font.PLAIN, 13)
        textArea.lineWrap = true
        textArea.wrapStyleWord = true
        textArea.margin = Insets(8, 10, 8, 10)

        val scrollPane = JScrollPane(textArea)
        scrollPane.border = BorderFactory.createLineBorder(Color(100, 100, 100), 1, true)
        scrollPane.preferredSize = Dimension(350, 100)

        // Create step ID field for step nodes
        val stepIdField = JTextField(stepId)
        stepIdField.foreground = Color.WHITE
        stepIdField.background = Color(25, 25, 25)
        stepIdField.caretColor = Color.WHITE
        stepIdField.font = Font("Arial", Font.PLAIN, 13)
        stepIdField.margin = Insets(8, 10, 8, 10)
        stepIdField.border = BorderFactory.createLineBorder(Color(100, 100, 100), 1, true)
        stepIdField.preferredSize = Dimension(350, 30)

        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER))
        buttonPanel.isOpaque = false

        val updateButton = createRoundedButton("Update")
        updateButton.addActionListener {
            // Update node content
            graph.model.beginUpdate()
            try {
                // Update cell value with HTML content based on node type
                if (isStepNode) {
                    graph.model.setValue(cell, createStepNodeHtml(textArea.text, stepIdField.text))
                } else {
                    graph.model.setValue(cell, createQueryNodeHtml(textArea.text))
                }

                // Update node data in listNode if needed
                if (listNode.listNode.isNotEmpty()) {
                    listNode.listNode[0].guildContent = textArea.text
                }

                // Call the update callback if provided
                updateCallback?.invoke()
            } finally {
                graph.model.endUpdate()
            }
            dialog.dispose()
        }

        val cancelButton = createRoundedButton("Cancel")
        cancelButton.addActionListener { dialog.dispose() }

        buttonPanel.add(updateButton)
        buttonPanel.add(cancelButton)

        val gbc = GridBagConstraints()
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.insets = Insets(10, 10, 5, 10)
        gbc.fill = GridBagConstraints.HORIZONTAL

        // Add header label based on node type
        val headerText = if (isStepNode) "EDIT STEP" else "EDIT USER QUERY"
        panel.add(JLabel(headerText).apply {
            foreground = Color.WHITE
            font = Font("Arial", Font.BOLD, 14)
        }, gbc)

        gbc.gridy = 1
        gbc.insets = Insets(5, 10, 5, 10)
        panel.add(JLabel("Content:").apply {
            foreground = Color.WHITE
            font = Font("Arial", Font.PLAIN, 12)
        }, gbc)

        gbc.gridy = 2
        panel.add(scrollPane, gbc)

        // Add step ID field only for step nodes
        if (isStepNode) {
            gbc.gridy = 3
            panel.add(JLabel("Step ID:").apply {
                foreground = Color.WHITE
                font = Font("Arial", Font.PLAIN, 12)
            }, gbc)

            gbc.gridy = 4
            panel.add(stepIdField, gbc)
        }

        gbc.gridy = if (isStepNode) 5 else 3
        gbc.insets = Insets(15, 10, 10, 10)
        panel.add(buttonPanel, gbc)

        dialog.contentPane = panel
        dialog.pack()

        // Force rounded shape on the dialog itself like in EditUtteranceDialog
        dialog.shape = java.awt.geom.RoundRectangle2D.Float(
            0f, 0f, dialog.width.toFloat(), dialog.height.toFloat(), 20f, 20f
        )

        // Position dialog so it doesn't cover the hover icons
        if (parent is GraphPanel) {
            val graphComponent = parent.getGraphComponent()
            val cellBounds = graph.getCellBounds(cell)

            if (cellBounds != null) {
                // Convert cell bounds to screen coordinates
                val point = Point(cellBounds.x.toInt(), cellBounds.y.toInt() + cellBounds.height.toInt())
                SwingUtilities.convertPointToScreen(point, graphComponent.graphControl)

                // Set dialog position with increased vertical offset (50px) to avoid covering hover icons
                dialog.setLocation(point.x + 10, point.y + 50)
            } else {
                dialog.setLocationRelativeTo(parent)
            }
        } else {
            dialog.setLocationRelativeTo(parent)
        }

        dialog.isVisible = true
    }

    /**
     * Create a rounded panel like in EditUtteranceDialog
     */
    private fun createRoundedPanel(): JPanel {
        return object : JPanel() {
            init {
                isOpaque = false
                border = BorderFactory.createLineBorder(Color(60, 60, 60), 1, true)
            }

            override fun paintComponent(g: Graphics) {
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                g2.color = Color(36, 36, 36)
                g2.fillRoundRect(0, 0, width, height, 20, 20)
                super.paintComponent(g)
            }
        }
    }

    /**
     * Create a rounded button like in EditUtteranceDialog
     */
    private fun createRoundedButton(text: String): JButton {
        return object : JButton(text) {
            init {
                isFocusPainted = false
                isContentAreaFilled = false
                foreground = Color.WHITE
                background = Color(60, 60, 60)
                font = Font("Arial", Font.PLAIN, 13)
                border = BorderFactory.createLineBorder(Color(150, 150, 150), 1, true)
                preferredSize = Dimension(90, 30)
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            }

            override fun paintComponent(g: Graphics) {
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                g2.color = background
                g2.fillRoundRect(0, 0, width, height, 16, 16)
                g2.color = foreground
                val fm = g.fontMetrics
                val textWidth = fm.stringWidth(text)
                val textY = (height + fm.ascent - fm.descent) / 2
                g2.drawString(text, (width - textWidth) / 2, textY)
            }
        }
    }

    /**
     * Extract both guide text and step ID from a cell
     */
    private fun extractNodeContents(cell: mxCell): Pair<String, String> {
        val value = cell.value?.toString() ?: ""

        // Default values
        var guideText = ""
        var stepId = ""

        // Check if it's HTML content
        if (value.contains("<")) {
            val textPattern = "<tr><td>(.*?)</td></tr>\\s*<tr><td[^>]*>(.*?)</td></tr>".toRegex(RegexOption.DOT_MATCHES_ALL)
            val match = textPattern.find(value)

            if (match != null && match.groupValues.size > 2) {
                guideText = match.groupValues[1].unescapeHtml()
                stepId = match.groupValues[2].unescapeHtml()
            } else {
                // Fallback to old pattern
                val oldPattern = "<tr><td>(.*?)</td></tr>".toRegex(RegexOption.DOT_MATCHES_ALL)
                val oldMatch = oldPattern.find(value)
                if (oldMatch != null) {
                    guideText = oldMatch.groupValues[1].unescapeHtml()
                }
            }
        } else {
            // It's already plain text
            guideText = value
        }

        return Pair(guideText, stepId)
    }

    /**
     * Create HTML content for a user query node
     */
    fun createQueryNodeHtml(text: String): String {
        return """
          <table style="width:250px; color:#fff; font-family:Arial; font-size:11px;margin-top:25px;padding-left:8px">
                <tr><td>${text.escapeHtml()}</td></tr> 
                <tr><td style="font-weight:bold; opacity:0.7;">User Query</td></tr>
            </table>
            """.trimIndent()
    }

    /**
     * Create HTML content for a step node
     */
    fun createStepNodeHtml(guideText: String, stepId: String): String {
        return """
          <table style="width:250px; color:#fff; font-family:Arial; font-size:11px;margin-top:25px;padding-left:8px">
                <tr><td>${guideText.escapeHtml()}</td></tr> 
                <tr><td style="font-weight:bold; opacity:0.7;">${stepId.escapeHtml()}</td></tr>
            </table>
            """.trimIndent()
    }

    /**
     * Create HTML content for a node with custom subtitle
     */
    fun createNodeHtml(text: String, subtitle: String): String {
        return """
          <table style="width:250px; color:#fff; font-family:Arial; font-size:9px;margin-top:30px;">
                  <tr><td>${text.escapeHtml()}</td></tr>
                  <tr><td style="font-weight:bold; opacity:0.7;">${subtitle.escapeHtml()}</td></tr>
                </table>
            """.trimIndent()
    }

    /**
     * Extension function to escape HTML special characters
     */
    private fun String.escapeHtml(): String {
        return this.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }

    /**
     * Extension function to unescape HTML special characters
     */
    private fun String.unescapeHtml(): String {
        return this.replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
    }
} 
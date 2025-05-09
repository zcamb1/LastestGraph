package com.samsung.iug.ui.graph

import com.samsung.iug.graph.listNode
import java.awt.*
import javax.swing.*
import javax.swing.border.LineBorder

class EditUtteranceDialog(
    private val buttonArea: Component, // ButtonPanel hoặc NodeWithConnector
    private val node: UtteranceNodeView
) : JDialog(
    SwingUtilities.getWindowAncestor(buttonArea),
    "Edit Node",
    ModalityType.APPLICATION_MODAL
) {

    private val inputArea = JTextArea(node.titleText)

    init {
        isUndecorated = true
        background = Color(0, 0, 0, 0) // transparent background to support round panel

        val content = RoundedPanel(20, Color(36, 36, 36))
        content.layout = GridBagLayout()
        content.border = LineBorder(Color(60, 60, 60), 1, true)

        val gbc = GridBagConstraints()

        // Title
        val titleLabel = JLabel("EDIT USER QUERY")
        titleLabel.foreground = Color.WHITE
        titleLabel.font = Font("SansSerif", Font.BOLD, 14)
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridwidth = 2
        gbc.insets = Insets(10, 10, 10, 10)
        content.add(titleLabel, gbc)

        // Input Area
        inputArea.foreground = Color.WHITE
        inputArea.background = Color(25, 25, 25)
        inputArea.border = LineBorder(Color(100, 100, 100), 1, true)
        inputArea.caretColor = Color.WHITE
        inputArea.font = Font("SansSerif", Font.PLAIN, 13)
        inputArea.lineWrap = true
        inputArea.wrapStyleWord = true
        inputArea.margin = Insets(8, 10, 8, 10)
        inputArea.isOpaque = true

        val scrollPane = JScrollPane(inputArea)
        scrollPane.border = LineBorder(Color(100, 100, 100), 1, true)
        scrollPane.preferredSize = Dimension(240, 70)
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED

        gbc.gridy = 1
        gbc.fill = GridBagConstraints.HORIZONTAL
        content.add(scrollPane, gbc)

        // Buttons
        val buttonPanel = JPanel()
        buttonPanel.layout = FlowLayout(FlowLayout.CENTER, 10, 10)
        buttonPanel.isOpaque = false

        val updateBtn = RoundedButton("Update")
        val closeBtn = RoundedButton("Close")

        updateBtn.addActionListener {
            node.titleText = inputArea.text
            listNode.listNode[0].guildContent = inputArea.text
            node.repaint()
            dispose()
        }

        closeBtn.addActionListener { dispose() }

        buttonPanel.add(updateBtn)
        buttonPanel.add(closeBtn)

        gbc.gridy = 2
        gbc.gridwidth = 2
        gbc.insets = Insets(10, 10, 10, 10)
        content.add(buttonPanel, gbc)

        setContentPane(content)
        pack()

        // Force rounded shape on the dialog itself
        shape = java.awt.geom.RoundRectangle2D.Float(
            0f, 0f, width.toFloat(), height.toFloat(), 20f, 20f
        )

        // Position below button area
        val screenLoc = buttonArea.locationOnScreen
        setLocation(screenLoc.x, screenLoc.y + buttonArea.height + 4)
    }

    // Rounded Panel
    class RoundedPanel(private val radius: Int, private val fillColor: Color) : JPanel() {
        init {
            isOpaque = false
        }

        override fun paintComponent(g: Graphics) {
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.color = fillColor
            g2.fillRoundRect(0, 0, width, height, radius, radius)
        }
    }

    // Rounded Button
    class RoundedButton(text: String) : JButton(text) {
        init {
            isFocusPainted = false
            isContentAreaFilled = false
            foreground = Color.WHITE
            background = Color(60, 60, 60)
            font = Font("SansSerif", Font.PLAIN, 13)
            border = LineBorder(Color(150, 150, 150), 1, true)
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
package com.samsung.iug.ui.graph

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

/**
 * A single node representing a step or utterance in the graph.
 * Displays a purple border, icon, and title/description text.
 * Double-clicking opens an edit dialog.
 */


class UtteranceNodeView : JPanel() {

    // Editable text fields
    var titleText: String = "What does the user ask?"
    var subtitleText: String = "User Query"

    init {
        val rootNode = Node("User Query")
        listNode.listNode.add(rootNode)
        layout = null
        isOpaque = false
        preferredSize = Dimension(280, 110)
        setBounds(100, 200, 280, 110)

        //Handle double-click to open edit popup
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2) {
                    // Pass NodeWithConnector (grandparent) instead of just NodeView
                    EditUtteranceDialog(this@UtteranceNodeView.parent, this@UtteranceNodeView).isVisible = true
                }
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val w = width
        val h = height

        //Draw node border
        g2.color = Color(146, 119, 255)
        g2.stroke = BasicStroke(2f)
        g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16)

        //Draw circle icon top-left
        val iconX = 16
        val iconY = 12
        val iconSize = 20
        g2.color = Color(146, 119, 255)
        g2.fillOval(iconX, iconY, iconSize, iconSize)

        //Draw title and subtitle
        g2.color = Color.WHITE
        g2.font = Font("SansSerif", Font.PLAIN, 14)
        g2.drawString(titleText, iconX, iconY + iconSize + 18)

        g2.font = Font("SansSerif", Font.BOLD, 12)
        g2.drawString(subtitleText, iconX, iconY + iconSize + 36)
    }
}
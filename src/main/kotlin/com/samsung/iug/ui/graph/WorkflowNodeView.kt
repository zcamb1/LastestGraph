package com.samsung.iug.ui.graph

import java.awt.*
import javax.swing.JPanel

class WorkflowNodeView : JPanel() {

    var idText: String = ""
    var nameText: String = ""
    var descText: String = ""

    init {
        layout = null
        isOpaque = false
        preferredSize = Dimension(280, 110)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val w = width
        val h = height

        // === Draw border ===
        g2.color = Color(146, 119, 255)
        g2.stroke = BasicStroke(2f)
        g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16)

        // === Icon (top left) ===
        val iconX = 16
        val iconY = 12
        val iconSize = 20
        g2.fillOval(iconX, iconY, iconSize, iconSize)

        // === Text info ===
        g2.color = Color.WHITE
        g2.font = Font("SansSerif", Font.BOLD, 13)
        g2.drawString(idText, iconX, iconY + iconSize + 14)

        g2.font = Font("SansSerif", Font.PLAIN, 12)
        g2.drawString(nameText, iconX, iconY + iconSize + 32)
        g2.drawString(descText, iconX, iconY + iconSize + 48)
    }
}
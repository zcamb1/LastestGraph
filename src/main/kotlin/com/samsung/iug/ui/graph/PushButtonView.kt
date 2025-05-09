package com.samsung.iug.ui.graph

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

/**
 * A clickable rounded "+" button with customizable action.
 */
class PlusButtonView(
    private val onClick: () -> Unit
) : JPanel() {

    init {
        preferredSize = Dimension(24, 24)
        isOpaque = false
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                onClick()
            }

            override fun mouseEntered(e: MouseEvent) {
                repaint()
            }

            override fun mouseExited(e: MouseEvent) {
                repaint()
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Background circle
        g2.color = Color(146, 119, 255)
        g2.fillOval(0, 0, width, height)

        // Plus sign
        g2.color = Color.WHITE
        g2.stroke = BasicStroke(2f)
        val cx = width / 2
        val cy = height / 2
        g2.drawLine(cx - 5, cy, cx + 5, cy)
        g2.drawLine(cx, cy - 5, cx, cy + 5)
    }
}
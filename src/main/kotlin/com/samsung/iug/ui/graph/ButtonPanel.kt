package com.samsung.iug.ui.graph

import java.awt.*
import javax.swing.*

class ButtonPanel : JPanel() {

    val editButton = CircleButton(Color(60, 100, 200))
    val extraButton = CircleButton(Color(60, 100, 200))

    init {
        layout = null
        isOpaque = false
        preferredSize = Dimension(280, 30)

        editButton.setBounds(30, 5, 20, 20)
        extraButton.setBounds(60, 5, 20, 20)

        add(editButton)
        add(extraButton)
    }

    class CircleButton(color: Color) : JButton() {
        init {
            isContentAreaFilled = false
            isBorderPainted = false
            background = color
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            isFocusPainted = false
        }

        override fun paintComponent(g: Graphics) {
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.color = background
            g2.fillOval(0, 0, width, height)
        }
    }
}
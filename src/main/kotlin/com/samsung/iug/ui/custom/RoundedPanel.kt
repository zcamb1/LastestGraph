package com.samsung.iug.ui.custom

import java.awt.*
import javax.swing.*

class RoundedPanel(
    private val radius: Int = 20,
    private val bgColor: Color = Color.WHITE
) : JPanel() {

    init {
        isOpaque = false
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        g2.color = bgColor
        g2.fillRoundRect(0, 0, width, height, radius, radius)

        super.paintComponent(g)
    }
}
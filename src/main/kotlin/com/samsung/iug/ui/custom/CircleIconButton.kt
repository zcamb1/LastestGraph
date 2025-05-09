package com.samsung.iug.ui.custom

import java.awt.*
import javax.swing.*

class CircleIconButton(
    private val diameter: Int,
    private val background: Color,
    private val borderColor: Color,
    private val strokeWidth: Float,
    private val innerIcon: Icon? = null
) : Icon {

    override fun getIconWidth(): Int = diameter
    override fun getIconHeight(): Int = diameter

    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        g2.color = background
        g2.fillOval(x, y, diameter, diameter)

        g2.color = borderColor
        g2.stroke = BasicStroke(strokeWidth)
        g2.drawOval(x, y, diameter - 1, diameter - 1)

        innerIcon?.let {
            val iconX = x + (diameter - it.iconWidth) / 2
            val iconY = y + (diameter - it.iconHeight) / 2
            it.paintIcon(c, g, iconX, iconY)
        }
    }
}
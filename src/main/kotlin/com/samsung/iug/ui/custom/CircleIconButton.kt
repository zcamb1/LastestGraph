package com.samsung.iug.ui.custom

import com.samsung.iug.utils.ImageHelper
import java.awt.Color
import java.awt.Dimension
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JButton

class CircleIconButton(
    private val size: Int,
    private val pathIcon: String,
    private val background: Color,
    private var backgroundSelected: Color? = null,
    private val borderColor: Color? = null,
    private var borderColorSelected: Color? = null,
    private val strokeWidth: Float = 0f,
) : JButton() {
    init {
        isFocusPainted = false
        isContentAreaFilled = false
        isBorderPainted = false
        preferredSize = Dimension(size + 5, size + 5)
        maximumSize = preferredSize

        if (backgroundSelected == null) {
            backgroundSelected = background
        }
        if (borderColorSelected == null) {
            borderColorSelected = borderColor
        }

        this.icon = getCircleIcon(false)
    }

    private fun getCircleIcon(isSelected: Boolean): Icon {
        val iconUrl = javaClass.getResource(pathIcon)
        val rawIcon = ImageIcon(iconUrl)
        val newIcon = ImageHelper.recolorImageIcon(rawIcon, Color.WHITE)
        val iconImage = ImageHelper.resizeIcon(newIcon, size / 3 * 2, size / 3 * 2)

        if (isSelected && backgroundSelected != null) {
            return CircleIcon(size, backgroundSelected!!, borderColorSelected, strokeWidth, iconImage)
        } else {
            return CircleIcon(size, background, borderColor, strokeWidth, iconImage)
        }
    }

    fun refreshColorSelected(isSelected: Boolean) {
        this.icon = getCircleIcon(isSelected)
    }
}
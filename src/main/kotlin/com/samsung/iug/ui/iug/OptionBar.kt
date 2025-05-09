package com.samsung.iug.ui.iug

import com.intellij.util.ui.JBUI
import com.samsung.iug.ui.custom.RoundedPanel
import java.awt.Color
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.border.EmptyBorder

class OptionBar(radius: Int = 30) : RoundedPanel(radius, Color.DARK_GRAY) {
    init {
        border = EmptyBorder(10, 20, 10, 20)
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        add(createLabel("Create new guide"))
        add(createLabel("Save guide"))
        add(createLabel("Open guide"))
        add(createLabel("Create a copy"))
        add(createLabel("Rename"))
        add(createLabel("Export JSON"))
    }

    private fun createLabel(text: String): JLabel {
        return JLabel(text).apply {
            foreground = Color.WHITE
            border = JBUI.Borders.empty(5, 5)
            font = font.deriveFont(14f)
        }
    }
}
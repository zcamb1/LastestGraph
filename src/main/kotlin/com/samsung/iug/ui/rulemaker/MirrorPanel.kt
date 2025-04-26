package com.samsung.iug.ui.rulemaker

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.border.TitledBorder

class MirrorPanel : JPanel(BorderLayout()) {
    private val mirrorScreenPanel = JPanel(BorderLayout()).apply {
        add(JBLabel("No screen connected", SwingConstants.CENTER), BorderLayout.CENTER)
        background = Color(45, 45, 45)
    }

    init {
        border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(JBColor.GRAY),
            "Mirror screen",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            null,
            JBColor.WHITE
        )
        background = Color(60, 63, 65)
        add(mirrorScreenPanel, BorderLayout.CENTER)
        preferredSize = Dimension(290, 300)
    }
}
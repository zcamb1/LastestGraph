package com.samsung.iug.ui.rulemaker

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.border.TitledBorder

class LayoutInspectorPanel: JPanel(BorderLayout()) {
    init {
        border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "LayoutInspectorPanel",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            null
        )
        background = Color.GRAY

        preferredSize = Dimension(300, 100)
    }
}
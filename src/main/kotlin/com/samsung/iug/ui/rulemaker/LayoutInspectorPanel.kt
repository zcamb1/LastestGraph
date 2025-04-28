package com.samsung.iug.ui.rulemaker

import java.awt.BorderLayout
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.border.TitledBorder

class LayoutInspectorPanel : JPanel(BorderLayout()) {
    init {
        border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Layout Inspector Panel",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            null
        )
    }
}
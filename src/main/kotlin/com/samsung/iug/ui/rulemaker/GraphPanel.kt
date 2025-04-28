package com.samsung.iug.ui.rulemaker

import java.awt.BorderLayout
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.border.TitledBorder

class GraphPanel : JPanel(BorderLayout()) {

    init {
        border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Graph",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            null
        )
    }
}
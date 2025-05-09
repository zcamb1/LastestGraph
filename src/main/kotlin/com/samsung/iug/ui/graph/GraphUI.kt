package com.samsung.iug.ui.graph

import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.BorderFactory
import javax.swing.JPanel

class GraphUI(): JPanel(BorderLayout()) {
    init {
        this.border = BorderFactory.createEmptyBorder(100, 100, 100, 100)

        val centerLayout = JPanel(FlowLayout(FlowLayout.RIGHT))
        centerLayout.isOpaque = false
        add(centerLayout, BorderLayout.CENTER)

        val addNode = AddNodeUI()
        centerLayout.add(addNode)
    }
}
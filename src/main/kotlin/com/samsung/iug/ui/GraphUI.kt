package com.samsung.iug.ui

import com.android.tools.adtui.common.border
import com.intellij.icons.AllIcons
import com.intellij.ui.util.maximumWidth
import com.intellij.ui.util.preferredHeight
import com.intellij.ui.util.preferredWidth
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

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
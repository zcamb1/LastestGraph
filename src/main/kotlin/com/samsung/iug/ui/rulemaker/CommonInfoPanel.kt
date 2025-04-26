package com.samsung.iug.ui.rulemaker

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JTextArea

class CommonInfoPanel : JPanel(BorderLayout()) {
    private val commonInfoArea = JTextArea("{\n\n}").apply {
        lineWrap = true
        wrapStyleWord = true
    }

    init {
        background = JBColor.GRAY
        add(JBScrollPane(commonInfoArea), BorderLayout.CENTER)
    }
}
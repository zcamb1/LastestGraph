package com.samsung.iug.ui.rulemaker

import com.intellij.ui.components.JBScrollPane
import java.awt.Color

import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JTextArea

class CommonInfoPanel : JPanel(BorderLayout()) {
    val commonInfoArea = JTextArea("{\n\n}").apply {
        lineWrap = true
        wrapStyleWord = true
    }

    init {
        background = Color(60, 63, 65)
        add(JBScrollPane(commonInfoArea), BorderLayout.CENTER)
    }
}
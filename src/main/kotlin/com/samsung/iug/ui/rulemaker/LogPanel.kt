package com.samsung.iug.ui.rulemaker

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.border.TitledBorder

class LogPanel : JPanel(BorderLayout()) {

    private val logMessagePanel = JTextArea().apply {
        isEditable = false
        lineWrap = true
        wrapStyleWord = true
        text = "Log messages will appear here..."
        background = Color(45, 45, 45)
        foreground = JBColor.WHITE
    }

    init {
        border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(JBColor.GRAY),
            "Log",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            null,
            JBColor.WHITE
        )
        background = Color(60, 63, 65)

        add(JBScrollPane(logMessagePanel), BorderLayout.CENTER)
        preferredSize = Dimension(300, 250)
    }

}
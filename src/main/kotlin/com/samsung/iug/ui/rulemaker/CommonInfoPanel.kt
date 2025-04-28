package com.samsung.iug.ui.rulemaker

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JTextArea

class CommonInfoPanel(var data: String = "\n\"packageName\": abc\n") : JPanel(BorderLayout()) {
    private val commonInfoArea = JTextArea("{$data}").apply {
        lineWrap = true
        wrapStyleWord = true
    }

    init {
        background = JBColor.GRAY
        add(JBScrollPane(commonInfoArea), BorderLayout.CENTER)
    }

    fun updatePackageName(packageName: String) {
        data = data
            .lines()
            .joinToString("\n") { line ->
                if (line.trim().startsWith("\"packageName\":")) {
                    "\"packageName\": \"$packageName\""
                } else {
                    line
                }
            }
            .apply {
                commonInfoArea.text = "{$this}"
            }
    }
}
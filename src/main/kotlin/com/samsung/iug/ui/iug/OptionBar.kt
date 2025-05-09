package com.samsung.iug.ui.iug

import com.samsung.iug.graph.listNode
import com.samsung.iug.ui.custom.RoundedPanel
import com.samsung.iug.utils.FileStorage
import com.samsung.iug.utils.JsonHelper
import java.awt.Color
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.border.EmptyBorder

class OptionBar(radius: Int = 30) : RoundedPanel(radius, Color.DARK_GRAY) {
    init {
        border = EmptyBorder(10, 20, 10, 20)
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        add(createLabel("Create new guide"))
        add(createLabel("Save guide"))
        add(createLabel("Open guide"))
        add(createLabel("Create a copy"))
        add(createLabel("Rename"))
        add(createLabel("Export JSON").apply {
            addActionListener {
                FileStorage.currentFile?.let { file ->
                    val chooser = JFileChooser(file.parent).apply {
                        fileSelectionMode = JFileChooser.FILES_ONLY
                        fileFilter = javax.swing.filechooser.FileNameExtensionFilter(file.name, file.extension)
                    }
                    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                        val result = JsonHelper.exportRuleToJsonFile(listNode.listNode, chooser.selectedFile)

                        if (result.first) {
                            JOptionPane.showMessageDialog(
                                this,
                                "Successfully exported rule: ${result.second}"
                            )
                        } else {
                            JOptionPane.showMessageDialog(
                                this,
                                "Failed to export rule: ${result.second ?: "Unknown error"}"
                            )
                        }
                    }
                }
            }
        })
    }

    private fun createLabel(text: String): JButton {
        return JButton(text).apply {
            foreground = Color.WHITE
            border = EmptyBorder(0,0,0,0)
            font = font.deriveFont(14f)
        }
    }
}
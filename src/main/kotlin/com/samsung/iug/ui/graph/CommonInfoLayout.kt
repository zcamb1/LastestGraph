package com.samsung.iug.ui.graph

import com.samsung.iug.ui.custom.JTextFieldCustom
import java.awt.*
import javax.swing.*

class CommonInfoLayout : JPanel() {
    private val mainContent = JPanel(GridBagLayout())
    init {
        this.apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.CENTER_ALIGNMENT
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            isOpaque = false
        }

        mainContent.apply {
            val gbcLabel = GridBagConstraints().apply {
                gridx = 0
                weightx = 0.2
                fill = GridBagConstraints.HORIZONTAL
            }

            val gbcInput = GridBagConstraints().apply {
                gridx = 1
                weightx = 1.0
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(0, 0, 10, 0)
            }

            var i = 0

            if (ViewNode.className != "") {
                gbcLabel.gridy = i
                add(JLabel("ClassName"), gbcLabel)
                val stepId = JTextFieldCustom("", 40)
                stepId.text = ViewNode.className
                gbcInput.gridy = i
                add(stepId, gbcInput)
                i += 1
            }

            if (ViewNode.resourceId != "") {
                gbcLabel.gridy = i
                add(JLabel("ResourceId"), gbcLabel)
                val resourceId = JTextFieldCustom("", 40)
                resourceId.text = ViewNode.resourceId
                gbcInput.gridy = i
                add(resourceId, gbcInput)
                i += 1
            }

            if (ViewNode.text != "") {
                gbcLabel.gridy = i
                add(JLabel("Text"), gbcLabel)
                val textV = JTextFieldCustom("", 40)
                textV.text = ViewNode.text
                gbcInput.gridy = i
                add(textV, gbcInput)
                i += 1
            }

            gbcLabel.gridy = i
            add(JLabel("Bounds"), gbcLabel)
            val bounds = JTextFieldCustom("", 40)
            bounds.text = "(${ViewNode.bounds.x}, ${ViewNode.bounds.y})(${ViewNode.bounds.x + ViewNode.bounds.width}, ${ViewNode.bounds.y + ViewNode.bounds.height})"
            gbcInput.gridy = i
            add(bounds, gbcInput)
        }

        val label1 = JLabel().apply {
            text = "<html><div style='width:100%; text-align: center;'>Add Step: Common Info</div></html>"
            font = Font("Arial", Font.BOLD, 14)
            foreground = Color.WHITE
            alignmentX = Component.CENTER_ALIGNMENT
            horizontalAlignment = JLabel.CENTER
        }

        val label2 = JLabel().apply {
            text = "<html><div style='width:100%; text-align: center;'>Edit the common information below or click on <br> confirm to create a new Step</div></html>"
            font = Font("Arial", Font.BOLD , 12)
            foreground = Color.GRAY
            alignmentX = Component.CENTER_ALIGNMENT
            horizontalAlignment = JLabel.CENTER
        }

        add(mainContent)
        add(Box.createVerticalStrut(10))
        add(label1)
        add(Box.createVerticalStrut(10))
        add(label2)
    }

    fun fillData() {
        mainContent.apply {
            val gbcLabel = GridBagConstraints().apply {
                gridx = 0
                weightx = 0.2
                fill = GridBagConstraints.HORIZONTAL
            }

            val gbcInput = GridBagConstraints().apply {
                gridx = 1
                weightx = 1.0
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(0, 0, 10, 0)
            }

            var i = 0

            if (ViewNode.className != "") {
                gbcLabel.gridy = i
                add(JLabel("ClassName"), gbcLabel)
                val stepId = JTextFieldCustom("", 40)
                stepId.text = ViewNode.className
                gbcInput.gridy = i
                add(stepId, gbcInput)
                i += 1
            }

            if (ViewNode.resourceId != "") {
                gbcLabel.gridy = i
                add(JLabel("ResourceId"), gbcLabel)
                val resourceId = JTextFieldCustom("", 40)
                resourceId.text = ViewNode.resourceId
                gbcInput.gridy = i
                add(resourceId, gbcInput)
                i += 1
            }

            if (ViewNode.text != "") {
                gbcLabel.gridy = i
                add(JLabel("Text"), gbcLabel)
                val textV = JTextFieldCustom("", 40)
                textV.text = ViewNode.text
                gbcInput.gridy = i
                add(textV, gbcInput)
                i += 1
            }

            gbcLabel.gridy = i
            add(JLabel("Bounds"), gbcLabel)
            val bounds = JTextFieldCustom("", 40)
            bounds.text = "(${ViewNode.bounds.x}, ${ViewNode.bounds.y})(${ViewNode.bounds.x + ViewNode.bounds.width}, ${ViewNode.bounds.y + ViewNode.bounds.height})"
            gbcInput.gridy = i
            add(bounds, gbcInput)
        }
    }
}
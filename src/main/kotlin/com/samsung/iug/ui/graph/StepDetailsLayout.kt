package com.samsung.iug.ui.graph

import com.samsung.iug.ui.custom.JTextFieldCustom
import java.awt.*
import javax.swing.*

object StepDetailsLayout : JPanel() {
    private val mainContent = JPanel(GridBagLayout())
    private val stepId = JTextFieldCustom("e.g.Onboarding", 40)
    private val screenId = JTextFieldCustom("Screen Id", 40)
    private val guideContent = JTextFieldCustom("Step description to be shown to the user", 80)

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

            gbcLabel.gridy = 0
            add(JLabel("Step ID"), gbcLabel)
            gbcLabel.gridy = 1
            add(JLabel("Screen ID"), gbcLabel)
            gbcLabel.gridy = 2
            add(JLabel("Guide Content"), gbcLabel)

            val gbcInput = GridBagConstraints().apply {
                gridx = 1
                weightx = 1.0
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(0, 0, 10, 0)
            }


            if (ViewNode.screenId != "") {
                screenId.text = ViewNode.screenId
            }

            gbcInput.gridy = 0
            add(stepId, gbcInput)
            gbcInput.gridy = 1
            add(screenId, gbcInput)
            gbcInput.gridy = 2
            add(guideContent, gbcInput)
        }

        val label1 = JLabel().apply {
            text = "<html><div style='width:100%; text-align: center;'>Add Step: Step details</div></html>"
            font = Font("Arial", Font.BOLD, 14)
            foreground = Color.WHITE
            alignmentX = Component.CENTER_ALIGNMENT
            horizontalAlignment = JLabel.CENTER
        }

        val label2 = JLabel().apply {
            text = "<html><div style='width:100%; text-align: center;'>Fill in or edit the step <br> details needed for the step creation highlighted below</div></html>"
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

    fun fillData () {
        mainContent.removeAll()
        mainContent.apply {
            val gbcLabel = GridBagConstraints().apply {
                gridx = 0
                weightx = 0.2
                fill = GridBagConstraints.HORIZONTAL
            }

            gbcLabel.gridy = 0
            add(JLabel("Step ID"), gbcLabel)
            gbcLabel.gridy = 1
            add(JLabel("Screen ID"), gbcLabel)
            gbcLabel.gridy = 2
            add(JLabel("Guide Content"), gbcLabel)

            val gbcInput = GridBagConstraints().apply {
                gridx = 1
                weightx = 1.0
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(0, 0, 10, 0)
            }

            val stepId = JTextFieldCustom("e.g.Onboarding", 40)
            val screenId = JTextFieldCustom(ViewNode.screenId, 40)
            if (ViewNode.screenId != "") {
                screenId.text = ViewNode.screenId
            }
            val guideContent = JTextFieldCustom("Step description to be shown to the user", 80)


            gbcInput.gridy = 0
            add(stepId, gbcInput)
            gbcInput.gridy = 1
            add(screenId, gbcInput)
            gbcInput.gridy = 2
            add(guideContent, gbcInput)
        }

        repaint()
        revalidate()
    }

    fun getData(): Map<String, String> {
        val data = mapOf(
            "stepId" to stepId.text.toString(),
            "guideContent" to guideContent.text.toString()
        )
        return data
    }
}
package com.samsung.iug.ui.rulemaker

import com.intellij.icons.AllIcons
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import com.samsung.iug.model.Rule
import com.samsung.iug.model.Step
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*
import java.awt.*
import javax.swing.table.DefaultTableModel

class StepInfoPanel(
    private var currentRule: Rule? = null
) : JPanel(BorderLayout()) {

    private val idField = JBTextField()
    private val screenIdField = JBTextField()
    private val guideContentArea = JTextArea().apply {
        lineWrap = true
        wrapStyleWord = true
    }
    private val nextStepsField = JBTextField()
    private val isSubStepCheckbox = JCheckBox("Is Sub-Step")
    private val layoutMatchersTable = createLayoutMatchersTable()
    private var currentStep: Step? = null

    init {
        val formPanel = createFormInfoPanel()
        val navigationPanel = createNavigationPanel()

        add(formPanel, BorderLayout.NORTH)
        add(layoutMatchersTable, BorderLayout.CENTER)
        add(navigationPanel, BorderLayout.SOUTH)
    }

    private fun createFormInfoPanel(): JPanel {
        val panel = JPanel(BorderLayout())

        val formPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Step ID:", idField)
            .addLabeledComponent("Screen ID:", screenIdField)
            .addLabeledComponent("Guide Content:", JBScrollPane(guideContentArea).apply {
                preferredSize = Dimension(300, 100)
                minimumSize = Dimension(200, 50)
            })
            .addLabeledComponent("Next Step IDs:", nextStepsField)
            .addComponent(isSubStepCheckbox)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        formPanel.border = JBUI.Borders.empty(10)
        panel.add(formPanel, BorderLayout.CENTER)

        return panel
    }

    private fun createLayoutMatchersTable(): JBTable {
        val columnNames =
            arrayOf("Match Target", "Match Operand", "Match Criteria", "Highlight Type", "Transition Condition")
        val tableModel = DefaultTableModel(columnNames, 0)

        val table = JBTable(tableModel)
        table.preferredScrollableViewportSize = Dimension(300, 150)

        return table
    }

    private fun createNavigationPanel(): JPanel {
        val panel = JPanel(FlowLayout(FlowLayout.CENTER))

        val prevStepButton = JButton("Previous Step").apply {
            icon = AllIcons.Actions.Back
            isEnabled = true
            addActionListener {
//                onPreviousStep()
            }
        }

        val nextStepButton = JButton("Next Step").apply {
            icon = AllIcons.Actions.Forward
            isEnabled = true
            addActionListener {
//                onNextStep()
            }
        }

        panel.add(prevStepButton)
        panel.add(nextStepButton)

        panel.border = BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            JBUI.Borders.empty(8)
        )

        return panel
    }
} 

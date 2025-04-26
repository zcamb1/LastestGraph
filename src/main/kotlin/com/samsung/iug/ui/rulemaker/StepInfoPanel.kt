package com.samsung.iug.ui.rulemaker

import com.intellij.icons.AllIcons
import com.intellij.ui.JBColor
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
            BorderFactory.createMatteBorder(1, 0, 0, 0, JBColor.LIGHT_GRAY),
            JBUI.Borders.empty(8)
        )

        return panel
    }

//    fun onPreviousStep() {
//        val currentStep = this.currentStep ?: return
//        val rule = currentRule ?: return
//        // Find all parent nodes
//        val parentIds = rule.steps.filter { it.nextStepIds.contains(currentStep.id) }.map { it.id }
//        if (parentIds.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "No previous step.", "Navigation", JOptionPane.INFORMATION_MESSAGE)
//            return
//        }
//        if (parentIds.size == 1) {
//            val prevStep = rule.steps.find { it.id == parentIds[0] }
//            if (prevStep != null) updateLayoutStep(prevStep)
//        } else {
//            val options = parentIds.toTypedArray()
//            val selected = JOptionPane.showInputDialog(
//                this,
//                "Select previous step:",
//                "Previous Step",
//                JOptionPane.QUESTION_MESSAGE,
//                null,
//                options,
//                options[0]
//            )
//            if (selected != null) {
//                val prevStep = rule.steps.find { it.id == selected }
//                if (prevStep != null) updateLayoutStep(prevStep)
//            }
//        }
//    }
//
//    /**
//     * Navigate to the next step
//     */
//    fun onNextStep() {
//        val currentStep = this.currentStep ?: return
//        val rule = currentRule ?: return
//        val nextIds = currentStep.nextStepIds
//        if (nextIds.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "No next step.", "Navigation", JOptionPane.INFORMATION_MESSAGE)
//            return
//        }
//        if (nextIds.size == 1) {
//            val nextStep = rule.steps.find { it.id == nextIds[0] }
//            if (nextStep != null) updateLayoutStep(nextStep)
//        } else {
//            val options = nextIds.toTypedArray()
//            val selected = JOptionPane.showInputDialog(
//                this,
//                "Select next step:",
//                "Next Step",
//                JOptionPane.QUESTION_MESSAGE,
//                null,
//                options,
//                options[0]
//            )
//            if (selected != null) {
//                val nextStep = rule.steps.find { it.id == selected }
//                if (nextStep != null) updateLayoutStep(nextStep)
//            }
//        }
//    }
//
//    /**
//     * Update the layout matchers table with data from the current step.
//     */
//    private fun updateLayoutMatchersTable() {
//        val tableModel = layoutMatchersTable.model as DefaultTableModel
//        tableModel.rowCount = 0
//
//        val step = currentStep ?: return
//
//        for (matcher in step.layoutMatchers) {
//            tableModel.addRow(
//                arrayOf(
//                    matcher.matchTarget,
//                    matcher.matchOperand,
//                    matcher.matchCriteria,
//                    matcher.highlightType
//                )
//            )
//        }
//    }
//
//    /**
//     * Set the step to edit.
//     */
//    fun updateLayoutStep(step: Step) {
//        currentStep = step
//
//        idField.text = step.id
//        screenIdField.text = step.screenId
//        guideContentArea.text = step.guideContent
//        nextStepsField.text = step.nextStepIds.joinToString(", ")
//        isSubStepCheckbox.isSelected = step.isSubStep
//
//        updateLayoutMatchersTable()
//
//        idField.isEditable = true
//        screenIdField.isEditable = true
//    }
//
//    /**
//     * Set the current rule for reference.
//     */
//    fun setRule(rule: Rule) {
//        currentRule = rule
//    }
//
//    /**
//     * Save changes to the current step.
//     */
//    fun saveChanges() {
//        if (currentStep == null) return
//
//        val oldId = currentStep!!.id
//        val newId = idField.text
//        val newScreenId = screenIdField.text
//
//        // Check if ID has changed and update all references in the rule
//        var idChanged = false
//
//        if (oldId != newId && currentRule != null) {
//            val success = currentRule!!.updateStepId(oldId, newId)
//
//            if (success) {
//                idChanged = true
//            } else {
//                // ID change failed, revert to old ID
//                JOptionPane.showMessageDialog(
//                    this,
//                    "Could not update step ID. ID reverted.",
//                    "ID Change Failed",
//                    JOptionPane.ERROR_MESSAGE
//                )
//                idField.text = oldId
//                currentStep!!.id = oldId
//                // Notify listener of the revert
//                onStepUpdated(currentStep!!)
//                return
//            }
//        }
//
//        // Update step with values from UI
//        currentStep!!.id = newId
//        currentStep!!.screenId = newScreenId
//        currentStep!!.guideContent = guideContentArea.text
//        currentStep!!.isSubStep = isSubStepCheckbox.isSelected
//
//        // Parse next step IDs
//        val nextStepIds = nextStepsField.text
//            .split(",")
//            .map { it.trim() }
//            .filter { it.isNotEmpty() }
//
//        currentStep!!.nextStepIds.clear()
//        currentStep!!.nextStepIds.addAll(nextStepIds)
//
//        // Notify listener
//        onStepUpdated(currentStep!!)
//
//        // Show confirmation message with ID change info if applicable
//        val message = if (idChanged) {
//            "Changes saved successfully!\nStep ID changed from $oldId to $newId. All references updated."
//        } else {
//            "Changes saved successfully!"
//        }
//
//        JOptionPane.showMessageDialog(
//            this,
//            message,
//            "Success",
//            JOptionPane.INFORMATION_MESSAGE
//        )
//    }
//
//    /**
//     * Create a new step with initial values.
//     * isSubStep is now managed internally via RuleMakerWindow.identifyMainPathAndSetSubSteps,
//     * but we still keep the UI control to allow manual override.
//     */
//    fun createNewStep(isSubStep: Boolean = false): Step {
//        val newStepId = "step_${System.currentTimeMillis()}"
//        val step = Step(
//            id = newStepId,
//            screenId = "com.example.activity",
//            guideContent = "New step",
//            isSubStep = isSubStep
//        )
//
//        // Set for editing
//        updateLayoutStep(step)
//
//        // ID should be editable for new steps
//        idField.isEditable = true
//
//        return step
//    }
//
//    /**
//     * Reset the form.
//     */
//    fun reset() {
//        currentStep = null
//        idField.text = ""
//        screenIdField.text = ""
//        guideContentArea.text = ""
//        nextStepsField.text = ""
//        isSubStepCheckbox.isSelected = false
//
//        val tableModel = layoutMatchersTable.model as DefaultTableModel
//        tableModel.rowCount = 0
//    }
} 

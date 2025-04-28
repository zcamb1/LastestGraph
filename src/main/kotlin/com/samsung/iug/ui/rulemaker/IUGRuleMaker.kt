package com.samsung.iug.ui.rulemaker

import com.intellij.openapi.project.Project
import com.intellij.util.ui.JBUI
import com.samsung.iug.logic.EditorPanelLogic
import com.samsung.iug.logic.NodeInteractionLogic
import com.samsung.iug.model.Rule
import com.samsung.iug.model.Step
import com.samsung.iug.service.Log
import com.samsung.iug.ui.screenmirror.MirrorPanel
import com.samsung.iug.utils.JsonHelper
import java.awt.*
import javax.swing.*
import javax.swing.border.TitledBorder
import com.samsung.iug.utils.RuleParser
import com.samsung.iug.logic.TopToolbarLogic
class IUGRuleMaker(private val path: String, private val username: String,private val project: Project) : JPanel(BorderLayout()) {

    private lateinit var rule: Rule
    private var commonInfoContent = CommonInfoPanel()
    private lateinit var currentStep: Step
    private var currentRule: Rule? = null
    private val ruleParser = RuleParser()
    private val editorLogic: EditorPanelLogic = EditorPanelLogic(::onStepUpdated)
    private val stepInfoPanel: StepInfoPanel = StepInfoPanel(editorLogic)
    private val topToolbarLogic = TopToolbarLogic(
        project,
        ruleParser,
        ::setRule,
        { currentRule }
    )
    private val nodeLogic: NodeInteractionLogic = NodeInteractionLogic(
        getCurrentRule = { currentRule },
        setRule = { rule -> currentRule = rule },
        refreshGraph = { graphPanel.refreshGraph() },
        showStepInEditor = { step -> stepInfoPanel.setStep(step) },
        createNewStep = { isSubStep -> editorLogic.createNewStep(isSubStep) },
        getCellForStep = { stepId -> graphPanel.getCellForStep(stepId) },
        getCellGeometry = { cell -> graphPanel.getCellGeometry(cell) },
        setCellGeometry = { cell, geo -> graphPanel.setCellGeometry(cell, geo) },
        showMessage = { msg, title, type -> JOptionPane.showMessageDialog(this, msg, title, type) }
    )

    private val graphPanel: GraphPanelMain = GraphPanelMain(
        onStepSelected = { step -> nodeLogic.onStepSelected(step) },
        onAddStep = { parentStep, parentCell, parentGeo ->
            nodeLogic.onAddStep(parentStep, parentCell, parentGeo)
        },
        onAddSubStep = { parentStep -> nodeLogic.onAddSubStep(parentStep) },
        onRemoveStep = { step -> nodeLogic.onRemoveStep(step) }
    ) { stepA, swapId -> nodeLogic.onSwapNode(stepA, swapId) }

    init {
        val maxWidth = 1200
        val maxHeight = 800

        // Top bar
        val topLayout = createTopToolBar().apply {
            preferredSize = Dimension(maxWidth, 50)
        }

        // Center layout: Common Info, Step Info, Mirror, Screen Info
        val combinedTabPanel = createCombinedTabPanel().apply {
            preferredSize = Dimension(300, 650)
        }
        val mirrorPanel = MirrorPanel().apply {
            preferredSize = Dimension(500, 650)
        }

        val centerLayout = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS) // horizontal
            background = Color.DARK_GRAY
            add(combinedTabPanel)
            add(mirrorPanel)
            preferredSize = Dimension(maxWidth, 650)
        }

        // Graph and Log
        val graphPanelContainer = createGraphPanel().apply {
            preferredSize = Dimension(800, 250)
        }
        val logPanelContainer = LogPanel().apply {
            preferredSize = Dimension(400, 250)
        }
        Log.init(logPanelContainer)

        val bottomLayout = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS) // horizontal
            background = Color.DARK_GRAY
            add(graphPanelContainer)
            add(logPanelContainer)
            preferredSize = Dimension(maxWidth, 250)
        }

        // Todo: Use a vertical split pane to divide top and bottom sections with preferred ratios

        // Add main layout
        background = Color.DARK_GRAY
        add(topLayout, BorderLayout.NORTH)
        add(centerLayout, BorderLayout.CENTER)
        add(bottomLayout, BorderLayout.SOUTH)
    }

    private fun createGraphPanel(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Step graph",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            null,
            Color.WHITE
        )
        panel.background = Color(60, 63, 65)

        // Make sure the graph is visible
        graphPanel.preferredSize = Dimension(950, 220)
        panel.add(graphPanel, BorderLayout.CENTER)

        // Set preferred size for the graph panel container
        panel.preferredSize = Dimension(900, 250)

        return panel
    }

    private fun createTopToolBar(): JPanel {
        // Title
        val titleLabel = JLabel("IUG Rule Maker Tool").apply {
            foreground = Color.WHITE
            border = JBUI.Borders.empty(5, 10)
            font = font.deriveFont(font.size + 2f)
        }

        // Button
        val exportButton = JButton("Export")
        val importButton = JButton("Import")
        val exitButton = JButton("Exit")

        exportButton.addActionListener {
            topToolbarLogic.exportRule(this)
        }
        importButton.addActionListener {
            topToolbarLogic.importRule()
        }
        exitButton.addActionListener {
            topToolbarLogic.exitApplication()
        }

        // Info user
        val userLabel = JLabel("Hello, $username ▼").apply {
            foreground = Color.WHITE
            border = JBUI.Borders.empty(5, 10)
        }

        val buttonsPanel = JPanel(FlowLayout(FlowLayout.CENTER)).apply {
            add(exportButton)
            add(importButton)
            add(exitButton)
        }

        val userPanel = JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
            add(userLabel)
        }

        val panel = JPanel(BorderLayout()).apply {
            add(titleLabel, BorderLayout.WEST)
            add(buttonsPanel, BorderLayout.CENTER)
            add(userPanel, BorderLayout.EAST)
        }

        return panel
    }

    private fun createCombinedTabPanel(): JPanel {
        val tabbedPane = JTabbedPane().apply {
            foreground = Color.WHITE
            addTab("Common Info", commonInfoContent)
            addTab("Step Info", stepInfoPanel)
            selectedIndex = 0
        }

        val panel = JPanel(BorderLayout()).apply {
            add(tabbedPane, BorderLayout.CENTER)
            border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                null,
                Color.WHITE
            )
        }

        return panel
    }

    private fun onClickImportFileJson() {
        val filePath = "$path/rule.json"
        rule = JsonHelper.readJson(filePath)
    }

    private fun onClickExportFileJson() {
        val filePath = "$path/rule.json"
        JsonHelper.writeJson(filePath, rule)
    }

    private fun onClickExit() {
        // todo
    }
    private fun onStepUpdated(step: Step) {
        nodeLogic.onStepUpdated(step)
    }

    private fun setRule(rule: Rule) {
        currentRule = rule

        // Pass rule to editor panel
        editorLogic.setRule(rule)

        // Display rule in graph panel
        graphPanel.displayRule(rule)

        // Apply layout for better visualization
        graphPanel.applyLayout()

        // Reset panels
        editorLogic.reset()
        stepInfoPanel.reset()
    }

    private fun updateRule(newRule: Rule) {
        rule = newRule
    }

    private fun updateLayoutStepInfo() {
        // todo
    }

    private fun updateLayoutCommonInfo() {
        // todo
    }

    private fun updateLayoutGraph() {
        // todo
    }

    private fun nextStep() {

    }

    private fun previousStep() {

    }


}
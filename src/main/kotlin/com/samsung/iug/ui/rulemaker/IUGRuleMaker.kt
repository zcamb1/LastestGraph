package com.samsung.iug.ui.rulemaker

import com.intellij.ui.util.maximumWidth
import com.intellij.ui.util.minimumWidth
import com.intellij.ui.util.preferredHeight
import com.intellij.ui.util.preferredWidth
import com.intellij.openapi.project.Project
import com.intellij.util.ui.JBUI
import com.samsung.iug.logic.EditorPanelLogic
import com.samsung.iug.logic.NodeInteractionLogic
import com.samsung.iug.model.Rule
import com.samsung.iug.model.Step
import com.samsung.iug.service.Log
import com.samsung.iug.ui.screenmirror.MirrorPanel
import java.awt.*
import javax.swing.*
import javax.swing.border.TitledBorder
import com.samsung.iug.logic.TopToolbarLogic
import com.samsung.iug.utils.AdbManager

class IUGRuleMaker(private val path: String, private val username: String, private val project: Project) :
    JPanel(BorderLayout()) {

    private var commonInfoContent = CommonInfoPanel()
    private var currentRule: Rule? = null
    private val editorLogic: EditorPanelLogic = EditorPanelLogic(::onStepUpdated)
    private val stepInfoPanel: StepInfoPanel = StepInfoPanel(editorLogic)
    private val topToolbarLogic = TopToolbarLogic(
        project,
        ::setRule
    ) { currentRule }

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

    private val graphPanel: GraphPanel = GraphPanel(
        onStepSelected = { step -> nodeLogic.onStepSelected(step) },
        onAddStep = { parentStep, parentCell, parentGeo ->
            nodeLogic.onAddStep(parentStep, parentCell, parentGeo)
        },
        onAddSubStep = { parentStep -> nodeLogic.onAddSubStep(parentStep) },
        onRemoveStep = { step -> nodeLogic.onRemoveStep(step) }
    ) { stepA, swapId -> nodeLogic.onSwapNode(stepA, swapId) }

    init {
        // ADB
        AdbManager.initializeAdb()

        val logPanelContainer = LogPanel().apply {
            preferredWidth = 400
            maximumWidth = 400
        }
        Log.init(logPanelContainer)

        // Top bar
        val topLayout = createTopToolBar().apply {
            preferredHeight = 50
        }

        // Center layout: Common Info, Step Info, Mirror, Screen Info
        val combinedTabPanel = createCombinedTabPanel().apply {
            preferredHeight = 650
            preferredWidth = 200
            maximumWidth = 200
        }
        val mirrorPanel = MirrorPanel().apply {
            preferredHeight = 650
        }

        val splitPaneH1 = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, combinedTabPanel, mirrorPanel).apply {
            resizeWeight = 0.7
            border = BorderFactory.createEmptyBorder()
            dividerSize = 5
        }

        val centerLayout = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS) // horizontal
            background = Color.DARK_GRAY
            add(splitPaneH1)
            preferredHeight = 650
        }

        // Graph and Log
        val graphPanelContainer = graphPanel.apply {
            preferredWidth = 800
            minimumWidth = 800
            border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Step Graph",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                null,
                Color.WHITE
            )
        }

        val bottomLayout = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS) // horizontal
            background = Color.DARK_GRAY
            add(graphPanelContainer)
            add(logPanelContainer)
        }

        // Split pane
        val mainSplitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, centerLayout, bottomLayout).apply {
            resizeWeight = 0.3
            border = BorderFactory.createEmptyBorder()
            dividerSize = 5
        }

        // Add main layout
        background = Color.DARK_GRAY
        add(topLayout, BorderLayout.NORTH)
        add(mainSplitPane)
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
            addTab("Step Info", stepInfoPanel)
            addTab("Common Info", commonInfoContent)
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
}
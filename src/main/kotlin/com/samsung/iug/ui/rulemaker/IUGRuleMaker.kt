package com.samsung.iug.ui.rulemaker

import com.intellij.util.ui.JBUI
import com.samsung.iug.model.Rule
import com.samsung.iug.model.Step
import com.samsung.iug.service.Log
import com.samsung.iug.ui.screenmirror.MirrorPanel
import com.samsung.iug.utils.JsonHelper
import java.awt.*
import javax.swing.*
import javax.swing.border.TitledBorder

class IUGRuleMaker(private val path: String, private val username: String) : JPanel(BorderLayout()) {

    private lateinit var rule: Rule
    private var commonInfoContent = CommonInfoPanel()
    private var stepInfoContent = StepInfoPanel()
    private lateinit var currentStep: Step

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
        val graphPanelContainer = GraphPanel().apply {
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
            onClickExportFileJson()
        }
        importButton.addActionListener {
            onClickImportFileJson()
        }
        exitButton.addActionListener {
            onClickExit()
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
            addTab("Step Info", stepInfoContent)
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
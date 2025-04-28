package com.samsung.iug.ui.rulemaker

import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import com.samsung.iug.model.Rule
import com.samsung.iug.model.Step
import com.samsung.iug.service.Log
import com.samsung.iug.ui.screenmirror.MirrorPanel
import com.samsung.iug.utils.JsonHelper
import java.awt.*
import javax.swing.*

class IUGRuleMaker(private val path: String, private val username: String) : JPanel(BorderLayout()) {

    private lateinit var rule: Rule
    private var commonInfoContent = CommonInfoPanel()
    private var stepInfoContent = StepInfoPanel()
    private lateinit var currentStep: Step

    init {
        // Top bar
        val topLayout = createTopToolBar()

        // Common Info, Step Info, Mirror, Screen Info
        val combinedTabPanel = createCombinedTabPanel()
        val mirrorPanel = MirrorPanel()


        combinedTabPanel.preferredSize = Dimension(300, 650)
        mirrorPanel.preferredSize = Dimension(400, 650)

        val centerLayout = JPanel()
        centerLayout.layout = BoxLayout(centerLayout, BoxLayout.X_AXIS) // horizontal
        centerLayout.background = JBColor.GRAY
        centerLayout.add(combinedTabPanel)
        centerLayout.add(mirrorPanel)

        // Graph and Log
        val graphPanelContainer = GraphPanel()
        val logPanelContainer = LogPanel()
        Log.init(logPanelContainer)

        val bottomLayout = JPanel()
        bottomLayout.layout = BoxLayout(bottomLayout, BoxLayout.X_AXIS) // horizontal
        bottomLayout.background = JBColor.GRAY
        bottomLayout.add(graphPanelContainer)
        bottomLayout.add(logPanelContainer)

        // Todo: Use a vertical split pane to divide top and bottom sections with preferred ratios

        // Add main layout
        background = JBColor.GRAY
        add(topLayout, BorderLayout.NORTH)
        add(centerLayout, BorderLayout.CENTER)
        add(bottomLayout, BorderLayout.SOUTH)
    }

    private fun createTopToolBar(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.background = JBColor.GRAY

        val titleLabel = JLabel("IUG Rule Maker Tool")
        titleLabel.foreground = JBColor.WHITE
        titleLabel.border = JBUI.Borders.empty(5, 10)
        titleLabel.font = titleLabel.font.deriveFont(titleLabel.font.size + 2f)

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

        val buttonsPanel = JPanel(FlowLayout(FlowLayout.CENTER))
        buttonsPanel.background = JBColor.GRAY
        buttonsPanel.add(exportButton)
        buttonsPanel.add(importButton)
        buttonsPanel.add(exitButton)

        val userPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        userPanel.background = JBColor.GRAY
        val userLabel = JLabel("Hello, $username ▼")
        userLabel.foreground = JBColor.WHITE
        userLabel.border = JBUI.Borders.empty(5, 10)
        userPanel.add(userLabel)

        panel.add(titleLabel, BorderLayout.WEST)
        panel.add(buttonsPanel, BorderLayout.CENTER)
        panel.add(userPanel, BorderLayout.EAST)

        return panel
    }

    private fun createCombinedTabPanel(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.background = JBColor.GRAY

        val tabbedPane = JTabbedPane()
        tabbedPane.background = JBColor.GRAY
        tabbedPane.foreground = JBColor.WHITE

        tabbedPane.addTab("Common Info", commonInfoContent)
        tabbedPane.addTab("Step Info", stepInfoContent)

        tabbedPane.selectedIndex = 0

        panel.add(tabbedPane, BorderLayout.CENTER)
        panel.preferredSize = Dimension(580, 300)

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
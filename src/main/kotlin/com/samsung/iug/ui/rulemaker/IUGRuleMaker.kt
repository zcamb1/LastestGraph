package com.samsung.iug.ui.rulemaker

import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import com.samsung.iug.ui.screenmirror.MirrorPanel
import java.awt.*
import javax.swing.*
import com.samsung.iug.service.Log

class IUGRuleMaker : JPanel(BorderLayout()) {

    init {
        background = Color(60, 63, 65)
        // Set background color for the main panel
        background = Color(60, 63, 65)

        // Create toolbar with title and actions at the top
        val topToolbarPanel = createTopToolbar()
        add(topToolbarPanel, BorderLayout.NORTH)

        // Main content panel
        val mainContentPanel = JPanel(BorderLayout())
        mainContentPanel.background = Color(60, 63, 65)

        // Top section with Common Info/Step Info (tab) và Mirror Screen bằng nhau, Screen Info bên phải
        val topSection = JPanel(BorderLayout())
        topSection.background = Color(60, 63, 65)

        val combinedTabPanel = createCombinedTabPanel()
        val mirrorPanel = MirrorPanel()
        val screenInfoPanel = ScreenInfoPanel()

        // Đặt cùng kích thước preferredSize cho hai panel
        combinedTabPanel.preferredSize = Dimension(400, 300)
        mirrorPanel.preferredSize = Dimension(400, 300)

        // Tạo container ngang cho tab + mirror
        val tabAndMirrorContainer = JPanel(GridLayout(1, 2, 10, 0))
        tabAndMirrorContainer.background = Color(60, 63, 65)
        tabAndMirrorContainer.add(combinedTabPanel)
        tabAndMirrorContainer.add(mirrorPanel)

        // Tạo container ngang cho tab+mirror và screen info
        val topPanelsLayout = JPanel()
        topPanelsLayout.layout = BoxLayout(topPanelsLayout, BoxLayout.X_AXIS)
        topPanelsLayout.background = Color(60, 63, 65)
        topPanelsLayout.add(tabAndMirrorContainer)
        topPanelsLayout.add(screenInfoPanel)

        // Add the horizontal layout to the top section
        topSection.add(topPanelsLayout, BorderLayout.CENTER)

        // Bottom section with Graph and Log
        val bottomSection = JPanel(BorderLayout())
        bottomSection.background = Color(60, 63, 65)

        // Create Graph and Log panels
        val graphPanelContainer = GraphPanel()
        val logPanelContainer = LogPanel()

        // Bottom panels layout with Graph and Log side by side
        val bottomPanelsLayout = JPanel()
        bottomPanelsLayout.layout = BoxLayout(bottomPanelsLayout, BoxLayout.X_AXIS)
        bottomPanelsLayout.background = Color(60, 63, 65)

        // Add graph panel (wide) and log panel (narrow) side by side
        bottomPanelsLayout.add(graphPanelContainer)
        bottomPanelsLayout.add(logPanelContainer)
        Log.i("","ggg")

        // Add the bottom panels layout to the bottom section
        bottomSection.add(bottomPanelsLayout, BorderLayout.CENTER)

        // Use a vertical split pane to divide top and bottom sections with preferred ratios
        val mainSplitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT, topSection, bottomSection)
        mainSplitPane.resizeWeight = 0.5 // Top panel gets 50% of space, bottom gets 50%
        mainSplitPane.border = null
        mainSplitPane.dividerSize = 5

        // Add the split pane to main content
        mainContentPanel.add(mainSplitPane, BorderLayout.CENTER)

        // Add main content to window
        add(mainContentPanel, BorderLayout.CENTER)

        // Set default size
        preferredSize = Dimension(1200, 700)
    }

    private fun createTopToolbar(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.background = Color(60, 63, 65)

        // Title
        val titleLabel = JLabel("IUG Rule Maker Tool")
        titleLabel.foreground = Color.WHITE
        titleLabel.border = JBUI.Borders.empty(5, 10)
        titleLabel.font = titleLabel.font.deriveFont(titleLabel.font.size + 2f)

        // Buttons
        val buttonsPanel = JPanel(FlowLayout(FlowLayout.CENTER))
        buttonsPanel.background = Color(60, 63, 65)

        val exportButton = JButton("Export")
        val importButton = JButton("Import")
        val exitButton = JButton("Exit")

        importButton.addActionListener {
//            openRuleFile()
        }

        buttonsPanel.add(exportButton)
        buttonsPanel.add(importButton)
        buttonsPanel.add(exitButton)

        // User info
        val userPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        userPanel.background = Color(60, 63, 65)

        val userLabel = JLabel("Hello, abcxyz ▼")
        userLabel.foreground = Color.WHITE
        userLabel.border = JBUI.Borders.empty(5, 10)
        userPanel.add(userLabel)

        // Add components to main panel
        panel.add(titleLabel, BorderLayout.WEST)
        panel.add(buttonsPanel, BorderLayout.CENTER)
        panel.add(userPanel, BorderLayout.EAST)

        return panel
    }

    private fun createCombinedTabPanel(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.background = Color(60, 63, 65)

        // Create tabbed pane
        val tabbedPane = JTabbedPane()
        tabbedPane.background = Color(60, 63, 65)
        tabbedPane.foreground = JBColor.WHITE

        // Create Common Info content
        val commonInfoContent = CommonInfoPanel()

        // Create Step Info content
        val stepInfoContent = StepInfoPanel()

        // Add tabs
        tabbedPane.addTab("Common Info", commonInfoContent)
        tabbedPane.addTab("Step Info", stepInfoContent)

        // Set Common Info as default tab
        tabbedPane.selectedIndex = 0

        panel.add(tabbedPane, BorderLayout.CENTER)
        panel.preferredSize = Dimension(580, 300)

        return panel
    }
}
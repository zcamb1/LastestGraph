package com.samsung.iug.ui.graph

import com.samsung.iug.ui.mirror.ScreenLayout
import java.awt.*
import javax.swing.*

class AddNodeUI(): JPanel(BorderLayout()) {
    private val cardLayout = CardLayout()
    private val contentPanel = JPanel(cardLayout)
    private val dotsPanel = JPanel()
    private val navigationPanel = JPanel()

    private val backButton = JButton("Close")
    private val nextButton = JButton("Next")
    private val dots = mutableListOf<JLabel>()

    private var currentIndex = 0
    private val totalTabs = 3

    private val screenLayout = ScreenLayout()
    private var stepDetails = StepDetailsLayout
    private var commonInfo = CommonInfoLayout

    init {
        this.apply {
            isOpaque = false
        }

        add(contentPanel, BorderLayout.CENTER)

        dotsPanel.layout = FlowLayout(FlowLayout.CENTER, 10, 5)
        for (i in 0 until totalTabs) {
            val dot = JLabel("o")
            dot.font = Font("Arial", Font.PLAIN, 20)
            dot.foreground = if (i == 0) {
                dot.text = "●"
                Color.WHITE
            } else {
                dot.text = "○"
                Color.GRAY
            }
            dots.add(dot)
            dotsPanel.add(dot)
        }

        contentPanel.add(screenLayout, "tab0")
        contentPanel.add(stepDetails, "tab1")
        contentPanel.add(commonInfo, "tab2")

        navigationPanel.layout = FlowLayout(FlowLayout.CENTER, 10, 5)

        backButton.addActionListener {showPreviousTab()}
        nextButton.addActionListener {showNextTab()}

        backButton.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        nextButton.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

        navigationPanel.add(backButton)
        navigationPanel.add(nextButton)

        val bottomLayout = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(dotsPanel)
            add(Box.createVerticalStrut(10))
            add(navigationPanel)
        }

        add(bottomLayout, BorderLayout.SOUTH)

        updateNavigationButtons()
    }

    private fun showNextTab() {
        if (currentIndex < totalTabs - 1) {
            stepDetails.fillData()
            commonInfo.fillData()
            currentIndex++
            cardLayout.show(contentPanel, "tab$currentIndex")
            updateDots()
            updateNavigationButtons()
        } else {
            val node = Node("")
            val data = stepDetails.getData()
            node.id = data["stepId"].toString()
            node.guildContent = data["guideContent"].toString()
            node.screenId = ViewNode.screenId
            node.className = ViewNode.className
            node.resourceId = ViewNode.resourceId
            node.text = ViewNode.text
            node.bounds = ViewNode.bounds
            listNode.listNode.add(node)
//            ListNodeView.repaintView()
            ViewNode.reset()
            GraphUI.closeNode()
        }
    }

    private fun showPreviousTab() {
        if (currentIndex > 0) {
            stepDetails.fillData()
            commonInfo.fillData()
            currentIndex--
            cardLayout.show(contentPanel, "tab$currentIndex")
            updateDots()
            updateNavigationButtons()
        } else {
            GraphUI.closeNode()
        }
    }

    private fun updateDots() {
        dots.forEachIndexed {index, dot ->
            dot.foreground = if (index == currentIndex) {
                dot.text = "●"
                Color.WHITE
            } else {
                dot.text = "○"
                Color.GRAY
            }
        }
    }

    private fun updateNavigationButtons() {
        backButton.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        nextButton.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        if (currentIndex == totalTabs - 1) {
            nextButton.text = "Create"
        } else {
            nextButton.text = "Next"
        }

        if (currentIndex == 0) {
            backButton.text = "Close"
        } else {
            backButton.text = "Back"
        }
    }
}

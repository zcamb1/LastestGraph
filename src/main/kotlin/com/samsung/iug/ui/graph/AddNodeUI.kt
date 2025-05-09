package com.samsung.iug.ui.graph

import com.samsung.iug.ui.ScreenLayout
import java.awt.*
import javax.swing.*

class AddNodeUI(): JPanel(BorderLayout()) {
    private val cardLayout = CardLayout()
    private val contentPanel = JPanel(cardLayout)
    private val dotsPanel = JPanel()
    private val navigationPanel = JPanel()

    private val backButton = JButton("Back")
    private val nextButton = JButton("Next")
    private val dots = mutableListOf<JLabel>()

    private var currentIndex = 0
    private val totalTabs = 3

    init {
        isOpaque = false
        add(contentPanel, BorderLayout.CENTER)

        dotsPanel.layout = FlowLayout(FlowLayout.CENTER, 10, 5)
        for (i in 0 until totalTabs) {
            val dot = JLabel(".")
            dot.font = Font("Arial", Font.PLAIN, 20)
            dot.foreground = if (i == 0) Color.BLUE else Color.GRAY
            dots.add(dot)
            dotsPanel.add(dot)
        }
        add(dotsPanel, BorderLayout.SOUTH)

        val screenLayout = ScreenLayout()
        contentPanel.add(screenLayout, "tab0")

        val tab2 = JPanel()
        contentPanel.add(tab2, "tab1")
        val tab3 = JPanel()
        contentPanel.add(tab3, "tab2")

        navigationPanel.layout = FlowLayout(FlowLayout.CENTER, 10, 5)
        backButton.addActionListener {showPreviousTab()}
        nextButton.addActionListener {showNextTab()}
        navigationPanel.add(backButton)
        navigationPanel.add(nextButton)
        add(navigationPanel, BorderLayout.NORTH)

        updateNavigationButtons()
    }

    private fun showNextTab() {
        if (currentIndex < totalTabs - 1) {
            currentIndex++
            cardLayout.show(contentPanel, "tab$currentIndex")
            updateDots()
            updateNavigationButtons()
        }
    }

    private fun showPreviousTab() {
        if (currentIndex > 0) {
            currentIndex--
            cardLayout.show(contentPanel, "tab$currentIndex")
            updateDots()
            updateNavigationButtons()
        }
    }

    private fun updateDots() {
        dots.forEachIndexed {index, dot ->
            dot.foreground = if (index == currentIndex) Color.BLUE else Color.GRAY
        }
    }

    private fun updateNavigationButtons() {
        backButton.isEnabled = currentIndex > 0
        if (currentIndex == totalTabs - 1) {
            nextButton.text = "Create"
        } else {
            nextButton.text = "Next"
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D

        g2.color = Color.decode("#343541")
        g2.fillRoundRect(0, 0, width, height, 25, 25)
    }
}

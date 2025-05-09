package com.samsung.iug.ui.graph

import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.JPanel
import com.intellij.ui.util.preferredHeight
import com.intellij.ui.util.preferredWidth
import com.samsung.iug.ui.iug.OptionBar
import com.samsung.iug.ui.log.LogPanel
import java.awt.*
import javax.swing.*

object GraphUI: JPanel(BorderLayout()) {
    private val graphPanel = GraphPanel // Đây là phần UI của Graph
    private val addNodePanel = AddNodeUI()
    private val optionBar = OptionBar()
    private val logBar = LogPanel().apply {
        background = Color.DARK_GRAY
        preferredSize = Dimension(600, 200)
        maximumSize = preferredSize
    }

    private val layeredPane = JLayeredPane().apply {
        layout = null
        add(graphPanel)
        add(addNodePanel)
        add(optionBar)
        add(logBar)
        setLayer(graphPanel, JLayeredPane.DEFAULT_LAYER)
        setLayer(addNodePanel, JLayeredPane.POPUP_LAYER)
        setLayer(optionBar, JLayeredPane.POPUP_LAYER)
        setLayer(logBar, JLayeredPane.POPUP_LAYER)
    }

    init {
        this.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        this.isOpaque = false

        addNodePanel.isVisible = false
        optionBar.isVisible = false
        logBar.isVisible = false

        add(layeredPane, BorderLayout.CENTER)
        graphPanel.setBounds(100, 50, 500, 150)
        graphPanel.repaint()
        graphPanel.revalidate()
        layeredPane.repaint()
        layeredPane.revalidate()

//        val listNodeView = ListNodeView
//        listNodeView.setBounds(150, 400, 500, 800)
//        listNodeView.repaint()
//        listNodeView.revalidate()
//        add(listNodeView, BorderLayout.SOUTH)

        repaint()
        revalidate()
    }

    fun addNode() {
        val centerX = layeredPane.width / 2 - 100
        val centerY = 50
        addNodePanel.setBounds(centerX, centerY, addNodePanel.preferredWidth, addNodePanel.preferredHeight)
        addNodePanel.isVisible = true
        addNodePanel.repaint()
        addNodePanel.revalidate()
        layeredPane.repaint()
        layeredPane.revalidate()
    }

    fun closeNode() {
        addNodePanel.isVisible = false
    }

    fun repaintLayered() {
        val centerX = layeredPane.width / 2 - 100
        val centerY = 50
        addNodePanel.setBounds(centerX, centerY, addNodePanel.preferredWidth, addNodePanel.preferredHeight)
        addNodePanel.repaint()
        addNodePanel.revalidate()
        layeredPane.repaint()
        layeredPane.revalidate()
    }


    fun showOptionPanel(isShow: Boolean) {
        if (isShow) {
            val x = layeredPane.width - optionBar.preferredWidth - 80
            val y = layeredPane.height / 2
            optionBar.setBounds(x, y, optionBar.preferredWidth, optionBar.preferredHeight)
            optionBar.isVisible = true
            optionBar.repaint()
            optionBar.revalidate()
            layeredPane.repaint()
            layeredPane.revalidate()
        } else {
            optionBar.isVisible = false
        }
    }

    fun showLogBarPanel(isShow: Boolean) {
        if (isShow) {
            val x = layeredPane.width - logBar.preferredWidth - 80
            val y = layeredPane.height / 2
            logBar.setBounds(x, y, logBar.preferredWidth, logBar.preferredHeight)
            logBar.isVisible = true
            logBar.repaint()
            logBar.revalidate()
            layeredPane.repaint()
            layeredPane.revalidate()
        } else {
            logBar.isVisible = false
        }
    }
}
package com.samsung.iug.ui.graph

import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.JPanel
import com.intellij.ui.util.preferredHeight
import com.intellij.ui.util.preferredWidth
import java.awt.Dimension
import javax.swing.*

object GraphUI: JPanel(BorderLayout()) {
    private val graphPanel = GraphPanel // Đây là phần UI của Graph
    private val addNodePanel = AddNodeUI()
    private val layeredPane = JLayeredPane().apply {
        layout = null
        add(graphPanel)
        add(addNodePanel)
        setLayer(graphPanel, JLayeredPane.DEFAULT_LAYER)
        setLayer(addNodePanel, JLayeredPane.POPUP_LAYER)
    }

    init {
        this.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        this.isOpaque = false

        addNodePanel.isVisible = false

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
}
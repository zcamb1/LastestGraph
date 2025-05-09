package com.samsung.iug.ui.graph

import java.awt.*
import javax.swing.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

object GraphPanel : JPanel() {
    private val node = UtteranceNodeView()
    private val buttons = ButtonPanel()

    init {
        layout = null
        isOpaque = false
//        preferredSize = Dimension(350, 140)
        // Set layout bounds
        node.setBounds(0, 0, 280, 110)
        buttons.setBounds(0, 110, 280, 30)
        buttons.isVisible = false

        // Add main parts
        add(node)
        add(buttons)

        // === Show/hide hover buttons logic ===
        addMouseListener(object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent?) {
                buttons.isVisible = true
            }

            override fun mouseExited(e: MouseEvent?) {
                val p = MouseInfo.getPointerInfo().location
                SwingUtilities.convertPointFromScreen(p, this@GraphPanel)
                if (!node.bounds.contains(p) && !buttons.bounds.contains(p)) {
                    buttons.isVisible = false
                }
            }
        })

        // === Open edit dialog on click ===
        buttons.editButton.addActionListener {
            EditUtteranceDialog(this, node).isVisible = true
        }
    }
}
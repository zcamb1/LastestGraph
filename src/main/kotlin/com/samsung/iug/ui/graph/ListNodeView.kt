package com.samsung.iug.ui.graph

import com.intellij.ui.util.preferredHeight
import com.intellij.ui.util.preferredWidth
import java.awt.FlowLayout
import javax.swing.Box
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

object ListNodeView: JPanel(FlowLayout(FlowLayout.CENTER)) {
    init {
        this.apply {
            preferredHeight = 150
            preferredWidth = 500
        }
        add(JLabel("List Node: "))
        for (node in listNode.listNode) {
            if (node.id != "User Query") {
                add(JButton(node.id))
                add(Box.createHorizontalStrut(10))
            }
        }
    }

    fun repaintView() {
        repaint()
        revalidate()
    }
}
package com.samsung.iug.ui.mirror

import java.awt.Color
import java.awt.Component
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

class ScreenLayout() : JPanel() {
    init {
        this.apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.CENTER_ALIGNMENT
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            isOpaque = false
        }

        val screen = ScreenMirror()

        val label1 = JLabel().apply {
            text = "<html><div style='width:100%; text-align: center;'>Add Step: Select a target</div></html>"
            font = Font("Arial", Font.BOLD, 14)
            foreground = Color.WHITE
            alignmentX = Component.CENTER_ALIGNMENT
            horizontalAlignment = JLabel.CENTER
        }

        val label2 = JLabel().apply {
            text = "<html><div style='width:100%; text-align: center;'>Select a UI target that Bixby will point to from the <br> highlighted targets</div></html>"
            font = Font("Arial", Font.BOLD , 12)
            foreground = Color.GRAY
            alignmentX = Component.CENTER_ALIGNMENT
            horizontalAlignment = JLabel.CENTER
        }

        add(screen)
        add(Box.createVerticalStrut(10))
        add(label1)
        add(Box.createVerticalStrut(10))
        add(label2)
    }
}
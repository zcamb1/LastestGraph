package com.samsung.iug.ui.preview

import com.samsung.iug.utils.GetDevice
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

object PreviewPanel {
    val panel = JPanel()

    init {
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = EmptyBorder(10, 10, 10, 10)

        // để tránh vẽ lại không cần thiết
        panel.isOpaque = false
        panel.isDoubleBuffered = true

        // Label "Preview"
        val label = JLabel("Preview")
        label.font = Font("Arial", Font.BOLD, 16)
        label.alignmentX = Component.LEFT_ALIGNMENT
        panel.add(label)

        // GetDevice Panel
        GetDevice.panel.alignmentX = Component.LEFT_ALIGNMENT
        panel.add(Box.createVerticalStrut(10))
        panel.add(GetDevice.panel)

        // ScreenMirror Panel
        ScreenMirror.panel.preferredSize = Dimension(400, 800)
        ScreenMirror.panel.alignmentX = Component.LEFT_ALIGNMENT
        panel.add(Box.createVerticalStrut(10))
        panel.add(ScreenMirror.panel)

        // Custom view with 2 circles
//        val circlePanel = object : JPanel() {
//            override fun paintComponent(g: Graphics) {
//                super.paintComponent(g)
//                val g2 = g as Graphics2D
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
//
//                val d = size
//                val radius = 30
//
//                // Circle 1 - blue filled
//                g2.color = Color.BLUE
//                g2.fillOval(20, 20, radius * 2, radius * 2)
//
//                // Circle 2 - outline
//                g2.color = Color.GRAY
//                g2.drawOval(80, 20, radius * 2, radius * 2)
//            }
//        }
//        circlePanel.preferredSize = Dimension(200, 80)
//        circlePanel.alignmentX = Component.LEFT_ALIGNMENT
//        panel.add(Box.createVerticalStrut(10))
//        panel.add(circlePanel)
    }
}
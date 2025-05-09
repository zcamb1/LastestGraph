package com.samsung.iug.ui.iug

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager
import java.awt.*
import javax.swing.JComponent
import javax.swing.JPanel

fun IUGMaker(project: Project) {
    val frame = WindowManager.getInstance().getFrame(project) ?: return
    val glassPane = frame.rootPane.glassPane as? JComponent ?: return

    val screenWidth = frame.width
    val screenHeight = frame.height

    val regionWidth = (screenWidth * 0.9).toInt()
    val regionHeight = (screenHeight * 0.9).toInt()
    val regionX = (screenWidth - regionWidth) / 2
    val regionY = (screenHeight - regionHeight) / 2

    val overlay = object : JPanel() {
        init {
            isOpaque = false
            bounds = Rectangle(0, 0, screenWidth, screenHeight)

            val panel = MainUI {
                frame.dispose()
            }
            panel.setBounds(regionX, regionY, regionWidth, regionHeight)
            panel.isOpaque = false

            this.layout = null
            this.add(panel)
        }

        override fun paintComponent(g: Graphics?) {
            val g2 = g as Graphics2D
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            g2.color = Color(100, 100, 100, 100)
            g2.fillRect(0, 0, width, height)

            val rect = Rectangle(regionX, regionY, regionWidth, regionHeight)
            g2.setClip(rect)

            g2.color = Color.BLACK
            g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 20, 20)

            val dotSpacing = 20
            for (y in rect.y + 20 until rect.y + rect.height - 20 step dotSpacing) {
                for (x in rect.x + 20 until rect.x + rect.width - 20 step dotSpacing) {
                    g2.color = if ((x + y) % 40 == 0) Color.LIGHT_GRAY else Color.GRAY
                    g2.fillOval(x, y, 2, 2)
                }
            }

            g2.setClip(null)
        }
    }

    glassPane.add(overlay)
    glassPane.repaint()
}

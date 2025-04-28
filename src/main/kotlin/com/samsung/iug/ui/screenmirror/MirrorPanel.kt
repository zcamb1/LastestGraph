package com.samsung.iug.ui.screenmirror

import com.samsung.iug.ui.rulemaker.CommonInfoPanel
import com.samsung.iug.ui.rulemaker.LayoutInspectorPanel
import com.samsung.iug.ui.rulemaker.ScreenInfoPanel
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JPanel

class MirrorPanel : JPanel(BorderLayout()) {
    private var commonInfoContent = CommonInfoPanel()

    init {
        val screenInfoPanel = ScreenInfoPanel {
            commonInfoContent.updatePackageName(it)
        }

        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        )

        add(GetDevice.panel, BorderLayout.NORTH)
        add(ScreenMirror.panel, BorderLayout.WEST)

        val infoPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
        }
        add(infoPanel, BorderLayout.EAST)
        infoPanel.add(screenInfoPanel)
        infoPanel.add(LayoutInspectorPanel())
    }
}
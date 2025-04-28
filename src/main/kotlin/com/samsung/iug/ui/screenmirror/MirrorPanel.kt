package com.samsung.iug.ui.screenmirror

import com.intellij.ui.JBColor
import com.samsung.iug.ui.rulemaker.CommonInfoPanel
import com.samsung.iug.ui.rulemaker.ScreenInfoPanel
import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.border.TitledBorder

class MirrorPanel: JPanel(BorderLayout()) {
    private var commonInfoContent = CommonInfoPanel()

    init {
        border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(JBColor.GRAY),
            "Mirror screen",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            null,
            JBColor.WHITE
        )

        val screenInfoPanel = ScreenInfoPanel {
            commonInfoContent.updatePackageName(it)
        }

        border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(JBColor.GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        )

        add(GetDevice.panel, BorderLayout.NORTH)
        add(ScreenMirror.panel, BorderLayout.WEST)
        add(screenInfoPanel, BorderLayout.EAST)
    }
}
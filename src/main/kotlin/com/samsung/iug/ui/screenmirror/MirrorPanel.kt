package com.samsung.iug.ui.screenmirror

import com.intellij.ui.util.minimumWidth
import com.samsung.iug.ui.rulemaker.CommonInfoPanel
import com.samsung.iug.ui.layoutinspector.LayoutInspectorPanel
import com.samsung.iug.ui.rulemaker.ScreenInfoPanel
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JSplitPane

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

        val screenMirror = ScreenMirror.panel.apply {
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
            minimumWidth = 400
        }

        val infoPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
            add(screenInfoPanel)
//            add(LayoutInspectorPanel())
            val layoutInspectorPanel = LayoutInspectorPanel()
            ScreenMirror.layoutInspectorPanel = layoutInspectorPanel
            add(layoutInspectorPanel)
        }

        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, screenMirror, infoPanel).apply {
            resizeWeight = 0.5
            border = BorderFactory.createEmptyBorder()
            dividerSize = 5
        }

        add(splitPane)
    }
}
package com.samsung.iug.ui.iug

import com.intellij.util.ui.JBUI
import com.samsung.iug.graph.listNode
import com.samsung.iug.ui.custom.CircleIconButton
import com.samsung.iug.ui.custom.RoundedPanel
import com.samsung.iug.ui.graph.GraphUI
import com.samsung.iug.ui.preview.PreviewPanel
import com.samsung.iug.utils.FileStorage
import com.samsung.iug.utils.JsonHelper
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

class MainUI(private val screenWidth: Int, private val screenHeight: Int, private val onClose: () -> Unit) : JPanel() {

    private var previewDialog: JDialog? = null
    private var isPreviewVisible = false
    private var isShowOptionBar = false
    private var isShowLogBar = false

    private val graphPanel = GraphUI

    init {
        preferredSize = Dimension(Toolkit.getDefaultToolkit().screenSize)
        border = EmptyBorder((screenHeight * 0.05).toInt(), 0, (screenHeight * 0.05).toInt(), 0)

        val topBar = createTopBar()
        val mainPanel = createMainLayout()

        FileStorage.currentFile?.let {
            val data = JsonHelper.parseRuleFromJson(it)
            listNode.listNode.addAll(data)
        }
        add(topBar)
        add(mainPanel)
    }

    private fun createTopBar(): JPanel {
        return JPanel(BorderLayout()).apply {
            val titleLabel = JLabel("Adjust Audio Levels.IUG").apply {
                foreground = Color.WHITE
                border = JBUI.Borders.empty(5, 10)
                font = font.deriveFont(18f)
            }

            val scaleButton = createWindowButton("/images/icon_expand.png")
            val closeButton = createWindowButton("/images/icon_close.png").apply {
                addActionListener {
                    onClose()
                }
            }

            val windowPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 10, 5)).apply {
                add(scaleButton)
                add(closeButton)
                isOpaque = false
            }

            add(titleLabel, BorderLayout.WEST)
            add(windowPanel, BorderLayout.EAST)
            isOpaque = false
            preferredSize = Dimension((screenWidth * 0.9).toInt(), 50)
        }
    }

    private fun createMainLayout(): JPanel {
        return JPanel().apply {
            preferredSize = Dimension(screenWidth, screenHeight)
            maximumSize = preferredSize
            layout = OverlayLayout(this)
            isOpaque = false

            val toolBars = createToolBars()

            add(toolBars)
            add(graphPanel)
        }
    }

    private fun createToolBars(): JPanel {
        val toolBar = JPanel().apply {
            preferredSize = Dimension(60, 450)
            maximumSize = preferredSize
            isOpaque = false
            alignmentX = 1.0f
            alignmentY = 0.4f

            val buttonAdd = createToolBarButton("/images/icon_add.png")
            val buttonZoomOut = createToolBarButton("/images/icon_zoom_out.png")
            val buttonZoomIn = createToolBarButton("/images/icon_zoom_in.png")
            val buttonPan = createToolBarButton("/images/icon_pan.png")
            val buttonConsole = createToolBarCircleButton("/images/icon_terminal.png")
            val buttonMirror = createToolBarCircleButton("/images/icon_flip.png")
            val buttonMore = createToolBarCircleButton("/images/icon_more.png")
            val buttonPreview = createToolBarCircleButton("/images/icon_play.png")

            buttonPreview.addActionListener {
                togglePreview(buttonPreview)
            }
            buttonConsole.addActionListener {
                isShowLogBar = !isShowLogBar
                graphPanel.showLogBarPanel(isShowLogBar)
                buttonConsole.refreshColorSelected(isShowLogBar)
            }
            buttonMore.addActionListener {
                isShowOptionBar = !isShowOptionBar
                graphPanel.showOptionPanel(isShowOptionBar)
                buttonMore.refreshColorSelected(isShowOptionBar)
            }
            buttonAdd.addActionListener {
                graphPanel.addNode()
            }

            val panelGroup = RoundedPanel(60, Color.DARK_GRAY).apply {
                preferredSize = Dimension(60, 200)
                maximumSize = preferredSize
                add(buttonAdd)
                add(buttonZoomOut)
                add(buttonZoomIn)
                add(buttonPan)
            }

            add(panelGroup)
            add(buttonPreview)
            add(buttonMirror)
            add(buttonConsole)
            add(buttonMore)
        }

        return toolBar
    }

    private fun togglePreview(button: CircleIconButton) {
        if (isPreviewVisible) {
            previewDialog?.dispose()
            isPreviewVisible = false
            button.refreshColorSelected(false)
        } else {
            previewDialog =
                JDialog(SwingUtilities.getWindowAncestor(this), "", Dialog.ModalityType.MODELESS).apply {
                    contentPane = PreviewPanel.panel
                    defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE
                    pack()
                    setLocationRelativeTo(null)
                    isVisible = true

                    addWindowListener(object : java.awt.event.WindowAdapter() {
                        override fun windowClosed(e: java.awt.event.WindowEvent?) {
                            isPreviewVisible = false
                            button.refreshColorSelected(false)
                        }
                    })
                }
            isPreviewVisible = true
            button.refreshColorSelected(true)
        }
    }

    private fun createWindowButton(pathIcon: String): JButton {
        return CircleIconButton(
            22,
            pathIcon,
            Color.DARK_GRAY,
            Color.DARK_GRAY,
            Color.GRAY,
            Color.GRAY,
            1f
        )
    }

    private fun createToolBarCircleButton(pathIcon: String): CircleIconButton {
        return CircleIconButton(
            35,
            pathIcon,
            Color.DARK_GRAY,
            Color(0x36498C),
            Color.GRAY,
            Color(0x1E90FF),
            2f
        )
    }

    private fun createToolBarButton(pathIcon: String): JButton {
        return CircleIconButton(
            35,
            pathIcon,
            Color.DARK_GRAY,
            Color.DARK_GRAY,
            Color.DARK_GRAY,
            Color.DARK_GRAY,
            2f
        )
    }
}
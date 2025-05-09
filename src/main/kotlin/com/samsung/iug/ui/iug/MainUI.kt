package com.samsung.iug.ui.iug

import com.intellij.util.ui.JBUI
import com.samsung.iug.ui.custom.CircleIconButton
import com.samsung.iug.ui.custom.RoundedPanel
import com.samsung.iug.ui.graph.GraphUI
import com.samsung.iug.ui.preview.PreviewPanel
import com.samsung.iug.utils.ImageHelper
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

class MainUI(private val screenWidth: Int, private val screenHeight: Int, private val onClose: () -> Unit) : JPanel() {

    private lateinit var buttonPreview: JButton
    private var previewDialog: JDialog? = null
    private var isPreviewVisible = false
    private val graphPanel = GraphUI()

    init {
        preferredSize = Dimension(Toolkit.getDefaultToolkit().screenSize)
        border = EmptyBorder((screenHeight * 0.05).toInt(), 0, (screenHeight * 0.05).toInt(), 0)

        val topBar = createTopBar()
        val mainPanel = createMainLayout()

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

            val scaleButton = createWindowButton("/images/icon_expand.png").apply {
            }
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
        return JPanel().apply {
            preferredSize = Dimension(60, 450)
            maximumSize = preferredSize
            isOpaque = false
            alignmentX = 1.0f
            alignmentY = 0.3f

            val panelGroup = RoundedPanel(60, Color.DARK_GRAY).apply {
                preferredSize = Dimension(60, 200)
                maximumSize = preferredSize

                val buttonAdd = createToolBarButton("/images/icon_add.png")
                val buttonZoomOut = createToolBarButton("/images/icon_zoom_out.png")
                val buttonZoomIn = createToolBarButton("/images/icon_zoom_in.png")
                val buttonPan = createToolBarButton("/images/icon_pan.png")

                buttonAdd.addActionListener {
                    graphPanel.addNode()
                }

                add(buttonAdd)
                add(buttonZoomOut)
                add(buttonZoomIn)
                add(buttonPan)
            }

            buttonPreview = createToolBarCircleButton("/images/icon_play.png", highlighted = false).apply {
                addActionListener {
                    togglePreview()
                }
            }
            val buttonMirror = createToolBarCircleButton("/images/icon_flip.png")
            val buttonConsole = createToolBarCircleButton("/images/icon_terminal.png")
            val buttonMore = createToolBarCircleButton("/images/icon_more.png")

            add(panelGroup)
            add(buttonPreview)
            add(buttonMirror)
            add(buttonConsole)
            add(buttonMore)
        }
    }

    private fun togglePreview() {
        if (isPreviewVisible) {
            previewDialog?.dispose()
            isPreviewVisible = false
        } else {
            previewDialog = JDialog(SwingUtilities.getWindowAncestor(this), "", Dialog.ModalityType.MODELESS).apply {
                contentPane = PreviewPanel.panel
                defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE
                pack()
                setLocationRelativeTo(null)
                isVisible = true

                addWindowListener(object : java.awt.event.WindowAdapter() {
                    override fun windowClosed(e: java.awt.event.WindowEvent?) {
                        isPreviewVisible = false
                        refreshPreviewButton()
                    }
                })
            }
            isPreviewVisible = true
        }
        refreshPreviewButton()
    }

    private fun refreshPreviewButton() {
        updateCircleButtonHighlight(buttonPreview, "/images/icon_play.png", isPreviewVisible)
    }

    private fun createWindowButton(pathIcon: String): JButton {
        return createCircleButton(pathIcon, 22, Color.WHITE, 1f, Color.DARK_GRAY)
    }

    private fun createToolBarCircleButton(pathIcon: String, highlighted: Boolean = false): JButton {
        val bgColor = if (highlighted) Color(0x36498C) else Color.DARK_GRAY
        val borderColor = if (highlighted) Color(0x1E90FF) else Color.GRAY
        return createCircleButton(pathIcon, 35, borderColor, 2f, bgColor)
    }

    private fun createToolBarButton(pathIcon: String, size: Int = 30): JButton {
        val iconUrl = javaClass.getResource(pathIcon)
        val rawIcon = ImageIcon(iconUrl)
        val newIcon = ImageHelper.recolorImageIcon(rawIcon, Color.WHITE)
        val icon = ImageHelper.resizeIcon(newIcon, size, size)
        return JButton(icon).apply {
            isBorderPainted = false
            isContentAreaFilled = false
            isFocusPainted = false
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            preferredSize = Dimension(size + 10, size + 10)
            maximumSize = preferredSize
        }
    }

    private fun createCircleButton(
        pathIcon: String,
        size: Int,
        borderColor: Color,
        strokeWidth: Float,
        backgroundColor: Color
    ): JButton {
        val iconUrl = javaClass.getResource(pathIcon)
        val rawIcon = ImageIcon(iconUrl)
        val newIcon = ImageHelper.recolorImageIcon(rawIcon, Color.WHITE)
        val iconImage = ImageHelper.resizeIcon(newIcon, size / 3 * 2, size / 3 * 2)

        val circleIcon = CircleIconButton(size, backgroundColor, borderColor, strokeWidth, iconImage)
        return JButton(circleIcon).apply {
            isFocusPainted = false
            isContentAreaFilled = false
            isBorderPainted = false
            preferredSize = Dimension(size + 5, size + 5)
            maximumSize = preferredSize
        }
    }

    private fun updateCircleButtonHighlight(
        button: JButton,
        pathIcon: String,
        highlighted: Boolean,
        size: Int = 35
    ) {
        val bgColor = if (highlighted) Color(0x36498C) else Color.DARK_GRAY
        val borderColor = if (highlighted) Color(0x1E90FF) else Color.GRAY

        val iconUrl = javaClass.getResource(pathIcon)
        val rawIcon = ImageIcon(iconUrl)
        val recoloredIcon = ImageHelper.recolorImageIcon(rawIcon, Color.WHITE)
        val iconImage = ImageHelper.resizeIcon(recoloredIcon, size / 3 * 2, size / 3 * 2)

        val circleIcon = CircleIconButton(size, bgColor, borderColor, 2f, iconImage)
        button.icon = circleIcon
        button.repaint()
    }
}
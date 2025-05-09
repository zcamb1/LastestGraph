package com.samsung.iug.ui.login

import java.awt.*
import java.awt.geom.RoundRectangle2D
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.JButton

class LoginDialog : JDialog() {

    private val marginTop = 10
    private val marginBottom = 20
    private val marginHorizontal = 20

    private val spacingBetweenLogoAndTitle = 60
    private val spacingBetweenTitleAndSubtitle = 10
    private val spacingBetweenSubtitleAndButton = 40
    private val spacingAfterButton = 10

    private val dynamicPanel = Box.createVerticalBox()

    init {
        title = "Login"
        isUndecorated = true
        setSize(400, 500)
        setLocationRelativeTo(null)
        isModal = true
        shape = RoundRectangle2D.Double(0.0, 0.0, width.toDouble(), height.toDouble(), 40.0, 40.0)
        contentPane = buildUI()
    }

    private fun buildUI(): JPanel {
        val wrapper = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val g2 = g as Graphics2D
                val gradient = GradientPaint(
                    0f, 0f, Color(90, 38, 140),
                    0f, height.toFloat(), Color(25, 25, 25)
                )
                g2.paint = gradient
                g2.fillRect(0, 0, width, height)
            }
        }

        wrapper.layout = BoxLayout(wrapper, BoxLayout.Y_AXIS)
        wrapper.border = EmptyBorder(marginTop, marginHorizontal, marginBottom, marginHorizontal)

        val logo = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

                val size = 80
                val x = (width - size) / 2
                val y = 10

                val thickness = 21f
                g2.stroke = BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
                g2.color = Color(255, 255, 255)
                g2.drawArc(x, y, size, size, 30, 295)

                val dotSize = 33
                g2.fillOval(x + size / 2 - dotSize / 2, y + size / 2 - dotSize / 2, dotSize, dotSize)
            }
        }

        logo.preferredSize = Dimension(100, 100)
        logo.maximumSize = Dimension(100, 100)
        logo.isOpaque = false
        logo.alignmentX = Component.CENTER_ALIGNMENT

        val title = JLabel("INTERACTIVE USER GUIDE").apply {
            foreground = Color(150, 150, 255)
            font = Font("Arial", Font.BOLD, 24)
            alignmentX = Component.CENTER_ALIGNMENT
            border = EmptyBorder(spacingBetweenLogoAndTitle, 0, spacingBetweenTitleAndSubtitle, 0)
        }

        val subtitle = JLabel("GUIDE CREATION PLUGIN").apply {
            foreground = Color.WHITE
            font = Font("Arial", Font.PLAIN, 18)
            alignmentX = Component.CENTER_ALIGNMENT
            border = EmptyBorder(0, 0, spacingBetweenSubtitleAndButton, 0)
        }

        val ssoButton = PurpleButton("Log In with SSO").apply {
            alignmentX = Component.CENTER_ALIGNMENT
            addActionListener {
                dispose()
                GuideMainDialog().isVisible = true
//    can not run on SRV
//                WebViewDialog().isVisible = true
            }
        }

        val orLabel = JLabel("or Login with Account").apply {
            foreground = Color(200, 200, 200)
            font = Font("Arial", Font.PLAIN, 14)
            alignmentX = Component.CENTER_ALIGNMENT
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            border = EmptyBorder(15, 0, 5, 0)
        }

        orLabel.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                dispose()
                ManualLoginDialog().isVisible = true
            }
        })

        dynamicPanel.alignmentX = Component.CENTER_ALIGNMENT
        wrapper.add(createTopBar(this))
        wrapper.add(logo)
        wrapper.add(title)
        wrapper.add(subtitle)
        wrapper.add(ssoButton)
        wrapper.add(orLabel)
        wrapper.add(dynamicPanel)
        wrapper.add(Box.createRigidArea(Dimension(0, spacingAfterButton)))

        return wrapper
    }
}


class PurpleButton(text: String) : JButton(text) {
    init {
        isFocusPainted = false
        isContentAreaFilled = false
        isBorderPainted = false
        foreground = Color.WHITE
        font = Font("Arial", Font.BOLD,18)
        preferredSize = Dimension(240, 50)
        maximumSize = Dimension(240, 50)
        minimumSize = Dimension(240, 50)
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        g2.color = Color(128, 0, 255)
        g2.fillRoundRect(0, 0, width, height, 20, 20)

        super.paintComponent(g)
    }

    override fun paintBorder(g: Graphics) {

    }
}

fun createTopBar(parent: JDialog, showBack: Boolean = false): JPanel {
    val topBar = JPanel().apply {
        layout = BorderLayout()
        isOpaque = false
        maximumSize = Dimension(400, 40)
        preferredSize = Dimension(400, 40)
        border = EmptyBorder(5, 0, 5, 0)
    }

    if (showBack) {
        val backBtn = JButton("←").apply {
            isFocusPainted = false
            isContentAreaFilled = false
            border = EmptyBorder(0, 10, 0, 0)
            foreground = Color.BLACK
            font = Font("Arial", Font.BOLD, 18)
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            horizontalAlignment = SwingConstants.LEFT
            addActionListener {
                parent.dispose()
                LoginDialog().isVisible = true
            }
        }
        topBar.add(backBtn, BorderLayout.WEST)
    }

    val closeBtn = JButton("x").apply {
        isFocusPainted = false
        isContentAreaFilled = false
        border = EmptyBorder(0, 0, 0, 10)
        foreground = Color.BLACK
        font = Font("Arial", Font.BOLD, 18)
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        horizontalAlignment = SwingConstants.RIGHT
        addActionListener { parent.dispose() }
    }

    topBar.add(closeBtn, BorderLayout.EAST)
    return topBar
}

package com.samsung.iug.ui.login

//    can not run on SRV
//import javafx.application.Platform
//import javafx.embed.swing.JFXPanel
//import javafx.scene.Scene
//import javafx.scene.layout.StackPane
//import javafx.scene.web.WebEngine
//import javafx.scene.web.WebView
import com.samsung.iug.ui.login.createTopBar
import com.samsung.iug.ui.login.GuideMainDialog
import java.awt.*
import java.awt.geom.RoundRectangle2D
import javax.swing.*
import javax.swing.border.EmptyBorder
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import javax.swing.border.MatteBorder

//    can not run on SRV
//class WebViewDialog : JDialog() {
//    private var jfxPanel: JFXPanel? = null
//    private var webEngine: WebEngine? = null
//
//    private val LOGIN_URL = "https://samsungu.udemy.com"
//    private val SUCCESS_URL = "https://samsungu.udemy.com/organization/home"
//
//    init {
//        isUndecorated = true
//        isModal = true
//        setSize(400, 500)
//        setLocationRelativeTo(null)
//
//        shape = RoundRectangle2D.Double(0.0, 0.0, width.toDouble(), height.toDouble(), 40.0, 40.0)
//
//        jfxPanel = JFXPanel()
//        val mainPanel = JPanel()
//        mainPanel.layout = BoxLayout(mainPanel, BoxLayout.Y_AXIS)
//        mainPanel.add(createTopBar(this, showBack = true))
//
//        mainPanel.add(jfxPanel)
//        contentPane = mainPanel
//
//        Platform.runLater {
//            val webView = WebView().apply {
//                isContextMenuEnabled = false
//            }
//
//            webView.engine.apply {
//                webEngine = this
//                isJavaScriptEnabled = true
//                load(LOGIN_URL)
//
//                locationProperty().addListener { _, _, newUrl ->
//                    println("🔗 Đang truy cập: $newUrl")
//                    if (newUrl.startsWith(SUCCESS_URL)) {
//                        Platform.runLater {
//                            SwingUtilities.invokeLater {
//                                dispose()
//                                GuideMainDialog().isVisible = true
//                            }
//                        }
//                    }
//                }
//            }
//            webView.childrenUnmodifiable.forEach {
//                if (it is javafx.scene.control.ScrollPane) {
//                    it.hbarPolicy = javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER
//                    it.vbarPolicy = javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER
//                }
//            }
//            jfxPanel?.scene = Scene(StackPane(webView))
//        }
//    }
//}

class ManualLoginDialog : JDialog() {
    init {
        title = "Login with account"
        isUndecorated = true
        isModal = true
        setSize(400, 500)
        setLocationRelativeTo(null)
        shape = RoundRectangle2D.Double(0.0, 0.0, width.toDouble(), height.toDouble(), 40.0, 40.0)

        val backgroundPanel = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val g2 = g as Graphics2D
                val gradient = GradientPaint(0f, 0f, Color(200, 200, 200), 0f, height.toFloat(), Color(150, 220, 220))
                g2.paint = gradient
                g2.fillRect(0, 0, width, height)
            }
        }
        backgroundPanel.layout = BorderLayout()

        val topBar = createTopBar(this, showBack = true)
        backgroundPanel.add(topBar, BorderLayout.NORTH)

        val contentPanel = JPanel()
        contentPanel.layout = BoxLayout(contentPanel, BoxLayout.Y_AXIS)
        contentPanel.isOpaque = false
        contentPanel.border = EmptyBorder(20, 40, 40, 40)

        val titleLabel = JLabel("Login with account").apply {
            alignmentX = Component.CENTER_ALIGNMENT
            font = Font("Arial", Font.BOLD, 24)
            foreground = Color(30, 30, 30)
        }

        val titlePanel = JPanel()
        titlePanel.layout = BoxLayout(titlePanel, BoxLayout.Y_AXIS)
        titlePanel.isOpaque = false
        titlePanel.alignmentX = Component.CENTER_ALIGNMENT

        titlePanel.add(titleLabel)

        val underline = JPanel().apply {
            maximumSize = Dimension(180, 2)
            preferredSize = Dimension(180, 2)
            minimumSize = Dimension(180, 2)
            background = Color.BLACK
            alignmentX = Component.CENTER_ALIGNMENT
        }
        titlePanel.add(Box.createRigidArea(Dimension(0, 6)))
        titlePanel.add(underline)
        titlePanel.add(Box.createRigidArea(Dimension(0, 50)))


        val userField = RoundedTextField("ID")
        val passField = RoundedPasswordField()


        val loginButton = object : JButton("Login") {
            init {
                alignmentX = Component.CENTER_ALIGNMENT
                foreground = Color.WHITE
                font = Font("Arial", Font.BOLD, 16)
                preferredSize = Dimension(300, 45)
                maximumSize = preferredSize
                minimumSize = preferredSize
                isFocusPainted = false
                isBorderPainted = false
                isContentAreaFilled = false
            }

            override fun paintComponent(g: Graphics) {
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                val gradient = GradientPaint(0f, 0f, Color(120, 80, 160), width.toFloat(), height.toFloat(), Color(180, 120, 255))
                g2.paint = gradient
                g2.fillRoundRect(0, 0, width, height, 20, 20)
                super.paintComponent(g)
            }
        }.apply {
            addActionListener {
                val user = userField.text
                val pass = String(passField.password)
                if (user != "ID" && pass != "Password" && user.isNotBlank() && pass.isNotBlank()) {
                    if (user == "admin" && pass == "admin") {
                        GuideMainDialog().isVisible = true
                        dispose()
                    } else {
                        JOptionPane.showMessageDialog(this@ManualLoginDialog, "Wrong account.")
                    }
                } else {
                    JOptionPane.showMessageDialog(this@ManualLoginDialog, "Please enter ID and password.")
                }
            }
        }

        contentPanel.add(Box.createVerticalGlue())
        contentPanel.add(titlePanel)
        contentPanel.add(Box.createRigidArea(Dimension(0, 20)))
        contentPanel.add(userField)
        contentPanel.add(Box.createRigidArea(Dimension(0, 15)))
        contentPanel.add(passField)
        contentPanel.add(Box.createRigidArea(Dimension(0, 25)))
        contentPanel.add(loginButton)
        contentPanel.add(Box.createVerticalGlue())

        backgroundPanel.add(contentPanel, BorderLayout.CENTER)
        contentPane = backgroundPanel
    }
}

class RoundedTextField(placeholder: String) : JTextField(placeholder) {
    init {
        font = Font("Arial", Font.PLAIN, 16)
        foreground = Color.GRAY
        border = EmptyBorder(0, 10, 0, 10)
        preferredSize = Dimension(300, 40)
        maximumSize = preferredSize
        isOpaque = false

        addFocusListener(object : FocusAdapter() {
            override fun focusGained(e: FocusEvent) {
                if (text == placeholder) {
                    text = ""
                    foreground = Color.BLACK
                }
            }

            override fun focusLost(e: FocusEvent) {
                if (text.isBlank()) {
                    text = placeholder
                    foreground = Color.GRAY
                }
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Background
        g2.color = Color.WHITE
        g2.fillRoundRect(0, 0, width, height, 15, 15)

        // Border
        g2.color = Color(120, 80, 160)
        g2.drawRoundRect(0, 0, width - 1, height - 1, 15, 15)

        super.paintComponent(g)
    }
}
class RoundedPasswordField : JPasswordField("Password") {
    init {
        font = Font("Arial", Font.PLAIN, 16)
        foreground = Color.GRAY
        border = EmptyBorder(0, 10, 0, 10)
        preferredSize = Dimension(300, 40)
        maximumSize = preferredSize
        isOpaque = false
        echoChar = 0.toChar()

        addFocusListener(object : FocusAdapter() {
            override fun focusGained(e: FocusEvent) {
                if (String(password) == "Password") {
                    text = ""
                    echoChar = '\u2022'
                    foreground = Color.BLACK
                }
            }

            override fun focusLost(e: FocusEvent) {
                if (String(password).isBlank()) {
                    text = "Password"
                    echoChar = 0.toChar()
                    foreground = Color.GRAY
                }
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Background
        g2.color = Color.WHITE
        g2.fillRoundRect(0, 0, width, height, 15, 15)

        // Border
        g2.color = Color(120, 80, 160)
        g2.drawRoundRect(0, 0, width - 1, height - 1, 15, 15)

        super.paintComponent(g)
    }
}


package com.samsung.iug.ui.login

//    can not run on SRV
//import javafx.application.Platform
//import javafx.embed.swing.JFXPanel
//import javafx.scene.Scene
//import javafx.scene.layout.StackPane
//import javafx.scene.web.WebEngine
//import javafx.scene.web.WebView
import com.samsung.iug.ui.manage.ScreenManager
import java.awt.*
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.awt.geom.RoundRectangle2D
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder

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
//                                //TODO next popup
//
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
                val gradient = GradientPaint(
                    0f, 0f, Color(200, 200, 200),
                    0f, height.toFloat(), Color(150, 220, 220)
                )
                g2.paint = gradient
                g2.fillRect(0, 0, width, height)
            }
        }

        backgroundPanel.layout = BoxLayout(backgroundPanel, BoxLayout.Y_AXIS)

        val topBar = createTopBar(this, showBack = true)
        backgroundPanel.add(topBar)

        val contentPanel = JPanel()
        contentPanel.layout = BoxLayout(contentPanel, BoxLayout.Y_AXIS)
        contentPanel.isOpaque = false
        contentPanel.border = EmptyBorder(20, 40, 40, 40)

        val titleLabel = JLabel("Login with account").apply {
            alignmentX = Component.CENTER_ALIGNMENT
            font = Font("Arial", Font.BOLD, 26)
            foreground = Color(30, 30, 30)
            border = EmptyBorder(10, 0, 30, 0)
        }

        fun createPlaceholderField(placeholder: String): JTextField {
            val field = JTextField(placeholder)
            field.foreground = Color.GRAY
            field.font = Font("Arial", Font.PLAIN, 16)
            field.background = Color.WHITE
            field.border = LineBorder(Color.GRAY)
            field.maximumSize = Dimension(300, 40)

            field.addFocusListener(object : FocusAdapter() {
                override fun focusGained(e: FocusEvent) {
                    if (field.text == placeholder) {
                        field.text = ""
                        field.foreground = Color.BLACK
                    }
                }

                override fun focusLost(e: FocusEvent) {
                    if (field.text.isBlank()) {
                        field.text = placeholder
                        field.foreground = Color.GRAY
                    }
                }
            })

            return field
        }

        val userField = createPlaceholderField("ID")

        val passField = JPasswordField("Password").apply {
            foreground = Color.GRAY
            font = Font("Arial", Font.PLAIN, 16)
            background = Color.WHITE
            border = LineBorder(Color.GRAY)
            maximumSize = Dimension(300, 40)
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

        val passwordContainer = JPanel().apply {
            layout = BorderLayout()
            background = Color.WHITE
            maximumSize = Dimension(300, 40)
            border = LineBorder(Color.GRAY)
            add(passField, BorderLayout.CENTER)
        }

        val loginButton = PurpleButton("Login").apply {
            alignmentX = Component.CENTER_ALIGNMENT
            addActionListener {
                val user = userField.text
                val pass = String(passField.password)
                if (user != "ID" && pass != "Password" && user.isNotBlank() && pass.isNotBlank()) {
                    if (user == "admin" && pass == "admin") {
                        //TODO next popup
                        JOptionPane.showMessageDialog(this@ManualLoginDialog, "$user login successful!")
//                        dispose()
                        ScreenManager.instance.showIUGMakerScreen()
                    } else {
                        JOptionPane.showMessageDialog(this@ManualLoginDialog, "Wrong account.")
                    }
                } else {
                    JOptionPane.showMessageDialog(this@ManualLoginDialog, "Please enter ID and password.")
                }
            }
        }

        contentPanel.add(titleLabel)
        contentPanel.add(userField)
        contentPanel.add(Box.createRigidArea(Dimension(0, 20)))
        contentPanel.add(passwordContainer)
        contentPanel.add(Box.createRigidArea(Dimension(0, 30)))
        contentPanel.add(loginButton)

        backgroundPanel.add(contentPanel)
        contentPane = backgroundPanel
    }
}
package com.samsung.iug.ui.login

import com.samsung.iug.ui.welcome.OptionScreen
import com.intellij.ide.util.PropertiesComponent
//import javafx.application.Platform
//import javafx.embed.swing.JFXPanel
//import javafx.scene.Scene
//import javafx.scene.web.WebEngine
//import javafx.scene.web.WebView
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

class BrowserPanel : JPanel(BorderLayout()){

    private val panel = JPanel(BorderLayout())
//    TODO can not run on SRV
//    private var jfxPanel: JFXPanel? = null
//    private var webEngine: WebEngine? = null

    var username = "unknown1"
    private val props = PropertiesComponent.getInstance()
    private val LOGIN_KEY = "samsungu.loggedIn"

    init {
        showLoginButton()
    }

    private fun showLoginButton() {
        // Tạo tiêu đề
        val titleLabel = JLabel("IUG Rule Maker Tool")
        titleLabel.horizontalAlignment = SwingConstants.CENTER
        titleLabel.font = Font("Arial", Font.BOLD, 40)
        titleLabel.foreground = Color.WHITE
        titleLabel.border = EmptyBorder(100, 0, 0, 0) // (top, left, bottom, right)

        // Tạo nút login
        val button = JButton("Login with Samsung Account")
        button.maximumSize = Dimension(300, 50)
        button.preferredSize = Dimension(300, 50)
        button.alignmentX = Component.CENTER_ALIGNMENT
        button.font = Font("Arial", Font.BOLD, 16)

        button.addActionListener {

//            TODO can not run on SRV
//            skip login step, if you want to login, remove comment and comment showMainUI() in last line
//            if (props.getBoolean(LOGIN_KEY, false)) {
//                // Nếu đã đăng nhập từ trước, bỏ qua đăng nhập
//                showMainUI()
//            } else {
//                openLoginPage()
//            }

            showMainUI()
        }

        // Panel chứa button
        val buttonPanel = Box.createVerticalBox()
        buttonPanel.background = Color.WHITE
        buttonPanel.add(Box.createVerticalGlue())
        buttonPanel.add(button)
        buttonPanel.add(Box.createVerticalGlue())

        // Wrapper chính
        val wrapper = JPanel(BorderLayout())
        wrapper.add(titleLabel, BorderLayout.NORTH)
        wrapper.add(buttonPanel, BorderLayout.CENTER)

        panel.removeAll()
        panel.add(wrapper, BorderLayout.CENTER)
        panel.revalidate()
        panel.repaint()
    }


// TODO can not run on SRV
//    private fun openLoginPage() {
//        if (jfxPanel != null) return
//
//        jfxPanel = JFXPanel()
//        panel.removeAll()
//        panel.add(jfxPanel, BorderLayout.CENTER)
//        panel.revalidate()
//        panel.repaint()
//
//        Platform.runLater {
//            val webView = WebView()
//            webEngine = webView.engine
//
//            webEngine!!.locationProperty().addListener { _, _, newUrl ->
//                println("🌐 Đang truy cập: $newUrl")
//                if (newUrl.startsWith("https://samsungu.udemy.com/organization/home")) {
//                    props.setValue(LOGIN_KEY, "true")
//
//                    Platform.runLater {
//                        webEngine?.load(null)
//                        webEngine = null
//
//                        SwingUtilities.invokeLater {
//                            jfxPanel?.let {
//                                panel.remove(it)
//                                jfxPanel = null
//                            }
//                            panel.revalidate()
//                            panel.repaint()
//                            showMainUI()
//                        }
//                    }
//                }
//            }
//
//            webEngine!!.load("https://samsungu.udemy.com")
//            jfxPanel!!.scene = Scene(webView)
//        }
//    }

    private fun showMainUI() {
        panel.removeAll()
        panel.add(
            OptionScreen(username) {
                // Khi logout, hiển thị lại nút login
                showLoginButton()
            },
            BorderLayout.CENTER
        )
        panel.revalidate()
        panel.repaint()
    }

    fun getComponent(): JComponent = panel
}

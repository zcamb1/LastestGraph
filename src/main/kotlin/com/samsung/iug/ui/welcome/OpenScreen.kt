package com.samsung.iug.ui.welcome

import com.intellij.ide.util.PropertiesComponent
import javax.swing.JLabel
import javax.swing.JPanel
import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

class OpenScreen(val path: String, val userName: String, private val onLogoutCallback: () -> Unit) : JPanel(BorderLayout()) {
    init {

        val topBar = JPanel(BorderLayout())
        topBar.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)

        val titleLabel = JLabel("IUG Rule Maker Tool")
        titleLabel.foreground = Color.WHITE
        titleLabel.font = Font("Arial", Font.BOLD, 20)

        val userPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 5, 0))


        val helloLabel = JLabel("Hello, $userName")
        helloLabel.foreground = Color.WHITE
        helloLabel.font = Font("Arial", Font.PLAIN, 14)

        val arrowButton = JButton("▼")
        arrowButton.foreground = Color.WHITE
        arrowButton.border = BorderFactory.createEmptyBorder()
        arrowButton.isFocusPainted = false

        val popupMenu = JPopupMenu()
        val logoutItem = JMenuItem("Log out")
        popupMenu.add(logoutItem)

        arrowButton.addActionListener {
            popupMenu.show(arrowButton, 0, arrowButton.height)
        }

        logoutItem.addActionListener {
            PropertiesComponent.getInstance().unsetValue("samsungu.loggedIn")
            onLogout()
        }

        userPanel.add(helloLabel)
        userPanel.add(arrowButton)

        topBar.add(titleLabel, BorderLayout.WEST)
        topBar.add(userPanel, BorderLayout.EAST)

        add(topBar, BorderLayout.NORTH)
        add(JLabel("🎉 Opened project at: $path"), BorderLayout.CENTER)
    }
    private fun onLogout() {
        onLogoutCallback() // <-- gọi về cha (BrowserPanel)
    }
}
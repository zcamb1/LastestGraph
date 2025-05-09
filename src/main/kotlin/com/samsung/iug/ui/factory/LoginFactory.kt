package com.samsung.iug.ui.factory

import com.samsung.iug.ui.login.LoginDialog
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import javax.swing.SwingUtilities

class LoginFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.isShowStripeButton = false
        toolWindow.hide(null)
        SwingUtilities.invokeLater {
            LoginDialog().isVisible = true
        }
    }
}
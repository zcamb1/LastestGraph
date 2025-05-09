package com.samsung.iug.ui.factory

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.samsung.iug.ui.manage.ScreenManager

class LoginFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        ScreenManager.init(project)
        toolWindow.isShowStripeButton = false
        toolWindow.hide(null)
        ScreenManager.instance.showLoginScreen()
    }
}
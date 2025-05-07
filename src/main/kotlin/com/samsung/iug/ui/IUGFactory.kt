package com.samsung.iug.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class IUGFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.isShowStripeButton = false
        toolWindow.hide(null)
        IUGUI(project)
    }

    override fun shouldBeAvailable(project: Project): Boolean {
        return true
    }
}
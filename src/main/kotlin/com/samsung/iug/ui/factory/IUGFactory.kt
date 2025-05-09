package com.samsung.iug.ui.factory

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.samsung.iug.ui.iug.IUGMaker

class IUGFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.isShowStripeButton = false
        toolWindow.hide(null)
        IUGMaker(project)
    }

    override fun shouldBeAvailable(project: Project): Boolean {
        return true
    }
}
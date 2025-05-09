package com.samsung.iug.ui.factory

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.samsung.iug.ui.iug.IUGMaker
import com.samsung.iug.ui.iug.MainUI

class IUGFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.isShowStripeButton = false
        toolWindow.hide(null)
        IUGMaker(project)
//        val iugPanel = MainUI{}
//        val content = ContentFactory.getInstance().createContent(iugPanel, "", false)
//        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project): Boolean {
        return true
    }
}
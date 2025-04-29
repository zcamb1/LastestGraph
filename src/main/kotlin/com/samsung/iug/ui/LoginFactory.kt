package com.samsung.iug.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.samsung.iug.ui.login.BrowserPanel

class LoginFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
//        val browserPanel = BrowserPanel(project)
//        val contentFactory = ContentFactory.getInstance()
//        val content = contentFactory.createContent(browserPanel.getComponent(), "Samsung Login", false)
//        toolWindow.contentManager.addContent(content)

        val browserPanel = BrowserPanel(project)
        val content = ContentFactory.getInstance().createContent(browserPanel.getComponent(), "", false)
        toolWindow.contentManager.addContent(content)
    }
}

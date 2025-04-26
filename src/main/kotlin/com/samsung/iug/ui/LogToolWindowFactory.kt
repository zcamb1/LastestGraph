package com.samsung.iug.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.samsung.iug.service.Log
import com.samsung.iug.ui.LogPanel

// temp added
class LogToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val logPanel = LogPanel()
        Log.init(logPanel)
        val content = ContentFactory.getInstance().createContent(logPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }

}

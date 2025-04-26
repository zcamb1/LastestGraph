package com.samsung.iug.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.samsung.iug.ui.rulemaker.IUGRuleMaker

class IUGFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val iugPanel = IUGRuleMaker()
        val content = ContentFactory.getInstance().createContent(iugPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}
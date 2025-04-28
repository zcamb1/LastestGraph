package com.samsung.iug.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.samsung.iug.ui.rulemaker.IUGRuleMaker
import com.samsung.iug.utils.AdbManager

class IUGFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        AdbManager.initializeAdb()
        val iugPanel = IUGRuleMaker("", "",project)
        val content = ContentFactory.getInstance().createContent(iugPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}
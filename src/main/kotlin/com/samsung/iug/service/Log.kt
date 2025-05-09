package com.samsung.iug.service

import com.samsung.iug.model.LogEntry
import com.samsung.iug.model.LogLevel
import com.samsung.iug.ui.log.LogPanel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Log {
    private var logPanel: LogPanel? = null

    fun init(panel: LogPanel) {
        logPanel = panel
        log(LogLevel.INFO, "LogService", "Initialized.")
    }

    fun log(level: LogLevel, tag: String, message: String) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val entry = LogEntry(timestamp, level, tag, message)
        logPanel?.appendLog(entry)

        val project = com.intellij.openapi.project.ProjectManager.getInstance().openProjects.firstOrNull()
        val toolWindow = com.intellij.openapi.wm.ToolWindowManager.getInstance(project ?: return)
            .getToolWindow("LogWindow")
        toolWindow?.show()
    }

    fun d(tag: String, message: String) = log(LogLevel.DEBUG, tag, message)
    fun i(tag: String, message: String) = log(LogLevel.INFO, tag, message)
    fun w(tag: String, message: String) = log(LogLevel.WARN, tag, message)
    fun e(tag: String, message: String) = log(LogLevel.ERROR, tag, message)
}

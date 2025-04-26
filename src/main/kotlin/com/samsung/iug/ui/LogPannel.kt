package com.samsung.iug.ui

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBList
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class LogPanel : JPanel(BorderLayout()) {
    private val allLogs = mutableListOf<LogEntry>()
    private val logListModel = DefaultListModel<String>()
    private val logList = JBList(logListModel)

    private val logLevelFilter = ComboBox(arrayOf("ALL", "DEBUG", "INFO", "WARN", "ERROR"))
    private val tagFilterField = JTextField(15)

    init {
        // Top control panel
        val controls = JPanel(FlowLayout(FlowLayout.LEFT))
        controls.add(JLabel("Level:"))
        controls.add(logLevelFilter)
        controls.add(JLabel("Tag:"))
        controls.add(tagFilterField)

        val copyButton = JButton("Copy All").apply {
            addActionListener {
                val combined = allLogs.joinToString("\n") { it.toString() }
                val selection = StringSelection(combined)
                Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, null)
            }
        }

        val clearButton = JButton("Clear").apply {
            addActionListener {
                allLogs.clear()
                refreshLogDisplay()
            }
        }

        controls.add(copyButton)
        controls.add(clearButton)

        add(controls, BorderLayout.NORTH)
        add(JScrollPane(logList), BorderLayout.CENTER)

        // Filter listeners
        logLevelFilter.addActionListener { refreshLogDisplay() }
        tagFilterField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = refreshLogDisplay()
            override fun removeUpdate(e: DocumentEvent?) = refreshLogDisplay()
            override fun changedUpdate(e: DocumentEvent?) = refreshLogDisplay()
        })
    }

    fun appendLog(entry: LogEntry) {
        allLogs.add(entry)
        refreshLogDisplay()
    }

    private fun refreshLogDisplay() {
        val selectedLevel = logLevelFilter.selectedItem?.toString()
        val tagQuery = tagFilterField.text.lowercase()

        val filtered = allLogs.filter {
            (selectedLevel == "ALL" || it.level.label == selectedLevel)
                    && (tagQuery.isEmpty() || it.tag.lowercase().contains(tagQuery))
        }

        logListModel.clear()
        filtered.forEach { logListModel.addElement(it.toString()) }
    }
}


package com.samsung.iug.ui.log

import com.intellij.openapi.util.IconLoader
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JLabel
import javax.swing.JList

class LogCellRenderer : DefaultListCellRenderer() {
    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component? {
        val label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus) as JLabel

        if (value is String) {
            when {
                value.contains("INFO") -> label.icon = IconLoader.getIcon("/icon/ic_info.png", javaClass)
                value.contains("DEBUG") -> label.icon = IconLoader.getIcon("/icon/ic_debug.png", javaClass)
                value.contains("WARN") -> label.icon = IconLoader.getIcon("/icon/ic_warn.png", javaClass)
                value.contains("ERROR") -> label.icon = IconLoader.getIcon("/icon/ic_error.png", javaClass)
            }
        }

        return label
    }
}
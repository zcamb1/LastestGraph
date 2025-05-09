package com.samsung.iug.ui.custom

import com.intellij.ui.util.preferredHeight
import com.intellij.ui.util.preferredWidth
import javax.swing.JTextField
import java.awt.*
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import javax.swing.BorderFactory

class JTextFieldCustom(private val placeholder: String, private val height: Int) : JTextField() {

    init {
        text = placeholder
        font = Font("Arial", Font.BOLD, 14)
        foreground = Color.WHITE
        border = BorderFactory.createEmptyBorder(5, 10, 5, 5)
        preferredHeight = height
        preferredWidth = 200
        isOpaque = false

        addFocusListener(object : FocusAdapter() {
            override fun focusGained(e: FocusEvent?) {
                if (text == placeholder) {
                    text = ""
                    foreground = Color.WHITE
                }
            }

            override fun focusLost(e: FocusEvent?) {
                if (text.isEmpty()) {
                    text = placeholder
                    foreground = Color.WHITE
                }
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D

        g2.color = Color.BLACK
        g2.fillRoundRect(1, 1, width - 1, height - 1, 5, 5)

        super.paintComponent(g)

        g2.color = Color.GRAY
        g2.stroke = BasicStroke(2f)
        g2.drawRoundRect(0, 0, width - 1, height - 1, 5, 5)

    }
}
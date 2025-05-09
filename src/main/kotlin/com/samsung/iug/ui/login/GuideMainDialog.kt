package com.samsung.iug.ui.login

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.RoundRectangle2D
import java.io.File
import javax.swing.*
import javax.swing.border.EmptyBorder

class GuideMainDialog : JDialog() {
    private val guideDir = File(System.getProperty("user.home"), "InteractiveGuides")

    init {
        isUndecorated = true
        isModal = true
        setSize(600, 520)
        setLocationRelativeTo(null)
        shape = RoundRectangle2D.Double(0.0, 0.0, width.toDouble(), height.toDouble(), 30.0, 30.0)

        if (!guideDir.exists()) {
            guideDir.mkdirs()
        }

        contentPane = buildUI()
    }

    private fun buildUI(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.background = Color(25, 25, 25)
        panel.border = EmptyBorder(20, 10, 20, 10)

        // Close button
        val closeButton = JButton("X").apply {
            foreground = Color.WHITE
            background = Color(60, 60, 60)
            border = BorderFactory.createEmptyBorder()
            font = Font("Arial", Font.BOLD, 12)
            isFocusPainted = false
            isContentAreaFilled = false
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            preferredSize = Dimension(30, 30)
            maximumSize = Dimension(30, 30)
            toolTipText = "Close"

            addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent?) {
                    background = Color(100, 0, 0)
                    isContentAreaFilled = true
                }

                override fun mouseExited(e: MouseEvent?) {
                    background = Color(60, 60, 60)
                    isContentAreaFilled = false
                }

                override fun mouseClicked(e: MouseEvent?) {
                    dispose()
                }
            })
        }

        val topPanel = JPanel(BorderLayout()).apply {
            isOpaque = false
            add(closeButton, BorderLayout.EAST)
            maximumSize = Dimension(Int.MAX_VALUE, 30)
            alignmentX = Component.LEFT_ALIGNMENT
        }


        val headerPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            isOpaque = false
            alignmentX = Component.LEFT_ALIGNMENT
            border = EmptyBorder(0, 0, 20, 0)
            alignmentX = Component.LEFT_ALIGNMENT
        }

        val logo = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                super.paintComponent(g)
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

                val size = 42
                val x = (width - size) / 2
                val y = 10

                val thickness = 10f
                g2.stroke = BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
                g2.color = Color(255, 255, 255)
                g2.drawArc(x, y, size, size, 30, 295)

                val dotSize = 20
                g2.fillOval(x + size / 2 - dotSize / 2, y + size / 2 - dotSize / 2, dotSize, dotSize)
            }
        }.apply {
            preferredSize = Dimension(50, 60)
            maximumSize = Dimension(50, 60)
            minimumSize = Dimension(50, 60)
            isOpaque = false
        }

        val titleBox = Box.createVerticalBox().apply {
            alignmentY = Component.CENTER_ALIGNMENT
            border = EmptyBorder(5, 10, 0, 0)
            isOpaque = false
        }

        val title = JLabel("INTERACTIVE USAGE GUIDE").apply {
            foreground = Color.WHITE
            font = Font("Arial", Font.BOLD, 18)
            alignmentX = Component.LEFT_ALIGNMENT
        }

        val subtitle = JLabel("GUIDE CREATION TOOL").apply {
            foreground = Color(180, 180, 180)
            font = Font("Arial", Font.PLAIN, 14)
            alignmentX = Component.LEFT_ALIGNMENT
        }

        titleBox.add(title)
        titleBox.add(subtitle)

        headerPanel.add(logo)
        headerPanel.add(titleBox)

        val sectionLabel = JLabel("Open a Recent Guide").apply {
            foreground = Color(200, 200, 200)
            font = Font("Arial", Font.BOLD, 14)
            alignmentX = Component.LEFT_ALIGNMENT
            border = EmptyBorder(20, 0, 10, 0)
        }

        val fileListPanel = JPanel()
        fileListPanel.layout = BoxLayout(fileListPanel, BoxLayout.Y_AXIS)
        fileListPanel.background = Color(30, 30, 30)

        val scrollPane = JScrollPane(fileListPanel).apply {
            alignmentX = Component.LEFT_ALIGNMENT
            preferredSize = Dimension(440, 200)
            border = BorderFactory.createLineBorder(Color.DARK_GRAY)
            background = Color(30, 30, 30)
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            alignmentX = Component.LEFT_ALIGNMENT
        }

        refreshFileList(fileListPanel)

        val buttonPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            isOpaque = false
            alignmentX = Component.LEFT_ALIGNMENT
            border = EmptyBorder(20, 0, 0, 0)
            alignmentX = Component.LEFT_ALIGNMENT
        }

        val newGuideBtn = GradientButton("+ New Guide").apply {
            preferredSize = Dimension(150, 40)
            addActionListener { createNewGuide() }
        }

        val openGuideBtn = JButton("Open Guide").apply {
            background = Color(50, 50, 50)
            foreground = Color.WHITE
            font = Font("Arial", Font.PLAIN, 14)
            isFocusPainted = false
            preferredSize = Dimension(150, 40)
            alignmentX = Component.LEFT_ALIGNMENT
            addActionListener { openGuide() }
        }

        buttonPanel.add(newGuideBtn)
        buttonPanel.add(Box.createRigidArea(Dimension(20, 0)))
        buttonPanel.add(openGuideBtn)

        panel.add(topPanel)
        panel.add(headerPanel)
        panel.add(sectionLabel)
        panel.add(scrollPane)
        panel.add(buttonPanel)

        return panel
    }

    private fun refreshFileList(fileListPanel: JPanel) {
        fileListPanel.removeAll()
        val files = guideDir.listFiles { _, name -> name.endsWith(".IUG") } ?: return
        files.sortedBy { it.name }.forEachIndexed { index, file ->
            val container = JPanel(BorderLayout()).apply {
                background = if (index % 2 == 0) Color(40, 40, 40) else Color(30, 30, 30)
                maximumSize = Dimension(Int.MAX_VALUE, 40)
            }

            val label = JLabel(file.name).apply {
                foreground = Color.WHITE
                font = Font("Arial", Font.BOLD, 15)
                border = EmptyBorder(8, 12, 8, 12)
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent?) {
                        JOptionPane.showMessageDialog(this@GuideMainDialog, "Selected file: ${file.absolutePath}")
                    }
                })
            }

            container.add(label, BorderLayout.WEST)
            fileListPanel.add(container)
        }
        fileListPanel.revalidate()
        fileListPanel.repaint()
    }

    private fun createNewGuide() {
        var index = 0
        var file: File
        do {
            val name = if (index == 0) "Test.IUG" else "Test$index.IUG"
            file = File(guideDir, name)
            index++
        } while (file.exists())

        file.writeText("This is a new guide.")
        JOptionPane.showMessageDialog(this, "File created: ${file.absolutePath}")
        isVisible = false
        GuideMainDialog().isVisible = true
    }

    private fun openGuide() {
        val chooser = JFileChooser(guideDir).apply {
            fileSelectionMode = JFileChooser.FILES_ONLY
            fileFilter = javax.swing.filechooser.FileNameExtensionFilter("IUG Files", "IUG")
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            val file = chooser.selectedFile
            JOptionPane.showMessageDialog(this, "File selected: ${file.absolutePath}")
        }
    }
}

class GradientButton(text: String) : JButton(text) {
    init {
        isContentAreaFilled = false
        isFocusPainted = false
        foreground = Color.WHITE
        font = Font("Arial", Font.BOLD, 14)
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        val width = width
        val height = height
        val gradient = GradientPaint(0f, 0f, Color(0, 150, 220), 0f, height.toFloat(), Color(0, 100, 180))
        g2.paint = gradient
        g2.fillRoundRect(0, 0, width, height, 10, 10)
        super.paintComponent(g)
    }
}

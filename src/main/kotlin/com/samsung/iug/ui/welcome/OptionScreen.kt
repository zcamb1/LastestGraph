package com.samsung.iug.ui.welcome

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.util.IconLoader
import java.awt.*
import java.io.File
import javax.swing.*

class OptionScreen(
    private val userName: String,
    private val onLogoutCallback: () -> Unit // <-- thêm callback
) : JPanel(BorderLayout()) {
    private val props = PropertiesComponent.getInstance()
    init {

        val topBar = JPanel(BorderLayout())
        topBar.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)

        val titleLabel = JLabel("IUG Rule Maker Tool")
        titleLabel.foreground = Color.WHITE
        titleLabel.font = Font("Arial", Font.BOLD, 20)

        val userPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 5, 0))


        val helloLabel = JLabel("Hello, $userName")
        helloLabel.foreground = Color.WHITE
        helloLabel.font = Font("Arial", Font.PLAIN, 14)

        val arrowButton = JButton("▼")
        arrowButton.foreground = Color.WHITE
        arrowButton.border = BorderFactory.createEmptyBorder()
        arrowButton.isFocusPainted = false

        val popupMenu = JPopupMenu()
        val logoutItem = JMenuItem("Log out")
        popupMenu.add(logoutItem)

        arrowButton.addActionListener {
            popupMenu.show(arrowButton, 0, arrowButton.height)
        }

        logoutItem.addActionListener {
            PropertiesComponent.getInstance().unsetValue("samsungu.loggedIn")
            onLogout()
        }

        userPanel.add(helloLabel)
        userPanel.add(arrowButton)

        topBar.add(titleLabel, BorderLayout.WEST)
        topBar.add(userPanel, BorderLayout.EAST)

        add(topBar, BorderLayout.NORTH)

        val buttonSize = Dimension(150, 120)
        val font = Font("Arial", Font.BOLD, 14)

        val iconCreate = IconLoader.getIcon("/general/newProject.svg", javaClass)
        val iconOpen = IconLoader.getIcon("/actions/menu-open.svg", javaClass)
        val iconJoin = IconLoader.getIcon("/nodes/plugin.svg", javaClass)

        val buttonCreate = JButton("Create\nNew Project", iconCreate)
        val buttonOpen = JButton("Open\nProject", iconOpen)
        val buttonJoin = JButton("Join\nOur Team", iconJoin)

        buttonCreate.preferredSize = buttonSize
        buttonOpen.preferredSize = buttonSize
        buttonJoin.preferredSize = buttonSize

        listOf(buttonCreate, buttonOpen, buttonJoin).forEach {
            it.verticalTextPosition = SwingConstants.BOTTOM
            it.horizontalTextPosition = SwingConstants.CENTER
            it.font = font
        }

        buttonCreate.addActionListener {
            val dialog = JDialog(null as JFrame?, "Create new project", true)
            dialog.layout = GridBagLayout()
            val gbc = GridBagConstraints().apply {
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(5, 5, 5, 5)
            }

            val nameField = JTextField(20)
            val pathField = JTextField(20)
            val browseButton = JButton("Browse")

            // Dòng 1: Nhập tên Project
            gbc.gridx = 0
            gbc.gridy = 0
            dialog.add(JLabel("Project name:"), gbc)

            gbc.gridx = 1
            gbc.gridwidth = 2
            dialog.add(nameField, gbc)

            // Dòng 2: Nhập path chứa Project + nút Browse
            gbc.gridy = 1
            gbc.gridx = 0
            gbc.gridwidth = 1
            dialog.add(JLabel("Project path:"), gbc)

            gbc.gridx = 1
            dialog.add(pathField, gbc)

            gbc.gridx = 2
            dialog.add(browseButton, gbc)

            // Nút Browse
            browseButton.addActionListener {
                val chooser = JFileChooser()
                chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                    pathField.text = chooser.selectedFile.absolutePath
                }
            }

            // Dòng 3: Nút Create và Cancel
            val createButton = JButton("Create")
            val cancelButton = JButton("Cancel")

            gbc.gridy = 2
            gbc.gridx = 1
            dialog.add(createButton, gbc)

            gbc.gridx = 2
            dialog.add(cancelButton, gbc)

            // Nút Cancel
            cancelButton.addActionListener {
                dialog.dispose()
            }

            // Nút Create
            createButton.addActionListener {
                val name = nameField.text.trim()
                val path = pathField.text.trim()

                if (name.isBlank() || path.isBlank()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter both project name and path.")
                    return@addActionListener
                }

                val fullPath = "$path$name"
                val projectDir = File(fullPath)

                if (projectDir.exists()) {
                    JOptionPane.showMessageDialog(dialog, "Project name already exists.")
                } else {
                    if (projectDir.mkdirs()) {
                        dialog.dispose()
                        removeAll()
                        add(CreateScreen(fullPath, userName, onLogoutCallback), BorderLayout.CENTER)
                        revalidate()
                        repaint()
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to create project folder.")
                    }
                }
            }

            dialog.pack()
            dialog.setLocationRelativeTo(null)
            dialog.isVisible = true
        }

        // Chức năng nút Open
        buttonOpen.addActionListener {
            val chooser = JFileChooser()
            chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            val result = chooser.showOpenDialog(this)
            if (result == JFileChooser.APPROVE_OPTION) {
                val path = chooser.selectedFile.absolutePath
                removeAll()
                add(OpenScreen(path, userName, onLogoutCallback), BorderLayout.CENTER)
                revalidate()
                repaint()
            }
        }

        // Chức năng nút Join
        buttonJoin.addActionListener {
            JOptionPane.showMessageDialog(this, "This is not supported now", "Notification", JOptionPane.INFORMATION_MESSAGE)
        }

        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER, 30, 0))
        buttonPanel.add(buttonCreate)
        buttonPanel.add(buttonOpen)
        buttonPanel.add(buttonJoin)

        val wrapper = JPanel(GridBagLayout())
        wrapper.add(buttonPanel)

        add(wrapper, BorderLayout.CENTER)
    }
    private fun onLogout() {
        onLogoutCallback() // <-- gọi về cha (BrowserPanel)
    }

}

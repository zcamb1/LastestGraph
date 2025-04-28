package com.samsung.iug.ui.rulemaker

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBTextField
import com.samsung.iug.service.Log
import com.samsung.iug.ui.screenmirror.GetDevice
import com.samsung.iug.utils.AdbManager
import com.samsung.iug.utils.DeviceManager
import java.awt.*
import java.awt.datatransfer.StringSelection
import java.io.File
import javax.swing.*
import javax.swing.border.TitledBorder

class ScreenInfoPanel(onApplyClick: (String) -> Unit) : JPanel(GridBagLayout()) {
    private val screenIdField = JBTextField().apply {
        isEditable = true
    }
    private val packageNameField = JBTextField().apply {
        isEditable = true
    }

    private val captureButton = JButton("Capture")
    private val recordButton = JButton("Record")

    private var recordingProcess: Process? = null
    private var savedRecordingFile: File? = null

    init {
        val screenInfoBox = JPanel()
        screenInfoBox.layout = GroupLayout(screenInfoBox)
        screenInfoBox.background = JBColor.GRAY
        screenInfoBox.border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(JBColor.GRAY),
            "Screen information",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            null,
//            JBColor.WHITE
        )
        val layout = screenInfoBox.layout as GroupLayout
        layout.autoCreateGaps = true
        layout.autoCreateContainerGaps = true

        val screenIdLabel = JLabel("Screen id:")
//        screenIdLabel.foreground = JBColor.WHITE
        val packageNameLabel = JLabel("Package name:")
//        packageNameLabel.foreground = JBColor.WHITE
        screenIdField.preferredSize = Dimension(160, 32)
        packageNameField.preferredSize = Dimension(160, 32)
        AdbManager.getTopResumedActivity(GetDevice.device, screenIdField, packageNameField)
        AdbManager.startListeningActivityChanges(GetDevice.device, screenIdField, packageNameField)

        val applyButton = JButton("Apply").apply {
            preferredSize = Dimension(100, 32)
            addActionListener { onApplyClick(packageNameField.text) }
        }

        val screenIdCopyButton = JButton(AllIcons.Actions.Copy).apply {
            preferredSize = Dimension(50, 30)
            addActionListener {
                val text = screenIdField.text
                val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                clipboard.setContents(StringSelection(text), null)
            }
        }

        val packageNameCopyButton = JButton(AllIcons.Actions.Copy).apply {
            preferredSize = Dimension(50, 30)
            addActionListener {
                val text = packageNameField.text
                val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                clipboard.setContents(StringSelection(text), null)
            }
        }

        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addGroup(
                    layout.createSequentialGroup()
                        .addGroup(
                            layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(screenIdLabel)
                                .addComponent(packageNameLabel)
                        )
                        .addGroup(
                            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(
                                    layout.createSequentialGroup()
                                        .addComponent(screenIdField)
                                        .addComponent(screenIdCopyButton)
                                )
                                .addGroup(
                                    layout.createSequentialGroup()
                                        .addComponent(packageNameField)
                                        .addComponent(packageNameCopyButton)
                                )
                        )
                )
                .addComponent(
                    applyButton,
                    GroupLayout.PREFERRED_SIZE,
                    GroupLayout.PREFERRED_SIZE,
                    GroupLayout.PREFERRED_SIZE
                )
        )
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(screenIdLabel)
                        .addComponent(screenIdField)
                        .addComponent(screenIdCopyButton)
                )
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(packageNameLabel)
                        .addComponent(packageNameField)
                        .addComponent(packageNameCopyButton)
                )
                .addGap(12)
                .addComponent(
                    applyButton,
                    GroupLayout.PREFERRED_SIZE,
                    GroupLayout.PREFERRED_SIZE,
                    GroupLayout.PREFERRED_SIZE
                )
        )
        screenInfoBox.preferredSize = Dimension(260, 160)
        screenInfoBox.maximumSize = Dimension(320, 180)

        // Control buttons căn giữa và lên cao một chút
        val controlPanel = JPanel(FlowLayout(FlowLayout.CENTER, 10, 10))
        controlPanel.background = JBColor.GRAY
        controlPanel.add(captureButton)
        controlPanel.add(recordButton)

        captureButton.addActionListener {
            captureScreen()
        }

        recordButton.addActionListener {
            toggleRecording()
        }

        // Panel phải dùng GridBagLayout để căn giữa box và đẩy controlPanel xuống cuối
        background = JBColor.GRAY
        val gbc = GridBagConstraints()

        // Thêm box ở giữa cột
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.weightx = 1.0
        gbc.weighty = 1.0
        gbc.anchor = GridBagConstraints.CENTER
        gbc.fill = GridBagConstraints.NONE
        add(screenInfoBox, gbc)

        // Thêm controlPanel căn giữa và lên cao một chút
        val gbc2 = GridBagConstraints()
        gbc2.gridx = 0
        gbc2.gridy = 1
        gbc2.weightx = 1.0
        gbc2.weighty = 0.0
        gbc2.anchor = GridBagConstraints.SOUTH
        gbc2.fill = GridBagConstraints.NONE
        add(controlPanel, gbc2)
        // Thêm khoảng cách phía dưới controlPanel
        val gbc3 = GridBagConstraints()
        gbc3.gridx = 0
        gbc3.gridy = 2
        gbc3.weightx = 1.0
        gbc3.weighty = 0.0
        gbc3.anchor = GridBagConstraints.SOUTH
        gbc3.fill = GridBagConstraints.NONE
        add(Box.createVerticalStrut(30), gbc3)

        preferredSize = Dimension(290, 300)
    }
    override fun removeNotify() {
        super.removeNotify()
        AdbManager.stopListeningActivityChanges()
    }

    private fun captureScreen() {
        if (!DeviceManager.isAdbDeviceConnected()) {
            Log.e("Capture screen", "No device connected.")
            Messages.showErrorDialog("No device connected.\nPlease connect a device via ADB.", "ADB Error")
            return
        }

        try {
            val descriptor = FileSaverDescriptor("Save Screenshot", "Save as PNG", "png")
            val fileWrapper = FileChooserFactory.getInstance()
                .createSaveFileDialog(descriptor, null)
                .save(null as VirtualFile?, "screenshot.png")

            fileWrapper?.file?.let { outputFile ->
                captureScreenAndSave(outputFile)
                Messages.showInfoMessage("Screenshot saved: ${outputFile.absolutePath}", "Capture Success")
            }
        } catch (ex: Exception) {
            Messages.showErrorDialog(ex.message ?: "Capture failed", "Capture Error")
        }
    }

    private fun captureScreenAndSave(outputFile: File) {
        val process = ProcessBuilder("adb", "exec-out", "screencap", "-p").start()
        val imageBytes = process.inputStream.readBytes()

        outputFile.outputStream().use {
            it.write(imageBytes)
        }

        VfsUtil.markDirtyAndRefresh(false, false, false, outputFile)
    }

    private fun toggleRecording() {
        if (!DeviceManager.isAdbDeviceConnected()) {
            Messages.showErrorDialog("No device connected.\nPlease connect a device via ADB.", "ADB Error")
            return
        }
        if (recordingProcess == null) {
            startRecording()
        } else {
            stopRecording()
        }
    }

    private fun startRecording() {
        try {
            val descriptor = FileSaverDescriptor("Save Recording", "Save as MP4", "mp4")
            val fileWrapper = FileChooserFactory.getInstance()
                .createSaveFileDialog(descriptor, null)
                .save(null as VirtualFile?, "recording.mp4")

            fileWrapper?.file?.let { outputFile ->
                savedRecordingFile = outputFile
                recordingProcess = ProcessBuilder("adb", "shell", "screenrecord", "/sdcard/demo.mp4").start()

                recordButton.text = "Stop"
                Messages.showInfoMessage("Recording started. Press 'Stop Record' to finish.", "Recording")
            }
        } catch (ex: Exception) {
            Messages.showErrorDialog(ex.message ?: "Start recording failed", "Record Error")
        }
    }

    private fun stopRecording() {
        try {
            ProcessBuilder("adb", "shell", "pkill", "-l2", "screenrecord").start().waitFor()
            recordingProcess?.waitFor()
            recordingProcess = null

            savedRecordingFile?.let { outputFile ->
                val pullProcess = ProcessBuilder("adb", "pull", "/sdcard/demo.mp4", outputFile.absolutePath).start()
                pullProcess.waitFor()

                ProcessBuilder("adb", "shell", "rm", "/sdcard/demo.mp4").start().waitFor()

                VfsUtil.markDirtyAndRefresh(false, false, false, outputFile)

                Messages.showInfoMessage("Recording saved: ${outputFile.absolutePath}", "Record Success")
            }

            recordButton.text = "Start"
        } catch (ex: Exception) {
            Messages.showErrorDialog(ex.message ?: "Stop recording failed", "Record Error")
        }
    }
}
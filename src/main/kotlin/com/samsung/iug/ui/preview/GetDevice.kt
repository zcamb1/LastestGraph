package com.samsung.iug.ui.preview

import com.android.ddmlib.IDevice
import com.intellij.icons.AllIcons.*
import com.intellij.openapi.diagnostic.thisLogger
import com.samsung.iug.utils.AdbUtils
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JPanel
import javax.swing.SwingUtilities
import java.awt.Color

object GetDevice {
    val panel: JPanel
    private val comboBox = JComboBox<String>()
    var device = ""

    init {
        panel = JPanel(FlowLayout(FlowLayout.LEFT))
        panel.border = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK)
        AdbUtils.initAdb()
        thisLogger().warn("adb yuguygyug")
        val devices = AdbUtils.getConnectedDevices()
        if (devices.isEmpty()) {
            comboBox.addItem("None")
        } else {
            devices.forEach {
                val manufacturer = it.getProperty("ro.product.manufacturer") ?: "Unknown"
                val model = it.getProperty("ro.product.model")  ?: "Unknown"
                comboBox.addItem("$manufacturer $model")
            }
        }

        val previousSelection: String? = comboBox.selectedItem?.toString()
        if (previousSelection != "None") {
            val serial = getSerialFromName(previousSelection.toString())
            if (serial != null && serial != "None") {
                device = serial
            }
        }

        comboBox.preferredSize = Dimension(200, 30)
        panel.add(comboBox)

        AdbUtils.addDeviceListener { device ->
            SwingUtilities.invokeLater {
                refreshDeviceList(device)
            }
        }

        comboBox.addActionListener{
            val selected = comboBox.selectedItem?.toString() ?: return@addActionListener
            val serial = getSerialFromName(selected)
            if (serial != null && serial != "None") {
                device = serial
            }
        }

        val buttonConnect = JButton(Actions.Execute)
        buttonConnect.preferredSize = Dimension(50, 30)
        panel.add(buttonConnect)

        var isRunning = false

        buttonConnect.addActionListener {
            if (device != "") {
                if (isRunning) {
                    buttonConnect.icon = Actions.Execute
                    isRunning = false
                    ScreenMirror.stopStream(device)
                } else {
                    buttonConnect.icon = Actions.Pause
                    isRunning = true
                    ScreenMirror.startStream(device)
                }
            }
        }

        val buttonControl = JButton("Start Control Device")
        panel.add(buttonControl)

        var isControl = false

        buttonControl.addActionListener {
            if (device != "") {
                if (isControl) {
                    buttonControl.text = "Start Control Device"
                    isControl = false
                    ScreenMirror.stopControl()
                } else {
                    buttonControl.text = "Stop Control Device"
                    isControl = true
                    ScreenMirror.startControl(device)
                }
            }
        }

    }

    private fun refreshDeviceList(devices: List<IDevice>) {
        comboBox.removeAllItems()

        if (devices.isEmpty()) {
            comboBox.addItem("None")
        } else {
            devices.forEach{
                val manufacturer = it.getProperty("ro.product.manufacturer") ?: "Unknown"
                val model = it.getProperty("ro.product.model")  ?: "Unknown"
                comboBox.addItem("$manufacturer $model")
            }
        }
    }

    private fun getSerialFromName(name: String): String? {
        return AdbUtils.getConnectedDevices().find {
            "${it.getProperty("ro.product.manufacturer") ?: "Unknown"} ${it.getProperty("ro.product.model")  ?: "Unknown"}" == name || it.serialNumber == name
        }?.serialNumber
    }
}
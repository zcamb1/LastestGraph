package com.samsung.iug.utils

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener
import com.android.ddmlib.IDevice
import com.samsung.iug.device.getAdb

object AdbUtils: IDeviceChangeListener {
    private var bridge: AndroidDebugBridge? = null
    private val listeners = mutableListOf<(List<IDevice>) -> Unit> ()
//    private val adbPath = GetAdb().absolutePath
    private val adbPath = getAdb().absolutePath

    fun initAdb() {
        AndroidDebugBridge.initIfNeeded(false)
        bridge = AndroidDebugBridge.createBridge(adbPath, false)

        for (i in 0..10) {
            if (bridge?.hasInitialDeviceList() == true) break
            Thread.sleep(500)
        }

        AndroidDebugBridge.addDeviceChangeListener(this)
    }

    fun getConnectedDevices(): List<IDevice> {
        return bridge?.devices?.toList() ?: emptyList()
    }

    fun addDeviceListener(listener: (List<IDevice>) -> Unit) {
        listeners.add(listener)
    }

    override fun deviceConnected(device: IDevice?) {
        notifyListeners()
    }

    override fun deviceDisconnected(device: IDevice?) {
        notifyListeners()
    }

    override fun deviceChanged(device: IDevice?, changeMask: Int) {
        notifyListeners()
    }

    private fun notifyListeners() {
        val devices = getConnectedDevices()
        listeners.forEach{it(devices)}
    }
}
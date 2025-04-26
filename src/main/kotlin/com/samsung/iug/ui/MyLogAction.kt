package com.samsung.iug.ui

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.thisLogger
import com.samsung.iug.service.Log

//temp added
class MyLogAction : AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        thisLogger().warn("QQ haha")
        Log.e("MyPlugin", "User clicked Test Log!")
        Log.w("QQ", "User clicked Test Log!")
    }
}
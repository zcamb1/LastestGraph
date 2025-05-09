package com.samsung.iug.ui.manage

import com.intellij.openapi.project.Project

class ScreenManager private constructor(private val project: Project) {
    private val loginScreen = LoginScreen()
    private val iugMakerScreen = IUGMakerScreen(project)

    fun showLoginScreen() {
        loginScreen.setVisible(true)
        iugMakerScreen.setVisible(false)
    }

    fun showIUGMakerScreen() {
        loginScreen.setVisible(false)
        iugMakerScreen.setVisible(true)
    }

    companion object {
        private var _instance: ScreenManager? = null
        fun init(project: Project) {
            if (_instance == null) {
                _instance = ScreenManager(project)
            }
        }

        val instance: ScreenManager
            get() = _instance ?: throw IllegalStateException("ScreenManager is not init")
    }
}
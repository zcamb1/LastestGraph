package com.samsung.iug.ui.manage

import com.intellij.openapi.project.Project
import com.samsung.iug.ui.iug.IUGMaker
import com.samsung.iug.ui.login.LoginDialog
import javax.swing.SwingUtilities

interface Screen {
    fun setVisible(visible: Boolean)
}

class LoginScreen : Screen {
    private var loginDialog: LoginDialog? = null
    override fun setVisible(visible: Boolean) {
        if (visible) {
            loginDialog = LoginDialog()
            SwingUtilities.invokeLater {
                loginDialog?.isVisible = true
            }
        } else {
            loginDialog?.dispose()
        }
    }
}

class IUGMakerScreen(private val project: Project) : Screen {
    override fun setVisible(visible: Boolean) {
        if (visible) {
            IUGMaker(project)
        }
    }
}
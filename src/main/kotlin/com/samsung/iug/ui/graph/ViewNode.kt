package com.samsung.iug.ui.graph

import java.awt.Rectangle

object ViewNode {
    var stepId: String = ""
    var screenId: String = ""
    var guildContent: String = ""
    var className: String = ""
    var resourceId: String = ""
    var text: String = ""
    var bounds: Rectangle = Rectangle(0, 0, 0, 0)
    var contentDescription: String = ""
    var currentScreen: String = ""

    fun reset() {
        this.apply {
            stepId = ""
            screenId = ""
            guildContent = ""
            className = ""
            resourceId = ""
            text = ""
            bounds = Rectangle(0, 0, 0, 0)
            contentDescription = ""
            currentScreen = ""
        }
    }
}
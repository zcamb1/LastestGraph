package com.samsung.iug.utils

import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import javax.swing.Icon
import javax.swing.ImageIcon

object ImageHelper {
    fun recolorImageIcon(original: ImageIcon, newColor: Color): ImageIcon {
        val image = BufferedImage(
            original.iconWidth,
            original.iconHeight,
            BufferedImage.TYPE_INT_ARGB
        )

        val g = image.createGraphics()
        g.drawImage(original.image, 0, 0, null)
        g.dispose()

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                val rgba = image.getRGB(x, y)
                val alpha = rgba shr 24 and 0xff
                if (alpha != 0) {
                    image.setRGB(x, y, (newColor.rgb and 0x00ffffff) or (alpha shl 24))
                }
            }
        }

        return ImageIcon(image)
    }

    fun resizeIcon(icon: ImageIcon, width: Int, height: Int): Icon {
        val resizedImage = icon.image.getScaledInstance(width, height, Image.SCALE_SMOOTH)
        return ImageIcon(resizedImage)
    }
}
package com.samsung.iug.ui.layoutinspector

import org.w3c.dom.Element
import org.w3c.dom.Node
import java.awt.Rectangle
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

object LayoutParser {

    private var lastLayoutHash: String? = null

    fun parse(device: String): List<ViewNode>? {
        return try {
            // Dump UI XML to device
            Runtime.getRuntime().exec("adb -s $device shell uiautomator dump /sdcard/window_dump.xml").waitFor()
            // Pull XML to host
            Runtime.getRuntime().exec("adb -s $device pull /sdcard/window_dump.xml").waitFor()

            val file = File("window_dump.xml")
            if (!file.exists()) return null

            val layoutDump = file.readText()

            val currentHash = sha256(layoutDump)
            if (currentHash == lastLayoutHash) {
                return null
            }
            lastLayoutHash = currentHash

            val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
            val root = doc.documentElement
            val rootNode = parseNode(root.firstChild)

            listOf(rootNode)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun sha256(input: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun parseNode(node: Node?): ViewNode {
        if (node == null || node.nodeType != Node.ELEMENT_NODE) {
            return ViewNode("Unknown", "", "", Rectangle())
        }
        val element = node as Element
        val className = element.getAttribute("class")
        val resourceId = element.getAttribute("resource-id")
        val text = element.getAttribute("text")
        val bounds = parseBounds(element.getAttribute("bounds"))

        val children = mutableListOf<ViewNode>()
        val childNodes = element.childNodes
        for (i in 0 until childNodes.length) {
            val child = childNodes.item(i)
            if (child.nodeType == Node.ELEMENT_NODE) {
                children.add(parseNode(child))
            }
        }

        return ViewNode(className, resourceId, text, bounds, children)
    }

    private fun parseBounds(boundsStr: String): Rectangle {
        // Format: [0,123][540,234]
        val regex = "\\[(\\d+),(\\d+)]\\[(\\d+),(\\d+)]".toRegex()
        val match = regex.find(boundsStr)
        return if (match != null) {
            val (x1, y1, x2, y2) = match.destructured
            Rectangle(x1.toInt(), y1.toInt(), x2.toInt() - x1.toInt(), y2.toInt() - y1.toInt())
        } else Rectangle()
    }
}
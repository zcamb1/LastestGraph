```
package com.samsung.iug.ui.graph.node

import com.intellij.openapi.ui.ComboBox
import com.samsung.iug.graph.listNode
import com.samsung.iug.ui.graph.GraphPanel
import com.samsung.iug.ui.graph.GraphUI
import com.samsung.iug.ui.graph.ViewNode
import com.samsung.iug.ui.graph.edit.EditNodeDialog
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.geom.RoundRectangle2D
import javax.swing.JMenuItem
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.SwingUtilities

/**
 * A custom node UI that displays ID, Name, and Description.
 * Used for workflow editor nodes.
 */
class WorkflowNodeView : JPanel() {
    var stepID: String = ""
    var guildContent: String = ""
    var parent: WorkflowNodeView? = null
    val children: MutableList<WorkflowNodeView> = mutableListOf()
    var level: Int = -1
    var point: Int = 0
    var scaleFactor = 1.0

    var selected: Boolean = false
    var selectedHandle: HandlePosition? = null
    var highlighted: Boolean = false


    enum class HandlePosition {
        TOP, RIGHT, BOTTOM, LEFT
    }

    init {
        layout = null
        isOpaque = false

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val handle = detectHandleAt(e.point)

                if (handle != null) {
                    val globalPoint = SwingUtilities.convertPoint(this@WorkflowNodeView, e.point, GraphPanel)
                    GraphPanel.draggingNode = this@WorkflowNodeView
                    GraphPanel.draggingHandle = handle
                    GraphPanel.dragEndPoint = globalPoint
                } else {
                    GraphPanel.deselectAllNodes()
                    selected = true
                    selectedHandle = null
                    repaint()
                }
            }

            override fun mouseClicked(e: MouseEvent) {
                if(GraphPanel.panMode){
                    val parentPanel=parent as? JPanel?:return
                    val parentEvent = SwingUtilities.convertMouseEvent(this@WorkflowNodeView,e,parentPanel)
                    parentPanel.dispatchEvent(parentEvent)
                }
                else{
                    if (e.clickCount == 2) {
                        if (stepID == "User Query") {
                            EditUtteranceDialog(this@WorkflowNodeView, this@WorkflowNodeView).isVisible = true
                        } else {
                            val node = listNode.findNode(stepID)
                            if (node != null) {
                                ViewNode.fill(node.stepId, node.screenId, node.guildContent, node.className, node.resourceId, node.text, node.bounds, node.contentDescription, "", "", node.screenMatcher, node.completionType)
                                EditNodeDialog(this@WorkflowNodeView, this@WorkflowNodeView).isVisible = true
                            }
                        }
                    }
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                if(GraphPanel.panMode){
                    val parentPanel=parent as? JPanel?:return
                    val parentEvent = SwingUtilities.convertMouseEvent(this@WorkflowNodeView,e,parentPanel)
                    parentPanel.dispatchEvent(parentEvent)
                }

                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e)
                }

                if (GraphPanel.draggingNode == this@WorkflowNodeView) {
                    val target = GraphPanel.findNodeAtScreen(e.locationOnScreen, this@WorkflowNodeView)
                    if (target != null && target != this@WorkflowNodeView) {
                        GraphPanel.addEdgeString(stepID, target.stepID)
                    }

                    selected = false
                    selectedHandle = null
                    repaint()

                    for (node in GraphPanel.nodes) {
                        node.highlighted = false
                    }

                    GraphPanel.draggingNode = null
                    GraphPanel.draggingHandle = null
                    GraphPanel.dragEndPoint = null
                    GraphPanel.repaint()
                }
            }

        })

        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                if (GraphPanel.draggingNode == this@WorkflowNodeView) {
                    GraphPanel.dragEndPoint = SwingUtilities.convertPoint(this@WorkflowNodeView, e.point, GraphPanel)
        
                    // Tìm node đang hover (trừ node đang kéo)
                    val screenPoint = SwingUtilities.convertPoint(this@WorkflowNodeView, e.point, null)
                    val target = GraphPanel.findNodeAtScreen(screenPoint, this@WorkflowNodeView)
                    for (node in GraphPanel.nodes) {
                        node.highlighted = (node == target)
                    }
        
                    GraphPanel.repaint()
                }
            }
        })
    }

    private fun detectHandleAt (p: Point): HandlePosition? {
        val r = (8 * scaleFactor).toInt()
        val hitBoxSize = r + 4

        val handlePoints = mapOf(
            HandlePosition.TOP to Point(width / 2, 0),
            HandlePosition.RIGHT to Point(width, height / 2),
            HandlePosition.BOTTOM to Point(width / 2, height),
            HandlePosition.LEFT to Point(0, height / 2)
        )

        for ((pos, center) in handlePoints) {
            val rect = Rectangle(center.x - hitBoxSize / 2, center.y - hitBoxSize / 2, hitBoxSize, hitBoxSize)
            if (rect.contains(p)) return pos
        }

        return null
    }

    private fun showContextMenu(e: MouseEvent) {
        SwingUtilities.invokeLater {
            val popupMenu = JPopupMenu()

            val deleteItem = JMenuItem("Delete Node")
            deleteItem.addActionListener {
                deleteNode()
            }

            if (stepID != "User Query") {
                popupMenu.add(deleteItem)
            }

            val addConnectionItem = JMenuItem("Add Connection")

            addConnectionItem.addActionListener {
                GraphPanel.showAddConnectionDialog(this)
            }

            popupMenu.add(addConnectionItem)

            popupMenu.isLightWeightPopupEnabled = false
            popupMenu.show(this@WorkflowNodeView, e.x , e.y)
        }
    }

    private fun deleteNode() {
        listNode.removeNode(stepID)
        GraphPanel.removeNode(stepID)
    }

    override fun toString(): String {
        return stepID
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)

        val w = width
        val h = height

        // Define border variables
        val borderThickness = 1.5f
        val cornerRadius = (33 * scaleFactor).toInt()

        // Create a background shape for the node
        val nodeBackground = RoundRectangle2D.Float(
            2f, 2f, 
            (w - 4).toFloat(), (h - 4).toFloat(), 
            cornerRadius.toFloat(), cornerRadius.toFloat()
        )

        // Create outer border shape
        val nodeBorder = RoundRectangle2D.Float(
            1f, 1f, 
            (w - 2).toFloat(), (h - 2).toFloat(), 
            cornerRadius.toFloat(), cornerRadius.toFloat()
        )

        // === Draw border with multi-pass technique for increased sharpness ===
        if (stepID == "User Query") {
            // Enhanced purple color for User Query nodes
            g2.color = Color(146, 119, 255)
        } else {
            g2.color = Color(74, 222, 128)
        }

        // First pass - thicker stroke
        g2.stroke = BasicStroke(
            borderThickness + 0.5f,
            BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND
        )
        g2.draw(nodeBorder)
        
        // Second pass - sharper stroke
        g2.stroke = BasicStroke(
            borderThickness,
            BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND
        )
        g2.draw(nodeBackground)

        // === Icon (top left) ===
        val iconX = (17 * scaleFactor).toInt()
        val iconY = (14 * scaleFactor).toInt()
        val iconSize = (27 * scaleFactor).toInt()
        g2.fillOval(iconX, iconY, iconSize, iconSize)

        // === Text info ===
        g2.color = Color.WHITE

        // Define variables for font sizes
        val descFontSize = (15 * scaleFactor).toInt()
        val titleFontSize = (15 * scaleFactor).toInt()
        
        // Define variables for spacing
        val initialSpacing = (25 * scaleFactor).toInt()
        val textSpacing = (27 * scaleFactor).toInt()
        val textLeftPadding = (5 * scaleFactor).toInt()
        
        // Calculate positions
        val baseY = iconY + iconSize
        val descY = baseY + initialSpacing
        val titleY = descY + textSpacing
        val textX = iconX + textLeftPadding
        
        // Description text (on top, closer to the icon)
        g2.font = Font("Arial", Font.PLAIN, descFontSize)
        g2.drawString(guildContent, textX, descY)
        
        // Title text (below description)
        g2.font = Font("Arial", Font.BOLD, titleFontSize)
        g2.drawString(stepID, textX, titleY)

        if (selected) {
            g2.color = Color.WHITE
            val r = (20 * scaleFactor).toInt()
            val rBig = (20 * scaleFactor).toInt()
         

            val positions = mapOf(
                HandlePosition.TOP to Point(w / 2 - r / 2, 0 - r / 2),
                HandlePosition.RIGHT to Point( w - r / 2, h / 2 - r / 2),
                HandlePosition.BOTTOM to Point(w / 2 - r / 2, h - r / 2),
                HandlePosition.LEFT to Point(0 - r / 2, h / 2 - r / 2)
            )

            for ((pos, point) in positions) {
                val size = if (selectedHandle == pos) rBig else r
                g2.fillOval(point.x, point.y, size, size)
            }

                   if (selectedHandle == HandlePosition.RIGHT) {
            g2.color = Color(200, 200, 255)  // Light blue highlight
                    g2.clip = null  
        }
        }

        if (highlighted) {
            val g2 = g as Graphics2D
            val margin = 2 // hoặc 3, 4 tuỳ bạn muốn viền dày hay mỏng
            g2.color = Color(255, 255, 0, 120) // vàng nhạt trong suốt
            g2.stroke = BasicStroke(4f)
            g2.drawRoundRect(
                margin, margin,
                width - 2 * margin,
                height - 2 * margin,
                (33 * scaleFactor).toInt(), // bo góc giống border node
                (33 * scaleFactor).toInt()
            )
        }
    }

    override fun contains(x: Int, y: Int): Boolean {
        return if(GraphPanel.panMode) false else super.contains(x, y)
    }

    override fun getPreferredSize(): Dimension {
        return Dimension((300*scaleFactor).toInt(), (115*scaleFactor).toInt())
    }
}
```

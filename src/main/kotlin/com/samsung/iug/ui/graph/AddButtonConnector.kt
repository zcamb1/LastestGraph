package com.samsung.iug.ui.graph

import com.mxgraph.model.mxCell
import com.mxgraph.model.mxGeometry
import com.mxgraph.view.mxGraph
import com.mxgraph.util.mxConstants
import javax.swing.JPanel

/**
 * Class xử lý tạo nút dấu cộng và kết nối nét đứt trong graph
 * Cung cấp các phương thức để tạo nút dấu cộng, kết nối node và xử lý sự kiện
 */
object AddButtonConnector {
    
    // Hằng số cho vị trí và kích thước
    const val BUTTON_SIZE = 36.0
    const val HORIZONTAL_SPACING = 150.0
    
    /**
     * Tạo nút dấu cộng (+) cho node nguồn và kết nối bằng nét đứt
     * 
     * @param graph Đối tượng mxGraph
     * @param parent Đối tượng parent trong graph
     * @param sourceNode Node nguồn cần thêm nút dấu cộng
     * @return Đối tượng nút dấu cộng đã tạo
     */
    fun createAddButtonForNode(graph: mxGraph, parent: Any, sourceNode: Any): mxCell {
        val nodeGeometry = graph.getCellGeometry(sourceNode)
        
        // Determine if the source node is a step node
        val isStepNode = graph.getCellStyle(sourceNode)?.toString()?.contains("stepNode") == true
        
        // Set the appropriate style
        val buttonStyle = if (isStepNode) "stepAddButton" else "addButton"
        val edgeStyle = if (isStepNode) "stepEdge" else "defaultEdge"
        
        // Tính toán vị trí trung tâm theo chiều dọc cho nút dấu cộng
        val nodeCenterY = nodeGeometry.y + (nodeGeometry.height / 2.0) - (BUTTON_SIZE / 2.0)
        
        // Tạo nút dấu cộng với ký hiệu "+"
        val addButtonNode = graph.insertVertex(
            parent, null, "+", 
            nodeGeometry.x + nodeGeometry.width + HORIZONTAL_SPACING,
            nodeCenterY,
            BUTTON_SIZE, BUTTON_SIZE,
            buttonStyle
        ) as mxCell
        
        // Kết nối node nguồn với nút dấu cộng bằng nét đứt
        graph.insertEdge(
            parent, null, "", 
            sourceNode, addButtonNode, 
            edgeStyle
        )
        
        return addButtonNode
    }
    
    /**
     * Tạo nút dấu cộng mới sau khi thêm node mới
     * 
     * @param graph Đối tượng mxGraph
     * @param parent Đối tượng parent trong graph
     * @param node Node mới vừa thêm
     * @param referenceGeometry Đối tượng geometry tham chiếu
     * @return Nút dấu cộng mới
     */
    fun createAddButtonAfterNode(
        graph: mxGraph, 
        parent: Any, 
        node: Any, 
        referenceGeometry: mxGeometry
    ): mxCell {
        val nodeGeometry = graph.getCellGeometry(node)
        
        // Tính toán vị trí cho nút dấu cộng mới
        val spacing = HORIZONTAL_SPACING
        val newButtonX = nodeGeometry.x + nodeGeometry.width + spacing
        val newButtonY = referenceGeometry.y 
        
        // Tạo nút dấu cộng mới
        val newAddButton = graph.insertVertex(
            parent, null, "+", 
            newButtonX, newButtonY, 
            BUTTON_SIZE, BUTTON_SIZE, 
            "addButton"
        ) as mxCell
        
        // Kết nối node với nút dấu cộng mới
        graph.insertEdge(
            parent, null, "", 
            node, newAddButton, 
            "defaultEdge"
        )
        
        return newAddButton
    }
    
    /**
     * Xác định xem một cell có phải là nút dấu cộng không
     * 
     * @param graph Đối tượng mxGraph 
     * @param cell Cell cần kiểm tra
     * @return true nếu cell là nút dấu cộng, ngược lại false
     */
    fun isAddButton(graph: mxGraph, cell: Any): Boolean {
        val style = graph.getCellStyle(cell)
        return style.getOrDefault(mxConstants.STYLE_SHAPE, "") == "addButtonShape" || 
               (cell is mxCell && cell.value == "+")
    }
    
    /**
     * Tìm node nguồn mà nút dấu cộng kết nối tới
     * 
     * @param graph Đối tượng mxGraph
     * @param addButton Nút dấu cộng cần tìm node nguồn
     * @return Node nguồn hoặc null nếu không tìm thấy
     */
    fun findParentNodeForAddButton(graph: mxGraph, addButton: mxCell): mxCell? {
        val edges = graph.getEdges(addButton)
        
        // Tìm cạnh đi vào nút dấu cộng
        for (edge in edges) {
            val edgeCell = edge as mxCell
            if (edgeCell.source != addButton) {
                return edgeCell.source as mxCell
            }
        }
        
        return null
    }
    
    /**
     * Xử lý sự kiện khi người dùng click vào nút dấu cộng
     * 
     * @param graphPanel Panel chứa graph
     * @param graph Đối tượng mxGraph
     * @param cell Cell được click (nút dấu cộng)
     */
    fun handleAddButtonClick(graphPanel: JPanel, graph: mxGraph, cell: mxCell) {
        if (isAddButton(graph, cell)) {
            // Lưu trữ button được click để dùng khi tạo node mới
            GraphUI.setSourceButtonCell(cell)
            
            // Hiển thị dialog thêm node mới (AddNodeUI)
            GraphUI.showAddNodeUI()
        }
    }
} 
/* 文件名：     TableShortcutModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-4
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import org.dom4j.Element;

import cn.sunline.suncard.powerdesigner.model.factory.TableModelFactory;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 表格快捷方式模型
 * @author  Manzhizhen
 * @version 1.0, 2012-12-4
 * @see 
 * @since 1.0
 */
public class TableShortcutModel implements DataObjectInterface{
	private String id; // 快捷方式的ID
	private TableModel targetTableModel; // 该快捷方式所指向的表格模型
	private PhysicalDiagramModel physicalDiagramModel;	// 该表格快捷方式所属的物理图形模型

	private static String elementName = "tableShortcutModel"; // 保存为document时候的顶节点name
	private Log logger = LogManager.getLogger(TableShortcutModel.class
			.getName());
	
	public TableModel getTargetTableModel() {
		return targetTableModel;
	}
	
	public void setTargetTableModel(TableModel targetTableModel) {
		this.targetTableModel = targetTableModel;
	}
	
	public PhysicalDiagramModel getPhysicalDiagramModel() {
		return physicalDiagramModel;
	}
	
	public void setPhysicalDiagramModel(PhysicalDiagramModel physicalDiagramModel) {
		this.physicalDiagramModel = physicalDiagramModel;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public static String getElementName() {
		return elementName;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element tableShortcutModelElement = parent.addElement(elementName);
		
		tableShortcutModelElement.addAttribute("id", id == null ? "" : id);
		tableShortcutModelElement.addAttribute("targetTableId", targetTableModel.getId());
		
		return tableShortcutModelElement;
	}
	
	@Override
	public TableShortcutModel clone() throws CloneNotSupportedException {
		TableShortcutModel newTableShortcutModel = new TableShortcutModel();
		newTableShortcutModel.setId(id);
		newTableShortcutModel.setPhysicalDiagramModel(physicalDiagramModel);
		newTableShortcutModel.setTargetTableModel(targetTableModel);
		
		return newTableShortcutModel;
	}

	@Override
	public TableShortcutModel getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("TableShortcutModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("TableShortcutModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		if(obj.length != 1 || !(obj[0] instanceof FileModel)) {
			logger.warn("传入的FileModel为空或不正确，无法将xml转换为TableShortcutModel对象！");
			return null;
		}
		
		FileModel fileModel = (FileModel) obj[0];
		
		setId(element.attributeValue("id").trim());
//		// 先建立一个临时的TableModel来储存未来的targetTableModel的ID
//		TableModel tempTableModel = new TableModel();
//		tempTableModel.setId(element.attributeValue("targetTableId").trim());
//		setTargetTableModel(tempTableModel);
//		
//		// 注意：targetTableModel需要额外设置
		
		TableModel tempTableModel = TableModelFactory.getTableModel(fileModel
				, element.attributeValue("targetTableId").trim());
		if(tempTableModel != null) {
			setTargetTableModel(tempTableModel);
		} else {
			logger.error("给表格快捷方式模型：" + id + "匹配目标表格对象失败，在TableModelFactory找不到对应的表格对象ID:" 
					+ element.attributeValue("targetTableId").trim());
		}
		
		return this;
	}
	
	
}

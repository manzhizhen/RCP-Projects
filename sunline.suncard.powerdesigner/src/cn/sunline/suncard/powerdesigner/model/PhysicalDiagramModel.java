/* 文件名：     PhysicalDragramModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import cn.sunline.suncard.powerdesigner.gef.xml.LineXmlProperty;
import cn.sunline.suncard.powerdesigner.gef.xml.NodeXmlProperty;
import cn.sunline.suncard.powerdesigner.model.factory.TableModelFactory;
import cn.sunline.suncard.powerdesigner.model.factory.TableShortcutModelFactory;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 物理图形模型
 * 一个物理图形模型下可以包含多个表（TableModel）
 * @author  Manzhizhen
 * @version 1.0, 2012-9-5
 * @see 
 * @since 1.0
 */
public class PhysicalDiagramModel implements DataObjectInterface{
	private String id;	// 物理图形模型名称（在树节点中也作为ID的一部分，同一个物理数据模型中该名称必须唯一）
	private String name;	// 物理图形模型的描述，用于显示
	private String note;	// 备注
	
	// 物理图形模型下面的所有表格，（注意，这里的Key是表格的ID，不是表格的名称）
	private LinkedHashMap<String, TableModel> tableMap = new LinkedHashMap<String, TableModel>(); 
	
	private Map<String, TableShortcutModel> tableShortcutMap = new HashMap<String, TableShortcutModel>(); // 物理图形模型下面的所有表格快捷方式
	
	private PackageModel packageModel; // 该物理图形模型所属的包模型；
	
	// 这个模型比较特别，应为它要支持GEF画图，所以还要额外的储存GEF图元信息
	private List<NodeXmlProperty> nodeXmlPropertyList = new ArrayList<NodeXmlProperty>();
	private List<LineXmlProperty> lineXmlPropertyList = new ArrayList<LineXmlProperty>();
	
	private static String elementName = "physicalDiagramModel"; // 保存为document时候的顶节点name
	private Log logger = LogManager.getLogger(PhysicalDiagramModel.class
			.getName());

	public Map<String, TableModel> getTableMap() {
		return tableMap;
	}

	public void setTableMap(LinkedHashMap<String, TableModel> tableMap) {
		if(tableMap != null) {
			this.tableMap = tableMap;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PackageModel getPackageModel() {
		return packageModel;
	}

	public void setPackageModel(PackageModel packageModel) {
		this.packageModel = packageModel;
	}
	
	public List<NodeXmlProperty> getNodeXmlPropertyList() {
		return nodeXmlPropertyList;
	}

	public void setNodeXmlPropertyList(List<NodeXmlProperty> nodeXmlPropertyList) {
		this.nodeXmlPropertyList = nodeXmlPropertyList;
	}

	public List<LineXmlProperty> getLineXmlPropertyList() {
		return lineXmlPropertyList;
	}

	public void setLineXmlPropertyList(List<LineXmlProperty> lineXmlPropertyList) {
		this.lineXmlPropertyList = lineXmlPropertyList;
	}
	
	public static String getElementName() {
		return elementName;
	}
	
	public Map<String, TableShortcutModel> getTableShortcutMap() {
		return tableShortcutMap;
	}

	public void setTableShortcutMap(Map<String, TableShortcutModel> tableShortcutMap) {
		this.tableShortcutMap = tableShortcutMap;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element physicalDiagramModelElement = parent.addElement(elementName);
		
		physicalDiagramModelElement.addAttribute("id", id == null ? "" : id);
		physicalDiagramModelElement.addAttribute("name", name == null ? "" : name);
		physicalDiagramModelElement.setText(note == null ? "" : note);
		
		Element tableModelE;
		Collection<TableModel> tableModels = tableMap.values();
		for(TableModel tableModel : tableModels) {
			tableModelE = physicalDiagramModelElement.addElement(TableModel.getElementName());
			tableModelE.addAttribute("id", tableModel.getId());
//			tableModel.getElementFromObject(physicalDiagramModelElement);
		}
		
		Element tableShortcutModelE;
		Collection<TableShortcutModel> tableShortcutModels = tableShortcutMap.values();
		for(TableShortcutModel tableShortcutModel : tableShortcutModels) {
			tableShortcutModelE = physicalDiagramModelElement.addElement(tableShortcutModel.getElementName());
			tableShortcutModelE.addAttribute("id", tableShortcutModel.getId());
//			tableShortcutModel.getElementFromObject(physicalDiagramModelElement);
		}
		
		return physicalDiagramModelElement;
	}

	@Override
	public PhysicalDiagramModel getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("PhysicalDiagramModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("PhysicalDiagramModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		if(obj.length != 1 || !(obj[0] instanceof FileModel)) {
			logger.warn("传入的FileModel为空或不正确，无法将xml转换为PhysicalDiagramModel对象！");
			return null;
		}
		
		FileModel fileModel = (FileModel) obj[0];
		
		setId(element.attributeValue("id").trim());
		setName(element.attributeValue("name").trim());
		setNote(element.getText());
		
		tableMap.clear();
		List<Element> elementList = element.elements(TableModel.getElementName());
		if(elementList != null) {
			for(Element tableModelElement : elementList) {
//				TableModel tableModel = new TableModel().getObjectFromElement(tableModelElement);
				TableModel tableModel = TableModelFactory.getTableModel(fileModel
						, tableModelElement.attributeValue("id").trim());
				if(tableModel != null) {
					tableModel.setPhysicalDiagramModel(this);
					tableMap.put(tableModel.getTableName(), tableModel);
				} else {
					logger.error("一个表格添加到物理图形模型失败，表格工厂找不到对应的表格ID:" 
							+ tableModelElement.attributeValue("id").trim());
				}
			}
		}
		
		tableShortcutMap.clear();
		elementList = element.elements(TableShortcutModel.getElementName());
		if(elementList != null) {
			for(Element tableShortcutModelElement : elementList) {
//				TableShortcutModel tableShortcutModel = new TableShortcutModel().getObjectFromElement(tableShortcutModelElement);
				
				TableShortcutModel tableShortcutModel = TableShortcutModelFactory.getTableShortcutModel(fileModel
						, tableShortcutModelElement.attributeValue("id").trim());
				if(tableShortcutModel != null) {
					tableShortcutModel.setPhysicalDiagramModel(this);
					tableShortcutMap.put(tableShortcutModel.getId(), tableShortcutModel);
				} else {
					logger.error("一个表格快捷方式添加到物理图形模型失败，表格工厂找不到对应的表格快捷方式ID:" 
							+ tableShortcutModelElement.attributeValue("id").trim());
				}
				
				
			}
		}
		
		return this;
	}
	
}

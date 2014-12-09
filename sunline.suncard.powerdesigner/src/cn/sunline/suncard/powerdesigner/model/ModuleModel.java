/* 文件名：     ModuleModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import cn.sunline.suncard.powerdesigner.model.factory.TableModelFactory;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 模块模型
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-12-5
 * @see
 * @since 1.0
 */
public class ModuleModel implements DataObjectInterface{
	private String id; // 一个文件下面的模块ID必须唯一
	private String name;
	private String note;
	private ProductModel productModel; // 该模块所属的产品
	private LinkedHashSet<TableModel> tableModelSet = new LinkedHashSet<TableModel>(); // 该模块所包含的表格
	
	private static String elementName = "moduleModel"; // 保存为document时候的顶节点name
	
	private Log logger = LogManager.getLogger(ModuleModel.class
			.getName());

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public ProductModel getProductModel() {
		return productModel;
	}

	public void setProductModel(ProductModel productModel) {
		this.productModel = productModel;
	}

	public Set<TableModel> getTableModelSet() {
		return tableModelSet;
	}

	public void setTableModelSet(LinkedHashSet<TableModel> tableModelSet) {
		this.tableModelSet = tableModelSet;
	}

	public static String getElementName() {
		return elementName;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element moduleModelElement = parent.addElement(elementName);

		moduleModelElement.addAttribute("id", id == null ? "" : id);
		moduleModelElement.addAttribute("name", name == null ? "" : name);
		moduleModelElement.setText(note == null ? "" : note);
		
		for(TableModel tableModel : tableModelSet) {
			moduleModelElement.addElement("tableId").setText(tableModel.getId() == null 
					? "" : tableModel.getId());
		}
		
		return moduleModelElement;
	}

	@Override
	public ModuleModel getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("ModuleModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("ModuleModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		
		if(obj.length != 1 || (!(obj[0] instanceof FileModel) && !(obj[0] instanceof HashMap))) {
			logger.warn("传入的参数列表为空或不正确，无法将xml转换为ModuleModel对象！");
			return null;
		}
		
		FileModel fileModel = null;	// 如果是在解析数据库设计文件时，应该传入文件模型
		Map<String, TableModel> tableModelMap = null;	// 如果是在解析项目文件时，则应该传入一个表格对象的键值Map
		if(obj[0] instanceof FileModel) {
			fileModel = (FileModel) obj[0];
		} else {
			tableModelMap = (Map<String, TableModel>) obj[0];
		}
		
		setId(element.attributeValue("id").trim());
		setName(element.attributeValue("name").trim());
		setNote(element.getText().trim());
		
		// 注意，tableModelSet需要额外配置，这里只是暂时储存表格对象的ID
		tableModelSet.clear();
		List<Element> tableIdList = element.elements("tableId");
		if(tableIdList != null) {
			for(Element tableIdElement : tableIdList) {
//				TableModel tableModel = new TableModel();
//				tableModel.setId(tableIdElement.getTextTrim());
//				tableModelSet.add(tableModel);
				TableModel tableModel;
				if(fileModel != null) {
					tableModel = TableModelFactory.getTableModel(fileModel, tableIdElement.getTextTrim());
				} else {
					tableModel = tableModelMap.get(tableIdElement.getTextTrim());
				}
				if(tableModel != null) {
					tableModelSet.add(tableModel);
				} else {
					logger.error("给模块模型：" + name + "匹配表格对象失败，在TableModelFactory或键值Map找不到对应的表格对象ID:" 
							+ tableIdElement.getTextTrim());
				}
			}
		}
		
		
		return this;
	}
	
	@Override
	public ModuleModel clone() throws CloneNotSupportedException {
		ModuleModel newModuleModel = new ModuleModel();
		newModuleModel.setId(id);
		newModuleModel.setName(name);
		newModuleModel.setNote(note);
		
		// 产品模型和tableModelSet暂时没克隆
		
		return newModuleModel;
	}

}

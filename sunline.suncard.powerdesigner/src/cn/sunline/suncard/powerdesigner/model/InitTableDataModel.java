/* 文件名：     InitTableDataModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-22
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 初始化表格数据的List
 * @author  Manzhizhen
 * @version 1.0, 2012-11-22
 * @see 
 * @since 1.0
 */
public class InitTableDataModel implements DataObjectInterface{
	private List<TableDataModel> initDataList = new ArrayList<TableDataModel>(); // 用于在创建表格时初始化表格数据。
	private TableModel tableModel; // 属于的TableModel
	
	private static String elementName = "initTableDataModel"; // 保存为document时候的顶节点name
	
	private Log logger = LogManager.getLogger(InitTableDataModel.class
			.getName());

	
	public TableModel getTableModel() {
		return tableModel;
	}

	public void setTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element initDataModelElement = parent.addElement(elementName);
		
		initDataModelElement.addAttribute("refTableId", tableModel.getId());
		
		for(TableDataModel tableDataModel : initDataList) {
			Element dataListElement = initDataModelElement.addElement("dataMap");
			Set<ColumnModel> columnModelSet = tableDataModel.getDataMap().keySet();
			for(ColumnModel columnModel : columnModelSet) {
				Element columnValueElement = dataListElement.addElement("columnValue");
				columnValueElement.addAttribute("columnId", columnModel.getId());
				columnValueElement.setText(tableDataModel.getDataMap().get(columnModel));
			}
		}
		
		
		return initDataModelElement;
	}

	@Override
	public InitTableDataModel getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("InitTableDataModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("InitTableDataModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		if(obj.length != 1 || !(obj[0] instanceof FileModel)) {
			logger.warn("传入的参数列表为空或不正确，无法将xml转换为InitTableDataModel对象！");
			return null;
		}
		
		FileModel fileModel = (FileModel) obj[0];
		
		initDataList.clear();
		List<Element> dataMapList = element.elements("dataMap");
		for(Element dataMapElement : dataMapList) {
			List<Element> columnValueElementList = dataMapElement.elements("columnValue");
			if(columnValueElementList != null && !columnValueElementList.isEmpty()) {
				TableDataModel newTableDataModel = new TableDataModel();
				for(Element columnValueElement : columnValueElementList) {
					ColumnModel columnModel = ColumnModelFactory.getColumnModel(fileModel
							, columnValueElement.attributeValue("columnId").trim());
					if(columnModel != null) {
						newTableDataModel.getDataMap().put(columnModel
								, columnValueElement.getTextTrim());
					} else {
						logger.error("在ColumnModelFactory中找不到对应此ID的ColumnModel" +
								"，给InitTableDataModel匹配列对象失败:" + columnValueElement.attributeValue("columnId").trim());
					}
				}
				
				initDataList.add(newTableDataModel);
			}
		}
		
		// 注意：tableModel需要额外设置
		
		return this;
	}
	
	@Override
	public InitTableDataModel clone() throws CloneNotSupportedException {
		InitTableDataModel newDataModel = new InitTableDataModel();
		newDataModel.setTableModel(tableModel);
		for(TableDataModel oldTableDataModel : initDataList) {
			TableDataModel newTableDataModel = new TableDataModel();
			Set<ColumnModel> columnModelSet = oldTableDataModel.getDataMap().keySet();
			for(ColumnModel columnModel : columnModelSet) {
				newTableDataModel.getDataMap().put(columnModel, oldTableDataModel.getDataMap().get(columnModel));
			}
			
			newDataModel.initDataList.add(newTableDataModel);
		}
		
		
		return newDataModel;
	}

	public static String getElementName() {
		return elementName;
	}

	public List<TableDataModel> getInitDataList() {
		return initDataList;
	}
	
	
	
}

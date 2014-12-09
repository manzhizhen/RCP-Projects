/* 文件名：     StoredProcedureModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-14
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.model.factory.TableModelFactory;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 储存过程模型
 * @author  Manzhizhen
 * @version 1.0, 2012-12-14
 * @see 
 * @since 1.0
 */
public class StoredProcedureModel implements DataObjectInterface{
	private String id;
	private String name;
	private String storedProceduerStr = "";
	private ProductModel productModel;
	
	private static String elementName = "storedProcedureModel"; // 保存为document时候的顶节点name
	private Log logger = LogManager.getLogger(StoredProcedureModel.class
			.getName());
	
	public String getStoredProceduerStr() {
		return storedProceduerStr;
	}
	
	public void setStoredProceduerStr(String storedProceduerStr) {
		this.storedProceduerStr = storedProceduerStr;
	}
	
	public ProductModel getProductModel() {
		return productModel;
	}
	
	public void setProductModel(ProductModel productModel) {
		this.productModel = productModel;
	}
	
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

	/**
	 * 获取该对象真正的储存过程语句（去掉各种标记）
	 */
	public String getRealStoredProcedure() {
		String realStoredProceduer = storedProceduerStr;
		
		String[] strArray = storedProceduerStr.split(DmConstants.BLANK);
		
		// 获取标记集合
		Set<String> tableIdsWithPre = new HashSet<String>();
		Set<String> columnIdsWithPre = new HashSet<String>();
		
		for (String str : strArray) {
			if (str.startsWith(DmConstants.PRE_TABLE)) {
				tableIdsWithPre.add(str);
			}
			if (str.startsWith(DmConstants.PRE_COLUMN)) {
				columnIdsWithPre.add(str);
			}
		}
		
		// 通过标记,查找对象
		Map<String, String> tableIdMap = new HashMap<String, String>();
		for (String tableIdWithPre : tableIdsWithPre) {
			String tableId = tableIdWithPre.substring(DmConstants.PRE_TABLE.length(), tableIdWithPre.length());
			TableModel tableModel = TableModelFactory.getTableModel(FileModel.getFileModelFromObj(productModel), tableId);
			if (tableModel != null) {
				tableIdMap.put(tableIdWithPre, tableModel.getTableName());
			}
		}
		
		Set<String> setTableIds = tableIdMap.keySet();
		for (String id : setTableIds) {
			realStoredProceduer = realStoredProceduer.replaceAll(id, tableIdMap.get(id));
		}
		
		// 通过标记,查找对象
		Map<String, String> columnIdMap = new HashMap<String, String>();
		for (String columnIdWithPre : columnIdsWithPre) {
			String columnId = columnIdWithPre.substring(DmConstants.PRE_COLUMN.length(), columnIdWithPre.length());
			ColumnModel columnModel = ColumnModelFactory.getColumnModel(FileModel.getFileModelFromObj(productModel), columnId);
			if (columnModel != null) {
				columnIdMap.put(columnIdWithPre, columnModel.getColumnName());
			}
		}
		
		Set<String> setColumnIds = columnIdMap.keySet();
		for (String id : setColumnIds) {
			realStoredProceduer = realStoredProceduer.replaceAll(id, columnIdMap.get(id));
		}
		
		return realStoredProceduer;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element storedModelElement = parent.addElement(elementName);

		storedModelElement.addAttribute("id", id == null ? "" : id); 
		storedModelElement.addAttribute("name", name == null ? "" 
				: name);
		storedModelElement.setText(storedProceduerStr == null ? "" 
				: storedProceduerStr.trim());
		
		return storedModelElement;
	}

	@Override
	public StoredProcedureModel getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("StoredProcedureModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("StoredProcedureModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setId(element.attributeValue("id").trim());
		setName(element.attributeValue("name").trim());
		setStoredProceduerStr(element.getText().trim());
		
		return this;
	}
	
	
}

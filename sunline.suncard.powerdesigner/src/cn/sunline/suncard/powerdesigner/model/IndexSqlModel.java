/* 文件名：     IndexSqlModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 表格对话框的索引模型
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-12-5
 * @see
 * @since 1.0
 */
public class IndexSqlModel implements DataObjectInterface{
	private String name = "";
	private String desc = "";
	private List<ColumnModel> columnList = new ArrayList<ColumnModel>(); // 该索引中所用到的列对象
//	private String indexSqlStr = ""; // 建索引的SQL语句(该语句不是真正的SQL语句,因为里面包含表格名称和列名称标记,导出时需要转换)
	private TableModel tableModel; // 所属的TableModel
	
	private static String elementName = "indexSqlModel"; // 保存为document时候的顶节点name
	private Log logger = LogManager.getLogger(IndexSqlModel.class
			.getName());

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * 根据用到的列对象ID，来生成真正的创建索引的SQL脚本
	 * @return
	 */
	public String getIndexSqlStr() {
		return "";
	}

	public TableModel getTableModel() {
		return tableModel;
	}

	public void setTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
	}
	
	public List<ColumnModel> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<ColumnModel> columnList) {
		this.columnList = columnList;
	}

	public static String getElementName() {
		return elementName;
	}

	@Override
	public IndexSqlModel clone() throws CloneNotSupportedException {
		IndexSqlModel newIndexSqlModel = new IndexSqlModel();
		
		newIndexSqlModel.setTableModel(tableModel);
		newIndexSqlModel.setName(name);
		newIndexSqlModel.setDesc(desc);
		
		// 克隆列的信息时需要注意，这里不能调用ColumnModel的克隆方法
		// 因为此对象的克隆方法一般是在克隆表格对象时候调用的，所以需要在表格模型的克隆方法里进行一些处理
		List<ColumnModel> newColumnList = new ArrayList<ColumnModel>();
		for(ColumnModel ColumnModel : columnList) {
			newColumnList.add(ColumnModel);
		}
		newIndexSqlModel.setColumnList(newColumnList);
		
		return newIndexSqlModel;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element indexSqlModelElement = parent.addElement(elementName);
		indexSqlModelElement.addAttribute("refTableId", tableModel.getId());
		indexSqlModelElement.addAttribute("name", name == null ? "" : name);
		indexSqlModelElement.addAttribute("desc", desc == null ? "" : desc);
		// 以符号分割列ID
		StringBuffer indexSqlStr = new StringBuffer("");
		for(ColumnModel columnModel : columnList) {
			indexSqlStr.append(columnModel.getId() + DmConstants.COMMA_SEPARATOR);
		}
		
		if(indexSqlStr.toString().endsWith(DmConstants.COMMA_SEPARATOR)) {
			indexSqlModelElement.setText(indexSqlStr.toString().substring(0, indexSqlStr.length() - 1));
		} else {
			indexSqlModelElement.setText("");
		}
		
		return indexSqlModelElement;
	}

	@Override
	public Object getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("IndexSqlModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("IndexSqlModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setName(element.attributeValue("name").trim());
		setDesc(element.attributeValue("desc").trim());
		
		columnList.clear();
//		String columnsIdStr = element.getTextTrim();
//		if(!columnsIdStr.isEmpty()) {
//			String[] columnIds = columnsIdStr.split(DmConstants.COMMA_SEPARATOR);
//			if(columnIds.length > 0) {
//				for(String columnId : columnIds) {
//					columnIdList.add(columnId);
//				}
//			}
//		}
		
		
		// 注意：tableModel需要额外设置
		return this;
	}

}

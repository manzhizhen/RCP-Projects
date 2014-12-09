/* 文件名：     CompareObjectManager.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-7
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.db.DataTypeModel;

/**
 * 比较对象管理
 * @author  Manzhizhen
 * @version 1.0, 2013-1-7
 * @see 
 * @since 1.0
 */
public class CompareObjectManager {
	public static String COMPARE_SAME = "COMPARE_SAME";	// 对象相同
	public static String COMPARE_MODIFY = "COMPARE_MODIFY"; // 对象不同
	public static String COMPARE_REMOVE = "COMPARE_REMOVE"; // 项目树不存在该对象
	public static String COMPARE_ADD = "COMPARE_ADD";   // 项目树有产品树没有的对象
	
	/**
	 * 比较两个表格对象
	 * @param leftTableModel
	 * @param rightTableModel
	 * @return
	 */
	public static String compareTableModel(TableModel leftTableModel, TableModel rightTableModel) {
		if(!leftTableModel.getTableName().equals(rightTableModel.getTableName())) {
			return COMPARE_MODIFY;
		}
		
		if(leftTableModel.getColumnList().size() != rightTableModel.getColumnList().size()) {
			return COMPARE_MODIFY;
		}
		
		Map<String, ColumnModel> leftColumnModelMap = new HashMap<String, ColumnModel>();
		List<ColumnModel> leftColumnModelList = leftTableModel.getColumnList();
		for(ColumnModel columnModel : leftColumnModelList) {
			leftColumnModelMap.put(columnModel.getColumnName(), columnModel);
		}
		
		Map<String, ColumnModel> rightColumnModelMap = new HashMap<String, ColumnModel>();
		List<ColumnModel> rightColumnModelList = rightTableModel.getColumnList();
		for(ColumnModel columnModel : rightColumnModelList) {
			rightColumnModelMap.put(columnModel.getColumnName(), columnModel);
		}
		
		Set<String> leftColumnModelSet = leftColumnModelMap.keySet();
		for(String columnModelName : leftColumnModelSet) {
			ColumnModel leftColumnModel = leftColumnModelMap.get(columnModelName);
			ColumnModel rightColumnModel = rightColumnModelMap.get(columnModelName);
			if(rightColumnModel == null) {
				return COMPARE_MODIFY;
			}
			
			String result = compareColumnModel(leftColumnModel, rightColumnModel);
			if(COMPARE_SAME.equals(result)) {
				continue ;
			} else {
				return result;
			}
		}
		
		return COMPARE_SAME;
	}
	
	/**
	 * 比较两个列对象
	 * @param leftTableModel
	 * @param rightTableModel
	 * @return
	 */
	public static String compareColumnModel(ColumnModel leftColumnModel, ColumnModel rightColumnModel) {
		if(!leftColumnModel.getColumnName().equals(rightColumnModel.getColumnName())) {
			return COMPARE_MODIFY;
		}
		
		DataTypeModel leftDataTypeModel = leftColumnModel.getDataTypeModel();
		DataTypeModel rightDataTypeModel = rightColumnModel.getDataTypeModel();
		
		if(!leftDataTypeModel.getName().equals(rightDataTypeModel.getName())) {
			return COMPARE_MODIFY;
		}
		
		if(leftDataTypeModel.getLength() != rightDataTypeModel.getLength()) {
			return COMPARE_MODIFY;
		}
		
		if(leftDataTypeModel.getPrecision() != rightDataTypeModel.getPrecision()) {
			return COMPARE_MODIFY;
		}
		
		return COMPARE_SAME;
	}
	
	/**
	 * 更新ColumnModel
	 * @throws CloneNotSupportedException 
	 */
	public static void updateColumnModel(ColumnModel oldColumnModel, ColumnModel newColumnModel) 
			throws CloneNotSupportedException {
		oldColumnModel.setColumnDesc(newColumnModel.getColumnDesc());
		oldColumnModel.setColumnNote(newColumnModel.getColumnNote());
		oldColumnModel.setDataTypeModel(newColumnModel.getDataTypeModel().clone());
		oldColumnModel.setPrimaryKey(newColumnModel.isPrimaryKey());
	}
	
}

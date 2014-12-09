/* 文件名：     ImportDefaultColumnCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-21
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 将一个ColumnModel导入成一个默认列的Command
 * @author  Manzhizhen
 * @version 1.0, 2013-1-21
 * @see 
 * @since 1.0
 */
public class ImportDefaultColumnCommand  extends Command {
	private Set<ColumnModel> columnModelSet; // 需要被修改的ColumnModel的集合
	private ColumnModel defaultColumnModel; // 需要导入的默认列
	
	private Log logger = LogManager.getLogger(ImportDefaultColumnCommand.class
			.getName());
	
	@Override
	public void execute() {
		if(columnModelSet == null || defaultColumnModel == null) {
			logger.error("传入的数据不完整，ImportDefaultColumnCommand无法执行！");
			return ;
		}
		for(ColumnModel columnModel : columnModelSet) {
			// 先取出该ColumnModel的表格下所有的列名
			Map<String, ColumnModel> columnModelMap = new HashMap<String, ColumnModel>();
			for(ColumnModel tempColumnModel : columnModel.getTableModel().getColumnList()) {
				columnModelMap.put(tempColumnModel.getColumnName(), tempColumnModel);
			}
			
			String columnName = defaultColumnModel.getColumnName();
			
			
//			// 如果源表格下面没有该名称的列，则可以直接修改列名
//			if(columnModelMap.get(columnName) == null) {
//				columnModel.setColumnName(columnName);
//				// 如果源表格下面有该名称的列，则需要覆盖该列
//			} else {
//				
//				for(int i = 0; ; i++) {
//					if(columnModelMap.get(columnName + "_Default" + i) == null) {
//						columnModel.setColumnName(columnName + "_Default" + i);
//						break ;
//					}
//				}
//			}
			
			columnModel.setColumnName(columnName);
			columnModel.setColumnDesc(defaultColumnModel.getColumnDesc());
			columnModel.setColumnNote(defaultColumnModel.getColumnNote());
			
			try {
				columnModel.setDataTypeModel(defaultColumnModel.getDataTypeModel().clone());
			} catch (CloneNotSupportedException e) {
				logger.error("克隆DataTypeModel出错，给ColumnModel" + columnModel.getColumnName() + "导入默认列或Domains失败！");
				e.printStackTrace();
				continue ;
			}
			
			if(defaultColumnModel.isDomainColumnModel()) {
				columnModel.setDomainId(defaultColumnModel.getId());
			} else {
				columnModel.setDomainId(defaultColumnModel.getDomainId());
			}
			columnModel.setCanNotNull(defaultColumnModel.isCanNotNull());
			columnModel.setSystemDefaultValueType(defaultColumnModel
					.getSystemDefaultValueType());
			columnModel.setSystemDefaultValue(defaultColumnModel.getSystemDefaultValue());
			columnModel.setInitDefaultValue(defaultColumnModel.getInitDefaultValue());
			
			columnModel.setMinValue(defaultColumnModel.getMinValue());
			columnModel.setMaxValue(defaultColumnModel.getMaxValue());
			columnModel.setCanGetMaxValue(defaultColumnModel.isCanGetMaxValue());
			columnModel.setCanGetMinValue(defaultColumnModel.isCanGetMinValue());
			columnModel.setMustNumber(defaultColumnModel.isMustNumber());
			columnModel.setStrMinLength(defaultColumnModel.getStrMinLength());
			
			columnModel.setUnitDesc(defaultColumnModel.getUnitDesc());
			columnModel.setAutoChangeChDesc(defaultColumnModel.isAutoChangeChDesc());
			columnModel.setPassDataSourceChange(defaultColumnModel.isPassDataSourceChange());
			columnModel.setDefaultSwitchMap((LinkedHashMap<String, String>) defaultColumnModel.getDefaultSwitchMap().clone());
			
			columnModel.setDataSourceContent(defaultColumnModel.getDataSourceContent());
			columnModel.setDataSourceDescContent(defaultColumnModel.getDataSourceDescContent());
			columnModel.setCustomDataMap((LinkedHashMap<String, String>) defaultColumnModel.getCustomDataMap().clone());
			columnModel.setLimitCondition(defaultColumnModel.getLimitCondition());
			columnModel.setMatchDefaultValue(defaultColumnModel.getMatchDefaultValue());
		}
		
		
		super.execute();
	}
	
	@Override
	public boolean canUndo() {
		return false;
	}

	public void setColumnModelSet(Set<ColumnModel> columnModelSet) {
		this.columnModelSet = columnModelSet;
	}

	public void setDefaultColumnModel(ColumnModel defaultColumnModel) {
		this.defaultColumnModel = defaultColumnModel;
	}
	
	
}

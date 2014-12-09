/* 文件名：     ColumnLabelProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-10
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.LineModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;

/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2012-9-10
 * @see 
 * @since 1.0
 */
public class FindObjectLabelProvider extends LabelProvider implements ITableLabelProvider{

	public FindObjectLabelProvider() {
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		switch(columnIndex) {
		case 0:
			if(element instanceof ColumnModel) {
				ColumnModel model = (ColumnModel) element;
				return model.getColumnName();
				
			} else if(element instanceof TableModel) {
				TableModel model = (TableModel) element;
				return model.getTableName();
				
			} else if(element instanceof LineModel) {
				LineModel model = (LineModel) element;
				return model.getName();
			} 
		
		case 1: 
			if(element instanceof ColumnModel) {
				ColumnModel model = (ColumnModel) element;
				
				// 如果是公共列字段
				if(model.isDomainColumnModel()) {
					return model.getColumnDesc() + " (Domain)";
					
				// 如果是物理数据模型下的默认列对象
				} else if(model.getTableModel() != null && model.getTableModel().getTableName().trim().isEmpty()) {
					return model.getColumnDesc() + " (Default)";
				} else {
					return model.getTableModel().getTableDesc() + " : " + model.getColumnDesc();
				}
				
			} else if(element instanceof TableModel) {
				TableModel model = (TableModel) element;
				return model.getTableDesc();
				
			} else if(element instanceof LineModel) {
				LineModel model = (LineModel) element;
				return model.getDesc();
			}
			
		}
		
		return null;
	}

}

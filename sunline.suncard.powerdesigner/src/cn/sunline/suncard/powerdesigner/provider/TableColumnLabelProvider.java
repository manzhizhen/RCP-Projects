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
import cn.sunline.suncard.powerdesigner.resource.DmConstants;

/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2012-9-10
 * @see 
 * @since 1.0
 */
public class TableColumnLabelProvider extends LabelProvider implements ITableLabelProvider{
	private List<ColumnModel> columnModelList;
	
	/**
	 * @param columnModelList
	 */
	public TableColumnLabelProvider(List<ColumnModel> columnModelList) {
		this.columnModelList = columnModelList;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(element instanceof ColumnModel) {
			ColumnModel model = (ColumnModel) element;
			
			switch(columnIndex) {
			case 0:
				if(columnModelList != null && columnModelList.indexOf(model) >= 0) {
					return columnModelList.indexOf(model) + 1 + "";
				} else {
					return "";
				}
			
			case 1: 
				return model.getColumnName() == null ? "" : model.getColumnName();
				
			case 2:
				return model.getColumnDesc() == null ? "" : model.getColumnDesc();
				
			case 3:
				return model.getDataTypeModel().getName() == null ? DmConstants.UNDEFINED : model.
						getDataTypeModel().getName();
				
			case 4:
				// 如果不包含"%"，说明该数据类型不需要参数
				if(!model.getDataTypeModel().getName().contains("%")) {
					return "";
				} else {
					return model.getDataTypeModel().getLength() == -1 ? "" :  model.
							getDataTypeModel().getLength() + "";
				}
				
			case 5:
				// 如果不包含","，说明该数据类型只有长度这个参数，而没有精度这个参数
				if(!model.getDataTypeModel().getName().contains(",")) {
					return "";
				} else {
					return model.getDataTypeModel().getPrecision() == -1 ? "" :  model.
							getDataTypeModel().getPrecision() + "";
				}
				
			case 6:
				if(model.isPrimaryKey()) {
					return "√";
				} else {
					return "×";
				}
				
			case 7:
				if(model.isForeignKey()) {
					return "√";
				} else {
					return "×";
				}
				
			case 8:
				if(model.isCanNotNull()) {
					return "√";
				} else {
					return "×";
				}
				
				
			}
			
			
		}
		return null;
	}

}

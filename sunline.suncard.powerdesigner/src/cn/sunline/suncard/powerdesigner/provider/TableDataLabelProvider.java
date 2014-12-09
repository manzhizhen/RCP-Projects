/* 文件名：     TableDataLabelProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     wzx
 * 修改时间：2012-11-29
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.TableDataModel;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 
 * @author  wzx
 * @version 1.0, 2012-11-29
 * @see 
 * @since 1.0
 */
public class TableDataLabelProvider extends LabelProvider implements ITableLabelProvider {
	private List<TableDataModel> tableDataList;
	private List<ColumnModel> columnNames;
	
	public TableDataLabelProvider(List<TableDataModel> tableDataList, List<ColumnModel> columnNames) {
		this.tableDataList = tableDataList;
		this.columnNames = columnNames;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		TableDataModel tableDataModel = (TableDataModel) element;
		switch (columnIndex) {
		case 0:
			if(tableDataList != null && tableDataList.indexOf(tableDataModel) >= 0) {
				return tableDataList.indexOf(tableDataModel) + 1 + "";
			} else {
				return "";
			}

		default:
			break;
		}
		
		if(columnIndex == columnNames.size() + 1) {
			if(tableDataModel.isCanModify()) {
				return I18nUtil.getMessage("YES");
			} else {
				return I18nUtil.getMessage("NO");
			}
		}
		
		return tableDataModel.getDataMap().get(columnNames.get(columnIndex - 1));
	}

}

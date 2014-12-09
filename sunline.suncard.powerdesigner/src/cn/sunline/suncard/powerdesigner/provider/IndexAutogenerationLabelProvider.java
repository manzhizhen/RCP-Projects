/* 文件名：     IndexAutogenerationLabelProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-12-19
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;

/**
 * 
 * @author Agree
 * @version 1.0, 2012-12-19
 * @see
 * @since 1.0
 */
public class IndexAutogenerationLabelProvider extends LabelProvider implements ITableLabelProvider{

private List<ColumnModel> columnModelList;
	
	/**
	 * @param sqlList
	 */
	public IndexAutogenerationLabelProvider(List<ColumnModel> sqlList) {
		this.columnModelList = sqlList;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof ColumnModel) {
			ColumnModel columnModel = (ColumnModel) element;
			
			switch (columnIndex) {
			case 0:
				if (columnModelList != null && columnModelList.indexOf(columnModel) >= 0) {
					return columnModelList.indexOf(columnModel) + 1 + "";
				} else {
					return "";
				}
				
			case 1:
				String str = columnModel.getColumnName() + ", "
						+ columnModel.getColumnDesc();
				return str == null ? "" : str;
			
			default:
				break;
			}
		}
		return null;
	}
	
}

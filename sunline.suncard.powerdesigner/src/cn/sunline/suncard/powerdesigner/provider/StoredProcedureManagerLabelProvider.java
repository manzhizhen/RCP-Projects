/* 文件名：     StoredProcedureManagerLabelProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     wzx
 * 修改时间：2012-11-27
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.powerdesigner.model.StoredProcedureModel;

/**
 * 
 * @author  wzx
 * @version 1.0, 2012-11-27
 * @see 
 * @since 1.0
 */
public class StoredProcedureManagerLabelProvider extends LabelProvider implements ITableLabelProvider {
	private List<StoredProcedureModel> sqlList;
	
	/**
	 * @param sqlList
	 */
	public StoredProcedureManagerLabelProvider(List<StoredProcedureModel> sqlList) {
		this.sqlList = sqlList;
	}
	
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof StoredProcedureModel) {
			StoredProcedureModel sql = (StoredProcedureModel) element;
			
			switch (columnIndex) {
			case 0:
				if (sqlList != null && sqlList.indexOf(sql) >= 0) {
					return sqlList.indexOf(sql) + 1 + "";
				} else {
					return "";
				}
				
			case 1:
				return sql == null ? "" : sql.getName();
				
			case 2:
				return sql == null ? "" : sql.getStoredProceduerStr();
				
			default:
				break;
			}
		}
		
		return null;
	}

}

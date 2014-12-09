/* 文件名：     SQLManagerLabelProvider.java
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

import cn.sunline.suncard.powerdesigner.model.SqlScriptModel;

/**
 * 
 * @author  wzx
 * @version 1.0, 2012-11-27
 * @see 
 * @since 1.0
 */
public class SQLManagerLabelProvider extends LabelProvider implements ITableLabelProvider {
	private List<SqlScriptModel> sqlList;
	
	/**
	 * @param sqlList
	 */
	public SQLManagerLabelProvider(List<SqlScriptModel> sqlList) {
		this.sqlList = sqlList;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof SqlScriptModel) {
			SqlScriptModel sql = (SqlScriptModel) element;
			
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
				return sql == null ? "" : sql.getSqlStr();
				
			default:
				break;
			}
		}
		
		return null;
	}

}

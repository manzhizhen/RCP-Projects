/* 文件名：     IndexAllShowProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-12-25
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.IndexSqlModel;

/**
 * 
 * @author  Agree
 * @version 1.0, 2012-12-25
 * @see 
 * @since 1.0
 */
public class IndexAllShowProvider extends LabelProvider implements ITableLabelProvider {
	private List<IndexSqlModel> sqlList;
	
	/**
	 * @param sqlList
	 */
	public IndexAllShowProvider(List<IndexSqlModel> sqlList) {
		this.sqlList = sqlList;
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
		if (element instanceof IndexSqlModel) {
			IndexSqlModel sql = (IndexSqlModel) element;
			
			switch (columnIndex) {
			case 0:
				if (sqlList != null && sqlList.indexOf(sql) >= 0) {
					return sqlList.indexOf(sql) + 1 + "";
				} else {
					return "";
				}
			case 1:
				return sql == null ? "" : sql.getTableModel().getTableName();
			case 2:
				return sql == null ? "" : sql.getName();
			case 3:
				return sql == null ? "" : sql.getDesc();
			case 4:	
				StringBuffer indexColumnName = new StringBuffer();
				if(sql.getColumnList().isEmpty()){
					return "";
				}else{
					for(ColumnModel columnModel : sql.getColumnList()){
						indexColumnName.append(columnModel.getColumnName() + ", ");
					}
					indexColumnName.deleteCharAt(indexColumnName.lastIndexOf(","));
				}
				
				return sql == null ? "" : indexColumnName.toString();
			default:
				break;
			}
		}
		
		return null;
	}
}

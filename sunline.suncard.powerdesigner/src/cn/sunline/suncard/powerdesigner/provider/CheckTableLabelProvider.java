/* 文件名：     CheckTableLabelProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-11-12
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.powerdesigner.model.TableModel;

/**
 * 
 * @author  Agree
 * @version 1.0, 2012-11-12
 * @see 
 * @since 1.0
 */
//表格所使用的标签提供者
	public class CheckTableLabelProvider extends LabelProvider implements ITableLabelProvider {
		
		private TableModel tableModel;
		@Override
		 public Image getColumnImage(Object element, int columnIndex) {
		  return null;
		 }
		 @Override
		 public String getColumnText(Object element, int columnIndex) {
		  if(element instanceof TableModel) {
			   tableModel = (TableModel) element;
		   
		   switch(columnIndex) {
		   case 0:
		    return "";
		   
		   case 1:
		    return tableModel.getTableName();
		   
		   case 2:
		    return tableModel.getTableDesc();
		   
		   }
		  }
		  
		  return null;
		 }
		}

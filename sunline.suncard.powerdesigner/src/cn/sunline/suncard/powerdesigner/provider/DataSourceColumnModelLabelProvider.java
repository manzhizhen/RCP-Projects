/* 文件名：     DataSourceColumnModelLabelProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-11-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import org.eclipse.jface.viewers.LabelProvider;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;

/**
 * 
 * @author  Agree
 * @version 1.0, 2012-11-28
 * @see 
 * @since 1.0
 */
public class DataSourceColumnModelLabelProvider extends LabelProvider{
	@Override
	public String getText(Object element) {
		if(element instanceof ColumnModel) {
			ColumnModel model = (ColumnModel) element;
			
			return model.getColumnName() == null ? "" : model.getColumnName();
		} else if(element instanceof String) {
			return element.toString();
		}
		
		return super.getText(element);
	}
}

/* 文件名：     ColumnModelLableProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-21
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import org.eclipse.jface.viewers.LabelProvider;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;

/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2013-1-21
 * @see 
 * @since 1.0
 */
public class ColumnModelLabelProvider extends LabelProvider{
	@Override
	public String getText(Object element) {
		if(element instanceof ColumnModel) {
			return ((ColumnModel)element).getColumnName() + "(" + ((ColumnModel)element)
					.getColumnDesc() + ")";
		}

		return super.getText(element);
	}
}

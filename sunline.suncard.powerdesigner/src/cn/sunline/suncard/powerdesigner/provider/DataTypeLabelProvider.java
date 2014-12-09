/* 文件名：     DatabaseTypeLabelProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import org.eclipse.jface.viewers.LabelProvider;

import cn.sunline.suncard.powerdesigner.model.db.DataTypeModel;
import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;

/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2012-9-5
 * @see 
 * @since 1.0
 */
public class DataTypeLabelProvider extends LabelProvider{
	@Override
	public String getText(Object element) {
		if(element instanceof DataTypeModel) {
			DataTypeModel model = (DataTypeModel) element;
			
			return model.getName() == null ? "" : model.getName();
		}
		
		return super.getText(element);
	}
}

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
import cn.sunline.suncard.powerdesigner.resource.DmConstants;

/**
 * 表格对话框中的“列属性”标签中所用到的标签提供者
 * @author  Manzhizhen
 * @version 1.0, 2012-9-5
 * @see 
 * @since 1.0
 */
public class CustomDataTypeLabelProvider extends LabelProvider{
	@Override
	public String getText(Object element) {
		if(element instanceof DataTypeModel) {
			DataTypeModel model = (DataTypeModel) element;
			
			String typeName = model.getName();
			if(typeName == null) {
				return "";
			}
			
			// 包含逗号，说明有两个参数
			if(typeName.contains(",")) {
				int index = typeName.indexOf(DmConstants.DATA_TYPE_PAR);
				if(index > 0) {
					// 替换第一个参数为数据类型的长度
					typeName = typeName.replaceAll(typeName.substring(index, index + 2), model.getLength() + "");
				}
				
				index = typeName.indexOf(DmConstants.DATA_TYPE_PAR);
				if(index > 0) {
					// 替换第一个参数为数据类型的精度
					typeName = typeName.replaceAll(typeName.substring(index, index + 2), model.getPrecision() + "");
				}
				
			// 只包含百分号，说明只有一个参数	
			} else if(typeName.contains(DmConstants.DATA_TYPE_PAR)) {
				int index = typeName.indexOf(DmConstants.DATA_TYPE_PAR);
				if(index > 0) {
					// 替换第一个参数为数据类型的长度
					typeName = typeName.replaceAll(typeName.substring(index, index + 2), model.getLength() + "");
				}
			}
			
			return typeName;
		}
		
		return super.getText(element);
	}
}

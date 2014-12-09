/* 文件名：     ColumnLabelProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-10
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;

/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2012-9-10
 * @see 
 * @since 1.0
 */
public class ProductModelTableLabelProvider extends LabelProvider implements ITableLabelProvider{
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int productIndex) {
		if(element instanceof ProductModel) {
			ProductModel model = (ProductModel) element;
			
			switch(productIndex) {
			case 0:
				return "";
			
			case 1: 
				return model.getId() == null ? "" : model.getId();
				
			case 2:
				return model.getName() == null ? "" : model.getName();
			}
		}
		return null;
	}

}

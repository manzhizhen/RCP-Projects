/* 文件名：     ColumnLabelProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-10
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;

/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2012-9-10
 * @see 
 * @since 1.0
 */
public class SelectTableLabelProvider extends LabelProvider implements ITableLabelProvider{

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int index) {
		if(element instanceof TableModel) {
			TableModel model = (TableModel) element;
			
			switch(index) {
			case 0:
					return "";
			
			case 1: 
				return model.getTableName() == null ? "" : model.getTableName();
				
			case 2:
				return model.getTableDesc() == null ? "" : model.getTableDesc();
				
			case 3:
//				StringBuffer productLabel = new StringBuffer();
//				String allProductLable = "";
//				 Set<String> labelList = model.getProductLabelSet();
//				for(String label : labelList) {
//					productLabel.append(label + ",");
//				}
//				
//				if(productLabel.toString().endsWith(",")){
//					allProductLable = productLabel.substring(0, productLabel.length() - 1);
//				}
//				
//				return allProductLable;
				return "";
				
			case 4:
//				model.getModuleLabel();
				return "";
			}
			
			
		}
		return null;
	}

}

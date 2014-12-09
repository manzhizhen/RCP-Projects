/* 文件名：     KeyToKeyLabelProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-10-22
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.powerdesigner.model.JoinModel;
import cn.sunline.suncard.powerdesigner.model.KeyToKeyModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;


/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2012-10-22
 * @see 
 * @since 1.0
 */
public class KeyToKeyModelProvider extends LabelProvider implements ITableLabelProvider{

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}


	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(element instanceof KeyToKeyModel) {
			KeyToKeyModel keyModel = (KeyToKeyModel) element;
			switch (columnIndex) {
			case 0:
				return keyModel.getPrimaryColumnModel().getColumnDesc() + "";

			case 1:
				return keyModel.getForeginColumnModel() == null ? 
					DmConstants.JOINS_NONE : keyModel.getForeginColumnModel().getColumnDesc();
				
			default:
				break;
			}
			
		}
		return null;
	}

}

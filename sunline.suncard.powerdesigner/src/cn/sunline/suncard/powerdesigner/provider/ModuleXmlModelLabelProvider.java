/* 文件名：     ModuleXmlModelLabelProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.powerdesigner.model.xml.ModuleXmlModel;

/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2012-12-28
 * @see 
 * @since 1.0
 */
public class ModuleXmlModelLabelProvider extends LabelProvider implements ITableLabelProvider{
	
	@Override
	public String getText(Object element) {
		if(element instanceof ModuleXmlModel) {
			ModuleXmlModel moduleXmlModel = (ModuleXmlModel) element;
			return moduleXmlModel.getId() + " - " + moduleXmlModel.getName();
			
		}
		
		return null;
	}
	
	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(element instanceof ModuleXmlModel) {
			ModuleXmlModel moduleXmlModel = (ModuleXmlModel) element;
			switch(columnIndex) {
			case 0:
				return moduleXmlModel.getId() == null ? "" : moduleXmlModel.getId();
				
			case 1:
				return moduleXmlModel.getName() == null ? "" : moduleXmlModel.getName();
				
			case 2:
				return moduleXmlModel.getNote() == null ? "" : moduleXmlModel.getNote();
				
			}
			
		}
		
		return null;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}
}

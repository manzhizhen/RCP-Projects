/* 文件名：     UserDefineTableCellModifier.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	自定义输入数据表格修改器
 * 修改人：     wzx
 * 修改时间：2012-12-27
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

import cn.sunline.suncard.powerdesigner.model.KeyValueModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;

/**
 * 自定义输入数据表格修改器
 * @author  wzx
 * @version 1.0, 2012-12-27
 * @see 
 * @since 1.0
 */
public class UserDefineTableCellModifier implements ICellModifier {
	private TableViewer tableViewer;
	
	/**
	 * 
	 */
	public UserDefineTableCellModifier(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	@Override
	public boolean canModify(Object element, String property) {
		if (DmConstants.COLUMN_PROPERTY_INDEX.equalsIgnoreCase(property)) {
			return false;
		}
		return true;
	}

	@Override
	public Object getValue(Object element, String property) {
		KeyValueModel keyValueModel = (KeyValueModel) element;
		if(property.equals(DmConstants.COLUMN_PROPERTY_INDEX)){
			List<KeyValueModel> models = (List<KeyValueModel>) tableViewer.getInput();
			return models == null  ? "" : models.indexOf(keyValueModel) + 1;
		}
		if(property.equals(DmConstants.COLUMN_PROPERTY_KEY)){
			return keyValueModel.getKey();
		}
		if(property.equals(DmConstants.COLUMN_PROPERTY_VALUE)){
			return keyValueModel.getValue();
		}
		return keyValueModel;
	}

	@Override
	public void modify(Object element, String property, Object value) {
		TableItem tableItem = (TableItem)element;
		KeyValueModel keyValueModel = (KeyValueModel)tableItem.getData();
		String string = (String)value;
		if(property.equals(DmConstants.COLUMN_PROPERTY_KEY)){
			keyValueModel.setKey(string);
		}
		if(property.equals(DmConstants.COLUMN_PROPERTY_VALUE)){
			keyValueModel.setValue(string);
		}
		
		tableViewer.refresh();//必须调用刷新，不然显示还是原来的
	}
}

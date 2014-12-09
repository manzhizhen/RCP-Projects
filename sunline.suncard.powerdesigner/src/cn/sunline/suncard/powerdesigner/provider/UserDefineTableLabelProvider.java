/* 文件名：     UserDefineTableLabelProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	用户自定义输入数据表格标签提供者
 * 修改人：     wzx
 * 修改时间：2012-12-27
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.powerdesigner.model.KeyValueModel;

/**
 * 用户自定义输入数据表格标签提供者
 * @author  wzx
 * @version 1.0, 2012-12-27
 * @see 
 * @since 1.0
 */
public class UserDefineTableLabelProvider extends LabelProvider implements ITableLabelProvider {
	private List<KeyValueModel> keyValueModelList = new ArrayList<KeyValueModel>();
	private LinkedHashMap<String, KeyValueModel> customDataMap;
	/**
	 * 构造函数
	 * @param customDataMap 自定义数据map
	 */
	public UserDefineTableLabelProvider(LinkedHashMap<String, KeyValueModel> customDataMap) {
		this.customDataMap = customDataMap;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		KeyValueModel keyValueModel = (KeyValueModel) element;
		this.keyValueModelList.clear();
		Collection<KeyValueModel> keyValueModels = customDataMap.values();
		for(KeyValueModel keyValueModelCopy : keyValueModels){
			this.keyValueModelList.add(keyValueModelCopy);
		}
		
		switch (columnIndex) {
		case 0:
			if (keyValueModelList != null && keyValueModelList.indexOf(keyValueModel) >= 0) {
				return keyValueModelList.indexOf(keyValueModel) + 1 + "";
			} else {
				return "";
			}
		case 1:
			return (String)keyValueModel.getKey();
			
		case 2:
			return (String)keyValueModel.getValue();

		default:
			break;
		}
		return null;
	}

}

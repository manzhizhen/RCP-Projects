/*
 * 文件名：BsFuncTvLabelProvider.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：标签提供器，用于定义数据的显示形式
 * 修改人：heyong
 * 修改时间：2011-9-23
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.sde.bs.entity.BsFunction;

/**
 * 标签提供器，用于定义数据的显示形式
 * 根据每个实体类定义其显示字段
 * @author heyong
 * @version 1.0, 2011-9-23
 * @since 1.0
 */
public class BsFuncTvLabelProvider implements ITableLabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		BsFunction function = (BsFunction)element;
		if (columnIndex == 0){
			return function.getId().getBankorgId() == null? "" : function.getId().getBankorgId().toString();
		}else if (columnIndex == 1){
			return function.getId().getPcId() == null? "" : function.getId().getPcId();
		}else if (columnIndex == 2){
			return function.getId().getFunctionId() == null? "" : function.getId().getFunctionId();
		}else if (columnIndex == 3){
			return function.getFunctionName() == null? "" : function.getFunctionName();
		}
		return null;
	}

}

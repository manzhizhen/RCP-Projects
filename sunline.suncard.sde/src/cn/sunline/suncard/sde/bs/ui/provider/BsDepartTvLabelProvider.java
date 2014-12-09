/*
 * 文件名：BsUserTvLabelProvider.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：标签提供器，用于定义数据的显示形式
 * 修改人：heyong
 * 修改时间：2011-9-24
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.sde.bs.entity.BsDepartment;

/**
 * 标签提供器，用于定义数据的显示形式
 * 此类为BsUser实体类的标签提供者，根据每个实体类定义其显示字段
 * @author heyong
 * @version 1.0, 2011-9-24
 * @since 1.0
 */
public class BsDepartTvLabelProvider implements ITableLabelProvider {

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
		BsDepartment department = (BsDepartment)element;
		if (columnIndex == 0){
			return department.getId().getBankorgId() == null? "" : department.getId().getBankorgId().toString();
		}else if (columnIndex == 1){
			return department.getId().getDepartmentId() == null? "" : department.getId().getDepartmentId();
		}else if (columnIndex == 2){
			return department.getDepartmentName() == null? "" : department.getDepartmentName();
		}
		return null;
	}

}

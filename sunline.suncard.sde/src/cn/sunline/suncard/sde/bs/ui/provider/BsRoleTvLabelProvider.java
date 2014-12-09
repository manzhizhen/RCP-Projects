/*
 * 文件名：BsRoleTvLabelProvider.java
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

import cn.sunline.suncard.sde.bs.entity.BsRole;

/**
 * 标签提供器，用于定义数据的显示形式
 * 此类为BsRole实体类的标签提供者，根据每个实体类定义其显示字段
 * @author heyong
 * @version 1.0, 2011-9-23
 * @since 1.0
 */
public class BsRoleTvLabelProvider implements ITableLabelProvider {

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
		BsRole role = (BsRole)element;
		if (columnIndex == 0){
			return role.getId().getBankorgId() == null? "" : role.getId().getBankorgId().toString();
		}else if (columnIndex == 1){
			return role.getId().getPcId() == null? "" : role.getId().getPcId();
		}else if (columnIndex == 2){
			return role.getId().getRoleId() == null? "" : role.getId().getRoleId();
		}else if (columnIndex == 3){
			return role.getRoleName() == null? "" : role.getRoleName();
		}
		return null;
	}

}

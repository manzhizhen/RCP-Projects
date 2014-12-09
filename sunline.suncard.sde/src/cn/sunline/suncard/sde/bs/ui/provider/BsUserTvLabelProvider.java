/*
 * 文件名：BsUserTvLabelProvider.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：标签提供器，用于定义数据的显示形式
 * 修改人：heyong
 * 修改时间：2011-9-24
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.provider;

import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.sde.bs.biz.BsDepartmentBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsDepartmentId;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 标签提供器，用于定义数据的显示形式
 * 此类为BsUser实体类的标签提供者，根据每个实体类定义其显示字段
 * @author heyong
 * @version 1.0, 2011-9-24
 * @since 1.0
 */
public class BsUserTvLabelProvider implements ITableLabelProvider {

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
		BsUser user = (BsUser)element;
		if (columnIndex == 0){
			return user.getId().getBankorgId() == null? "" : user.getId().getBankorgId().toString();
		}else if (columnIndex == 1){
			return user.getId().getUserId() == null? "" : user.getId().getUserId();
		}else if (columnIndex == 2){
			return user.getUserName() == null? "" : user.getUserName();
		}else if (columnIndex == 3){
			String departmentName = Constants.INITIAL_USER_DEPARTMENT_ID;
			if (!user.getDepartmentId().equals(Constants.INITIAL_USER_DEPARTMENT_ID)){
				departmentName = new BsDepartmentBiz().findByPk(new BsDepartmentId(
						(Long) Context.getSessionMap().get(Constants.BANKORG_ID), user.getDepartmentId()))
						.getDepartmentName();
			}
			return departmentName;
		}else if (columnIndex == 4){
			return user.getUserStatus() == null? "" : I18nUtil.getMessage(user.getUserStatus());
		}else if (columnIndex == 5){
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
			return user.getLasetLogginDate() == null ? "" : dateFormat.format(user.getLasetLogginDate());
		}
		return null;
	}

}

/*
 * 文件名：
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：heyong
 * 修改时间：2011-10-17
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.provider;

import org.eclipse.jface.viewers.LabelProvider;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsDepartment;

public class BsDepartCvLabelProvider extends LabelProvider{

	@Override
	public String getText(Object element) {
		if (element instanceof BsDepartment){
			BsDepartment department = (BsDepartment) element;
			return department.getDepartmentName();
		}else if (element instanceof String && element.equals(Constants.INITIAL_USER_DEPARTMENT_ID)){
			return Constants.INITIAL_USER_DEPARTMENT_ID;
		}
		return "";
	}
}

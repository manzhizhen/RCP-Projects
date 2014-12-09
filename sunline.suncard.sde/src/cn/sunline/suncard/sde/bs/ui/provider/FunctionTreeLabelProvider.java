/*
 * 文件名：
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：周兵
 * 修改时间：2011-10-13
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.provider;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.sde.bs.entity.BsDepartment;
import cn.sunline.suncard.sde.bs.entity.BsFunction;
import cn.sunline.suncard.sde.bs.entity.BsRole;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.tree.FunctionTree;

public class FunctionTreeLabelProvider implements ILabelProvider {

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
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		if(element instanceof FunctionTree){
			return ((FunctionTree)element).getName();
		}else if(element instanceof BsUser){
			return ((BsUser)element).getId().getUserId() 
					+ "[ " + ((BsUser)element).getUserName() + " ]";
		}else if(element instanceof BsDepartment){
			return ((BsDepartment)element).getDepartmentName();
		}else if(element instanceof BsRole){
			return ((BsRole)element).getRoleName();
		}else if(element instanceof BsFunction){
			return ((BsFunction)element).getFunctionName();
		}
		return null;
	}

}

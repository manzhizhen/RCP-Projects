/*
 * 文件名：UserStatusCvLabelProvider.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：用户状态标签提供者
 * 修改人：heyong
 * 修改时间：2011-9-26
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.provider;

import org.eclipse.jface.viewers.LabelProvider;

import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 用户状态标签提供者
 * 
 * @author heyong
 * @version 1.0, 2011-9-26
 * @see 
 * @since 1.0
 */
public class UserStatusCvLabelProvider extends LabelProvider{

	@Override
	public String getText(Object element) {
		return I18nUtil.getMessage(super.getText(element));
	}

	
}

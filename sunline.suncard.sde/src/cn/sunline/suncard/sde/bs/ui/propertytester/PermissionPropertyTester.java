/*
 * 文件名：PermissionPropertyTester.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：heyong
 * 修改时间：2011-10-8
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.propertytester;

import org.eclipse.core.expressions.PropertyTester;

import cn.sunline.suncard.sde.bs.system.PermissionContext;

/**
 * 权限控制类
 * 用于根据当前登录的用户，得到其所有权限，对其进行操作的验证
 * @author heyong
 * @version 1.0, 2011-10-8
 * @see 
 * @since 1.0
 */
public class PermissionPropertyTester extends PropertyTester {
	
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		//此处开发阶段暂时返回true,实际使用时，改回下面返回值
//		return true;
		return PermissionContext.getInstance().checkPermission(((String) expectedValue).trim());
	}
}

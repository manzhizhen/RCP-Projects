/*
 * 文件名：ExteriorVariable.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：外部变量类
 * 修改人： xcc
 * 修改时间：2011-11-02
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.syntax.imp;

/**
 * 外部变量类
 * 为外部变量提供必需的操作
 * @author    xcc
 * @version   1.0, 2011-11-02
 * @since     1.0
 */

public class ExteriorVariable extends Element {
	
	private final boolean hasExpression=true;

	@Override
	public boolean hasExpression() {
		return this.hasExpression;
	}

}

/*
 * 文件名：InnerVariable.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：内部变量类
 * 修改人： xcc
 * 修改时间：2011-11-10
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.syntax.imp;

/**
 * 内部变量类
 * 提供内部变量检查，计算所必须的操作
 * @author    xcc
 * @version   1.0, 2011-11-10
 * @since     1.0
 */
public class InnerVariable extends Element {
	
	private final boolean hasExpression=true;
	private String variableType = null;

	/* (non-Javadoc)
	 * @see cn.sunline.suncard.sde.syntax.IElement#hasExpression()
	 */
	@Override
	public boolean hasExpression() {
		// TODO Auto-generated method stub
		return hasExpression;
	}

	public String getVariableType() {
		return variableType;
	}

	public void setVariableType(String variableType) {
		this.variableType = variableType;
	}

}

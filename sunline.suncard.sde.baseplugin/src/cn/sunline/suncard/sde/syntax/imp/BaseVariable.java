/*
 * 文件名：     BaseVariable.java
 * 版权：          Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：          基础变量类
 * 修改人：     wzx
 * 修改时间：2012-2-22
 * 修改内容：创建
 */

package cn.sunline.suncard.sde.syntax.imp;

/**
 * 基础变量类
 * 为基础变量提供必需的操作
 * @author  wzx
 * @version , 2012-2-22
 * @see     
 * @since   1.0
 */

public class BaseVariable extends Element {

	private final boolean hasExpression = true;
	
	@Override
	public boolean hasExpression() {
		// TODO Auto-generated method stub
		return hasExpression;
	}

}

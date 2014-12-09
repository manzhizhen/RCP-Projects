/* 文件名：     CompareObjectModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-7
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

/**
 * 用于记录比较的模型
 * @author  Manzhizhen
 * @version 1.0, 2013-1-7
 * @see 
 * @since 1.0
 */
public class CompareObjectModel {
	private String compareFlag;
	private Object leftObject; 	// 比较时的主要对象
	private Object rightObject; // 如果是COMPARE_DIFF，则需要用到rightObject

	public String getCompareFlag() {
		return compareFlag;
	}

	public void setCompareFlag(String compareFlag) {
		this.compareFlag = compareFlag;
	}

	public Object getLeftObject() {
		return leftObject;
	}

	public void setLeftObject(Object leftObject) {
		this.leftObject = leftObject;
	}

	public Object getRightObject() {
		return rightObject;
	}

	public void setRightObject(Object rightObject) {
		this.rightObject = rightObject;
	}
	
}

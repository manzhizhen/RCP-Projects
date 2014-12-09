/* 文件名：     ActionTreeNode.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-9
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.model;

/**
 * 描述
 * 
 * @author 易振强
 * @version 1.0, 2012-4-9
 * @see
 * @since 1.0
 */
public class ActionTreeNode implements java.io.Serializable{
	private String name;
	private String desc;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}

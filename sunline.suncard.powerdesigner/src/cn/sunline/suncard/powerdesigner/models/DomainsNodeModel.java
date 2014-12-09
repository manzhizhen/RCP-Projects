/* 文件名：     TablesNodeModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-10-11
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.models;

import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;

/**
 * Domains树节点所绑定的对象
 * 其实只是做个标记，没实际作用
 * @author  Manzhizhen
 * @version 1.0, 2012-10-11
 * @see 
 * @since 1.0
 */
public class DomainsNodeModel {
	private PhysicalDataModel physicalDataModel;

	public void setPhysicalDataModel(PhysicalDataModel physicalDataModel) {
		this.physicalDataModel = physicalDataModel;
	}

	public PhysicalDataModel getPhysicalDataModel() {
		return physicalDataModel;
	}
}

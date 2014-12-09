/**
 * 文件名：DictionarysNodeModel.java
 * 版权：
 * 描述：
 * 修改人： Manzhizhen
 * 修改时间：2013-3-15
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.models;

import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;

/**
 * 业务字典集合节点
 * @author Manzhizhen
 * @version 1.0 2013-3-15
 * @see
 * @since 1.0
 */
public class DictionarysNodeModel {
	private PhysicalDataModel physicalDataModel;

	public void setPhysicalDataModel(PhysicalDataModel physicalDataModel) {
		this.physicalDataModel = physicalDataModel;
	}

	public PhysicalDataModel getPhysicalDataModel() {
		return physicalDataModel;
	}
}

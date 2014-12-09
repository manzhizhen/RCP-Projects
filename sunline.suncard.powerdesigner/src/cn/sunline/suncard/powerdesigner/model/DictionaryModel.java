/**
 * 文件名：DictionaryModel.java
 * 版权：
 * 描述：
 * 修改人： Manzhizhen
 * 修改时间：2013-3-15
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import org.dom4j.Element;

/**
 * 业务字典模型
 * @author Manzhizhen
 * @version 1.0 2013-3-15
 * @see
 * @since 1.0
 */
public class DictionaryModel implements DataObjectInterface{
	private PhysicalDataModel physicalDataModel;

	public PhysicalDataModel getPhysicalDataModel() {
		return physicalDataModel;
	}

	public void setPhysicalDataModel(PhysicalDataModel physicalDataModel) {
		this.physicalDataModel = physicalDataModel;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getObjectFromElement(Element element, Object... obj) {
		// TODO Auto-generated method stub
		return null;
	}
}

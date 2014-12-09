/* 文件名：     ModelDataObject.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-11
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import org.dom4j.Element;

/**
 * 描述
 * @author  易振强
 * @version 1.0, 2012-4-11
 * @see 
 * @since 1.0
 */
public interface DataObjectInterface {
//	/**
//	 * 设置该元素的名称
//	 */
//	public String getElementName();
	
	/**
	 * 将模型对应的数据对象转化成xml的Element
	 * @param Element 父节点元素
	 */
	public Element getElementFromObject(Element parent);
	
	/**
	 * 将模型对应的xml转化成数据对象
	 * @param 该节点对象元素或父节点对象
	 */
	public Object getObjectFromElement(Element element, Object...obj);
	
}

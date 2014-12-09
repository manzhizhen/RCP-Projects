/* 文件名：     KeyToKeyModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-10-24
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import org.dom4j.Element;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 保存了一条主外键的名称
 * @author  Manzhizhen
 * @version 1.0, 2012-10-24
 * @see 
 * @since 1.0
 */
public class KeyToKeyModel implements DataObjectInterface{
	private ColumnModel primaryColumnModel;
	private ColumnModel foreginColumnModel;
	
	private static String elementName = "key";
	private Log logger = LogManager.getLogger(KeyToKeyModel.class
			.getName());
	
	public ColumnModel getPrimaryColumnModel() {
		return primaryColumnModel;
	}

	public void setPrimaryColumnModel(ColumnModel primaryColumnModel) {
		this.primaryColumnModel = primaryColumnModel;
	}

	public ColumnModel getForeginColumnModel() {
		return foreginColumnModel;
	}

	public void setForeginColumnModel(ColumnModel foreginColumnModel) {
		this.foreginColumnModel = foreginColumnModel;
	}

	public static String getElementName() {
		return elementName;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element keyModelElement = parent.addElement(elementName);
		
		keyModelElement.addAttribute("primaryColumnName", primaryColumnModel == null ? 
				"" : primaryColumnModel.getColumnName());
		keyModelElement.addAttribute("foreginColumnName", foreginColumnModel == null ? 
				"" : foreginColumnModel.getColumnName());
		
		return keyModelElement;
	}

	@Override
	public KeyToKeyModel getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("KeyToKeyModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("KeyToKeyModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		// 从XML文件中获取该对象时需要额外赋值
		
		return this;
	}
	
	@Override
	public KeyToKeyModel clone() throws CloneNotSupportedException {
		KeyToKeyModel keyModel = new KeyToKeyModel();
		keyModel.setPrimaryColumnModel(primaryColumnModel);
		keyModel.setForeginColumnModel(foreginColumnModel);
		
		return keyModel;
	}
}

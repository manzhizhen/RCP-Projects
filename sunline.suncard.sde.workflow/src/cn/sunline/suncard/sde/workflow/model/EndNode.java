/*
 * 文件名：    EndNode.java
 * 版权：          Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：          
 * 修改人：     wzx
 * 修改时间：2012-3-26
 * 修改内容：创建
 */

package cn.sunline.suncard.sde.workflow.model;

import org.dom4j.Element;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 
 * 
 * @author  wzx
 * @version , 2012-3-26
 * @see     
 * @since   1.0
 */

public class EndNode implements DataObjectInterface{
	private static String elementName = "endNode";
	private String name;
	private String description;
	
	private Log logger = LogManager.getLogger(EndNode.class.getName());
	
	public EndNode() {
		name = new String();
		description = I18nUtil.getMessage("END_MODEL");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element endElement = parent.addElement(elementName);
		endElement.addAttribute("name", name + "");
		
		Element descElement = endElement.addElement("description");
		descElement.setText(description + "");
		
		return endElement;
	}

	@Override
	public EndNode getObjectFromElement(Element element) {
		if(element == null) {
			logger.warn("EndNode的Element为空，无法将xml转换为对象！");
			return null;
		}

		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("EndNode的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setName(element.attributeValue("name"));
		setDescription(element.elementText("description"));
		
		return this;
	}
	
	public static String getElementName() {
		return elementName;
	}
}

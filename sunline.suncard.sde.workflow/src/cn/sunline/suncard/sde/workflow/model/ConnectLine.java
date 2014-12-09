/*
 * 文件名：     ExtVarComposite.java
 * 版权：          Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：          工作流连接线模型
 * 修改人：     wzx
 * 修改时间：2012-3-26
 * 修改内容：创建
 */

package cn.sunline.suncard.sde.workflow.model;

import org.dom4j.Element;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 工作流连接线模型
 * 
 * @author  wzx
 * @version , 2012-3-26
 * @see     
 * @since   1.0
 */

public class ConnectLine implements DataObjectInterface{
	private static String elementName = "connectLine";
	
	private String name;
	private String description;
	private String targetNodeName;
	
	private Log logger = LogManager.getLogger(ConnectLine.class.getName());
	
	public ConnectLine() {
		name = new String();
		targetNodeName = name;
		description = new String();
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

	public String getTargetNodeName() {
		return targetNodeName;
	}

	public void setTargetNodeName(String targetNodeName) {
		this.targetNodeName = targetNodeName;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element connectLineElement = parent.addElement(elementName);
		connectLineElement.addAttribute("name", name + "");
		
		Element descElement = connectLineElement.addElement("description");
		descElement.setText(description + "");
		
		Element targetElement = connectLineElement.addElement("to");
		targetElement.setText(targetNodeName + "");
		
		return connectLineElement;
	}

	@Override
	public ConnectLine getObjectFromElement(Element element) {
		if(element == null) {
			logger.warn("ConnectLine的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("ConnectLine的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setName(element.attributeValue("name"));
		setDescription(element.elementText("description"));
		setTargetNodeName(element.elementText("to"));
		
		return this;
	}
	
	public static String getElementName() {
		return elementName;
	}
	
}

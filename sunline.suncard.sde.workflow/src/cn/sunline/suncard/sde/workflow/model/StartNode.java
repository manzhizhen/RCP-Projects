/*
 * 文件名：     ExtVarComposite.java
 * 版权：          Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：          工作流开始节点模型
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
 * 工作流开始节点模型
 * 
 * @author  wzx
 * @version , 2012-3-26
 * @see     
 * @since   1.0
 */

public class StartNode implements DataObjectInterface{
	private static String elementName = "startNode";
	
	private String name;
	private String description;
	private ConnectLine connectLine;
	
	private Log logger = LogManager.getLogger(StartNode.class.getName());
	
	public StartNode() {
		name = "ACD_START";
		description = I18nUtil.getMessage("START_MODEL");
		connectLine = new ConnectLine();
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

	public ConnectLine getConnectLine() {
		return connectLine;
	}

	public void setConnectLine(ConnectLine connectLine) {
		this.connectLine = connectLine;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element startElement = parent.addElement(elementName);
		startElement.addAttribute("name", name + "");
		
		Element descElement = startElement.addElement("description");
		descElement.setText(description + "");
		
		connectLine.getElementFromObject(startElement);
		
		return startElement;
	}

	@Override
	public StartNode getObjectFromElement(Element element) {
		if(element == null ) {
			logger.warn("StartNode的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("StartNode的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setName(element.attributeValue("name"));
		setDescription(element.elementText("description"));
		connectLine.getObjectFromElement(element.element(ConnectLine.getElementName()));
		
		return this;
	}
	
	public static String getElementName() {
		return elementName;
	}
	
}

/*
 * 文件名：     ExtVarComposite.java
 * 版权：          Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：          工作流决策节点模型
 * 修改人：     wzx
 * 修改时间：2012-3-26
 * 修改内容：创建
 */

package cn.sunline.suncard.sde.workflow.model;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 工作流决策节点模型
 * 
 * @author  wzx
 * @version , 2012-3-26
 * @see     
 * @since   1.0
 */

public class DecisionNode implements DataObjectInterface{
	private static String elementName = "decisionNode";
	
	private String name;
	private String description;
	private String handlerClass;
//	private List<ConnectLine> connectLines;
//	private List<CommonNode> commonNodes;
	
	private Log logger = LogManager.getLogger(DecisionNode.class.getName());
	
	public DecisionNode() {
		name = new String();
		description = I18nUtil.getMessage("TASK_MODEL") + I18nUtil.getMessage("DECISION");
		handlerClass = "cn.sunline.suncard.trm.wf.TrmDecisionHandler";
//		connectLines = new ArrayList<ConnectLine>();
//		commonNodes = new ArrayList<CommonNode>();
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

	public String getHandlerClass() {
		return handlerClass;
	}

	public void setHandlerClass(String handlerClass) {
		this.handlerClass = handlerClass;
	}

//	public List<ConnectLine> getConnectLines() {
//		return connectLines;
//	}
//
//	public void setConnectLines(List<ConnectLine> connectLines) {
//		this.connectLines = connectLines;
//	}
//
//	public List<CommonNode> getCommonNodes() {
//		return commonNodes;
//	}
//
//	public void setCommonNodes(List<CommonNode> commonNodes) {
//		this.commonNodes = commonNodes;
//	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element decisionElement = parent.addElement(elementName);
		decisionElement.addAttribute("name", name + "");
		
		Element descElement = decisionElement.addElement("description");
		descElement.setText(description + "");
		
		Element classElement = decisionElement.addElement("handlerClass");
		classElement.setText(handlerClass + "");
		
//		Element connectLinesElement = decisionElement.addElement("connectlines");
//		for(ConnectLine line : connectLines) {
//			line.getElementFromObject(connectLinesElement);
//		}
		
//		Element commonNodesElement = decisionElement.addElement("commonnodes");
//		for(CommonNode node : commonNodes) {
//			node.getElementFromObject(commonNodesElement);
//		}
		
		return decisionElement;
	}

	@Override
	public DecisionNode getObjectFromElement(Element element) {
		if(element == null) {
			logger.warn("DecisionNode的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("DecisionNode的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setName(element.attributeValue("name"));
		setDescription(element.elementText("description"));
		setHandlerClass(element.elementText("handlerClass"));

		return this;
	}
	
	public static String getElementName() {
		return elementName;
	}
}

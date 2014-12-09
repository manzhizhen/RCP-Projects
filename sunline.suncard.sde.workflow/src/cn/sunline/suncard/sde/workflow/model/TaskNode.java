/*
 * 文件名：     ExtVarComposite.java
 * 版权：          Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：          工作流任务节点模型
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
import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditor;

/**
 * 工作流任务节点模型
 * 
 * @author  wzx
 * @version , 2012-3-26
 * @see     
 * @since   1.0
 */

public class TaskNode implements DataObjectInterface {
	private static String elementName = "taskNode";
	
	private String name;
	private String type;
	private String description;
	private Task task;
	private DecisionNode decisionNode;
	
	private Log logger = LogManager.getLogger(TaskNode.class.getName());
	
	public TaskNode() {
		name = new String();
		description = new String();
		task = new Task();
		decisionNode = new DecisionNode();
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

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public DecisionNode getDecisionNode() {
		return decisionNode;
	}

	public void setDecisionNode(DecisionNode decisionNode) {
		this.decisionNode = decisionNode;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element taskNodeElement = parent.addElement(elementName);
		taskNodeElement.addAttribute("name", name == null ? "" : name);
		taskNodeElement.addAttribute("type", type == null ? "" : type);
		
		Element descElement = taskNodeElement.addElement("description");
		descElement.setText(description + "");
		
		task.getElementFromObject(taskNodeElement);
		
		decisionNode.getElementFromObject(taskNodeElement);
		
		return taskNodeElement;
		
	}

	@Override
	public TaskNode getObjectFromElement(Element element) {
		if(element == null ) {
			logger.warn("TaskNode的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("TaskNode的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setName(element.attributeValue("name"));
		setType(element.attributeValue("type"));
		setDescription(element.elementText("description"));
		
		task.getObjectFromElement(element.element(Task.getElementName()));
		
		decisionNode.getObjectFromElement(element.element(DecisionNode.getElementName()));
		
		return this;
	}
	
	public static String getElementName() {
		return elementName;
	}

}

/*
 * 文件名：    Task.java
 * 版权：          Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：          工作流任务模型
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
 * 工作流任务模型
 * 
 * @author  wzx
 * @version , 2012-3-26
 * @see     
 * @since   1.0
 */

public class Task implements DataObjectInterface{
	private static String elementName = "task";
	
	private String name;
	private String description;
	private String assignmentClass;
	
	private Log logger = LogManager.getLogger(Task.class.getName());
	
	public Task() {
		name = new String();
		description = I18nUtil.getMessage("TASK_MODEL");
		assignmentClass = "cn.sunline.suncard.trm.wf.TrmAssignmentHandler";
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

	public String getAssignmentClass() {
		return assignmentClass;
	}

	public void setAssignmentClass(String assignmentClass) {
		this.assignmentClass = assignmentClass;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element taskElement = parent.addElement(elementName);
		taskElement.addAttribute("name", name + "");
		
		Element descElement = taskElement.addElement("description");
		descElement.setText(description);
		
		Element assignmentClassElement = taskElement.addElement("assignmentClass");
		assignmentClassElement.setText(assignmentClass);
		
		return taskElement;
	}

	@Override
	public Task getObjectFromElement(Element element) {
		if(element == null) {
			logger.warn("Task的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("Task的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setName(element.attributeValue("name"));
		setDescription(element.elementText("description"));
		setAssignmentClass(element.elementText("assignmentClass"));
		
		return this;
	}
	
	public static String getElementName() {
		return elementName;
	}
	
}

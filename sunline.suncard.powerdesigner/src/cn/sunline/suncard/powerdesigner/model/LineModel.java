/* 文件名：     LineNode.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-16
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 连接线上绑定的数据对象 
 * @author 易振强
 * @version 1.0, 2012-4-16
 * @see
 * @since 1.0
 */
public class LineModel implements DataObjectInterface{
	private static String elementName = "lineModel";
	
	private String name = ""; 
	private String desc = ""; 
	private String note = ""; 
	
	private String constraintName = ""; // 约束名称
	private String incidenceRelation; // 关联关系
	
	private String parentTableModelId = ""; // 父表的ID
	
	private Log logger = LogManager.getLogger(LineModel.class.getName());

	public LineModel() {
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getConstraintName() {
		return constraintName;
	}

	public void setConstraintName(String constraintName) {
		this.constraintName = constraintName;
	}

	public String getIncidenceRelation() {
		return incidenceRelation;
	}

	public void setIncidenceRelation(String incidenceRelation) {
		this.incidenceRelation = incidenceRelation;
	}
	
	public String getParentTableModelId() {
		return parentTableModelId;
	}

	public void setParentTableModelId(String parentTableModelId) {
		this.parentTableModelId = parentTableModelId;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element lineModelElement = parent.addElement(elementName);
		
		lineModelElement.addAttribute("name", name == null ? "" : name);
		lineModelElement.addAttribute("desc", desc == null ? "" : desc);
		lineModelElement.addAttribute("constraintName", constraintName == null ? 
				"" : constraintName);
		lineModelElement.addAttribute("incidenceRelation", incidenceRelation == null ? 
				"" : incidenceRelation);		
		lineModelElement.addAttribute("parentTableModelId", parentTableModelId == null ? 
				"" : parentTableModelId);
		lineModelElement.setText(note == null ? "" : note);
		
		return lineModelElement;
	}

	@Override
	public LineModel getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("LineModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("LineModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setName(element.attributeValue("name").trim());
		setDesc(element.attributeValue("desc").trim());
		setParentTableModelId(element.attributeValue("parentTableModelId").trim());
		setNote(element.getText());
		setConstraintName(element.attributeValue("constraintName").trim());
		setIncidenceRelation(element.attributeValue("incidenceRelation").trim());
		
		return this;
	}
	
	public static String getElementName() {
		return elementName;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		LineModel newLineModel = new LineModel();
		newLineModel.setName(name);
		newLineModel.setDesc(desc);
		newLineModel.setNote(note);
		newLineModel.setConstraintName(constraintName);
		newLineModel.setIncidenceRelation(incidenceRelation);
		newLineModel.setParentTableModelId(parentTableModelId);
		
		return newLineModel;
	}
}

/* 文件名：     ProjectGroupModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-29
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

import java.util.LinkedHashSet;

import org.dom4j.Element;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 项目群模型
 * @author  Manzhizhen
 * @version 1.0, 2012-12-29
 * @see 
 * @since 1.0
 */
public class ProjectGroupModel implements DataObjectInterface{
	private String id;	// 一个项目控件中项目群ID必须唯一
	private String name;
	private String note;
	private LinkedHashSet<ProjectModel> projectModelSet = new LinkedHashSet<ProjectModel>();
	private static String elementName = "projectGroupModel"; // 保存为document时候的顶节点name

	private Log logger = LogManager.getLogger(ProjectGroupModel.class
			.getName());
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public LinkedHashSet<ProjectModel> getProjectModelSet() {
		return projectModelSet;
	}

	public void setProjectModelSet(LinkedHashSet<ProjectModel> projectModelSet) {
		this.projectModelSet = projectModelSet;
	}

	public static String getElementName() {
		return elementName;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element projectGroupModelElement = parent.addElement(elementName);
		
		projectGroupModelElement.addAttribute("id", id == null ? "" : id);
		projectGroupModelElement.addAttribute("name", id == null ? "" : name);
		projectGroupModelElement.addAttribute("note", id == null ? "" : note);
		
		return projectGroupModelElement;
	}

	@Override
	public ProjectGroupModel getObjectFromElement(Element element, Object... obj) {
		if(element == null ) {
			logger.warn("ProjectGroupModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("ProjectGroupModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setId(element.attributeValue("id").trim());
		setName(element.attributeValue("name").trim());
		setNote(element.attributeValue("note").trim());
		
		return this;
	}
	
	
}

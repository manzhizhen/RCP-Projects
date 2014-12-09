/* 文件名：     ModuleXmlModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model.xml;

import org.dom4j.Element;

import cn.sunline.suncard.powerdesigner.model.DataObjectInterface;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 用来读取和操作ModuleDataConfig.xml配置文件的模型
 * @author  Manzhizhen
 * @version 1.0, 2012-12-28
 * @see 
 * @since 1.0
 */
public class ModuleXmlModel implements DataObjectInterface{
	private String id; // 一个产品下面的模块ID必须唯一
	private String name;
	private String note;
	
	private static String elementName = "moduleData"; // 保存为document时候的顶节点name
	private Log logger = LogManager.getLogger(ModuleXmlModel.class
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

	public static String getElementName() {
		return elementName;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element moduleModelElement = parent.addElement(elementName);
		
		moduleModelElement.addAttribute("id", id == null ? "" : id);
		moduleModelElement.addAttribute("name", name == null ? "" : name);
		moduleModelElement.setText(note == null ? "" : note);
		
		return moduleModelElement;
	}

	@Override
	public ModuleXmlModel getObjectFromElement(Element element, Object... obj) {
		if(element == null ) {
			logger.warn("ModuleXmlModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("ModuleXmlModel的Element为空，无法将xml转换为ModuleXmlModel对象！");
				return null;
			}
		}	
		
		setId(element.attributeValue("id").trim());
		setName(element.attributeValue("name"));
		setNote(element.getTextTrim());
		
		return this;
	}
	
	
}

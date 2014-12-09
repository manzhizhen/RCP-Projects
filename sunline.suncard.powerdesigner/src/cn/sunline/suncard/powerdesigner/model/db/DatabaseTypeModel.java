/* 文件名：     DatabaseTypeModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model.db;

import org.dom4j.Element;

import cn.sunline.suncard.powerdesigner.model.DataObjectInterface;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;


/**
 * 数据库类型模型
 * @author  Manzhizhen
 * @version 1.0, 2012-9-5
 * @see 
 * @since 1.0
 */
public class DatabaseTypeModel implements DataObjectInterface{
	private String databaseName = "";
	private String version = "";
	private String type = "";
	private String code = "";
	private static String elementName = "databaseTypeModel"; // 保存为document时候的顶节点name
	
	private Log logger = LogManager.getLogger(DatabaseTypeModel.class
			.getName());
	
	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element databaseTypeModelElement = parent.addElement(elementName);
		databaseTypeModelElement.addAttribute("type", type == null ? "" : type);
		databaseTypeModelElement.addAttribute("version", version == null ? "" : version);
		databaseTypeModelElement.addAttribute("code", code == null ? "" : code);
		databaseTypeModelElement.setText(databaseName.trim());
		
		return databaseTypeModelElement;
	}

	@Override
	public DatabaseTypeModel getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("DatabaseTypeModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("DatabaseTypeModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setType(element.attributeValue("type").trim());
		setVersion(element.attributeValue("version").trim());
		setCode(element.attributeValue("code").trim());
		setDatabaseName(element.getTextTrim());
		
		return this;
	}
	
	public static String getElementName() {
		return elementName;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof DatabaseTypeModel) {
			DatabaseTypeModel otherDatabaseTypeModel = (DatabaseTypeModel) obj;
			
			return (type.equals(otherDatabaseTypeModel.getType()) && version.equals(otherDatabaseTypeModel.getVersion()) );
		}
		
		return super.equals(obj);
	}
	
	
}

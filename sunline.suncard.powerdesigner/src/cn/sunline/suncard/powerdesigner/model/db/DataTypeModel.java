/* 文件名：     DataTypeModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-10
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model.db;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;

import cn.sunline.suncard.powerdesigner.model.DataObjectInterface;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 数据库数据类型模型
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-9-10
 * @see
 * @since 1.0
 */
public class DataTypeModel implements DataObjectInterface{
	public String name = ""; // 名称,用于显示
	public String type = ""; // 数据类型
	public int length = -1; // 长度,-1表示该类型没有长度
	public int precision = -1; // 精度,-1表示该类型没有精度

	private Log logger = LogManager.getLogger(DataTypeModel.class
			.getName());
	
	private static String elementName = "dataTypeModel"; // 保存为document时候的顶节点name
	
	public DataTypeModel() {
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public DataTypeModel clone() throws CloneNotSupportedException {
		DataTypeModel newModel = new DataTypeModel();
		newModel.setName(name);
		newModel.setType(type);
		newModel.setLength(length);
		newModel.setPrecision(precision);
		
		return newModel;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof DataTypeModel) {
			DataTypeModel model = (DataTypeModel) obj;
			
			return name.equals(model.getName());
		}
		
		return false;
	}
	
	public static String getElementName() {
		return elementName;
	}


	@Override
	public Element getElementFromObject(Element parent) {
		Element dataTypeModelElement = parent.addElement(elementName);
		
		dataTypeModelElement.addAttribute("name", name == null ? "" : name);
		dataTypeModelElement.addAttribute("type", type == null ? "" : type);
		dataTypeModelElement.addAttribute("length", length + "");
		dataTypeModelElement.addAttribute("precision", precision + "");
		
		return dataTypeModelElement;
	}

	@Override
	public DataTypeModel getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("DataTypeModel的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("DataTypeModel的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setName(element.attributeValue("name"));
		setType(element.attributeValue("type"));
		
		try {
			setLength(Integer.valueOf(element.attributeValue("length").trim()));
			setPrecision(Integer.valueOf(element.attributeValue("precision").trim()));
		} catch (NumberFormatException e) {
			logger.error("设置DataTypeModel的长度或精度失败！" + e.getMessage());
			e.printStackTrace();
		}
		
		return this;
	}
	

}

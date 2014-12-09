/* 文件名：     SwitchActionAndXml.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-13
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.sunline.suncard.sde.workflow.model.ActionTreeNode;

/**
 * 将Action和XML文件互转
 * @author  易振强
 * @version 1.0, 2012-4-13
 * @see 
 * @since 1.0
 */
public class SwitchActionAndXml {
	/**
	 * 将一个Action对象转换成XML的字符串
	 */
	public static String getXmlFromAction(ActionTreeNode action) {
		if(action == null) {
			return null;
		}
		
		Document document = DocumentHelper.createDocument();
		
		Element element = document.addElement("actiontreenode");
		element.addAttribute("name", action.getName());
		element.addElement("description").setText(action.getDesc());
		

		return document.asXML();
	}
	
	/**
	 * 将文件解析成一个Action对象
	 */
	public static ActionTreeNode getActionFromXml(String filePath) throws FileNotFoundException, DocumentException {
		if(filePath == null || !new File(filePath).isFile()) {
			return null;
		}
		
		SAXReader saxReader = new SAXReader();
		
		
		Document document = saxReader.read(new FileInputStream(filePath));
		
		if(document == null) {
			return null;
		}
		
		Element element = document.getRootElement();
		ActionTreeNode actionNode = new ActionTreeNode();
		actionNode.setName(element.attributeValue("name"));
		actionNode.setDesc(element.element("description").getTextTrim());
		

		return actionNode;
	}
	
}

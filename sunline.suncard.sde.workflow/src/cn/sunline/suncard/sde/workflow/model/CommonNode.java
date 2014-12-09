/*
 * 文件名：     ExtVarComposite.java
 * 版权：          Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：          工作流普通节点模型
 * 修改人：     wzx
 * 修改时间：2012-3-26
 * 修改内容：创建
 */

package cn.sunline.suncard.sde.workflow.model;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.CDATA;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.InvalidXPathException;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.ProcessingInstruction;
import org.dom4j.QName;
import org.dom4j.Text;
import org.dom4j.Visitor;
import org.dom4j.XPath;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 工作流普通节点模型
 * 
 * @author  wzx
 * @version , 2012-3-26
 * @see     
 * @since   1.0
 */

public class CommonNode implements DataObjectInterface{
	private static String elementName = "commonNode";
	
	private String name;
	private String description;
	private String actionClass;
	private Log logger = LogManager.getLogger(CommonNode.class.getName());
	
	
	// 其实CommonNode可以不需要连接线信息,因为该连接线信息主要是保存下一个TaskNode的节点Id(Name),
	// 但这一信息可以从GEF图形上的连接线的目标节点(其数据对象就是TaskNode)中获得
//	private ConnectLine connectLine;
	
	public CommonNode() {
		name = new String();
		description = new String();
		actionClass = "cn.sunline.suncard.trm.wf.TrmWorkFlowActionHandler";
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

	public String getActionClass() {
		return actionClass;
	}

	public void setActionClass(String actionClass) {
		this.actionClass = actionClass;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element commonNodeElement = parent.addElement(elementName);
		commonNodeElement.addAttribute("name", name + "");
		
		Element descElement = commonNodeElement.addElement("description");
		descElement.setText(description + "");
		
		Element classElement = commonNodeElement.addElement("actionClass");
		classElement.setText(actionClass + "");
		
		return commonNodeElement;
	}

	@Override
	public CommonNode getObjectFromElement(Element element) {
		if(element == null) {
			logger.warn("CommonNode的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("CommonNode的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setName(element.attributeValue("name"));
		setDescription(element.elementText("description"));
		setActionClass(element.elementText("actionClass"));
		
		return this;
	}
	
	public static String getElementName() {
		return elementName;
	}
}

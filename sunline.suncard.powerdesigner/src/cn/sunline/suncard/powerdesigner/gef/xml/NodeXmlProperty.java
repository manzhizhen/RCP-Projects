/* 文件名：     NodeXmlProperty.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-11
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.xml;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import cn.sunline.suncard.powerdesigner.model.DataObjectInterface;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;



/**
 * 模型图元要保存为XML所需要保存的属性
 * @author  易振强
 * @version 1.0, 2012-4-11
 * @see 
 * @since 1.0
 */
public class NodeXmlProperty implements DataObjectInterface{
	private String id;		//节点id
	private String type; 	// 节点的类型
	private Rectangle rectangle = new Rectangle(); // 节点位置
	private List<String> lineIdList = new ArrayList<String>(); // 以该节点出发的连接线ID
	
	private static String elementName = "node"; // 保存为document时候的顶节点name
	private Log logger = LogManager.getLogger(NodeXmlProperty.class
			.getName());

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getLineIdList() {
		return lineIdList;
	}

	public void setLineIdList(List<String> lineIdList) {
		this.lineIdList = lineIdList;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public void setRectangle(Rectangle rectangle) {
		this.rectangle = rectangle;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element nodeElement = parent.addElement(elementName);
		
		nodeElement.addAttribute("id", id == null ? "" : id);
		nodeElement.addAttribute("type", type == null ? "" : type);
		Element locationE = nodeElement.addElement("location");
		locationE.addAttribute("x", rectangle.x + "");
		locationE.addAttribute("y", rectangle.y + "");
		locationE.addAttribute("width", rectangle.width + "");
		locationE.addAttribute("height", rectangle.height + "");
		
		Element connectionsE = nodeElement.addElement("connections");
		for(String lineId : lineIdList) {
			connectionsE.addElement("connection").setText(lineId);
		}
		
		return nodeElement;
	}

	@Override
	public NodeXmlProperty getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("NodeXmlProperty的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("NodeXmlProperty的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setId(element.attributeValue("id").trim());
		setType(element.attributeValue("type").trim());
		Element locationE = element.element("location");
		setRectangle(new Rectangle(Integer.valueOf(locationE.attributeValue("x").trim()), Integer.valueOf(locationE.attributeValue("y").trim()), 
				Integer.valueOf(locationE.attributeValue("width").trim()), Integer.valueOf(locationE.attributeValue("height").trim())));
		
		lineIdList = new ArrayList<String>();
		Element lineIdsE = element.element("connections");
		List<Element> lineIdEList = lineIdsE.elements();
		for(Element lineIdE : lineIdEList) {
			lineIdList.add(lineIdE.getTextTrim());
		}
		
		return this;
	}
}

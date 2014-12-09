/* 文件名：     LineXmlProperty.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-11
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;
import org.eclipse.draw2d.geometry.Point;

import cn.sunline.suncard.powerdesigner.model.DataObjectInterface;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 连接线要保存为XML所需要保存的属性
 * @author  易振强
 * @version 1.0, 2012-4-11
 * @see 
 * @since 1.0
 */
public class LineXmlProperty implements DataObjectInterface{
	private String id;				//连接线id
	private String type;			// 连接线类型
	private String label;			//连接线的标签
	
	private String sourceNodeId;	//连接线的开始节点ID
	private String targetNodeId;	//连接线的结束节点ID
	
	private List<Point> bendPointList = new ArrayList<Point>(); //锚点的坐标List
	
	private static String elementName = "connection"; // 保存为document时候的顶节点name
	
	private Log logger = LogManager.getLogger(LineXmlProperty.class
			.getName());

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSourceNodeId() {
		return sourceNodeId;
	}

	public void setSourceNodeId(String sourceNodeId) {
		this.sourceNodeId = sourceNodeId;
	}

	public String getTargetNodeId() {
		return targetNodeId;
	}

	public void setTargetNodeId(String targetNodeId) {
		this.targetNodeId = targetNodeId;
	}

	public List<Point> getBendPointList() {
		return bendPointList;
	}

	public void setBendPointList(List<Point> bendPointList) {
		this.bendPointList = bendPointList;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		Element lineElement = parent.addElement(elementName);
		
		lineElement.addAttribute("id", id == null ? "" : id);
		lineElement.addAttribute("type", type == null ? "" : type);
		lineElement.addAttribute("sourceNodeId", sourceNodeId == null ? "" : sourceNodeId);
		lineElement.addAttribute("targetNodeId", targetNodeId == null ? "" : targetNodeId);
		lineElement.addElement("label").setText(label == null ? "" : label);
		
		Element anchorsE = lineElement.addElement("anchors");
		for(Point point : bendPointList) {
			Element anchorElement = anchorsE.addElement("anchor");
			anchorElement.addAttribute("x", point.x + "");
			anchorElement.addAttribute("y", point.y + "");
		}
		
		return lineElement;
	}
	
	

	@Override
	public LineXmlProperty getObjectFromElement(Element element, Object...obj) {
		if(element == null ) {
			logger.warn("LineXmlProperty的Element为空，无法将xml转换为对象！");
			return null;
		}
		
		if(!elementName.equals(element.getName())) {
			element = element.element(elementName);
			if(element == null ) {
				logger.warn("LineXmlProperty的Element为空，无法将xml转换为对象！");
				return null;
			}
		}
		
		setId(element.attributeValue("id").trim());
		setType(element.attributeValue("type").trim());
		setSourceNodeId(element.attributeValue("sourceNodeId").trim());
		setTargetNodeId(element.attributeValue("targetNodeId").trim());
		
		setLabel(element.elementText("label").trim());
		
		bendPointList = new ArrayList<Point>(); 
		Element anchorsElement = element.element("anchors");
		List<Element> anchorElementList = anchorsElement.elements();
		for(Element anchorElement : anchorElementList) {
			bendPointList.add(new Point(Integer.valueOf(anchorElement.attributeValue("x").trim()), 
					Integer.valueOf(anchorElement.attributeValue("y").trim())));
		}
		
		
		return this;
	}
	
}

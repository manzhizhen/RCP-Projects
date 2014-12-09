/* 文件名：     LineXmlProperty.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-11
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.xml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;

/**
 * 连接线要保存为XML所需要保存的属性
 * @author  易振强
 * @version 1.0, 2012-4-11
 * @see 
 * @since 1.0
 */
public class LineXmlProperty {
	private String id;				//连接线id
	private String name;			//连接线名称
	private String label;			//连接线的标签
	private String type; 			// 连接线的类型
	
	private String sourceNodeId;	//连接线的开始节点ID
	private String targetNodeId;	//连接线的结束节点ID
	
	private List<Point> bendPointList = new ArrayList<Point>(); //锚点的坐标List

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}

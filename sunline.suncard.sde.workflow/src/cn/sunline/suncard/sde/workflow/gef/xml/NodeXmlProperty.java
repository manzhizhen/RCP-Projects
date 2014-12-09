/* 文件名：     NodeXmlProperty.java
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
import org.eclipse.draw2d.geometry.Rectangle;

import cn.sunline.suncard.sde.workflow.gef.model.AbstractLineModel;


/**
 * 模型图元要保存为XML所需要保存的属性
 * @author  易振强
 * @version 1.0, 2012-4-11
 * @see 
 * @since 1.0
 */
public class NodeXmlProperty {
	private String id;		//节点id
	private String name;	//节点名称
	private String label;	// 节点需要展现的标签
	private Rectangle rectangle = new Rectangle(); // 节点位置
	
	private String type; // 节点的类型
	
	private List<String> lineIdList = new ArrayList<String>(); // 以该节点出发的连接线ID

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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<String> getLineIdList() {
		return lineIdList;
	}

	public void setLineIdList(List<String> lineIdList) {
		this.lineIdList = lineIdList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public void setRectangle(Rectangle rectangle) {
		this.rectangle = rectangle;
	}
}

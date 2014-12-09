/* 文件名：     TreeContent.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2011-10-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.tree.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.bsf.Main;
import org.eclipse.swt.graphics.Image;


/**
 * 树模板的内容
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */

public class TreeContent {
	private String id;
	private String nodeName = "";	// 该名字一般用于排序时取值，由标签提供者负责更新
	private Object obj;
	private TreeContent parent;
	private Image image = null;
	private List<TreeContent> childrenList = new ArrayList<TreeContent>();
	
	
	public TreeContent() {
	}
	
	/**
	 * 节点构造函数（父节点有内容，有名字、有图标）
	 * @param nodeName 父节点名称
	 * @param Object 父节点对象
	 * @param childrenList 子对象List
	 * @param clazz
	 */
	public TreeContent(String id, Object obj, List<TreeContent> childrenList, Image image) {
		this.id = id;
		this.obj = obj;
		this.childrenList = childrenList;
		
		for(TreeContent content : childrenList) {
			content.setParent(this);
		}
		
		this.image = image;
	}
	
	
	/**
	 * 节点构造函数（无图标）
	 * @param obj
	 * @param nodeName
	 * @param clazz
	 */
	public TreeContent(String id, Object obj) {
		this.id = id;
		this.obj = obj;
	}
	
	/**
	 * 节点构造函数（有图标）
	 * @param obj
	 * @param nodeName
	 * @param clazz
	 */
	public TreeContent(String id, Object obj, Image image) {
		this.id = id;
		this.obj = obj;
		
		this.image = image;
	}
	
	public boolean hasChildren() {
		return childrenList != null && childrenList.size() > 0;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public List<TreeContent> getChildrenList() {
		return childrenList;
	}

	@Override
	public String toString() {
		return "节点ID：" + id + " 节点数据对象：" + obj.toString() ;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public TreeContent getParent() {
		return parent;
	}

	public void setParent(TreeContent parent) {
		this.parent = parent;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setChildrenList(List<TreeContent> childrenList) {
		this.childrenList = childrenList;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	
	
}



/* 文件名：     DatabaseDiagramModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.model;

import java.util.ArrayList;
import java.util.List;


/**
 * 集合模型
 * @author  Manzhizhen
 * @version 1.0, 2012-9-6
 * @see 
 * @since 1.0
 */
public class DatabaseDiagramGefModel extends AbstractListenerGefModel{
	public static final String P_CHILDREN = "_children";	// 当子模型有改变时，会使用此标记
	
	private List<AbstractGefModel> children = new ArrayList<AbstractGefModel>();	// 子模型列表

	private List<AbstractGefModel> outlineChildren = new ArrayList<AbstractGefModel>();		// outline树的子模型列表。
	
	/**
	 *  获得子模型的方法
	 */
	public List<AbstractGefModel> getChildren() {
		return children;
	}
	
	/**
	 *  添加子模型
	 */
	public void addChild(AbstractGefModel child) {
		// 添加子模型
		children.add(child);
		child.setParentGefModel(this);
		outlineChildren.add(child);
		
//		// 如果子模型中含有LabelModel模型，则还要添加该LabelModel
//		if(child instanceof AbstractModel && ((AbstractModel) child).
//				getLabelModel() != null) {
//			children.add(((AbstractModel)child).getLabelModel());
//		}
		
		firePropertyListenerChange(P_CHILDREN, null, null);
	}

	/**
	 *  添加子模型List
	 */
	public void addChildList(List<AbstractGefModel> childList) {
		for(AbstractGefModel obj : childList) {
			if(obj instanceof AbstractGefModel) {
				addChild(obj);
			}
		}
		
	}

	/**
	 *  删除一个子模型
	 */
	public void removeChild(AbstractGefModel child) {
		// 删除一个子模型
		children.remove(child);
		outlineChildren.remove(child);
		
//		if(child instanceof AbstractModel && ((AbstractModel) child).
//				getLabelModel() != null) {
//			children.remove(((AbstractModel)child).getLabelModel());
//		}
		
		// 删除子模型后通知EditPart
		firePropertyListenerChange(P_CHILDREN, null, null);
	}

	/**
	 * 删除子模型List
	 */
	public void removeChildList(List<AbstractGefModel> childList) {
		for(AbstractGefModel obj : childList) {
			if(obj instanceof AbstractGefModel) {
				removeChild(obj);
			}
		}
	}
	
	/**
	 * 刷新全部的子模型
	 */
	public void refreshChildren() {
		firePropertyListenerChange(P_CHILDREN, null, null);
	}
	
	
	public List<AbstractGefModel> getOutlineChildren() {
		return outlineChildren;
	}
}

/* 文件名：     WorkFlowModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.model;

import java.util.ArrayList;
import java.util.List;


/**
 * 集合模型模型
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */

public class WorkFlowModel extends AbstractListenerModel{
	
	public static final String P_CHILDREN = "_children";	// 当子模型有改变时，会使用此标记
	
	private List<AbstractModel> children = new ArrayList<AbstractModel>();	// 子模型列表

	private List<AbstractModel> outlineChildren = new ArrayList<AbstractModel>();		// outline树的子模型列表。
	
	/**
	 *  获得子模型的方法
	 */
	public List<AbstractModel> getChildren() {
		return children;
	}
	
	/**
	 *  添加子模型
	 */
	public void addChild(AbstractModel child) {
		// 添加子模型
		children.add(child);
		outlineChildren.add(child);
		
		// 如果子模型中含有LabelModel模型，则还要添加该LabelModel
		if(child instanceof AbstractModel && ((AbstractModel) child).
				getLabelModel() != null) {
			children.add(((AbstractModel)child).getLabelModel());
		}
		
		firePropertyListenerChange(P_CHILDREN, null, null);
	}

	/**
	 *  添加子模型List
	 */
	public void addChildList(List<AbstractModel> childList) {
		for(AbstractModel obj : childList) {
			if(obj instanceof AbstractModel && ((AbstractModel) obj).
					getLabelModel() != null) {
				addChild(obj);
			}
		}
		
	}

	/**
	 *  删除一个子模型
	 */
	public void removeChild(AbstractModel child) {
		// 删除一个子模型
		children.remove(child);
		outlineChildren.remove(child);
		
		if(child instanceof AbstractModel && ((AbstractModel) child).
				getLabelModel() != null) {
			children.remove(((AbstractModel)child).getLabelModel());
		}
		
		// 删除子模型后通知EditPart
		firePropertyListenerChange(P_CHILDREN, null, null);
	}

	/**
	 * 删除子模型List
	 */
	public void removeChildList(List<AbstractModel> childList) {
		for(AbstractModel obj : childList) {
			if(obj instanceof AbstractModel && ((AbstractModel) obj).
					getLabelModel() != null) {
				removeChild(obj);
			}
		}
	}
	
	public List<AbstractModel> getOutlineChildren() {
		return outlineChildren;
	}
}

/* 文件名：     WorkFlowEditorInput.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import cn.sunline.suncard.sde.workflow.gef.model.WorkFlowModel;
import cn.sunline.suncard.sde.workflow.model.WorkFlowTreeNode;

/**
 * 工作流Editor对应的Input
 * 
 * @author 易振强
 * @version 1.0, 2012-3-26
 * @see
 * @since 1.0
 */
public class WorkFlowEditorInput implements IEditorInput {
	private String name = " "; // editor的名字
	private WorkFlowTreeNode treeModel; // 绑定的集合模型
	private String state = null; // 设定打开Editor的状态

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolTipText() {
		return name;
	}

	public WorkFlowTreeNode getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(WorkFlowTreeNode treeModel) {
		this.treeModel = treeModel;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof WorkFlowEditorInput)) {
			return false;
		}
		
		if (state == null) {
			if (this.getName().equals(((WorkFlowEditorInput) obj).getName())) {
				return true;
			}
			
		} else {
			if (this.getTreeModel().getId().equals(((WorkFlowEditorInput) obj).getTreeModel().getId())
					&& this.state
							.equals(((WorkFlowEditorInput) obj).getState())) {
				return true;
			}
		}
		
		return super.equals(obj);
	}

}

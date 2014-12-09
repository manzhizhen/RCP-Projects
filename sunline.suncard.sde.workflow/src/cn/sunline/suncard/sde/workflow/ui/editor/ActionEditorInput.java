/* 文件名：     ActionEditorInput.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-9
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.ui.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditorInput;
import cn.sunline.suncard.sde.workflow.model.ActionTreeNode;

/**
 * 描述
 * @author  易振强
 * @version 1.0, 2012-4-9
 * @see 
 * @since 1.0
 */
public class ActionEditorInput implements IEditorInput {
	private String name = "";
	private String flag = null;
	private ActionTreeNode node;
	
	
	public ActionEditorInput(){}
	
	public ActionEditorInput(String name){
		this.name = name;
	}
	
	public ActionEditorInput(String name, ActionTreeNode node){
		this.name = name;
		this.node = node;
	}
	
	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
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

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public ActionTreeNode getNode() {
		return node;
	}

	public void setNode(ActionTreeNode node) {
		this.node = node;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof ActionEditorInput)) {
			return false;
		}
		
		if (flag == null) {
			if (this.getName().equals(((ActionEditorInput) obj).getName())) {
				return true;
			}
			
		} else {
			if (this.getName().equals(((ActionEditorInput) obj).getName())
					&& this.flag
							.equals(((ActionEditorInput) obj).getFlag())) {
				return true;
			}
		}
		
		return super.equals(obj);
	}
	

}

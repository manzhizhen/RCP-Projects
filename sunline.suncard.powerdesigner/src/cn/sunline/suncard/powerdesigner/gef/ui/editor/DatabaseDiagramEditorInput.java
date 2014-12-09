/* 文件名：     DatabaseDiagramEditorInput.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.ui.editor;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;

/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2012-9-6
 * @see 
 * @since 1.0
 */
public class DatabaseDiagramEditorInput implements IEditorInput{
	private String editorName = ""; // editor的名字
	private TreeContent physicalDiagramTreeContent;
	
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
		// TODO Auto-generated method stub
		return editorName;
	}

	@Override
	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolTipText() {
		return editorName;
	}

	public String getEditorName() {
		return editorName;
	}

	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}

	public TreeContent getPhysicalDiagramTreeContent() {
		return physicalDiagramTreeContent;
	}

	public void setPhysicalDiagramTreeContent(TreeContent physicalDiagramTreeContent) {
		this.physicalDiagramTreeContent = physicalDiagramTreeContent;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof DatabaseDiagramEditorInput)) {
			return false;
		}
		
		return physicalDiagramTreeContent.getObj().equals(((DatabaseDiagramEditorInput)obj).
				getPhysicalDiagramTreeContent().getObj());
	}
	
}

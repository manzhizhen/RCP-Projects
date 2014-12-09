/* 文件名：     WorkFlowInfoProcess.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	editor与xml交互的信息桥梁类
 * 修改人：     易振强
 * 修改时间：2012-3-28
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.sde.workflow.gef.model.AbstractLineModel;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;
import cn.sunline.suncard.sde.workflow.gef.model.LabelModel;
import cn.sunline.suncard.sde.workflow.gef.model.WorkFlowModel;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditor;

/**
 * editor与xml交互的信息桥梁类
 * @author  tpf
 * @version 1.0, 2012-3-28
 * @see 
 * @since 1.0
 */
public class WorkFlowInfoProcess {

	//editor
	private WorkFlowEditor workFlowEditor;
	
	// 取到编辑器上的所有元素
	private List workFlowElements;
	
	// 编辑器上的所有节点的list
	private List<AbstractModel> nodeModels = new ArrayList<AbstractModel>();

	// 编辑器上的所有连接线的list
	private List<AbstractLineModel> lineModels = new ArrayList<AbstractLineModel>();

	// 编辑器上的有用节点模型list
	private List<AbstractModel> normalNodeModels = new ArrayList<AbstractModel>();
	
	/**
	 * 初始化workflow信息
	 */
	public void initWorkFlowInfo() {
		getWorkFlowEditor();			//初始化editor
		
		initWorkFlowElement();			//取到WorkFlowEditor对象中的所有元素
		
		initModelList();				//初始化所有的节点model和所有的连接线model
		
		initNormalNodeModel();			//初始化所有有用的节点model
	}
	
	/**
	 * 通过WorkFlowModel初始化WorkFlowInfoProcess
	 */
	public void initWorkFlowInfo(WorkFlowModel workFlowModel) {
		
		workFlowElements = workFlowModel.getChildren();		//初始化workFlow模型元素
		
		initModelList();									//初始化所有的节点model和所有的连接线model
		
		initNormalNodeModel();								//初始化所有有用的节点model
	}
	
	/**
	 * 取得WorkFlowEditor对象
	 */
	public WorkFlowEditor getWorkFlowEditor() {
		if(workFlowEditor != null) {
			return workFlowEditor;
		} else {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorPart editorPart = page.getActiveEditor();
			if(editorPart instanceof WorkFlowEditor) {
				workFlowEditor = ((WorkFlowEditor)editorPart);
			}
			return workFlowEditor;
		}
	}
	
	/**
	 * 取到ScorecardEditor对象中的所有元素
	 */
	private void initWorkFlowElement() {
		workFlowElements = workFlowEditor.getWorkFlowModel().getChildren();
	}
	
	/**
	 * 取到所有的节点model和所有的连接线model
	 */
	private void initModelList() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < workFlowElements.size(); i++) {
			Object obj = workFlowElements.get(i);
			if (obj instanceof AbstractModel) {
				AbstractModel nodeModel = (AbstractModel) obj;
				if (nodeModel.getLabelModel() == null) {
					continue;
				}
//				setModelProperty(nodeModel); // 修改时设置模型的属性信息
				if(lineModels.containsAll(nodeModel.getSourceConnections())) {
					lineModels.removeAll(nodeModel.getSourceConnections());
					lineModels.addAll(nodeModel.getSourceConnections());
				} else {
					lineModels.addAll(nodeModel.getSourceConnections());
				}
				nodeModels.add(nodeModel);
			}
		}
	}

	/**
	 * 初始化所有有用节点
	 */
	private void initNormalNodeModel() {
		for (AbstractModel model : nodeModels) {
			if (model instanceof LabelModel) {
				continue;
			}
			normalNodeModels.add(model);
		}
	}

	public List getWorkFlowElements() {
		return workFlowElements;
	}

	public void setWorkFlowElements(List workFlowElements) {
		this.workFlowElements = workFlowElements;
	}

	public List<AbstractModel> getNodeModels() {
		return nodeModels;
	}

	public void setNodeModels(List<AbstractModel> nodeModels) {
		this.nodeModels = nodeModels;
	}

	public List<AbstractLineModel> getLineModels() {
		return lineModels;
	}

	public void setLineModels(List<AbstractLineModel> lineModels) {
		this.lineModels = lineModels;
	}

	public List<AbstractModel> getNormalNodeModels() {
		return normalNodeModels;
	}

	public void setNormalNodeModels(List<AbstractModel> normalNodeModels) {
		this.normalNodeModels = normalNodeModels;
	}
}

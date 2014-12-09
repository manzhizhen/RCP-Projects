/* 文件名：     TaskModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	任务节点
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.model;

import cn.sunline.suncard.sde.workflow.gef.model.props.PropertySourceAdapter;
import cn.sunline.suncard.sde.workflow.model.DataObjectInterface;
import cn.sunline.suncard.sde.workflow.model.TaskNode;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 任务节点
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */
public class TaskModel extends AbstractModel{
	public static final String PROP_ID_LABEL = "taskmodel_label_text";	// 任务（Task对象）的描述
	
	public static final String PROP_TASKNODE_NAME = "PROP_TASKNODE_NAME";
	public static final String PROP_TASKNODE_DESC = "PROP_TASKNODE_DESC";
	public static final String PROP_TASK_NAME = "PROP_TASK_NAME";
	public static final String PROP_TASK_HANDLER = "PROP_TASK_HANDLER";
	public static final String PROP_DEC_NAME = "PROP_DEC_NAME";
	public static final String PROP_DEC_DESC = "PROP_DEC_DESC";
	public static final String PROP_DEC_HANDLER = "PROP_DEC_HANDLER";
	
	private TaskNode taskNode;
	
	public TaskModel() {
		labelModel = new LabelModel(this);
		labelModel.setLabelName(I18nUtil.getMessage("TASK_MODEL"));
		nodeXmlProperty.setLabel(I18nUtil.getMessage("TASK_MODEL"));
		taskNode = new TaskNode();
	}
	

	@Override
	public void refreshName() {
	}

	@Override
	public void updateImage() {
	}

	public TaskNode getTaskNode() {
		return taskNode;
	}


	public void setTaskNode(TaskNode taskNode) {
		this.taskNode = taskNode;
	}
	
	@Override
	public void setLabelName(String name) {
		super.setLabelName(name);
		getTaskNode().getTask().setDescription(name);
		getTaskNode().getDecisionNode().setDescription(name + I18nUtil.getMessage("DECISION"));
	}
	
	/**
	 * Task的处理类
	 */
	public void setTaskHandler(String handler) {
		taskNode.getTask().setAssignmentClass(handler);
		firePropertyListenerChange(PROP_TASK_HANDLER, null, handler);
	}
	
	/**
	 * Decision的处理类
	 */
	public void setDecHandler(String handler) {
		taskNode.getDecisionNode().setHandlerClass(handler);
		firePropertyListenerChange(PROP_DEC_HANDLER, null, handler);
	}
	
	/**
	 * Task的描述，也就是节点的Label显示内容
	 */
	public void setTaskDesc(String desc) {
		taskNode.getTask().setDescription(desc);
		firePropertyListenerChange(PROP_ID_LABEL, null, desc);
	}


	@Override
	public DataObjectInterface getDataObject() {
		return taskNode;
	}

	@Override
	public void setDataObject(DataObjectInterface object) {
		taskNode = (TaskNode) object;
	}
	
}

/* 文件名：     TaskPropertySource.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-6
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.model.props;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;

/**
 * 任务模型的属性视图
 * @author  易振强
 * @version 1.0, 2012-4-6
 * @see 
 * @since 1.0
 */
public class TaskPropertySource extends AbstractPropertySource{
	private TaskModel taskModel;
	
	public TaskPropertySource(TaskModel taskModel) {
		this.taskModel = taskModel;
	}

	@Override
	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		PropertyDescriptor nameDescriptor = new PropertyDescriptor(TaskModel.PROP_TASKNODE_NAME, I18nUtil.getMessage("NAME"));
		PropertyDescriptor  labelDescriptor= new PropertyDescriptor(TaskModel.PROP_TASKNODE_DESC, I18nUtil.getMessage("DESC"));
		labelDescriptor.setCategory(I18nUtil.getMessage("BASE_ATTRI"));
		nameDescriptor.setCategory(I18nUtil.getMessage("BASE_ATTRI"));
		
		PropertyDescriptor taskNameDescriptor = new PropertyDescriptor(TaskModel.PROP_TASK_NAME, I18nUtil.getMessage("TASK_NAME"));
		PropertyDescriptor taskDescDescriptor = new PropertyDescriptor(TaskModel.PROP_ID_LABEL, I18nUtil.getMessage("TASK_DESC"));
		TextPropertyDescriptor handlerDescriptor = new TextPropertyDescriptor(TaskModel.PROP_TASK_HANDLER, I18nUtil.getMessage("HANDLER_CLASS"));
		taskNameDescriptor.setCategory(I18nUtil.getMessage("TASK_ATTRI"));
		taskDescDescriptor.setCategory(I18nUtil.getMessage("TASK_ATTRI"));
		handlerDescriptor.setCategory(I18nUtil.getMessage("TASK_ATTRI"));	
		PropertyDescriptor decNameDescriptor = new PropertyDescriptor(TaskModel.PROP_DEC_NAME, I18nUtil.getMessage("DECISION_NAME"));
		PropertyDescriptor decDescDescriptor = new PropertyDescriptor(TaskModel.PROP_DEC_DESC, I18nUtil.getMessage("DECISION_DESC"));
		TextPropertyDescriptor dechandlerDescriptor = new TextPropertyDescriptor(TaskModel.PROP_DEC_HANDLER, I18nUtil.getMessage("HANDLER_CLASS"));
		decNameDescriptor.setCategory(I18nUtil.getMessage("DECISION_ATTRI"));
		decDescDescriptor.setCategory(I18nUtil.getMessage("DECISION_ATTRI"));
		dechandlerDescriptor.setCategory(I18nUtil.getMessage("DECISION_ATTRI"));
		
		IPropertyDescriptor[] props = new IPropertyDescriptor[]{
				nameDescriptor, labelDescriptor,
				taskNameDescriptor, taskDescDescriptor, handlerDescriptor,
				decNameDescriptor, decDescDescriptor, dechandlerDescriptor};
		
		return props;
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals(TaskModel.PROP_TASKNODE_NAME)) { 
		      return taskModel.getTaskNode().getName();
		      
		} else if(id.equals(TaskModel.PROP_TASKNODE_DESC)) {
			 return taskModel.getTaskNode().getDescription();
			 
		} else if(id.equals(TaskModel.PROP_TASK_NAME)) {
			 return taskModel.getTaskNode().getTask().getName();
			 
		} else if(id.equals(TaskModel.PROP_ID_LABEL)) {
			 return taskModel.getTaskNode().getTask().getDescription();
			 
		} else if(id.equals(TaskModel.PROP_TASK_HANDLER)) {
			 return taskModel.getTaskNode().getTask().getAssignmentClass();
			 
		} else if(id.equals(TaskModel.PROP_DEC_NAME)) {
			 return taskModel.getTaskNode().getDecisionNode().getName();
			 
		} else if(id.equals(TaskModel.PROP_DEC_DESC)) {
			 return taskModel.getTaskNode().getDecisionNode().getDescription();
			 
		} else if(id.equals(TaskModel.PROP_DEC_HANDLER)) {
			 return taskModel.getTaskNode().getDecisionNode().getHandlerClass();
		}
		
		return null;
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (id.equals(TaskModel.PROP_TASK_HANDLER) || id.equals(TaskModel.PROP_DEC_HANDLER)) { 
		      return true;
		} 
		
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if(id.equals(TaskModel.PROP_TASK_HANDLER)) {
			 taskModel.getTaskNode().getTask().setAssignmentClass((String) value);
			 
		}else if(id.equals(TaskModel.PROP_DEC_HANDLER)) {
			 taskModel.getTaskNode().getDecisionNode().setHandlerClass((String) value);
		}
	}
	
	
}

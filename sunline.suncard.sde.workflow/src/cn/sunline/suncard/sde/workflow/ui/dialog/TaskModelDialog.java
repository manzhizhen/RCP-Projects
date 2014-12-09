/* 文件名：     TaskModelDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-29
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.ui.dialog;

import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.sde.bs.dict.BizDictManager;
import cn.sunline.suncard.sde.bs.dict.DictComboViewer;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.workflow.gef.command.dialog.TaskModelChangeCommand;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractLineModel;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;
import cn.sunline.suncard.sde.workflow.model.CommonNode;
import cn.sunline.suncard.sde.workflow.model.ConnectLine;
import cn.sunline.suncard.sde.workflow.model.DecisionNode;
import cn.sunline.suncard.sde.workflow.model.Task;
import cn.sunline.suncard.sde.workflow.model.TaskNode;
import cn.sunline.suncard.sde.workflow.ui.dialog.factory.TitleAreaDialogMould;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;

/**
 * 双击任务节点打开的对话框
 * @author  易振强
 * @version 1.0, 2012-3-29
 * @see 
 * @since 1.0
 */
public class TaskModelDialog extends TitleAreaDialogMould {
	private Composite composite;
	
	private Text nameText;
	private Text descText;
	private Text decNameText;
	private Text decDescText;
	private Text decHandlerText;
	
	private TaskModel taskModel;
	private CommandStack commandStack;
	
	Log logger = LogManager.getLogger(AddWorkFlowDialog.class);
	private Text taskHandlerText;
	private Text taskNameText;
	private Text taskDescText;

	private Combo typeCombo;

	private DictComboViewer typeComboViewer;
	
	public TaskModelDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(676, 607);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText(I18nUtil.getMessage("TASK_MODEL_ATTR"));
		super.configureShell(newShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(I18nUtil.getMessage("TASK_MODEL_ATTR"));
		
		Control control = super.createDialogArea(parent);
		composite = super.getMouldComposite();
		
		
		createControl();
		
		try {
			initData();
		} catch (DocumentException e) {
			// 解析业务字典出错！
			logger.error("解析业务字典出错！" + e.getMessage());
			setErrorMessage(I18nUtil.getMessage("ANALYSIS_DICT_ERROR") + e.getMessage());
			MessageDialog.openError(getShell(), I18nUtil.getMessage("MESSAGE"), 
					I18nUtil.getMessage("ANALYSIS_DICT_ERROR") + e.getMessage());
			e.printStackTrace();
		}
		
		createEvent();
		
		return control;
	}
	
	/**
	 * 初始化控件数据
	 * @throws DocumentException 
	 */
	private void initData() throws DocumentException {
		if(taskModel == null || taskModel.getTaskNode() == null) {
			logger.error("任务节点对象为空，无法初始化数据！");
			return ;
		}
		
		TaskNode taskNode = taskModel.getTaskNode();
		
		nameText.setText(taskNode.getName() == null ? "" : taskNode.getName());
		descText.setText(taskNode.getDescription() == null ? "" : taskNode.getDescription());
		
		Map<String, String> map = null;
		map = BizDictManager.getDictIdValue("assignType");
		typeComboViewer.setMap(map);
		
		typeComboViewer.setSelect(taskNode.getType());
		
		Task task = taskNode.getTask();
		taskNameText.setText(task.getName() == null ? "" : task.getName());
		taskDescText.setText(task.getDescription() == null ? "" : task.getDescription());
		taskHandlerText.setText(task.getAssignmentClass() == null ? "" : task.getAssignmentClass());
		
		DecisionNode decNode = taskNode.getDecisionNode();
		if(decNode != null) {
			decNameText.setText(decNode.getName() == null ? "" : decNode.getName());
			decDescText.setText(decNode.getDescription() == null ? "" : decNode.getDescription());
			decHandlerText.setText(decNode.getHandlerClass() == null ? "" : decNode.getHandlerClass());
			
		}
	}

	/**
	 * 创建对话框控件
	 */
	private void createControl() {
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.right = new FormAttachment(0, 80);
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 5);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText(I18nUtil.getMessage("TASK_NODE_NAME") + ":");
		
		nameText = new Text(composite, SWT.BORDER);
		FormData fd_nameText = new FormData();
		fd_nameText.right = new FormAttachment(50);
		fd_nameText.bottom = new FormAttachment(lblNewLabel, 3, SWT.BOTTOM);
		fd_nameText.left = new FormAttachment(lblNewLabel, 9);
		nameText.setLayoutData(fd_nameText);
		
		nameText.setEditable(false);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.left = new FormAttachment(0, 5);
		fd_lblNewLabel_1.top = new FormAttachment(lblNewLabel, 13);
		fd_lblNewLabel_1.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText(I18nUtil.getMessage("TASK_NODE_DESC") + ":");
		
		descText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		FormData fd_descText = new FormData();
		fd_descText.bottom = new FormAttachment(nameText, 67, SWT.BOTTOM);
		fd_descText.top = new FormAttachment(nameText, 7);
		fd_descText.left = new FormAttachment(lblNewLabel_1, 9);
		fd_descText.right = new FormAttachment(100, -10);
		descText.setLayoutData(fd_descText);
		
		descText.setEditable(false);
		
		Group taskNameGroup = new Group(composite, SWT.NONE);
		taskNameGroup.setText(I18nUtil.getMessage("TASK_GROUP"));
		taskNameGroup.setLayout(new FormLayout());
		FormData fd_taskNameGroup = new FormData();
		fd_taskNameGroup.top = new FormAttachment(descText, 11);
		fd_taskNameGroup.left = new FormAttachment(0, 10);
		fd_taskNameGroup.right = new FormAttachment(100, -10);
		fd_taskNameGroup.height = 110;
		taskNameGroup.setLayoutData(fd_taskNameGroup);
		
		Group group_1 = new Group(composite, SWT.NONE);
		group_1.setText(I18nUtil.getMessage("DECISION_GROUP"));
		group_1.setLayout(new FormLayout());
		FormData fd_group_1 = new FormData();
		fd_group_1.top = new FormAttachment(taskNameGroup, 10);
		fd_group_1.height = 125;
		fd_group_1.left = new FormAttachment(0, 10);
		fd_group_1.right = new FormAttachment(100, -10);
		
		Label label = new Label(taskNameGroup, SWT.NONE);
		label.setText(I18nUtil.getMessage("TASK_NAME") + ":");
		label.setAlignment(SWT.RIGHT);
		FormData fd_label = new FormData();
		fd_label.right = new FormAttachment(0, 70);
		fd_label.top = new FormAttachment(0, 10);
		fd_label.left = new FormAttachment(0, 5);
		label.setLayoutData(fd_label);
		
		taskHandlerText = new Text(taskNameGroup, SWT.BORDER | SWT.READ_ONLY);
		FormData fd_taskHandlerText = new FormData();
		fd_taskHandlerText.right = new FormAttachment(100, -10);
		taskHandlerText.setLayoutData(fd_taskHandlerText);
		
		Label label_1 = new Label(taskNameGroup, SWT.NONE);
		label_1.setText(I18nUtil.getMessage("TASK_DESC") + ":");
		label_1.setAlignment(SWT.RIGHT);
		FormData fd_label_1 = new FormData();
		fd_label_1.top = new FormAttachment(label, 12);
		fd_label_1.right = new FormAttachment(label, 0, SWT.RIGHT);
		label_1.setLayoutData(fd_label_1);
		
		taskNameText = new Text(taskNameGroup, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		fd_taskHandlerText.left = new FormAttachment(taskNameText, 0, SWT.LEFT);
		FormData fd_taskNameText = new FormData();
		fd_taskNameText.top = new FormAttachment(0, 7);
		fd_taskNameText.right = new FormAttachment(50);
		fd_taskNameText.left = new FormAttachment(label, 5);
		taskNameText.setLayoutData(fd_taskNameText);
		taskNameText.setFocus();
		
		Label label_2 = new Label(taskNameGroup, SWT.NONE);
		fd_taskHandlerText.top = new FormAttachment(label_2, -3, SWT.TOP);
		label_2.setText(I18nUtil.getMessage("HANDLER_CLASS") + ":");
		label_2.setAlignment(SWT.RIGHT);
		FormData fd_label_2 = new FormData();
		fd_label_2.top = new FormAttachment(label_1, 15);
		fd_label_2.right = new FormAttachment(label, 0, SWT.RIGHT);
		label_2.setLayoutData(fd_label_2);
		
		taskDescText = new Text(taskNameGroup, SWT.BORDER);
		FormData fd_taskDescText = new FormData();
		fd_taskDescText.left = new FormAttachment(label_1, 5);
		fd_taskDescText.top = new FormAttachment(label, 10);
		fd_taskDescText.right = new FormAttachment(100, -10);
		taskDescText.setLayoutData(fd_taskDescText);
		group_1.setLayoutData(fd_group_1);
		
		Label lblNewLabel_3 = new Label(group_1, SWT.NONE);
		FormData fd_lblNewLabel_3 = new FormData();
		fd_lblNewLabel_3.right = new FormAttachment(0, 70);
		fd_lblNewLabel_3.top = new FormAttachment(0, 7);
		fd_lblNewLabel_3.left = new FormAttachment(0, 5);
		lblNewLabel_3.setLayoutData(fd_lblNewLabel_3);
		lblNewLabel_3.setAlignment(SWT.RIGHT);
		lblNewLabel_3.setText(I18nUtil.getMessage("DECISION_NAME") + ":");
		
		decNameText = new Text(group_1, SWT.BORDER | SWT.READ_ONLY);
		FormData fd_decNameText = new FormData();
		fd_decNameText.right = new FormAttachment(50);
		fd_decNameText.top = new FormAttachment(0, 4);
		fd_decNameText.left = new FormAttachment(lblNewLabel_3, 5);
		decNameText.setLayoutData(fd_decNameText);
		
		Label lblNewLabel_4 = new Label(group_1, SWT.NONE);
		lblNewLabel_4.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_4 = new FormData();
		fd_lblNewLabel_4.left = new FormAttachment(0, 5);
		fd_lblNewLabel_4.top = new FormAttachment(lblNewLabel_3, 14);
		fd_lblNewLabel_4.right = new FormAttachment(lblNewLabel_3, 0, SWT.RIGHT);
		lblNewLabel_4.setLayoutData(fd_lblNewLabel_4);
		lblNewLabel_4.setText(I18nUtil.getMessage("DECISION_DESC") + ":");
		
		decDescText = new Text(group_1, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI);
		FormData fd_decDescText = new FormData();
		fd_decDescText.left = new FormAttachment(lblNewLabel_4, 5);
		fd_decDescText.bottom = new FormAttachment(decNameText, 66, SWT.BOTTOM);
		fd_decDescText.right = new FormAttachment(100, -10);
		fd_decDescText.top = new FormAttachment(decNameText, 8);
		decDescText.setLayoutData(fd_decDescText);
		
		Label lblNewLabel_5 = new Label(group_1, SWT.NONE);
		lblNewLabel_5.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_5 = new FormData();
		fd_lblNewLabel_5.left = new FormAttachment(0, 5);
		fd_lblNewLabel_5.top = new FormAttachment(lblNewLabel_4, 47);
		fd_lblNewLabel_5.right = new FormAttachment(lblNewLabel_3, 0, SWT.RIGHT);
		lblNewLabel_5.setLayoutData(fd_lblNewLabel_5);
		lblNewLabel_5.setText(I18nUtil.getMessage("HANDLER_CLASS") + ":");
		
		decHandlerText = new Text(group_1, SWT.BORDER | SWT.READ_ONLY);
		FormData fd_decHandlerText = new FormData();
		fd_decHandlerText.right = new FormAttachment(100, -10);
		fd_decHandlerText.top = new FormAttachment(decDescText, 6);
		fd_decHandlerText.left = new FormAttachment(lblNewLabel_5, 5);
		decHandlerText.setLayoutData(fd_decHandlerText);
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_2 = new FormData();
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText("分配方式:");
		
		typeCombo = new Combo(composite, SWT.READ_ONLY);
		fd_lblNewLabel_2.top = new FormAttachment(typeCombo, 3, SWT.TOP);
		fd_lblNewLabel_2.right = new FormAttachment(typeCombo, -8);
		FormData fd_typeCombo = new FormData();
		fd_typeCombo.width = 200;
		fd_typeCombo.top = new FormAttachment(group_1, 6);
		fd_typeCombo.left = new FormAttachment(nameText, 0, SWT.LEFT);
		typeCombo.setLayoutData(fd_typeCombo);
		typeComboViewer = new DictComboViewer(typeCombo);
	}

	public void setTaskModel(TaskModel taskModel) {
		this.taskModel = taskModel;
	}
	
	/**
	 * 创建事件
	 */
	public void createEvent() {
		
		// 任务节点的名称和描述随着任务的名字而改变
		taskNameText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				String text = taskNameText.getText().trim();
				if(!"".equals(text)) {
					nameText.setText(text + "Task");
					descText.setText(text + "Description");
					decNameText.setText(text + "Decision");
				} else {
					nameText.setText("");
					descText.setText("");
					decNameText.setText("");
				}
			}
		});
		
		taskDescText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				String text = taskDescText.getText().trim();
				if(!"".equals(text)) {
					decDescText.setText(text + "决策");
				} else {
					decDescText.setText("");
				}
			}
		});
	}
	
	/**
	 * 校验数据
	 */
	public boolean checkData() {
		String str = nameText.getText().trim();
		if("".equals(str)) {
			setMessage("任务节点名字不能为空");
			return false;
		}
		
		str = descText.getText().trim();
		if("".equals(str)) {
			setMessage("任务节点描述不能为空");
			return false;
		}
		
		setMessage("");
		return true;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}
	
	@Override
	protected void okPressed() {
//		if(!checkData()) {
//			return ;
//		}
		
//		TaskNode taskNode = taskModel.getTaskNode();
//		taskNode.setName(nameText.getText().trim());
//		taskNode.setDescription(descText.getText().trim());
//		
//		
//		Task task = taskNode.getTask();
//		task.setName(taskNameText.getText().trim());
//		task.setDescription(taskDescText.getText().trim());
//		task.setAssignmentClass(taskHandlerText.getText().trim());
//		
//		taskModel.setLabelName(taskDescText.getText().trim());
//		
//		DecisionNode decisionNode = taskNode.getDecisionNode();
//		decisionNode.setName(decNameText.getText().trim());
//		decisionNode.setDescription(decDescText.getText().trim());
//		decisionNode.setHandlerClass(decHandlerText.getText().trim());
		
		TaskModelChangeCommand command = new TaskModelChangeCommand();
		command.setTaskModel(taskModel);
		
		Task task = new Task();
		task.setName(taskNameText.getText().trim());
		task.setDescription(taskDescText.getText().trim());
		task.setAssignmentClass(taskHandlerText.getText().trim());
		command.setNewTask(task);
		
		command.setNewDeciHandler(decHandlerText.getText().trim());
		
		command.setNewType((String) typeComboViewer.getSelectKey());
		
		commandStack.execute(command);
		
		super.okPressed();
	}
	
	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
	}
}

/**
 * 任务节点ComboViewe的标签提供者
 * @author  易振强
 * @version 1.0, 2012-3-29
 * @see 
 * @since 1.0
 */
class TaskLabelProvider extends LabelProvider{
	@Override
	public String getText(Object element) {
		if(element instanceof Task) {
			Task task = (Task) element;
			
			String desc = task.getDescription();
			
			if(desc == null || "".endsWith(desc)) {
				return task.getName();
				
			} else {
				return task.getName() + "-" + desc;
			}
		}
		
		return super.getText(element);
	}
	
}

/**
 * 转换列表ComboViewe的标签提供者
 * @author  易振强
 * @version 1.0, 2012-3-29
 * @see 
 * @since 1.0
 */
class TranLabelProvider extends LabelProvider{
	@Override
	public String getText(Object element) {
		if(element instanceof ConnectLine) {
			ConnectLine line = (ConnectLine) element;
			String desc = line.getDescription();
			
			if(line == null || "".endsWith(desc)) {
				return line.getName();
				
			} else {
				return line.getName() + "-" + desc;
			}
		}
		
		return super.getText(element);
	}
	
}

/* 文件名：     ConnAndNodeDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-29
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.ui.dialog;

import java.awt.event.PaintEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.workflow.gef.command.dialog.LineModelChangeCommand;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractLineModel;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;
import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.gef.model.LineModel;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;
import cn.sunline.suncard.sde.workflow.model.ActionTreeNode;
import cn.sunline.suncard.sde.workflow.model.CommonNode;
import cn.sunline.suncard.sde.workflow.model.ConnectLine;
import cn.sunline.suncard.sde.workflow.model.DecisionNode;
import cn.sunline.suncard.sde.workflow.model.LineNode;
import cn.sunline.suncard.sde.workflow.tree.WorkFlowTreeViewPart;
import cn.sunline.suncard.sde.workflow.ui.dialog.factory.TitleAreaDialogMould;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Table;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;

/**
 * 填写连接线到节点的对话框
 * 
 * @author 易振强
 * @version 1.0, 2012-3-29
 * @see
 * @since 1.0
 */
public class LineModelDialog extends TitleAreaDialogMould {
	private Composite composite;

	private Combo tranNameCombo;
	private Text tranDescText;
	private Text nodeNameText;
	private Text nodeDescText;

	private DecisionNode decisionNode;
	private TaskModel taskModel;
	private LineModel lineModel;
	private LineNode lineNode;
	private ComboViewer tranComboViewer;

	Log logger = LogManager.getLogger(LineModelDialog.class);
	private Text actionText;
//	private Text targetNodeText;
	
	private CommandStack commandStack;
	

	public LineModelDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.MIN);
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText(I18nUtil.getMessage("TRAN_LIST"));
		super.configureShell(newShell);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(691, 340);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(I18nUtil.getMessage("TRAN_LIST"));

		Control control = super.createDialogArea(parent);
		composite = super.getMouldComposite();

		createControl();

		initData();
		
		createEvent();

		return control;
	}

	/**
	 * 初始化各控件的值
	 */
	private void initData() {
		if (taskModel == null || lineModel == null) {
			logger.error("TaskModel或LineModel为空，无法初始化数据！");
			return;
		}

		AbstractModel model = lineModel.getTarget();
		
		// 获得目标节点的名称
		String targetName = null;
		if(model instanceof TaskModel) {
			targetName = ((TaskModel)model).getTaskNode().getName();
		} else if(model instanceof EndModel) {
			targetName = ((EndModel)model).getEndNode().getName();
		}
		
		tranComboViewer.setInput(WorkFlowTreeViewPart.allActionList);
		
		lineNode = lineModel.getLineNode();
		
		String tranName = lineNode.getCommonNode().getName();
		for(ActionTreeNode actionTreeNode : WorkFlowTreeViewPart.allActionList) {
			if(actionTreeNode.getName().equals(tranName)) {
				tranComboViewer.setSelection(new StructuredSelection(actionTreeNode));
				break ;
			}
		}
//		tranNameCombo.setText(lineNode.getCommonNode().getName() + "");
		
		
		tranDescText.setText(lineNode.getCommonNode().getDescription() + "");
		
		nodeNameText.setText(lineNode.getCommonNode().getName() + "");
		nodeDescText.setText(lineNode.getCommonNode().getDescription() + "");
		
//		targetNodeText.setText(targetName + "");
//		targetNodeText.setText(lineNode.getCommonNode().getConnectLine().getTargetNodeName() + "");
		
		actionText.setText(lineNode.getCommonNode().getActionClass() + "");
		
	}
	
	/**
	 * 创建对话框控件
	 */
	private void createControl() {

		Group group = new Group(composite, SWT.NONE);
		group.setText(I18nUtil.getMessage("TRAN_ATTRI"));
		group.setLayout(new FormLayout());
		FormData fd_group = new FormData();
		fd_group.top = new FormAttachment(0, 5);
		fd_group.bottom = new FormAttachment(100, -5);
		fd_group.left = new FormAttachment(0, 10);
		fd_group.right = new FormAttachment(100, -10);
		group.setLayoutData(fd_group);

		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.left = new FormAttachment(0, 10);
		fd_lblNewLabel_1.right = new FormAttachment(0, 75);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText(I18nUtil.getMessage("TRAN_NAME") + ":");

		tranNameCombo = new Combo(group, SWT.READ_ONLY);
		tranComboViewer = new ComboViewer(tranNameCombo);
		fd_lblNewLabel_1.top = new FormAttachment(tranNameCombo, 3, SWT.TOP);
		FormData fd_tranNameText = new FormData();
		fd_tranNameText.right = new FormAttachment(60);
		fd_tranNameText.left = new FormAttachment(lblNewLabel_1, 10);
		tranNameCombo.setLayoutData(fd_tranNameText);
		
		tranComboViewer.setContentProvider(new ArrayContentProvider());
		tranComboViewer.setLabelProvider(new TranComboLabelProvider());

		Label lblNewLabel_2 = new Label(group, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.top = new FormAttachment(lblNewLabel_1, 13);
		fd_lblNewLabel_2.left = new FormAttachment(0, 10);
		fd_lblNewLabel_2.right = new FormAttachment(lblNewLabel_1, 0, SWT.RIGHT);
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText(I18nUtil.getMessage("TRAN_DESC") + ":");

		tranDescText = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		FormData fd_tranDescText = new FormData();
		fd_tranDescText.right = new FormAttachment(100, -10);
		fd_tranDescText.top = new FormAttachment(tranNameCombo, 6);
		fd_tranDescText.left = new FormAttachment(tranNameCombo, 0, SWT.LEFT);
		tranDescText.setLayoutData(fd_tranDescText);

		Label lblNewLabel_3 = new Label(group, SWT.NONE);
		FormData fd_lblNewLabel_3 = new FormData();
		fd_lblNewLabel_3.right = new FormAttachment(lblNewLabel_1, 0, SWT.RIGHT);
		lblNewLabel_3.setLayoutData(fd_lblNewLabel_3);
		lblNewLabel_3.setText(I18nUtil.getMessage("AUTO_NODE_NAME") + ":");

		nodeNameText = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		FormData fd_nodeNameText = new FormData();
		fd_nodeNameText.right = new FormAttachment(60);
		fd_nodeNameText.top = new FormAttachment(lblNewLabel_3, -3, SWT.TOP);
		fd_nodeNameText.left = new FormAttachment(tranNameCombo, 0, SWT.LEFT);
		nodeNameText.setLayoutData(fd_nodeNameText);

		Label lblNewLabel_4 = new Label(group, SWT.NONE);
		fd_lblNewLabel_3.bottom = new FormAttachment(lblNewLabel_4, -13);
		lblNewLabel_4.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_4 = new FormData();
		fd_lblNewLabel_4.top = new FormAttachment(0, 93);
		fd_lblNewLabel_4.right = new FormAttachment(lblNewLabel_1, 0, SWT.RIGHT);
		lblNewLabel_4.setLayoutData(fd_lblNewLabel_4);
		lblNewLabel_4.setText(I18nUtil.getMessage("AUTO_NODE_DESC") + ":");

		nodeDescText = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		FormData fd_nodeDescText = new FormData();
		fd_nodeDescText.bottom = new FormAttachment(lblNewLabel_4, 3,
				SWT.BOTTOM);
		fd_nodeDescText.right = new FormAttachment(100, -10);
		fd_nodeDescText.left = new FormAttachment(tranNameCombo, 0, SWT.LEFT);
		nodeDescText.setLayoutData(fd_nodeDescText);

		Label sdsd = new Label(group, SWT.NONE);
		sdsd.setAlignment(SWT.RIGHT);
		FormData fd_sdsd = new FormData();
		fd_sdsd.top = new FormAttachment(lblNewLabel_4, 15);
		fd_sdsd.right = new FormAttachment(lblNewLabel_1, 0, SWT.RIGHT);
		sdsd.setLayoutData(fd_sdsd);
		sdsd.setText(I18nUtil.getMessage("ACTION_CLASS") + ":");
		
		actionText = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		FormData fd_actionText = new FormData();
		fd_actionText.right = new FormAttachment(100, -10);
		fd_actionText.top = new FormAttachment(nodeDescText, 6);
		fd_actionText.left = new FormAttachment(tranNameCombo, 0, SWT.LEFT);
		actionText.setLayoutData(fd_actionText);
		
//		Label label = new Label(group, SWT.NONE);
//		FormData fd_label = new FormData();
//		fd_label.top = new FormAttachment(sdsd, 13);
//		fd_label.right = new FormAttachment(lblNewLabel_1, 0, SWT.RIGHT);
//		label.setLayoutData(fd_label);
//		label.setText("目标节点" + ":");
//		
//		targetNodeText = new Text(group, SWT.BORDER | SWT.READ_ONLY);
//		FormData fd_tragetNodeText = new FormData();
//		fd_tragetNodeText.top = new FormAttachment(actionText, 6);
//		fd_tragetNodeText.left = new FormAttachment(tranNameCombo, 0, SWT.LEFT);
//		fd_tragetNodeText.right = new FormAttachment(100, -10);
//		targetNodeText.setLayoutData(fd_tragetNodeText);
	}

	/**
	 * 创建各控件的消息事件
	 */
	private void createEvent() {
		tranComboViewer.getCombo().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection select = (IStructuredSelection) tranComboViewer.getSelection();
				ActionTreeNode node = (ActionTreeNode) select.getFirstElement();
				
				tranNameCombo.setText(node.getName() + "");
				tranDescText.setText(node.getDesc() + "");
				
				nodeNameText.setText(node.getName() + "");
				nodeDescText.setText(node.getDesc() + "");
				
				super.widgetSelected(e);
			}
		});
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
//		if(checkData()) {
	//		connectLine.setName(tranNameCombo.getText().trim());
	//		connectLine.setDescription(tranDescText.getText().trim());
	//		connectLine.setTargetNodeName(targetNodeText.getText().trim());
	//		commonNode.setName(nodeNameText.getText().trim());
	//		commonNode.setDescription(nodeDescText.getText().trim());
	//		commonNode.setActionClass(actionText.getText().trim());
	//		lineModel.setLabelText(tranDescText.getText().trim());
			
			LineModelChangeCommand command = new LineModelChangeCommand();
			command.setNode(lineNode.getCommonNode());
			command.setLineModel(lineModel);
			
			IStructuredSelection select = (IStructuredSelection) tranComboViewer.getSelection();
			if(!select.isEmpty()) {
				command.setNewName(((ActionTreeNode)select.getFirstElement()).getName());
			}
//			command.setNewName(tranNameCombo.getText().trim());
			
			
			command.setNewDesc(tranDescText.getText().trim());
			command.setNewHandler(actionText.getText().trim());
//			command.setNewTarget(targetNodeText.getText().trim());
			
			commandStack.execute(command);
			
//			if(!lineList.contains(connectLine)) {
//				lineList.add(connectLine);
//				nodeList.add(commonNode);
//			}
//		}
		
		
		super.okPressed();
	}
	
	/**
	 * 检查数据正确性
	 */
	private boolean checkData() {
		String str = tranNameCombo.getText().trim();
		if("".equals(str)) {
			setMessage("转换名称不能为空！");
			return false;
		}
		
		str = tranDescText.getText().trim();
		if("".equals(str)) {
			setMessage("转换描述不能为空！");
			return false;
		}
		
		str = nodeNameText.getText().trim();
		if("".equals(str)) {
			setMessage("自动节点名称不能为空！");
			return false;
		}
		
		str = nodeDescText.getText().trim();
		if("".equals(str)) {
			setMessage("自动节点描述不能为空！");
			return false;
		}
		
		str = actionText.getText().trim();
		if("".equals(str)) {
			setMessage("Action类不能为空！");
			return false;
		}
		
//		str = targetNodeText.getText().trim();
//		if("".equals(str)) {
//			setMessage("目标节点不能为空不能为空！");
//			return false;
//		}
		
		setMessage("");
		return true;
	}
	
	public void setDecisionNode(DecisionNode decisionNode) {
		this.decisionNode = decisionNode;
	}

	public void setTaskModel(TaskModel taskModel) {
		this.taskModel = taskModel;
	}

	public void setLineModel(LineModel lineModel) {
		this.lineModel = lineModel;
	}
	
	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
	}
}

/**
 * 转换列表的标签提供者
 * 
 * @author 易振强
 * @version 1.0, 2012-3-29
 * @see
 * @since 1.0
 */
class TranComboLabelProvider extends LabelProvider{
	@Override
	public String getText(Object element) {
		if(element instanceof ActionTreeNode) {
			ActionTreeNode node = (ActionTreeNode) element;
			
			return node.getName() + "-" + node.getDesc();
		}
		
		return super.getText(element);
	}
}

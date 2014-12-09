/* 文件名：     EndModelDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-29
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.ui.dialog;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.workflow.gef.command.dialog.EndModelChangeCommand;
import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.model.EndNode;
import cn.sunline.suncard.sde.workflow.ui.dialog.factory.TitleAreaDialogMould;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;

/**
 * 结束节点对话框
 * @author  易振强
 * @version 1.0, 2012-3-29
 * @see 
 * @since 1.0
 */
public class EndModelDialog extends TitleAreaDialogMould {
	private Composite composite;
	private Text nameText;
	private Text descText;
	
	private EndModel endModel;
	private CommandStack commandStack;
	
	Log logger = LogManager.getLogger(EndModelDialog.class);
	
	public EndModelDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText(I18nUtil.getMessage("END_MODEL_ATTR"));
		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(509, 289);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(I18nUtil.getMessage("END_MODEL_ATTR"));
		Control control = super.createDialogArea(parent);
		composite = super.getMouldComposite();
		
		createControl();
		
		initData();
		
		return control;
	}
	
	/**
	 * 初始化控件的值
	 */
	private void initData() {
		if(endModel == null) {
			return ;
		}
		
		EndNode node = endModel.getEndNode();
		nameText.setText(node.getName() == null ? "" : node.getName());
		descText.setText(node.getDescription() == null ? "" : node.getDescription());
		
	}

	/**
	 * 创建控件
	 */
	public void createControl() {
		Label lblNewLabel = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText(I18nUtil.getMessage("END_NODE_NAME") + ":");
		
		nameText = new Text(composite, SWT.BORDER);
		FormData fd_nameText = new FormData();
		fd_nameText.right = new FormAttachment(50);
		fd_nameText.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_nameText.left = new FormAttachment(lblNewLabel, 6);
		nameText.setLayoutData(fd_nameText);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(lblNewLabel, 19);
		fd_lblNewLabel_1.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText(I18nUtil.getMessage("END_NODE_DESC") + ":");
		
		descText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		FormData fd_descText = new FormData();
		fd_descText.bottom = new FormAttachment(nameText, 80, SWT.BOTTOM);
		fd_descText.top = new FormAttachment(nameText, 16);
		fd_descText.left = new FormAttachment(lblNewLabel_1, 7);
		fd_descText.right = new FormAttachment(100, -10);
		descText.setLayoutData(fd_descText);
	}

	@Override
	protected void okPressed() {
//		if(!checkData()) {
//			return ;
//		}
		
//		endModel.getEndNode().setName(nameText.getText().trim());
//		endModel.getEndNode().setDescription(descText.getText().trim());
		
		EndModelChangeCommand command = new EndModelChangeCommand();
		command.setEndModel(endModel);
		command.setNewName(nameText.getText().trim());
		command.setNewDesc(descText.getText().trim());
		commandStack.execute(command);
		
		super.okPressed();
	}
	
	public boolean checkData() {
		String str = nameText.getText().trim();
		if("".equals(str)) {
			setMessage("结束节点的名称不能为空！");
			return false;
		}
		
		str = descText.getText().trim();
		if("".equals(str)) {
			setMessage("结束节点的描述不能为空！");
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
	
	public void setEndModel(EndModel endModel) {
		this.endModel = endModel;
	}
	
	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
	}
	
}

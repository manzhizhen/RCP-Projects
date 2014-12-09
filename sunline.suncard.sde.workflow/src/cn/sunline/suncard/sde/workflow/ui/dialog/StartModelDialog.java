/* 文件名：     StartModelDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-29
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.ui.dialog;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.workflow.gef.command.dialog.StartModelChangeCommand;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;
import cn.sunline.suncard.sde.workflow.ui.dialog.factory.TitleAreaDialogMould;

/**
 * 双击开始节点打开的对话框
 * @author  易振强
 * @version 1.0, 2012-3-29
 * @see 
 * @since 1.0
 */
public class StartModelDialog extends TitleAreaDialogMould {
	private Composite composite;
	private Text nameText;
	private Text descText;
	
	private StartModel startModel;
	private CommandStack commandStack;
	
	Log logger = LogManager.getLogger(AddWorkFlowDialog.class);
	
	public StartModelDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(509, 300);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText(I18nUtil.getMessage("START_MODEL_ATTR"));
		super.configureShell(newShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(I18nUtil.getMessage("START_MODEL_ATTR"));
		
		Control control = super.createDialogArea(parent);
		composite = super.getMouldComposite();

		createControl();
		
		initData();
		
		return control;
	}
	
	/**
	 * 初始化控件数据
	 */
	private void initData() {
		if(startModel == null) {
			logger.error("绑定的开始节点为空，无法初始化数据！");
		}
		
		String nameStr = startModel.getStartNode().getName();
		nameText.setText(nameStr == null ? "" : nameStr);
		
		String descStr = startModel.getStartNode().getDescription();
		descText.setText(descStr == null ? "" : descStr);
	}

	/**
	 * 创建对话框控件
	 */
	private void createControl() {
		Label nameLabel = new Label(composite, SWT.NONE);
		nameLabel.setAlignment(SWT.RIGHT);
		FormData fd_nameLabel = new FormData();
		fd_nameLabel.top = new FormAttachment(0, 10);
		fd_nameLabel.left = new FormAttachment(0, 10);
		nameLabel.setLayoutData(fd_nameLabel);
		nameLabel.setText(I18nUtil.getMessage("START_MODEL_NAME") + ":");
		
		nameText = new Text(composite, SWT.BORDER);
		FormData fd_nameText = new FormData();
		fd_nameText.right = new FormAttachment(50);
		fd_nameText.top = new FormAttachment(nameLabel, -3, SWT.TOP);
		fd_nameText.left = new FormAttachment(nameLabel, 5);
		nameText.setLayoutData(fd_nameText);
		
		Label descLabel = new Label(composite, SWT.NONE);
		descLabel.setAlignment(SWT.RIGHT);
		FormData fd_descLabel = new FormData();
		fd_descLabel.top = new FormAttachment(nameLabel, 15);
		fd_descLabel.left = new FormAttachment(nameLabel, 0, SWT.LEFT);
		descLabel.setLayoutData(fd_descLabel);
		descLabel.setText(I18nUtil.getMessage("START_MODEL_DESC") + ":");
		
		descText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		FormData fd_descText = new FormData();
		fd_descText.top = new FormAttachment(descLabel, -18);
		fd_descText.bottom = new FormAttachment(descLabel, 77, SWT.BOTTOM);
		fd_descText.right = new FormAttachment(100, -10);
		fd_descText.left = new FormAttachment(descLabel, 5);
		descText.setLayoutData(fd_descText);
	}
	
	/**
	 * 校验填写数据的正确性
	 */
	public boolean checkData() {
		String nameStr = nameText.getText().trim();
		if("".equals(nameStr)) {
			setMessage("开始节点名字不能为空！");
			return false;
		}
		
		setMessage("");
		return true;
	}
	
	@Override
	protected void okPressed() {
//		if(!checkData()) {
//			return ;
//		}
		
		if(startModel == null) {
			logger.error("绑定的开始节点为空，更新数据失败！");
		}
		
		StartModelChangeCommand command = new StartModelChangeCommand();
		command.setStartModel(startModel);
		command.setNewName(nameText.getText().trim());
		command.setNewDesc(descText.getText().trim());
		
		commandStack.execute(command);
		
		super.okPressed();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}
	
	public void setStartModel(StartModel startModel) {
		this.startModel = startModel;
	}

	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
	}
}

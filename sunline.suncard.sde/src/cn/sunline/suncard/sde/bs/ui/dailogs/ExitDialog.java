/*
 * 文件名：
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：tpf
 * 修改时间：2012-3-13
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.dailogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

import cn.sunline.suncard.sde.bs.util.I18nUtil;

public class ExitDialog extends Dialog {

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ExitDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText(I18nUtil.getMessage("confirmExit"));
		
		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setText("");
		
		Button btnCheckButton = new Button(container, SWT.CHECK);
		btnCheckButton.setText(I18nUtil.getMessage("rememberUserDecision"));

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 150);
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText(I18nUtil.getMessage("titleInfo"));
		super.configureShell(newShell);
	}
	
	
}

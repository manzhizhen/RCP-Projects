/* 文件名：     TitleAreaDialogMould.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	TitleAreaDialog对话框模板
 * 修改人：     易振强
 * 修改时间：2012-3-28
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.ui.dialog.factory;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * TitleAreaDialog对话框模板
 * @author  易振强
 * @version 1.0, 2012-3-28
 * @see 
 * @since 1.0
 */
public class TitleAreaDialogMould extends TitleAreaDialog {
	private Composite composite;
	
	/**
	 * @param parentShell
	 */
	public TitleAreaDialogMould(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Control createDialogArea(org.eclipse.swt.widgets.Composite parent) {
		Control control =   super.createDialogArea(parent);
		
		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new FormLayout());
		
		return control;
	}
	
//	@Override
//	protected void createButtonsForButtonBar(Composite parent) {
//		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
//				true);
//		createButton(parent, IDialogConstants.CANCEL_ID,
//				I18nUtil.getMessage("CANCEL"), false);
//	}

	public Composite getMouldComposite() {
		return composite;
	}

}

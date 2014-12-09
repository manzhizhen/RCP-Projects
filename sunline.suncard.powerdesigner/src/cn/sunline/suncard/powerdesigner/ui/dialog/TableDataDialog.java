/* 文件名：     TableDataDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     wzx
 * 修改时间：2012-11-29
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.command.UpdateTableDataCommand;
import cn.sunline.suncard.powerdesigner.model.InitTableDataModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.ui.dialog.composite.TableDataComposite;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 表格初始化数据对话框
 * @author  wzx
 * @version 1.0, 2012-11-29
 * @see 
 * @since 1.0
 */
public class TableDataDialog extends TitleAreaDialog {
	private Log logger = LogManager.getLogger(TableDataDialog.class.getName());
	
	private TableDataComposite tableDataComposite;
	private TableModel tableModel;
	private InitTableDataModel initTableDataModel;
	private UpdateTableDataCommand command;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public TableDataDialog(Shell parentShell, InitTableDataModel initTableDataModel) {
		super(parentShell);
//		try {
			this.tableModel = initTableDataModel.getTableModel();
			this.initTableDataModel = initTableDataModel;
//		} catch (CloneNotSupportedException e) {
//			logger.error("赋值表格时出现错误！表格Id : " + tableModel.getTableName() + "错误信息：" + e.getMessage());
//			e.printStackTrace();
//		}
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(I18nUtil.getMessage("TABLE_DATA_SET"));
		setMessage(I18nUtil.getMessage("TABLE_DATA_SET"));
		
		Composite area = (Composite) super.createDialogArea(parent);
		tableDataComposite = new TableDataComposite(area, SWT.NONE);
		tableDataComposite.setTableModel(tableModel);
		tableDataComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"), true);
		createButton(parent, IDialogConstants.CANCEL_ID, I18nUtil.getMessage("CANCEL"), false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(1000, 600);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#setShellStyle(int)
	 */
	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE | SWT.PRIMARY_MODAL);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText(I18nUtil.getMessage("TABLE_DATA_DIALOG") + " - " + tableModel.getTableDesc());
		super.configureShell(newShell);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		command = new UpdateTableDataCommand(tableModel, tableDataComposite.getInitTableDataModel());
		super.okPressed();
	}
	
	/**
	 * @return the command
	 */
	public UpdateTableDataCommand getCommand() {
		return command;
	}
}

/* 文件名：     IndexEditDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-12-16
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.powerdesigner.model.IndexSqlModel;
import cn.sunline.suncard.powerdesigner.model.IndexSqlModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 管理一个表的索引
 * @author  Agree
 * @version 1.0, 2012-12-16
 * @see 
 * @since 1.0
 */
public class IndexEditDialog extends TitleAreaDialog {
	private Log logger = LogManager.getLogger(SQLManagerDialog.class.getName());
	
	private Text sqlText;
	
	private String oldSql;
	private String newSql;
	private List<String> sqlList;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public IndexEditDialog(Shell parentShell, String IndexSql, List<String> sqlList) {
		super(parentShell);
		this.oldSql = IndexSql;
		this.sqlList = sqlList;
		this.newSql = null;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("索引SQL编辑");
		setMessage("索引SQL编辑");
		
		Composite area = (Composite) super.createDialogArea(parent);
		
		Label lblNewLabel1 = new Label(area, SWT.NONE);
		lblNewLabel1.setText("");
		
		Label lblNewLabel = new Label(area, SWT.NONE);
		lblNewLabel.setText("索引SQL编辑" + ":");
		
		sqlText = new Text(area, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		sqlText.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		sqlText.setText(oldSql);
		
		// 创建界面控件侦听器
		createListeners();

		return area;
	}
	
	/**
	 * 创建界面控件侦听器
	 */
	private void createListeners() {
		
		// 文本框内容改变侦听器
		sqlText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if (sqlList.contains(sqlText.getText().trim())) {
					setErrorMessage("该索引SQL语句已存在！");
					logger.error("用户编写了重复的索引sql语句");
				} else {
					setErrorMessage(null);
				}
			}
		});
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
		return new Point(600, 400);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#setShellStyle(int)
	 */
	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText(I18nUtil.getMessage("SQL_EDIT_DIALOG"));
		super.configureShell(newShell);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		// 如果有错,直接返回
		if (getErrorMessage() != null) {
			return;
		}
		
		newSql = sqlText.getText().trim();
		
		super.okPressed();
	}
	
	/**
	 * @return the newSql
	 */
	public String getNewSql() {
		return newSql;
	}
}

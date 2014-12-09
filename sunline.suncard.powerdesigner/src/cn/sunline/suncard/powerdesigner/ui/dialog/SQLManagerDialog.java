/* 文件名：     SQLManagerDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     wzx
 * 修改时间：2012-11-23
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.command.SQLManagerCommand;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.SqlScriptModel;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.ui.dialog.composite.SQLManagerComposite;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * SQL模型管理对话框
 * @author  wzx
 * @version 1.0, 2012-11-23
 * @see 
 * @since 1.0
 */
public class SQLManagerDialog extends TitleAreaDialog {
	private Log logger = LogManager.getLogger(SQLManagerDialog.class.getName());
	
	private SQLManagerComposite sqlManagerComposite;
	private ProductModel productModel;
	private SQLManagerCommand command;
	private TreeContent treeContent;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SQLManagerDialog(Shell parentShell, ProductModel productModel, TreeContent treeContent) {
		super(parentShell);
		this.productModel = productModel;
		this.treeContent = treeContent;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(I18nUtil.getMessage("SQL_MANAGER_SET"));
		setMessage(I18nUtil.getMessage("SQL_MANAGER_SET"));
		
		Composite area = (Composite) super.createDialogArea(parent);
		sqlManagerComposite = new SQLManagerComposite(area, SWT.NONE);
		sqlManagerComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		sqlManagerComposite.setPhysicalDataModel(productModel);
		
		// 设置显示的sql语句
		LinkedHashSet<SqlScriptModel> sqlList = productModel.getSqlSet();
		List<SqlScriptModel> cloneSqlList = new ArrayList<SqlScriptModel>();
		for (SqlScriptModel sql : sqlList) {
			cloneSqlList.add(sql);
		}
		sqlManagerComposite.setColumnModelList(cloneSqlList);
		sqlManagerComposite.initColumnItemData();	// 初始化数据
		sqlManagerComposite.createEvent();	// 创建事件
		
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),	true);
		createButton(parent, IDialogConstants.CANCEL_ID, I18nUtil.getMessage("CANCEL"), false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(730, 600);
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
		newShell.setText(I18nUtil.getMessage("SQL_MANAGER"));
		super.configureShell(newShell);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		List<SqlScriptModel> sqlList = sqlManagerComposite.getColumnModelList();
		command = new SQLManagerCommand(productModel, sqlList, treeContent);
		super.okPressed();
	}
	
	/**
	 * @return the command
	 */
	public SQLManagerCommand getCommand() {
		return command;
	}
}

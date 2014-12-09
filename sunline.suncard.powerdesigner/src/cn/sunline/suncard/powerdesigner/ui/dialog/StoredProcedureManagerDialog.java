/* 文件名：     StoredProcedureManagerDialog.java
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

import cn.sunline.suncard.powerdesigner.command.StoredProcedureManagerCommand;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.StoredProcedureModel;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.ui.dialog.composite.StoredProcedureManagerComposite;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 
 * @author  wzx
 * @version 1.0, 2012-11-23
 * @see 
 * @since 1.0
 */
public class StoredProcedureManagerDialog extends TitleAreaDialog {
	private Log logger = LogManager.getLogger(StoredProcedureManagerDialog.class.getName());
	
	private StoredProcedureManagerComposite storedProcedureManagerComposite;
	private ProductModel productModel;
	private StoredProcedureManagerCommand command;
	private TreeContent treeContent;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public StoredProcedureManagerDialog(Shell parentShell, ProductModel productModel, TreeContent treeContent) {
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
		setTitle(I18nUtil.getMessage("STOREDPROCEDURE_MANAGER_SET"));
		setMessage(I18nUtil.getMessage("STOREDPROCEDURE_MANAGER_SET"));
		
		Composite area = (Composite) super.createDialogArea(parent);
		
		storedProcedureManagerComposite = new StoredProcedureManagerComposite(area, SWT.NONE);
		storedProcedureManagerComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		storedProcedureManagerComposite.setPhysicalDataModel(productModel);
		
		// 设置显示的sql语句
		LinkedHashSet<StoredProcedureModel> sqlList = productModel.getStoredProcedureSet();
		List<StoredProcedureModel> cloneSqlList = new ArrayList<StoredProcedureModel>();
		for (StoredProcedureModel sql : sqlList) {
			cloneSqlList.add(sql);
		}
		storedProcedureManagerComposite.setColumnModelList(cloneSqlList);
		
		storedProcedureManagerComposite.initColumnItemData();	// 初始化数据
		storedProcedureManagerComposite.createEvent();	// 创建事件

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
		return new Point(730, 600);
	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText(I18nUtil.getMessage("STOREDPROCEDURE_MANAGER"));
		super.configureShell(newShell);
	}
	
	@Override
	protected void okPressed() {
		List<StoredProcedureModel> sqlList = storedProcedureManagerComposite.getColumnModelList();
		command = new StoredProcedureManagerCommand(productModel, sqlList, treeContent);
		super.okPressed();
	}
	
	/**
	 * @return the command
	 */
	public StoredProcedureManagerCommand getCommand() {
		return command;
	}
}

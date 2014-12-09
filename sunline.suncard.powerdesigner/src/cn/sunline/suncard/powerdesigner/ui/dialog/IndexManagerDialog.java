/* 文件名：     IndexManagerDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-12-16
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.command.IndexManagerCommand;
import cn.sunline.suncard.powerdesigner.command.SQLManagerCommand;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.IndexSqlModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.ui.dialog.composite.IndexManagerComposite;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
/**
 * 用于管理一个表的索引
 * @author  Agree
 * @version 1.0, 2012-12-16
 * @see 
 * @since 1.0
 */
public class IndexManagerDialog extends TitleAreaDialog {
	private Log logger = LogManager.getLogger(SQLManagerDialog.class.getName());
	
	private IndexSqlModel newIndexSqlModel;
	
	private IndexManagerComposite sqlManagerComposite;
	private TableModel tableModel;
	private IndexManagerCommand command;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public IndexManagerDialog(Shell parentShell, TableModel tableModel) {
		super(parentShell);
		this.tableModel = tableModel;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("索引SQL设置");
		setMessage("索引SQL设置");
		
		Composite area = (Composite) super.createDialogArea(parent);
		sqlManagerComposite = new IndexManagerComposite(area, SWT.NONE);
		sqlManagerComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
//		sqlManagerComposite.setPhysicalDataModel(tableModel);
		
		// 设置显示的sql语句
		Set<IndexSqlModel> sqlList = tableModel.getIndexSqlModelSet();
		List<IndexSqlModel> cloneSqlList = new ArrayList<IndexSqlModel>();
		for (IndexSqlModel sql : sqlList) {
			cloneSqlList.add(sql);
		}
		sqlManagerComposite.setTableModel(tableModel);
		sqlManagerComposite.setColumnModelList(cloneSqlList);
		
		try {
			sqlManagerComposite.initColumnItemData();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			logger.error("索引克隆失败");
			e.printStackTrace();
		}	// 初始化数据
		
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
		return new Point(700, 500);
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
		List<IndexSqlModel> sqlList = sqlManagerComposite.getColumnModelList();
		command = new IndexManagerCommand(tableModel, sqlList);
		
//		IndexSqlModel indexSqlModel = new IndexSqlModel();//上面一条语句完成了这四句的功能
//		indexSqlModel.setTableModel(tableModel);
//		indexSqlModel.setIndexSqlList(sqlList);
//		tableModel.setIndexSqlModel(indexSqlModel);
		
		super.okPressed();
	}
	
	/**
	 * 
	 */
	public IndexSqlModel getNewIndexSqlModel() {
		// TODO Auto-generated method stub
		
		newIndexSqlModel = sqlManagerComposite.getNewSql();
		return newIndexSqlModel;
	}
	
	/**
	 * @return the command
	 */
	public IndexManagerCommand getCommand() {
		return command;
	}
}

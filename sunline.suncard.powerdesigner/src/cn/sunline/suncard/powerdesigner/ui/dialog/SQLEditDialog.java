/* 文件名：     SQLEditDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     wzx
 * 修改时间：2012-11-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.SqlScriptModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.provider.SqlColumnModelLabelProvider;
import cn.sunline.suncard.powerdesigner.provider.TableModelLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * SQL脚本编辑对话框
 * @author  wzx
 * @version 1.0, 2012-11-28
 * @see 
 * @since 1.0
 */
public class SQLEditDialog extends TitleAreaDialog {
	private Log logger = LogManager.getLogger(SQLManagerDialog.class.getName());
	
	private Text txtName;
	private Text sqlText;
	private ComboViewer cvTable;
	private Button btnTable;
	private ComboViewer cvColumn;
	private Button btnColumn;
	
	private SqlScriptModel oldSql;
	private SqlScriptModel newSql;
	private List<SqlScriptModel> sqlList;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SQLEditDialog(Shell parentShell, SqlScriptModel sql, List<SqlScriptModel> sqlList) {
		super(parentShell);
		this.oldSql = sql;
		this.sqlList = sqlList;
		this.newSql = new SqlScriptModel();
		this.newSql.setId(oldSql.getId());
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(I18nUtil.getMessage("SQL_EDIT"));
		setMessage(I18nUtil.getMessage("SQL_EDIT"));
		
		Composite area = (Composite) super.createDialogArea(parent);
		
		Composite composite = new Composite(area, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new FormLayout());
		
		Label lblName = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel1 = new FormData();
		fd_lblNewLabel1.top = new FormAttachment(0, 10);
		fd_lblNewLabel1.left = new FormAttachment(0, 10);
		lblName.setLayoutData(fd_lblNewLabel1);
		lblName.setText("名称:");
		
		txtName = new Text(composite, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.right = new FormAttachment(100, -6);
		fd_text.top = new FormAttachment(0, 7);
		fd_text.left = new FormAttachment(lblName, 6);
		txtName.setLayoutData(fd_text);
		
		// 分割线
		Label lblSql = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		lblSql.setText("SQL:");
		FormData fd_lblSql = new FormData();
		fd_lblSql.top = new FormAttachment(txtName, 6);
		fd_lblSql.left = new FormAttachment(lblName, 0, SWT.LEFT);
		fd_lblSql.right = new FormAttachment(100, -6);
		lblSql.setLayoutData(fd_lblSql);
		
		// 表格标签
		Label lblTable = new Label(composite, SWT.NONE);
		FormData fd_lblTable = new FormData();
		fd_lblTable.top = new FormAttachment(lblSql, 14);
		fd_lblTable.left = new FormAttachment(lblName, 0, SWT.LEFT);
		lblTable.setLayoutData(fd_lblTable);
		lblTable.setText("表格:");
		
		// 表格插入按钮
		btnTable = new Button(composite, SWT.NONE);
		btnTable.setText("插入");
		FormData fd_btnTable = new FormData();
		fd_btnTable.top = new FormAttachment(lblTable, -5, SWT.TOP);
		fd_btnTable.right = new FormAttachment(100, -6);
		fd_btnTable.width = 60;
		btnTable.setLayoutData(fd_btnTable);
		
		// 表格选择下拉框
		cvTable = new ComboViewer(composite, SWT.READ_ONLY);
		Combo comboTable = cvTable.getCombo();
		FormData fd_comboTable = new FormData();
		fd_comboTable.left = new FormAttachment(lblTable, 6);
		fd_comboTable.top = new FormAttachment(lblTable, -3, SWT.TOP);
		fd_comboTable.right = new FormAttachment(btnTable, -6);
		comboTable.setLayoutData(fd_comboTable);
		
		// 字段标签
		Label lblColumn = new Label(composite, SWT.NONE);
		FormData fd_lblColumn = new FormData();
		fd_lblColumn.top = new FormAttachment(comboTable, 13);
		fd_lblColumn.left = new FormAttachment(0, 10);
		lblColumn.setLayoutData(fd_lblColumn);
		lblColumn.setText("字段:");
		
		// 字段插入按钮
		btnColumn = new Button(composite, SWT.NONE);
		btnColumn.setText("插入");
		FormData fd_btnColumn = new FormData();
		fd_btnColumn.top = new FormAttachment(lblColumn, -5, SWT.TOP);
		fd_btnColumn.right = new FormAttachment(100, -6);
		fd_btnColumn.width = 60;
		btnColumn.setLayoutData(fd_btnColumn);
		
		// 字段选择下拉框
		cvColumn = new ComboViewer(composite, SWT.READ_ONLY);
		Combo comboColumn = cvColumn.getCombo();
		FormData fd_comboColumn = new FormData();
		fd_comboColumn.left = new FormAttachment(lblColumn, 6);
		fd_comboColumn.top = new FormAttachment(lblColumn, -3, SWT.TOP);
		fd_comboColumn.right = new FormAttachment(btnColumn, -6);
		comboColumn.setLayoutData(fd_comboColumn);
		
		// sql语句内容标签
		Label lblContent = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(comboColumn, 6);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblContent.setLayoutData(fd_lblNewLabel);
		lblContent.setText("SQL语句:");
		
		// sql语句编辑框
		sqlText = new Text(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		FormData fd_sqlText = new FormData();
		fd_sqlText.top = new FormAttachment(lblContent, 6);
		fd_sqlText.bottom = new FormAttachment(100, -10);
		fd_sqlText.left = new FormAttachment(0, 10);
		fd_sqlText.right = new FormAttachment(100, -6);
		sqlText.setLayoutData(fd_sqlText);
		
		// 初始化数据
		initData();
		
		// 创建界面控件侦听器
		createListeners();

		return composite;
	}
	
	/**
	 * 初始化界面数据
	 */
	private void initData() {
		// 初始化sql名称
		txtName.setText(oldSql.getName() == null ? "" : oldSql.getName());
		
		// 初始化sql内容编辑框
		sqlText.setText(oldSql.getSqlStr());
		
		// 初始化表格下拉框
		cvTable.setLabelProvider(new TableModelLabelProvider());
		cvTable.setContentProvider(new ArrayContentProvider());
		Set<TableModel> tableModels = oldSql.getProductModel().getAllTableModel();
		cvTable.setInput(tableModels);
		
		// 初始化字段下拉框
		cvColumn.setLabelProvider(new SqlColumnModelLabelProvider());
		cvColumn.setContentProvider(new ArrayContentProvider());
		List<ColumnModel> columnModels = new ArrayList<ColumnModel>();
		for (TableModel tableModel : tableModels) {
			columnModels.addAll(tableModel.getColumnList());
		}
		cvColumn.setInput(columnModels);
	}
	
	/**
	 * 创建界面控件侦听器
	 */
	private void createListeners() {
		// 名称文本框内容改变侦听器
		txtName.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if (txtName.getText().trim().isEmpty()) {
					setErrorMessage("SQL语句名称不能为空！");
					logger.error("用户编写了空的sql语句名称");
					return;
				} else {
					setErrorMessage(null);
				}
				
				for (SqlScriptModel model : sqlList) {
					if (model.getName().equalsIgnoreCase((txtName.getText().trim()))) {
						setErrorMessage("该SQL语句名称已存在！");
						logger.error("用户编写了重复的sql语句名称");
						return;
					} else {
						setErrorMessage(null);
					}
				}
			}
		});
		
		// 文本框内容改变侦听器
		sqlText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				for (SqlScriptModel model : sqlList) {
					if (model.getSqlStr().equalsIgnoreCase((sqlText.getText()))) {
						setErrorMessage("该SQL语句已存在！");
						logger.error("用户编写了重复的sql语句");
						return;
					} else {
						setErrorMessage(null);
					}
				}
			}
		});
		
		// 插入表格侦听器
		btnTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection select = (IStructuredSelection) cvTable.getSelection();
				TableModel tableModel = (TableModel) select.getFirstElement();
				
				if (tableModel == null) {
					return;
				}
				
				String strId = DmConstants.BLANK 
						+ DmConstants.PRE_TABLE 
						+ tableModel.getId() 
						+ DmConstants.BLANK;
				int index = sqlText.getCaretPosition();
				String beforeStr = sqlText.getText().substring(0, index);
				String afterStr = sqlText.getText().substring(index, sqlText.getText().length());
				sqlText.setText(beforeStr + strId + afterStr);
			}
		});
		
		// 插入字段侦听器
		btnColumn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection select = (IStructuredSelection) cvColumn.getSelection();
				ColumnModel columnModel = (ColumnModel) select.getFirstElement();
				
				if (columnModel == null) {
					return;
				}
				
				String strId = DmConstants.BLANK 
						+ DmConstants.PRE_COLUMN 
						+ columnModel.getId() 
						+ DmConstants.BLANK;
				int index = sqlText.getCaretPosition();
				String beforeStr = sqlText.getText().substring(0, index);
				String afterStr = sqlText.getText().substring(index, sqlText.getText().length());
				sqlText.setText(beforeStr + strId + afterStr);
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
		
		newSql.setName(txtName.getText().trim());
		newSql.setSqlStr(sqlText.getText().trim());
		
		super.okPressed();
	}
	
	/**
	 * @return the newSql
	 */
	public SqlScriptModel getNewSql() {
		return newSql;
	}
}

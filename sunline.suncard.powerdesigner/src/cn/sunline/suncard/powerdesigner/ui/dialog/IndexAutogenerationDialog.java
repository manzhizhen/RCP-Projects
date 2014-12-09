/* 文件名：     IndexAutogenerationDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-12-18
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.IndexSqlModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.provider.IndexAutogenerationLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 用来自动生成所选择的索引语句对话框
 * @author Agree
 * @version 1.0, 2012-12-18
 * @see
 * @since 1.0
 */
public class IndexAutogenerationDialog extends TitleAreaDialog {
	private Text textIndexName;
	private Text textDesc;
	private Table table;

	private IndexSqlModel oldIndexSqlModel;
	private List<IndexSqlModel> sqlList;
	private IndexSqlModel newIndexSqlModel;
	private TableViewer checkboxTableViewer;
	private TableModel tableModel;

	private Log logger = LogManager.getLogger(SQLManagerDialog.class.getName());
	private Button selectAllButton;
	private Table table_1;
	private Button buttonRight;
	private Button buttonLeft;
	private Button buttonTop;
	private Button buttonDown;
	private TableViewer checkboxTableViewerSelected;
	private List<ColumnModel> columnModelsRight = new ArrayList<ColumnModel>();
	private List<ColumnModel> columnModelsLeft = new ArrayList<ColumnModel>();

	/**
	 * @param parentShell
	 */
	public IndexAutogenerationDialog(Shell parentShell,
			IndexSqlModel oldIndexSqlModel, List<IndexSqlModel> sqlList,
			TableModel tableModel) {
		super(parentShell);

		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE
				| SWT.PRIMARY_MODAL);
		
		this.oldIndexSqlModel = oldIndexSqlModel;
		this.sqlList = sqlList;
		this.newIndexSqlModel = new IndexSqlModel();
		this.tableModel = tableModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		setTitle("索引自动生成");
		setMessage("索引自动生成");

		
		Control control = (Control) super.createDialogArea(parent);
		Composite composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		composite.setLayout(new FormLayout());

		Label label = new Label(composite, SWT.NONE);
		FormData fd_label = new FormData();
		fd_label.top = new FormAttachment(0, 10);
		fd_label.left = new FormAttachment(0, 10);
		label.setLayoutData(fd_label);
		label.setText("索引名称：");

		textIndexName = new Text(composite, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.top = new FormAttachment(label, -3, SWT.TOP);
		fd_text.left = new FormAttachment(label, 6);
		textIndexName.setLayoutData(fd_text);

		Label label_1 = new Label(composite, SWT.NONE);
		FormData fd_label_1 = new FormData();
		fd_label_1.top = new FormAttachment(label, 6);
		fd_label_1.left = new FormAttachment(label, 0, SWT.LEFT);
		label_1.setLayoutData(fd_label_1);
		label_1.setText("索引描述：");

		textDesc = new Text(composite, SWT.BORDER);
		fd_text.right = new FormAttachment(textDesc, 0, SWT.RIGHT);
		FormData fd_text_1 = new FormData();
		fd_text_1.top = new FormAttachment(textIndexName, 3);
		fd_text_1.right = new FormAttachment(100, -10);
		fd_text_1.left = new FormAttachment(label_1, 6);
		textDesc.setLayoutData(fd_text_1);

		Label label_2 = new Label(composite, SWT.NONE);
		FormData fd_label_2 = new FormData();
		fd_label_2.top = new FormAttachment(label_1, 12);
		fd_label_2.left = new FormAttachment(label, 0, SWT.LEFT);
		label_2.setLayoutData(fd_label_2);
		label_2.setText("索引字段：");

		checkboxTableViewer = new TableViewer(composite, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		table = checkboxTableViewer.getTable();
		FormData fd_table = new FormData();
		fd_table.top = new FormAttachment(textDesc, 6);
		fd_table.left = new FormAttachment(label_2, 6);
		fd_table.right = new FormAttachment(50, 0);
		fd_table.bottom = new FormAttachment(100, -10);
		table.setLayoutData(fd_table);
		table.setTouchEnabled(true);

//		selectAllButton = new Button(composite, SWT.CHECK);
//		FormData fd_button = new FormData();
//		fd_button.bottom = new FormAttachment(100, -10);
//		fd_button.left = new FormAttachment(label, 0, SWT.LEFT);
//		selectAllButton.setLayoutData(fd_button);
//		selectAllButton.setText("全选");
		// selectAllButton.setSelection(true);

		buttonRight = new Button(composite, SWT.NONE);
		FormData fd_button1 = new FormData();
		fd_button1.top = new FormAttachment(textDesc, 6);
		fd_button1.left = new FormAttachment(table, 6);
		buttonRight.setLayoutData(fd_button1);
		buttonRight.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_RIGHT));

		buttonLeft = new Button(composite, SWT.NONE);
		FormData fd_button_1 = new FormData();
		fd_button_1.bottom = new FormAttachment(table, 0, SWT.BOTTOM);
		fd_button_1.left = new FormAttachment(table, 6);
		buttonLeft.setLayoutData(fd_button_1);
		buttonLeft.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_LEFT));

		checkboxTableViewerSelected = new TableViewer(composite, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		table_1 = checkboxTableViewerSelected.getTable();
		FormData fd_table_1 = new FormData();
		fd_table_1.left = new FormAttachment(buttonLeft, 6);
		fd_table_1.bottom = new FormAttachment(table, 0, SWT.BOTTOM);
		fd_table_1.top = new FormAttachment(table, 0, SWT.TOP);
		table_1.setLayoutData(fd_table_1);
		table_1.setTouchEnabled(true);

		buttonTop = new Button(composite, SWT.NONE);
		fd_table_1.right = new FormAttachment(buttonTop, -6);
		FormData fd_button_2 = new FormData();
		fd_button_2.top = new FormAttachment(textDesc, 6);
		fd_button_2.right = new FormAttachment(textIndexName, 0, SWT.RIGHT);
		buttonTop.setLayoutData(fd_button_2);
		buttonTop.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_UP));

		buttonDown = new Button(composite, SWT.NONE);
		FormData fd_button_3 = new FormData();
		fd_button_3.top = new FormAttachment(buttonLeft, 0, SWT.TOP);
		fd_button_3.right = new FormAttachment(textIndexName, 0, SWT.RIGHT);
		buttonDown.setLayoutData(fd_button_3);
		buttonDown.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_DOWN));

		// 增加显示标签

		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				checkboxTableViewer, SWT.NONE);
		TableColumn nameColumn = tableViewerColumn.getColumn();
		nameColumn.setWidth(30);
		nameColumn.setText("");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				checkboxTableViewer, SWT.NONE);
		TableColumn descColumn = tableViewerColumn_1.getColumn();
		descColumn.setWidth(200);
		descColumn.setText("待选字段");

		TableViewerColumn tableViewerColumnSelected = new TableViewerColumn(
				checkboxTableViewerSelected, SWT.NONE);
		TableColumn nameColumnSelected = tableViewerColumnSelected.getColumn();
		nameColumnSelected.setWidth(30);
		nameColumnSelected.setText("");

		TableViewerColumn tableViewerColumnSelected_1 = new TableViewerColumn(
				checkboxTableViewerSelected, SWT.NONE);
		TableColumn nameColumnSelected_1 = tableViewerColumnSelected_1
				.getColumn();
		nameColumnSelected_1.setWidth(200);
		nameColumnSelected_1.setText("已选字段");

		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table_1.setLinesVisible(true);
		table_1.setHeaderVisible(true);

		textIndexName.setText(oldIndexSqlModel.getName());
		textDesc.setText(oldIndexSqlModel.getDesc());

		try {
			initControlRight();
			initControlLeft();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 顺序不能更改，因为左边内容依赖右边内容

		// 增加按钮事件
		createEvent();

		createListeners();

		// 全选按钮事件
		// createCheckBoxEvent();
		return composite;
	}

	/**
	 * 填充左边字段内容
	 * 
	 * @throws CloneNotSupportedException
	 */
	private void initControlLeft() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		List<ColumnModel> columnModels = tableModel.getColumnList();
		for (ColumnModel columnModel : columnModels) {
			columnModelsLeft.add(columnModel.clone());
		}

		checkboxTableViewer
				.setLabelProvider(new IndexAutogenerationLabelProvider(
						columnModelsLeft));
		checkboxTableViewer.setContentProvider(new ArrayContentProvider());

		// TableItem tableItem = new TableItem(table, SWT.None);
		// tableItem.setText("字段");
		// 删除右边已有字段
		List<ColumnModel> columnModels2 = new ArrayList<ColumnModel>();
		if (!columnModelsRight.isEmpty()) {
			for (ColumnModel columnModelRight : columnModelsRight) {
				for (ColumnModel columnModelLeft : columnModelsLeft) {
					if (columnModelLeft.getId() == columnModelRight.getId()) {
						columnModels2.add(columnModelLeft);
					}
				}
			}
		}
		columnModelsLeft.removeAll(columnModels2);

		checkboxTableViewer.setInput(columnModelsLeft);
		// checkboxTableViewer.setAllChecked(true);//表格全选
	}

	/**
	 * 填充右边字段内容
	 * 
	 * @throws CloneNotSupportedException
	 */
	private void initControlRight() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		List<ColumnModel> columnModels = oldIndexSqlModel.getColumnList();
		for (ColumnModel columnModel : columnModels) {
			columnModelsRight.add(columnModel.clone());
		}

		checkboxTableViewerSelected
				.setLabelProvider(new IndexAutogenerationLabelProvider(
						columnModelsRight));
		checkboxTableViewerSelected
				.setContentProvider(new ArrayContentProvider());

		checkboxTableViewerSelected.setInput(columnModelsRight);
	}

	/**
	 * 制造向右的方法
	 */
	private void createButtonRightEvent() {
		// TODO Auto-generated method stub
		if (columnModelsLeft.isEmpty() || getCheckboxTableViewerSelection() == null) {
			return;
		}
		columnModelsRight.addAll(getCheckboxTableViewerSelection());
		columnModelsLeft.removeAll(getCheckboxTableViewerSelection());

		checkboxTableViewerSelected.setInput(columnModelsRight);
		checkboxTableViewer.setInput(columnModelsLeft);
	}

	/**
	 * 制造向左的方法
	 */
	private void createButtonLeftEvent() {
		// TODO Auto-generated method stub
		if (columnModelsRight.isEmpty() || getCheckboxTableViewerSelectedSelection() == null) {
			return;
		}
		columnModelsLeft.addAll(getCheckboxTableViewerSelectedSelection());
		columnModelsRight.removeAll(getCheckboxTableViewerSelectedSelection());

		checkboxTableViewerSelected.setInput(columnModelsRight);
		checkboxTableViewer.setInput(columnModelsLeft);

	}

	/**
	 * 创建移动按钮事件
	 * 
	 * @param boolean 如果为ture，表示上移，否则表示下移
	 */
	private void moveButtonEvent(boolean isUpMove) {
		List<ColumnModel> list = getCheckboxTableViewerSelectedSelection();
		if (list == null) {
			return;
		}

		Map<Integer, ColumnModel> sortMap = new HashMap<Integer, ColumnModel>(); // 对上移顺序做个排序，先移动行号小的
		for (Object obj : list) {
			sortMap.put(new Integer(columnModelsRight.indexOf(obj)),
					(ColumnModel) obj);
		}

		if (isUpMove) {
			for (int i = 0; i < columnModelsRight.size(); i++) {
				Object obj = sortMap.get(new Integer(i));

				if (obj == null) {
					continue;
				} else if (i == 0) {
					// 最小的行号为0，无法上移
					break;
				}

				// 把该元素和前面的元素交换位置
				columnModelsRight.set(i, columnModelsRight.get(i - 1));
				columnModelsRight.set(i - 1, (ColumnModel) obj);
			}
		} else {
			for (int i = columnModelsRight.size() - 1; i > -1; i--) {
				Object obj = sortMap.get(new Integer(i));

				if (obj == null) {
					continue;
				} else if (i == columnModelsRight.size() - 1) {
					// 最大的行号为columnModelList.size() - 1，无法下移
					break;
				}

				// 把该元素和前面的元素交换位置
				columnModelsRight.set(i, columnModelsRight.get(i + 1));
				columnModelsRight.set(i + 1, (ColumnModel) obj);
			}
		}

		checkboxTableViewerSelected.setInput(columnModelsRight);
		checkboxTableViewerSelected.setSelection(new StructuredSelection(list));
	}

	// /**
	// * 创建全选按钮监听事件
	// */
	// public void createCheckBoxEvent() {
	// selectAllButton.addSelectionListener(new SelectionAdapter() {
	//
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// // TODO Auto-generated method stub
	// checkboxTableViewer.setAllChecked(selectAllButton
	// .getSelection());
	// }
	// });
	// }

	/**
	 * 创建界面控件侦听器
	 */
	private void createListeners() {

		// 文本框内容改变侦听器
		textIndexName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (sqlList.contains(textIndexName.getText().trim())) {
					setErrorMessage("该索引SQL语句已存在！");
					logger.error("用户编写了重复的索引sql语句");
				} else {
					setErrorMessage(null);
				}
			}
		});
	}

	/**
	 * Create contents of the button bar. 改变完成取消按钮样式
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}

	/**
	 * 根据输入内容生成新的索引SQL
	 * 
	 * @return the newSql
	 */
	public IndexSqlModel getNewIndexSqlModel() {

		return newIndexSqlModel;
	}

	/**
	 * true为有错误，false为无误
	 * @return
	 */
	private boolean initNewIndexSqlModel() {
		// String indexSql =
		// "CREATE INDEX @INDEX_NAME@ ON @TABLE_NAME@ (@COLUMN_LIST@);";
		//
		// StringBuffer columnIndex = new StringBuffer();
		//
		// for (ColumnModel selectedColumn : getTableSelection()) {
		// columnIndex.append(selectedColumn.getColumnName() + ", ");
		// }
		// columnIndex.deleteCharAt(columnIndex.lastIndexOf(","));
		//
		// indexSql = indexSql.replaceAll("@INDEX_NAME@",
		// textIndexName.getText());
		// indexSql = indexSql.replaceAll("@TABLE_NAME@",
		// tableModel.getTableName());
		// indexSql = indexSql.replaceAll("@COLUMN_LIST@",
		// columnIndex.toString());
		//
		// newSql = indexSql;
		if (textIndexName.getText().isEmpty()) {
			 setErrorMessage("请填写索引名称");
			 return true;
		} else if (textDesc.getText().isEmpty()) {
			 setErrorMessage("请填写索引描述");
			 return true;
		} else if (columnModelsRight.isEmpty()) {
			 setErrorMessage("请选择索引字段");
			 return true;
		} else if (checkChinese(textIndexName.getText())) {
			 setErrorMessage("索引名不能包含中文名");
			 return true;
		} else {
			newIndexSqlModel.setName(textIndexName.getText());
			newIndexSqlModel.setDesc(textDesc.getText());
			newIndexSqlModel.setColumnList(columnModelsRight);
			newIndexSqlModel.setTableModel(tableModel);
			return false;
		}
	}
	
	/**
	 * 检查是否是汉字
	 * true为有汉字，false为无汉字
	 */
	private boolean checkChinese(String IndexName) {
		// TODO Auto-generated method stub
		String regEx = "[\\u4e00-\\u9fa5]";

		Pattern p = Pattern.compile(regEx);

		Matcher m = p.matcher(IndexName);
		
		if(m.find()){
			return true;
		}
		
		return false;
	}

	/**
	 * 得到左边所选择的表格
	 */
	private List<ColumnModel> getCheckboxTableViewerSelection() {
		IStructuredSelection select = (IStructuredSelection) checkboxTableViewer
				.getSelection();

		if (select.isEmpty()) {
			return null;
		}

		List<ColumnModel> list = select.toList();

		return list;
	}

	/**
	 * 得到右边所选择的表格
	 */
	private List<ColumnModel> getCheckboxTableViewerSelectedSelection() {
		IStructuredSelection select = (IStructuredSelection) checkboxTableViewerSelected
				.getSelection();

		if (select.isEmpty()) {
			return null;
		}

		List<ColumnModel> list = select.toList();

		return list;
	}

	/*
	 * (non-Javadoc) 设置标题
	 * 
	 * @see
	 * org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets
	 * .Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("索引生成窗口");
		super.configureShell(newShell);
	}

	public void createEvent() {
		buttonRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createButtonRightEvent();

			}
		});

		buttonLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createButtonLeftEvent();
			}
		});

		buttonTop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveButtonEvent(true);
			}
		});

		buttonDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveButtonEvent(false);
			}
		});
	}

	/**
	 * 绘制窗口大小
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#getInitialSize()
	 */
	@Override
	protected Point getInitialSize() {
		// TODO Auto-generated method stub
		return new Point(600, 500);
	}

	/**
	 * 完成按钮事件
	 * 
	 * @param parent
	 */
	@Override
	protected void okPressed() {
		// 如果有错,直接返回
		if (getErrorMessage() != null) {
			return;
		}
		if(initNewIndexSqlModel()){//完成OK操作并且验证，true为错误，false为正确
			return;
		}

		super.okPressed();
	}
}

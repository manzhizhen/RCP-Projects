/* 文件名：     IndexManagerComposite.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-12-16
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog.composite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import cn.sunline.suncard.powerdesigner.model.IndexSqlModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.db.DataTypeModel;
import cn.sunline.suncard.powerdesigner.provider.IndexSqlManagerLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.transfer.ColumnModelTransfer;
import cn.sunline.suncard.powerdesigner.ui.dialog.IndexAutogenerationDialog;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;

/**
 * IndexManagerDialog的composite
 * @author Agree
 * @version 1.0, 2012-12-16
 * @see
 * @since 1.0
 */
public class IndexManagerComposite extends Composite {
	private Button delButton;
	// private Button cutButton;
	private Button downButton;
	// private Button pasButton;
	private Button upButton;
	private Button addButton;
	private TableViewer columnTableViewer;
	private Table columnTable;
	// private Button copButton;

	private List<IndexSqlModel> cloneColumnModelList = new ArrayList<IndexSqlModel>();
	private TableModel tableModel = new TableModel();

	private DataTypeModel undefinedDataTypeModel; // 未定义数据类型（即用户没有选择数据类型时显示的数据类型）;
	private List<DataTypeModel> dataTypeList = new ArrayList<DataTypeModel>(); // 记录该数据库所支持的数据类型List

	private ProductModel productModel = null; // 该物理数据模型是为了新建默认列对象时，默认列对象能向上遍历用的

	private Log logger = LogManager.getLogger(SQLManagerComposite.class
			.getName());
	private IndexSqlModel newSql;

	/**
	 * @param parent
	 * @param style
	 */
	public IndexManagerComposite(Composite parent, int style) {
		super(parent, style);
		createColumnItem();
	}

	/**
	 * 初始化列标签数据
	 * @throws CloneNotSupportedException 
	 */
	public void initColumnItemData() throws CloneNotSupportedException {

		// // 让该表格可以编辑
		// String[] columnProperties = new String[] { "num", "name" };
		// columnTableViewer.setColumnProperties(columnProperties);
		//
		// CellEditor[] cellEditors = new CellEditor[1];
		// cellEditors[0] = new TextCellEditor(columnTable);
		// columnTableViewer.setCellEditors(cellEditors);

		columnTableViewer.setContentProvider(new ArrayContentProvider());
		
		LinkedHashSet<IndexSqlModel> cloneIndexSet = new LinkedHashSet<IndexSqlModel>();
		
		LinkedHashSet<IndexSqlModel> indexSqlModels = tableModel.getIndexSqlModelSet();
		for(IndexSqlModel indexSqlModel : indexSqlModels){
			cloneIndexSet.add(indexSqlModel.clone());
		}
		cloneColumnModelList.clear();
		cloneColumnModelList.addAll(cloneIndexSet);
		
		columnTableViewer.setLabelProvider(new IndexSqlManagerLabelProvider(
				cloneColumnModelList));
		
		columnTableViewer.setInput(cloneColumnModelList);
	}

	public void createEvent() {
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addButtonEvent();
			}
		});

		delButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				delButtonEvent();
			}
		});

		upButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveButtonEvent(true);
			}
		});

		downButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveButtonEvent(false);
			}
		});

		columnTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				openColumnDialog();
			}
		});

		columnTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 127) {
					delButtonEvent();

				} else if ((e.stateMask & SWT.CTRL) != 0
						&& e.keyCode == (int) 'n') {
					addButtonEvent();
				}

			}
		});

	}

	/**
	 * 创建列控件
	 */
	private void createColumnItem() {
		setLayout(new FormLayout());

		delButton = new Button(this, SWT.NONE);
		FormData fd_delButton = new FormData();
		fd_delButton.width = 30;
		fd_delButton.top = new FormAttachment(0, 0);
		fd_delButton.right = new FormAttachment(100, -10);
		delButton.setLayoutData(fd_delButton);
		delButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_DEL));
		delButton.setToolTipText("删除");

		Label label = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_label = new FormData();
		fd_label.right = new FormAttachment(delButton, -5, SWT.LEFT);
		fd_label.top = new FormAttachment(delButton, 0, SWT.TOP);
		fd_label.bottom = new FormAttachment(delButton, 0, SWT.BOTTOM);
		fd_label.width = 1;
		label.setLayoutData(fd_label);

		downButton = new Button(this, SWT.NONE);
		FormData fd_downButton = new FormData();
		fd_downButton.width = 30;
		fd_downButton.top = new FormAttachment(delButton, 0, SWT.TOP);
		fd_downButton.right = new FormAttachment(label, -6);
		downButton.setLayoutData(fd_downButton);
		// downButton.setText("↓");
		downButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_DOWN));
		downButton.setToolTipText("下移一行");

		upButton = new Button(this, SWT.NONE);
		FormData fd_upButton = new FormData();
		fd_upButton.width = 30;
		fd_upButton.top = new FormAttachment(delButton, 0, SWT.TOP);
		fd_upButton.right = new FormAttachment(downButton, -6);
		upButton.setLayoutData(fd_upButton);
		// upButton.setText("↑");
		upButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_UP));
		upButton.setToolTipText("上移一行");

		addButton = new Button(this, SWT.NONE);
		FormData fd_addButton = new FormData();
		fd_addButton.width = 30;
		fd_addButton.top = new FormAttachment(delButton, 0, SWT.TOP);
		fd_addButton.right = new FormAttachment(upButton, -6);
		addButton.setLayoutData(fd_addButton);
		// addButton.setText("Add");
		addButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.A_NEW_IMAGE));
		addButton.setToolTipText("插入一行");

		columnTableViewer = new TableViewer(this, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		columnTable = columnTableViewer.getTable();
		columnTable.setTouchEnabled(true);
		FormData fd_columnTable = new FormData();
		fd_columnTable.top = new FormAttachment(delButton, 6);
		fd_columnTable.right = new FormAttachment(delButton, 0, SWT.RIGHT);
		fd_columnTable.left = new FormAttachment(0, 10);
		fd_columnTable.bottom = new FormAttachment(100, -10);
		columnTable.setLayoutData(fd_columnTable);

		columnTable.setLinesVisible(true);
		columnTable.setHeaderVisible(true);

		TableViewerColumn tableViewerColumn_0 = new TableViewerColumn(
				columnTableViewer, SWT.NONE);
		TableColumn numColumn = tableViewerColumn_0.getColumn();
		numColumn.setWidth(50);
		numColumn.setText("");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				columnTableViewer, SWT.NONE);
		TableColumn nameColumn = tableViewerColumn_1.getColumn();
		nameColumn.setWidth(200);
		nameColumn.setText("索引名字");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
				columnTableViewer, SWT.NONE);
		TableColumn descColumn = tableViewerColumn_2.getColumn();
		descColumn.setWidth(200);
		descColumn.setText("索引描述");

		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(
				columnTableViewer, SWT.NONE);
		TableColumn fieldColumn = tableViewerColumn_3.getColumn();
		fieldColumn.setWidth(200);
		fieldColumn.setText("索引字段");
	}

	/**
	 * 添加新列的按钮事件 在设置一个物理数据模型的默认列对象的列表时，为了能向上遍历，必须重写这个方法， 并传入该物理数据模型
	 */
	protected void addButtonEvent() {
		// ColumnModel columnModel = new ColumnModel();
		// String newName = getNewColumnName();
		// columnModel.setColumnName(newName);
		// columnModel.setColumnDesc(newName);
		// columnModel.setDataTypeModel(undefinedDataTypeModel);
		// columnModel.setTableModel(tableModel);

		String indexSqlName = "INDEX_" + tableModel.getTableName().toUpperCase() + "_" + (cloneColumnModelList.size() + 1);

		IndexSqlModel indexSqlModel = new IndexSqlModel();
		indexSqlModel.setName(indexSqlName);
		
//		int i = 2;
//		while (columnModelList.contains(indexSqlModel)) {
//			indexSqlName = "INDEX_" + tableModel.getTableName().toUpperCase() + "_" + (columnModelList.size() + i);
//			i++;
//		}
//		indexSqlModel.setName(indexSqlName);
		
		indexSqlModel.setDesc("请填写描述");
		indexSqlModel.setColumnList(tableModel.getColumnList());
		
		cloneColumnModelList.add(indexSqlModel);
		columnTableViewer.setInput(cloneColumnModelList);
		columnTableViewer.setSelection(new StructuredSelection(indexSqlModel));
	}

	/**
	 * 删除一列的按钮事件
	 */
	private void delButtonEvent() {
		List<IndexSqlModel> list = getTableSelection();

		if (list == null) {
			return;
		}

		// List<String> cloneColumnModelList = new ArrayList<String>();
		// for(String str : columnModelList){
		// if(list.contains(str)){
		// continue;
		// }
		// cloneColumnModelList.add(str);
		// }
		// columnModelList = cloneColumnModelList;
		cloneColumnModelList.removeAll(list);
		columnTableViewer.setInput(cloneColumnModelList);

		if (!cloneColumnModelList.isEmpty()) {
			columnTableViewer.setSelection(new StructuredSelection(
					cloneColumnModelList.get(cloneColumnModelList.size() - 1)));
		}
	}

	/**
	 * 创建移动按钮事件
	 * 
	 * @param boolean 如果为ture，表示上移，否则表示下移
	 */
	private void moveButtonEvent(boolean isUpMove) {
		List<IndexSqlModel> list = getTableSelection();
		if (list == null) {
			return;
		}

		Map<Integer, IndexSqlModel> sortMap = new HashMap<Integer, IndexSqlModel>(); // 对上移顺序做个排序，先移动行号小的
		for (Object obj : list) {
			sortMap.put(new Integer(cloneColumnModelList.indexOf(obj)), (IndexSqlModel) obj);
		}

		if (isUpMove) {
			for (int i = 0; i < cloneColumnModelList.size(); i++) {
				Object obj = sortMap.get(new Integer(i));

				if (obj == null) {
					continue;
				} else if (i == 0) {
					// 最小的行号为0，无法上移
					break;
				}

				// 把该元素和前面的元素交换位置
				cloneColumnModelList.set(i, cloneColumnModelList.get(i - 1));
				cloneColumnModelList.set(i - 1, (IndexSqlModel) obj);
			}
		} else {
			for (int i = cloneColumnModelList.size() - 1; i > -1; i--) {
				Object obj = sortMap.get(new Integer(i));

				if (obj == null) {
					continue;
				} else if (i == cloneColumnModelList.size() - 1) {
					// 最大的行号为columnModelList.size() - 1，无法下移
					break;
				}

				// 把该元素和前面的元素交换位置
				cloneColumnModelList.set(i, cloneColumnModelList.get(i + 1));
				cloneColumnModelList.set(i + 1, (IndexSqlModel) obj);
			}
		}

		columnTableViewer.setInput(cloneColumnModelList);
		columnTableViewer.setSelection(new StructuredSelection(list));
	}

	/**
	 * 复制按钮事件
	 */
	protected void copButtonEvent() {
		List list = getTableSelection();

		if (list == null) {
			return;
		}

		Clipboard clipboard = new Clipboard(getShell().getDisplay());
		clipboard.setContents(new Object[] { list.toArray(new IndexSqlModel[] {}) },
				new Transfer[] { ColumnModelTransfer.getInstance() });

		clipboard.dispose();
	}

	/**
	 * 粘贴按钮事件
	 */
	protected void pasButtonEvent() {
		Clipboard clipboard = new Clipboard(getShell().getDisplay());

		Object obj = clipboard.getContents(ColumnModelTransfer.getInstance());
		if (obj == null) {
			return;
		}

		// if (obj instanceof ColumnModel[]) {
		// addCopyColumnModel((ColumnModel[]) obj);
		// }

		clipboard.dispose();
	}

	/**
	 * 剪切按钮事件
	 */
	protected void cutButtonEvent() {
		copButtonEvent();

		List<IndexSqlModel> modelList = getTableSelection();

		cloneColumnModelList.removeAll(modelList);
		columnTableViewer.setInput(cloneColumnModelList);
	}

	/**
	 * 打开Column属性对话框
	 */
	protected void openColumnDialog() {
		IStructuredSelection select = (IStructuredSelection) columnTableViewer
				.getSelection();
		if (select.isEmpty()) {
			return;
		}

		IndexSqlModel oldSql = (IndexSqlModel) select.getFirstElement();

//		IndexEditDialog sqlEditDialog = new IndexEditDialog(getShell(), oldSql,
//				columnModelList);
		IndexAutogenerationDialog indexAutogenerationDialog = new IndexAutogenerationDialog(
				getShell(), oldSql, cloneColumnModelList, tableModel);

		int returnCode = indexAutogenerationDialog.open();
		if (returnCode == IDialogConstants.OK_ID) {
			 newSql = indexAutogenerationDialog.getNewIndexSqlModel();
			int index = cloneColumnModelList.indexOf(oldSql);
			cloneColumnModelList.set(index, newSql);
		}

		if (getShell() != null && !getShell().isDisposed()) {
			columnTableViewer.refresh();
		}
	}

	/**
	 * 获取表格所选择的行
	 * 
	 * @return
	 */
	public List<IndexSqlModel> getTableSelection() {
		IStructuredSelection select = (IStructuredSelection) columnTableViewer
				.getSelection();

		if (select.isEmpty()) {
			return null;
		}

		List<IndexSqlModel> list = select.toList();

		return list;
	}

	/**
	 * @deprecated 移动列模型到某个TableItem下方
	 * 
	 * @param modelList
	 */
	public void moveColumnModelListTo(IndexSqlModel[] models, TableItem item) {
		if (models == null || models.length == 0 || item == null) {
			return;
		}

		// 要移动到哪一行以下
		int selectNum = item.getParent().indexOf(item);
		IndexSqlModel insertModel = cloneColumnModelList.get(selectNum); // 要插入到该对象下面

		Map<Integer, IndexSqlModel> sortMap = new HashMap<Integer, IndexSqlModel>(); // 对上移顺序做个排序，先移动行号小的
		List<IndexSqlModel> needSortModelList = new ArrayList<IndexSqlModel>();
		for (IndexSqlModel model : models) {
			sortMap.put(new Integer(cloneColumnModelList.indexOf(model)),
					(IndexSqlModel) model);
			needSortModelList.add(model);
		}

		cloneColumnModelList.removeAll(needSortModelList);
		// 将刚才添加到末尾的那些Model放入到insertModel下方
		int newSelectNum = cloneColumnModelList.indexOf(insertModel); // 要插入到该对象下面
		List<IndexSqlModel> downList = new ArrayList<IndexSqlModel>(); // 记录要插入对象一下的行
		for (int i = newSelectNum + 1, length = cloneColumnModelList.size(); i < length; i++) {
			downList.add(cloneColumnModelList.get(i));
		}
		cloneColumnModelList.removeAll(downList);

		// 把要插入的按原来行号从小到大放入List末尾
		for (int i = 0, length = cloneColumnModelList.size()
				+ needSortModelList.size() + downList.size(); i < length; i++) {
			IndexSqlModel columnModel = sortMap.get(i);
			if (columnModel != null) {
				// columnModelList.add(columnModel);
			}
		}

		cloneColumnModelList.addAll(downList);

		columnTableViewer.setInput(cloneColumnModelList);
	}

	/**
	 * @return the newSql
	 */
	public IndexSqlModel getNewSql() {
		return newSql;
	}

	/**
	 * 该控件的数据来源
	 * 
	 * @param columnModelList
	 */
	public void setColumnModelList(List<IndexSqlModel> columnModelList) {
		this.cloneColumnModelList = columnModelList;
	}

	/**
	 * 如果是TableGefModelDialog来打开该Composite,需要设置其对应的TableModel
	 * 
	 * @param tableModel
	 *            the tableModel to set
	 */
	public void setTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
	}

	public List<IndexSqlModel> getColumnModelList() {
		return cloneColumnModelList;
	}

	public List<DataTypeModel> getDataTypeList() {
		return dataTypeList;
	}

	public void setPhysicalDataModel(ProductModel productModel) {
		this.productModel = productModel;
	}
}

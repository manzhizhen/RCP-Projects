/* 文件名：     StoredProcedureManagerComposite.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-23
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog.composite;


import java.util.ArrayList;
import java.util.HashMap;
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

import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.StoredProcedureModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.db.DataTypeModel;
import cn.sunline.suncard.powerdesigner.provider.SQLManagerLabelProvider;
import cn.sunline.suncard.powerdesigner.provider.StoredProcedureManagerLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.transfer.ColumnModelTransfer;
import cn.sunline.suncard.powerdesigner.ui.dialog.StoredProcedureEditDialog;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;

/**
 * 表格中字段编辑用到的Composite
 * @author  Manzhizhen
 * @version 1.0, 2012-11-23
 * @see 
 * @since 1.0
 */
public class StoredProcedureManagerComposite extends Composite{
	private Button delButton;
//	private Button cutButton;
	private Button downButton;
//	private Button pasButton;
	private Button upButton;
	private Button addButton;
	private TableViewer columnTableViewer;
	private Table columnTable;
//	private Button copButton;
	
	private List<StoredProcedureModel> columnModelList = new ArrayList<StoredProcedureModel>();
	private TableModel tableModel = new TableModel();
	
	private DataTypeModel undefinedDataTypeModel; // 未定义数据类型（即用户没有选择数据类型时显示的数据类型）;
	private List<DataTypeModel> dataTypeList = new ArrayList<DataTypeModel>(); // 记录该数据库所支持的数据类型List
	
	private ProductModel productModel = null; // 该物理数据模型是为了新建默认列对象时，默认列对象能向上遍历用的
	
	private Log logger = LogManager.getLogger(StoredProcedureManagerComposite.class.getName());
	

	/**
	 * @param parent
	 * @param style
	 */
	public StoredProcedureManagerComposite(Composite parent, int style) {
		super(parent, style);
		createColumnItem();
	}
	
	/**
	 * 初始化列标签数据
	 */
	public void initColumnItemData() {
//		undefinedDataTypeModel = new DataTypeModel();
//		undefinedDataTypeModel.setName(DmConstants.UNDEFINED);
//		undefinedDataTypeModel.setType(DmConstants.UNDEFINED);
		
		// 获取该数据库支持的数据类型
//		PhysicalDiagramModel physicalDiagramModel = tableModel
//				.getPhysicalDiagramModel();
//		if (physicalDiagramModel == null
//				|| physicalDiagramModel.getPhysicalDataModel() == null
//				|| physicalDiagramModel.getPhysicalDataModel()
//						.getDatabaseTypeModel() == null) {
////			setErrorMessage("无法找到该表格所属的数据库类型！");
//			MessageDialog.openError(getShell(), I18nUtil.getMessage("MESSAGE"),
//					"无法找到该列对象所属的数据库类型！");
//			return;
//		}
		
//		dataTypeList.add(undefinedDataTypeModel); // 添加未定义数据类型
		// 添加该数据库所支持的所有数据类型
//		dataTypeList.addAll(DatabaseManager
//				.getDataTypeList(physicalDiagramModel.getPhysicalDataModel()
//						.getDatabaseTypeModel()));
		
		// 让该表格可以编辑
		String[] columnProperties = new String[] { "num", "name"};
		columnTableViewer.setColumnProperties(columnProperties);

//		String[] typeNameArray = new String[dataTypeList.size()];
//		for (int i = 0; i < dataTypeList.size(); i++) {
//			typeNameArray[i] = dataTypeList.get(i).getName();
//		}

		CellEditor[] cellEditors = new CellEditor[1];
		cellEditors[0] = new TextCellEditor(columnTable);
//		cellEditors[1] = new TextCellEditor(columnTable);
//		cellEditors[2] = new TextCellEditor(columnTable);
//		cellEditors[3] = new ComboBoxCellEditor(columnTable, typeNameArray);
//		// cellEditors[3] = new ComboBoxCellEditor(columnTable, new
//		// String[]{"sdfsdf", "sdfdsf"});
//		cellEditors[4] = new TextCellEditor(columnTable);
//		cellEditors[5] = new TextCellEditor(columnTable);
//		cellEditors[6] = new CheckboxCellEditor(columnTable);
//		cellEditors[7] = new CheckboxCellEditor(columnTable);
//		cellEditors[8] = new CheckboxCellEditor(columnTable);
//		cellEditors[9] = new CheckboxCellEditor(columnTable);
		columnTableViewer.setCellEditors(cellEditors);

//		ICellModifier modifier = new DataTypeCellModify(columnTableViewer, this);
//		columnTableViewer.setCellModifier(modifier);

		columnTableViewer.setContentProvider(new ArrayContentProvider());
		columnTableViewer.setLabelProvider(new StoredProcedureManagerLabelProvider(columnModelList));

		columnTableViewer.setInput(columnModelList);
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

		/*copButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				copButtonEvent();
			}
		});

		pasButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				pasButtonEvent();
			}
		});

		cutButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cutButtonEvent();
			}
		});*/

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
		
		
//		columnTable.addMouseListener(new MouseListener() {
//			@Override
//			public void mouseUp(MouseEvent e) {
//			}
//			
//			@Override
//			public void mouseDown(MouseEvent e) {
//			}
//			
//			@Override
//			public void mouseDoubleClick(MouseEvent e) {
//			}
//		});
		
		// columnTable.addSelectionListener(new SelectionAdapter() {
				// @Override
				// public void widgetSelected(SelectionEvent e) {
				// TableItem[] tableItems = columnTable.getItems();
				// List<TableItem> itemList = Arrays.asList(columnTable.getSelection());
				//
				// for(TableItem tableItem : tableItems){
				// if(itemList != null && itemList.contains(tableItem)) {
				// tableItem.setBackground(ColorConstants.black);
				// tableItem.setForeground(ColorConstants.white);
				// } else {
				// tableItem.setBackground(ColorConstants.white);
				// tableItem.setForeground(ColorConstants.black);
				// }
				// }
				//
				// columnTable.redraw();
				// }
				// });
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

		/*pasButton = new Button(this, SWT.NONE);
		FormData fd_pasButton = new FormData();
		fd_pasButton.width = 30;
		fd_pasButton.bottom = new FormAttachment(delButton, 0, SWT.BOTTOM);
		fd_pasButton.right = new FormAttachment(delButton, -6);
		pasButton.setLayoutData(fd_pasButton);
		// pasButton.setText("Pas");
		pasButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_PASTE));
		pasButton.setToolTipText("粘贴");

		copButton = new Button(this, SWT.NONE);
		FormData fd_copButton = new FormData();
		fd_copButton.width = 30;
		fd_copButton.bottom = new FormAttachment(delButton, 0, SWT.BOTTOM);
		fd_copButton.right = new FormAttachment(pasButton, -6);
		copButton.setLayoutData(fd_copButton);
		// copButton.setText("Cop");
		copButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_COPY));
		copButton.setToolTipText("复制");

		cutButton = new Button(this, SWT.NONE);
		FormData fd_cutButton = new FormData();
		fd_cutButton.width = 30;
		fd_cutButton.top = new FormAttachment(delButton, 0, SWT.TOP);
		fd_cutButton.right = new FormAttachment(copButton, -6);
		cutButton.setLayoutData(fd_cutButton);
		// cutButton.setText("Cut");
		cutButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_CUT));
		cutButton.setToolTipText("剪切");*/

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

		TableViewerColumn tableViewerColumn_0 = new TableViewerColumn(columnTableViewer, SWT.NONE);
		TableColumn numColumn = tableViewerColumn_0.getColumn();
		numColumn.setWidth(50);
		numColumn.setText("");

		TableViewerColumn tableViewerColumn = new TableViewerColumn(columnTableViewer, SWT.NONE);
		TableColumn nameColumn = tableViewerColumn.getColumn();
		nameColumn.setWidth(150);
		nameColumn.setText("名称");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(columnTableViewer, SWT.NONE);
		TableColumn descColumn = tableViewerColumn_1.getColumn();
		descColumn.setWidth(490);
		descColumn.setText("存储过程内容");

		/*TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(columnTableViewer, SWT.NONE);
		TableColumn typeColumn = tableViewerColumn_2.getColumn();
		typeColumn.setWidth(130);
		typeColumn.setText("数据类型");

		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(columnTableViewer, SWT.NONE);
		TableColumn lengthColumn = tableViewerColumn_3.getColumn();
		lengthColumn.setWidth(60);
		lengthColumn.setText("长度");

		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(columnTableViewer, SWT.NONE);
		TableColumn precisionColumn = tableViewerColumn_4.getColumn();
		precisionColumn.setWidth(60);
		precisionColumn.setText("精度");

		TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(columnTableViewer, SWT.NONE);
		TableColumn pColumn = tableViewerColumn_5.getColumn();
		pColumn.setWidth(25);
		pColumn.setText("P");

		TableViewerColumn tableViewerColumn_6 = new TableViewerColumn(columnTableViewer, SWT.NONE);
		TableColumn fColumn = tableViewerColumn_6.getColumn();
		fColumn.setWidth(25);
		fColumn.setText("F");

		TableViewerColumn tableViewerColumn_7 = new TableViewerColumn(columnTableViewer, SWT.NONE);
		TableColumn mColumn = tableViewerColumn_7.getColumn();
		mColumn.setWidth(25);
		mColumn.setText("M");
		
		TableViewerColumn tableViewerColumn_8 = new TableViewerColumn(columnTableViewer, SWT.NONE);
		TableColumn iColumn = tableViewerColumn_8.getColumn();
		iColumn.setWidth(25);
		iColumn.setText("I");*/

	}


	/**
	 * 添加新列的按钮事件
	 * 在设置一个物理数据模型的默认列对象的列表时，为了能向上遍历，必须重写这个方法，
	 * 并传入该物理数据模型
	 */
	protected void addButtonEvent() {
//		ColumnModel columnModel = new ColumnModel();
//		String newName = getNewColumnName();
//		columnModel.setColumnName(newName);
//		columnModel.setColumnDesc(newName);
//		columnModel.setDataTypeModel(undefinedDataTypeModel);
//		columnModel.setTableModel(tableModel);
		
		StoredProcedureModel sql = new StoredProcedureModel();
		sql.setId(getNewId());
		sql.setName(getNewName());
		sql.setStoredProceduerStr("eidt storedProcedure_" + (columnModelList.size() + 1));
		sql.setProductModel(productModel);
		columnModelList.add(sql);
		
		columnTableViewer.setInput(columnModelList);
		columnTableViewer.setSelection(new StructuredSelection(sql));
	}
	
	/**
	 * 删除一列的按钮事件
	 */
	private void delButtonEvent() {
		List<StoredProcedureModel> list = getTableSelection();

		if (list == null) {
			return;
		}

		columnModelList.removeAll(list);
		columnTableViewer.setInput(columnModelList);
		
		if (!columnModelList.isEmpty()) {
			columnTableViewer.setSelection(new StructuredSelection(
					columnModelList.get(columnModelList.size() - 1)));
		}
	}
	
	/**
	 * 创建移动按钮事件
	 * 
	 * @param boolean 如果为ture，表示上移，否则表示下移
	 */
	private void moveButtonEvent(boolean isUpMove) {
		List<StoredProcedureModel> list = getTableSelection();
		if (list == null) {
			return;
		}

		Map<Integer, StoredProcedureModel> sortMap = new HashMap<Integer, StoredProcedureModel>(); // 对上移顺序做个排序，先移动行号小的
		for (Object obj : list) {
			sortMap.put(new Integer(columnModelList.indexOf(obj)),
					(StoredProcedureModel) obj);
		}

		if (isUpMove) {
			for (int i = 0; i < columnModelList.size(); i++) {
				Object obj = sortMap.get(new Integer(i));

				if (obj == null) {
					continue;
				} else if (i == 0) {
					// 最小的行号为0，无法上移
					break;
				}

				// 把该元素和前面的元素交换位置
				columnModelList.set(i, columnModelList.get(i - 1));
				columnModelList.set(i - 1, (StoredProcedureModel) obj);
			}
		} else {
			for (int i = columnModelList.size() - 1; i > -1; i--) {
				Object obj = sortMap.get(new Integer(i));

				if (obj == null) {
					continue;
				} else if (i == columnModelList.size() - 1) {
					// 最大的行号为columnModelList.size() - 1，无法下移
					break;
				}

				// 把该元素和前面的元素交换位置
				columnModelList.set(i, columnModelList.get(i + 1));
				columnModelList.set(i + 1, (StoredProcedureModel) obj);
			}
		}

		columnTableViewer.setInput(columnModelList);
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
		clipboard.setContents(
				new Object[] { list.toArray(new String[] {}) },
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

//		if (obj instanceof ColumnModel[]) {
//			addCopyColumnModel((ColumnModel[]) obj);
//		}

		clipboard.dispose();
	}
	
	/**
	 * 剪切按钮事件
	 */
	protected void cutButtonEvent() {
		copButtonEvent();

		List<StoredProcedureModel> modelList = getTableSelection();
		
		columnModelList.removeAll(modelList);
		columnTableViewer.setInput(columnModelList);
	}
	
	/**
	 * 粘贴或拖拽操作时添加列模型调用的函数
	 * 
	 * @param modelList
	 */
//	public void addCopyColumnModel(String[] modelList) {
//		if (modelList == null || modelList.length == 0) {
//			return;
//		}
//
//		for (String columnModel : modelList) {
//			// 如果发现有列名重复，则需要修改其列名
//			if (columnModelList.contains(columnModel)) {
//				// 记住原来的名字
//				String oldName = columnModel.getColumnName();
//				int num = 1;
//				while (true) {
//					columnModel.setColumnName(oldName + "_" + num);
//					if (columnModelList.contains(columnModel)) {
//						num++;
//					} else {
//						break;
//					}
//				}
//			}
//
//			columnModel.setParentTableColumnId(null);
//			columnModel.setPrimaryKey(false);
//
//			columnModel.setTableModel(tableModel);
//			columnModelList.add(columnModel);
//		}
//
//		columnTableViewer.setInput(columnModelList);
//	}
	
	/**
	 * 打开Column属性对话框
	 */
	protected void openColumnDialog() {
		IStructuredSelection select = (IStructuredSelection) columnTableViewer
				.getSelection();
		if (select.isEmpty()) {
			return;
		}

		StoredProcedureModel oldSql = (StoredProcedureModel) select.getFirstElement();
		StoredProcedureEditDialog sqlEditDialog = new StoredProcedureEditDialog(getShell(), oldSql, columnModelList);
		int returnCode = sqlEditDialog.open();
		if (returnCode == IDialogConstants.OK_ID) {
			StoredProcedureModel newSql = sqlEditDialog.getNewSql();
			newSql.setProductModel(productModel);
			int index = columnModelList.indexOf(oldSql);
			columnModelList.set(index, newSql);
		}

		if(getShell() != null && !getShell().isDisposed()) {
			columnTableViewer.refresh();
		}
	}
	
	/**
	 * 添加一个新列时，默认生成的列名称
	 */
//	private String getNewColumnName() {
//		List<String> nameList = new ArrayList<String>();
//		for (ColumnModel columnModel : columnModelList) {
//			nameList.add(columnModel.getColumnName());
//		}
//
//		int index = columnModelList.size() + 1;
//		while (true) {
//			if (nameList.contains(DmConstants.COLUMN_PREFIX + index)) {
//				index++;
//			} else {
//				break;
//			}
//		}
//
//		return DmConstants.COLUMN_PREFIX + index;
//	}
	
	/**
	 * 获取表格所选择的行
	 * 
	 * @return
	 */
	public List<StoredProcedureModel> getTableSelection() {
		IStructuredSelection select = (IStructuredSelection) columnTableViewer
				.getSelection();

		if (select.isEmpty()) {
			return null;
		}

		List<StoredProcedureModel> list = select.toList();

		return list;
	}
	
	/**
	 * 该控件的数据来源
	 * @param columnModelList
	 */
	public void setColumnModelList(List<StoredProcedureModel> columnModelList) {
		this.columnModelList = columnModelList;
	}

	/**
	 * 如果是TableGefModelDialog来打开该Composite,需要设置其对应的TableModel
	 * @param tableModel the tableModel to set
	 */
	public void setTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
	}

	public List<StoredProcedureModel> getColumnModelList() {
		return columnModelList;
	}

	public List<DataTypeModel> getDataTypeList() {
		return dataTypeList;
	}

	/**
	 * 只有在编辑一个物理数据模型下面的默认列对象时，才需要设置该物理数据模型
	 */
	public void setPhysicalDataModel(ProductModel productModel) {
		this.productModel = productModel;
	}
	
	/**
	 * 获取新对象Id
	 * @return 新对象Id
	 */
	private String getNewId() {
		int length = columnModelList.size();
		
		if (length == 0) {
			return DmConstants.PRE_SP_ID + 0;
		}
		
		int maxValue = 0;
		for (int i = 0 ; i < length ; i++) {
			String id = columnModelList.get(i).getId();
			int temp = Integer.parseInt(id.substring(DmConstants.PRE_SP_ID.length(), id.length()));
			if (temp > maxValue) {
				maxValue = temp;
			}
		}
		
		return DmConstants.PRE_SP_ID + (maxValue + 1);
	}
	
	/**
	 * 获取新对象名称
	 * @return 新对象名称
	 */
	private String getNewName() {
		int length = columnModelList.size();
		
		if (length == 0) {
			return DmConstants.PRE_SP_NAME + 0;
		}
		
		int maxValue = 0;
		for (int i = 0 ; i < length ; i++) {
			String name = columnModelList.get(i).getName();
			int temp = Integer.parseInt(name.substring(DmConstants.PRE_SP_NAME.length(), name.length()));
			if (temp > maxValue) {
				maxValue = temp;
			}
		}
		
		return DmConstants.PRE_SP_NAME + (maxValue + 1);
	}
}

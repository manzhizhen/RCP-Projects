/* 文件名：     TableDataComposite.java
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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
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

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.InitTableDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.TableDataModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.db.DataTypeModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.provider.TableDataLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.transfer.ColumnModelTransfer;
import cn.sunline.suncard.powerdesigner.ui.dialog.TableDataCellModifier;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 表格中字段编辑用到的Composite
 * @author  Manzhizhen
 * @version 1.0, 2012-11-23
 * @see 
 * @since 1.0
 */
public class TableDataComposite extends Composite{
	private Log logger = LogManager.getLogger(TableDataComposite.class.getName());
	
	public static final String COLUMN_INDEX = "columnIndex";
	
	private Button delButton;
//	private Button cutButton;
	private Button downButton;
//	private Button pasButton;
	private Button upButton;
	private Button addButton;
	private TableViewer columnTableViewer;
	private Table columnTable;
//	private Button copButton;
	
//	private List<String> columnModelList = new ArrayList<String>();
	private TableModel tableModel = new TableModel();
	private List<ColumnModel> tableColumnNames = new ArrayList<ColumnModel>();	// 定义表格字段名称列表
	private List<TableDataModel> tableDataList = new ArrayList<TableDataModel>();	// 定义表格数据列表
	private int tableColumnNum;	// 定义表格行数
	
	private DataTypeModel undefinedDataTypeModel; // 未定义数据类型（即用户没有选择数据类型时显示的数据类型）;
	private List<DataTypeModel> dataTypeList = new ArrayList<DataTypeModel>(); // 记录该数据库所支持的数据类型List
	
	private PhysicalDataModel physicalDataModel = null; // 该物理数据模型是为了新建默认列对象时，默认列对象能向上遍历用的
	public static final String OPERA_ATTRIBUTE = "@OPERA_ATTRIBUTE@";
	
	
	/**
	 * @param parent
	 * @param style
	 */
	public TableDataComposite(Composite parent, int style) {
		super(parent, style);
		
		createColumnItem();
		
//		initColumnItemData();
//		createEvent();
	}
	
	/**
	 * 初始化列标签数据
	 */
	public void initColumnItemData() {
		columnTableViewer.setLabelProvider(new TableDataLabelProvider(tableDataList, tableColumnNames));
		columnTableViewer.setContentProvider(new ArrayContentProvider());
		
		// 让该表格可以编辑
		String[] columnProperties = new String[tableColumnNum + 2];
		columnProperties[0] = COLUMN_INDEX;
		
		CellEditor[] cellEditors = new CellEditor[tableColumnNum + 2];
		cellEditors[0] = new TextCellEditor(columnTable);
		cellEditors[tableColumnNum + 1] = new ComboBoxCellEditor(columnTable
				, new String[]{I18nUtil.getMessage("YES"), I18nUtil.getMessage("NO")});
		columnProperties[tableColumnNum + 1] =  OPERA_ATTRIBUTE;
		
		for (int i = 0; i < tableColumnNum; i++) {
			columnProperties[i + 1] = tableColumnNames.get(i).getColumnName();
			cellEditors[i + 1] = new TextCellEditor(columnTable);
		}
		columnTableViewer.setCellEditors(cellEditors);
		columnTableViewer.setColumnProperties(columnProperties);

		ICellModifier modifier = new TableDataCellModifier(columnTableViewer, this, tableColumnNames);
		columnTableViewer.setCellModifier(modifier);
		

		columnTableViewer.setInput(tableDataList);
		columnTableViewer.refresh();
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

		createTableControl();
	}
	
	private void createTableControl() {
		TableViewerColumn tableViewerColumn_0 = new TableViewerColumn(columnTableViewer, SWT.NONE);
		TableColumn numColumn = tableViewerColumn_0.getColumn();
		numColumn.setWidth(50);
		numColumn.setText("序号");
		
		tableColumnNames.clear();
		tableColumnNum = tableModel.getColumnList().size();
		TableViewerColumn[] tableViewerColumns = new TableViewerColumn[tableColumnNum + 1];
		TableColumn[] tableColumns = new TableColumn[tableColumnNum + 1];
		for (int i = 0; i < tableColumnNum; i++) {
			tableViewerColumns[i] = new TableViewerColumn(columnTableViewer, SWT.NONE);
			tableColumns[i] = tableViewerColumns[i].getColumn();
			tableColumns[i].setWidth(130);
			tableColumns[i].setText(tableModel.getColumnList().get(i).getColumnDesc());
			
			// 赋值字段名称列表
			tableColumnNames.add(tableModel.getColumnList().get(i));
		}
		
		// 操作属性列
		tableViewerColumns[tableColumnNum] = new TableViewerColumn(columnTableViewer, SWT.NONE);
		tableColumns[tableColumnNum] = tableViewerColumns[tableColumnNum].getColumn();
		tableColumns[tableColumnNum].setWidth(130);
		tableColumns[tableColumnNum].setText(I18nUtil.getMessage("OPERA_ATTRIBUTE"));
	}
	
	/**
	 * 根据最新的列信息来重绘表格
	 */
	public void redrawControl() {
		TableColumn[] tableColumns = columnTable.getColumns();
		for(TableColumn tableColumn : tableColumns) {
			tableColumn.dispose();
		}
		
		createTableControl();
		
		initColumnItemData();
		columnTableViewer.refresh(true);
	}

	/**
	 * 添加新列的按钮事件
	 * 在设置一个物理数据模型的默认列对象的列表时，为了能向上遍历，必须重写这个方法，
	 * 并传入该物理数据模型
	 */
	protected void addButtonEvent() {
		TableDataModel tableDataModel = new TableDataModel();
		
		for (int i = 0; i < tableColumnNum; i++) {
			// 获取列模型
//			String columnId = tableColumnNames.get(i);
//			FileModel fileModel = tableModel.getPhysicalDiagramModel().getPackageModel().getPhysicalDataModel().getFileModel();
//			ColumnModel columnModel = ColumnModelFactory.getColumnModel(fileModel, columnId);
			
			ColumnModel columnModel =  tableColumnNames.get(i);
			
			// 判断列模型数据来源,根据列模型数据来源初始化
			String systemDefaultValueType = columnModel.getSystemDefaultValueType();
			
			// 如果数据来源类型为系统日期、系统时间、登录用户、机构代码
			// 则赋值列模型的initDefaultValue成员变量值
			if (DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_D.equalsIgnoreCase(systemDefaultValueType) 
					|| DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_T.equalsIgnoreCase(systemDefaultValueType) 
					|| DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_U.equalsIgnoreCase(systemDefaultValueType) 
					|| DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_O.equalsIgnoreCase(systemDefaultValueType)) {
				tableDataModel.getDataMap().put(columnModel, columnModel.getInitDefaultValue());
				
				// 如果数据来源类型为用户自定义
				// 则赋值列模型的systemDefaultValue成员变量值
			} else if (DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_C.equalsIgnoreCase(systemDefaultValueType)) {
				tableDataModel.getDataMap().put(columnModel, columnModel.getSystemDefaultValue());
				
			} else {
				tableDataModel.getDataMap().put(columnModel, DmConstants.EMPTY_STRING);
			}
		}
		
		if (tableDataList.contains(tableDataModel)) {
			return;
		}
		
		tableDataList.add(tableDataModel);
		
		columnTableViewer.setInput(tableDataList);
		columnTableViewer.setSelection(new StructuredSelection(tableDataModel));
	}
	
	/**
	 * 删除一列的按钮事件
	 */
	private void delButtonEvent() {
		List<TableDataModel> list = getTableSelection();

		if (list == null) {
			return;
		}

		tableDataList.removeAll(list);
		columnTableViewer.setInput(tableDataList);
		
		if (!tableDataList.isEmpty()) {
			columnTableViewer.setSelection(new StructuredSelection(
					tableDataList.get(tableDataList.size() - 1)));
		}
	}
	
	/**
	 * 创建移动按钮事件
	 * 
	 * @param boolean 如果为ture，表示上移，否则表示下移
	 */
	private void moveButtonEvent(boolean isUpMove) {
		List<TableDataModel> list = getTableSelection();
		if (list == null) {
			return;
		}

		Map<Integer, TableDataModel> sortMap = new HashMap<Integer, TableDataModel>(); // 对上移顺序做个排序，先移动行号小的
		for (Object obj : list) {
			sortMap.put(new Integer(tableDataList.indexOf(obj)),
					(TableDataModel) obj);
		}

		if (isUpMove) {
			for (int i = 0; i < tableDataList.size(); i++) {
				Object obj = sortMap.get(new Integer(i));

				if (obj == null) {
					continue;
				} else if (i == 0) {
					// 最小的行号为0，无法上移
					break;
				}

				// 把该元素和前面的元素交换位置
				tableDataList.set(i, tableDataList.get(i - 1));
				tableDataList.set(i - 1, (TableDataModel) obj);
			}
		} else {
			for (int i = tableDataList.size() - 1; i > -1; i--) {
				Object obj = sortMap.get(new Integer(i));

				if (obj == null) {
					continue;
				} else if (i == tableDataList.size() - 1) {
					// 最大的行号为columnModelList.size() - 1，无法下移
					break;
				}

				// 把该元素和前面的元素交换位置
				tableDataList.set(i, tableDataList.get(i + 1));
				tableDataList.set(i + 1, (TableDataModel) obj);
			}
		}

		columnTableViewer.setInput(tableDataList);
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
	 * 获取表格所选择的行
	 * 
	 * @return
	 */
	public List<TableDataModel> getTableSelection() {
		IStructuredSelection select = (IStructuredSelection) columnTableViewer
				.getSelection();

		if (select.isEmpty()) {
			return null;
		}

		List<TableDataModel> list = select.toList();

		return list;
	}
	
	/**
	 * 移动列模型到某个TableItem下方
	 * 
	 * @param modelList
	 */
	public void moveColumnModelListTo(TableDataModel[] models, TableItem item) {
		if (models == null || models.length == 0 || item == null) {
			return;
		}

		// 要移动到哪一行以下
		int selectNum = item.getParent().indexOf(item);
		TableDataModel insertModel = tableDataList.get(selectNum); // 要插入到该对象下面

		Map<Integer, TableDataModel> sortMap = new HashMap<Integer, TableDataModel>(); // 对上移顺序做个排序，先移动行号小的
		List<TableDataModel> needSortModelList = new ArrayList<TableDataModel>();
		for (TableDataModel model : models) {
			sortMap.put(new Integer(tableDataList.indexOf(model)),
					(TableDataModel) model);
			needSortModelList.add(model);
		}

		tableDataList.removeAll(needSortModelList);
		// 将刚才添加到末尾的那些Model放入到insertModel下方
		int newSelectNum = tableDataList.indexOf(insertModel); // 要插入到该对象下面
		List<TableDataModel> downList = new ArrayList<TableDataModel>(); // 记录要插入对象一下的行
		for (int i = newSelectNum + 1, length = tableDataList.size(); i < length; i++) {
			downList.add(tableDataList.get(i));
		}
		tableDataList.removeAll(downList);

		// 把要插入的按原来行号从小到大放入List末尾
		for (int i = 0, length = tableDataList.size()
				+ needSortModelList.size() + downList.size(); i < length; i++) {
			TableDataModel columnModel = sortMap.get(i);
			if (columnModel != null) {
				tableDataList.add(columnModel);
			}
		}

		tableDataList.addAll(downList);

		columnTableViewer.setInput(tableDataList);
	}

	public InitTableDataModel getInitTableDataModel() {
		InitTableDataModel initTableDataModel = new InitTableDataModel();
		
		initTableDataModel.setTableModel(tableModel);
		initTableDataModel.getInitDataList().removeAll(initTableDataModel.getInitDataList());
		
		for (TableDataModel tableDataModel : tableDataList) {
			initTableDataModel.getInitDataList().add(tableDataModel);
		}
		
		return initTableDataModel;
	}

	public void setTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
		
		if(tableModel != null) {
			// 初始化成员变量
			tableDataList = tableModel.getInitTableDataModel().getInitDataList();

		}
	}
	
	
}

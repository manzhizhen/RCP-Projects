/* 文件名：     TableModelDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-8
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.ui.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.DocumentException;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.command.UpdateTableModelCommand;
import cn.sunline.suncard.powerdesigner.db.DatabaseGeneration;
import cn.sunline.suncard.powerdesigner.db.DatabaseManager;
import cn.sunline.suncard.powerdesigner.db.KeyWordsManager;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.listener.ColumnModelDragSourceListener;
import cn.sunline.suncard.powerdesigner.listener.ColumnModelDropTargetListener;
import cn.sunline.suncard.powerdesigner.manager.ProductSpaceManager;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProductSpaceModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.db.DataTypeModel;
import cn.sunline.suncard.powerdesigner.model.db.KeyWords;
import cn.sunline.suncard.powerdesigner.provider.ProductModelLabelProvider;
import cn.sunline.suncard.powerdesigner.provider.TableColumnLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmDictConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.transfer.ColumnModelTransfer;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContentProvider;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeLabelProvider;
import cn.sunline.suncard.powerdesigner.ui.dialog.WindowsManagerDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.composite.ColumnModelComposite;
import cn.sunline.suncard.powerdesigner.ui.dialog.composite.ModuleCheckboxTreeComposite;
import cn.sunline.suncard.powerdesigner.ui.dialog.composite.TableDataComposite;
import cn.sunline.suncard.powerdesigner.ui.dialog.composite.TableInfoComposite;
import cn.sunline.suncard.sde.bs.dict.BizDictManager;
import cn.sunline.suncard.sde.bs.dict.DictComboViewer;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.CheckboxTreeViewer;

/**
 * 在GEF界面上双击表格图形弹出的对话框
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-9-8
 * @see
 * @since 1.0
 */
public class TableGefModelDialog extends TitleAreaDialog {
	private Composite composite;

	private TableGefModel tableGefModel;
	private TableModel tableModel;
	private TableModel cloneTableModel; // 便于TableModel的还原
	
	private boolean isProjectTable = false; // 是否是项目树上的表格，如果是项目树上的表格，则有些控件不能初始化

	private UpdateTableModelCommand command;

	// 一个TableModel只能打开一个对话框
	private static Map<TableModel, TableGefModelDialog> tableDialogMap = new HashMap<TableModel, TableGefModelDialog>();

	private CTabFolder folder; // 文件夹选项卡
	private CTabItem tableItem; // Table属性表情
	private CTabItem columnItem; // 表格列属性标签
	private CTabItem initTableItem;	// 初始化表格数据标签
	private CTabItem previewItem;
	
	private TableInfoComposite tableInfoComposite;	// 表格基本信息的Composite
	private ColumnModelComposite columnModelComposite; // 列属性标签的Composite
	private TableDataComposite tableDataComposite;

	private List<ColumnModel> columnModelList = new ArrayList<ColumnModel>();

	private Log logger = LogManager.getLogger(TableGefModelDialog.class
			.getName());

	private DataTypeModel undefinedDataTypeModel; // 未定义数据类型（即用户没有选择数据类型时显示的数据类型）;
	private StyledText previewText;

	public TableGefModelDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}

	@Override
	protected void configureShell(Shell newShell) {
		// 表格模型对话框
		newShell.setText(I18nUtil.getMessage("TABLEMODEL_DIALOG"));
		setDefaultImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.TABLE_16));
		super.configureShell(newShell);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(750, 600);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// 表格模型属性
		setTitle(I18nUtil.getMessage("TABLEMODEL_ATTRI"));
		setMessage(I18nUtil.getMessage("TABLEMODEL_ATTRI"));
		setTitleImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.TABLE_64));

		Control control = super.createDialogArea(parent);

		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		composite.setLayout(new FormLayout());

		try {
			createControl();
			initControlData();
			createEvent();
		} catch (CloneNotSupportedException e) {
			logger.error("克隆TableModel失败！" + e.getMessage());
			setErrorMessage("克隆TableModel失败！" + e.getMessage());
			e.printStackTrace();
		}

		return control;
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Control control =super.createContents(parent);
		// 如果在打开该对话框之前，窗口管理对话框已经打开，这需要更新窗口管理对话框的数据
		WindowsManagerDialog.refreshData();
		return control;
	}

	@Override
	public int open() {
		TableGefModelDialog tableGefModelDialog = getTableDialogMap()
				.get(tableModel);
		if (tableGefModelDialog != null) {
			tableGefModelDialog.getShell().setMinimized(false);
			return Window.OK;
		} else {
			getTableDialogMap().put(tableModel, this);
			return super.open();
		}
	}

	@Override
	public boolean close() {
		getTableDialogMap().remove(tableModel);
		WindowsManagerDialog.refreshData();
		
		// 关闭与其相关的ColumnPropertiesDialog
		List<ColumnModel> columnModelList = tableModel.getColumnList();
		for(ColumnModel columnModel : columnModelList) {
			ColumnPropertiesDialog dialog = ColumnPropertiesDialog.getColumnDialogMap().get(columnModel);
			if(dialog != null) {
				dialog.close();
			}
		}
		
		return super.close();
	}

	private void createControl() throws CloneNotSupportedException {
		folder = new CTabFolder(composite, SWT.NONE | SWT.BORDER);

		// 设置标签栏的高度
		folder.setTabHeight(20);
		folder.marginHeight = 2;
		folder.marginWidth = 2;
		folder.setMaximizeVisible(true);
		folder.setMinimizeVisible(true);

		// 设置圆角
		folder.setSimple(false);

		folder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.getSource() instanceof CTabFolder) {
					CTabFolder cTabFolder = ((CTabFolder) e.getSource());
					if ("预览".equals(cTabFolder.getSelection().getText())) {
						// try {
						initPreviewData();
						// } catch (CloneNotSupportedException e1) {
						// logger.error("克隆TableModel失败！" + e1.getMessage());
						// e1.printStackTrace();
						// }
					}

				}
				super.widgetSelected(e);
			}
		});

		FormData folderData = new FormData();
		folderData.right = new FormAttachment(100, 0);
		folderData.top = new FormAttachment(0, 0);
		folderData.left = new FormAttachment(0, 0);
		folderData.bottom = new FormAttachment(100, 0);
		folder.setLayoutData(folderData);
		folder.setLayout(new FormLayout());

		createTableItem();
		createColumnItem();
		createPreviewItem();
		createInitTableItem();
	}

	/**
	 * 创建预览标签
	 */
	private void createPreviewItem() {
		previewItem = new CTabItem(folder, SWT.NONE);
		previewItem.setText("预览");
		Image previewItemImage = CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.PREVIEW_ITEM_16);
		previewItem.setImage(previewItemImage);

		Composite previewComposite = new Composite(folder, SWT.NONE);
		previewComposite.setLayout(new FormLayout());
		previewItem.setControl(previewComposite);

		previewText = new StyledText(previewComposite, SWT.BORDER | SWT.READ_ONLY
				| SWT.WRAP | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		previewText.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_previewText = new FormData();
		fd_previewText.top = new FormAttachment(0, 0);
		fd_previewText.bottom = new FormAttachment(100, 0);
		fd_previewText.left = new FormAttachment(0, 0);
		fd_previewText.right = new FormAttachment(100, 0);
		previewText.setLayoutData(fd_previewText);

	}

	/**
	 * 创建列属性标签
	 */
	private void createColumnItem() {
		columnItem = new CTabItem(folder, SWT.NONE);
		columnItem.setText("列属性");
		Image columnItemImage = CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_ITEM_16);
		columnItem.setImage(columnItemImage);

		columnModelComposite = new ColumnModelComposite(folder, SWT.NONE);
		columnItem.setControl(columnModelComposite);
	}
	
	/**
	 * 创建表格数据初始化标签
	 */
	private void createInitTableItem() {
		initTableItem = new CTabItem(folder, SWT.NONE);
		initTableItem.setText("数据初始化");
		Image columnItemImage = CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.A_INIT_DATA);
		initTableItem.setImage(columnItemImage);
		
		tableDataComposite = new TableDataComposite(folder, SWT.NONE);
		
		initTableItem.setControl(tableDataComposite);
	}

	/**
	 * 创建表格属性标签
	 */
	private void createTableItem() {
		tableItem = new CTabItem(folder, SWT.NONE);
		tableItem.setText("表属性");
		Image tableItemImage = CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.TABLE_ITEM_16);
		tableItem.setImage(tableItemImage);
		
		tableInfoComposite = new TableInfoComposite(folder, SWT.NONE);
		tableInfoComposite.setModuleCheckboxTreeCompositeShow(!isProjectTable);
		tableItem.setControl(tableInfoComposite);
	}

	private void initControlData() throws CloneNotSupportedException {
		if (tableModel == null) {
			// 表模型为空，无法初始化数据！
			setErrorMessage(I18nUtil.getMessage("TABLEMODEL_IS_EMPTY_CAN_NOT_INIT_DATA"));
			MessageDialog.openError(getShell(), I18nUtil.getMessage("MESSAGE"),
					I18nUtil.getMessage("TABLEMODEL_IS_EMPTY_CAN_NOT_INIT_DATA"));
			return;
		}
		
		cloneTableModel = tableModel.clone(false);
		// 为了实现sql预览，需要设置这个
		cloneTableModel.setLineModelList(tableModel.getLineModelList());

		getShell().setText(I18nUtil.getMessage("TABLEMODEL_ATTRI") + " - " + tableModel.getTableDesc());

		try {
			initTableItemData();
		} catch (DocumentException e) {
			setErrorMessage("读取表格类型业务字典出错！" + e.getMessage());
			logger.error("读取表格类型业务字典出错！" + e.getMessage());
			e.printStackTrace();
		}
		initColumnItemData();
		
		// 初始化预览标签中的数据
		initPreviewData();
		
		// 初始化表格初始化数据标签
		tableDataComposite.setTableModel(cloneTableModel);
		tableDataComposite.initColumnItemData();
		
	}

	private void initPreviewData() {
		previewText.setText("");
		DatabaseManager databaseManager = new DatabaseManager();
		DatabaseGeneration databaseGeneration = new DatabaseGeneration();

			if(databaseGeneration.isKeyDropForegin()){
			previewText.append(databaseManager
					.getSqlDropForeignKeyModel(cloneTableModel));// 键选项的drop外键按钮
			}
			if (databaseGeneration.isTableDrop()) {// 表格选项框的drop表格按钮
				previewText.append(databaseManager.getSqlDropTableModel(
						cloneTableModel
						));
			}

			if (databaseGeneration.isTableCreate()) {// 表格选项框的create表格按钮
				previewText.append(databaseManager
						.getSqlCreateTableAndPrimaryKeyModel(
								cloneTableModel,
								databaseGeneration.isColumnDefault(),// 列选项框的默认按钮
								databaseGeneration.isKeyCreatePrimary(),// 键选项的create主键按钮
								databaseGeneration.isColumnCheck()));//列选项框的检查按钮
			}

			if (databaseGeneration.isTableComment()) {// 表格选项框的注释按钮
				previewText.append(databaseManager
						.getSqlCommentTableModel(cloneTableModel));
			}

			if (databaseGeneration.isInitializeSQL()) {// 列选项框的初始化SQL按钮
				previewText.append(databaseManager
						.getSqlInitializeSQLModel(cloneTableModel));
			}

			if (databaseGeneration.isKeyCreateForegin()) {// 表格选项框的createFogegin按钮
				previewText.append(databaseManager
						.getSqlCreateForeignKeyModel(cloneTableModel));
			}

		
		if(!previewText.getText().isEmpty()){
			previewText.setText(previewText.getText().trim());
		}
		
		// 关键字变色
		KeyWordsManager keyWordsManager = new KeyWordsManager();
		List<KeyWords> kewWordsList = keyWordsManager.getKeyWordsList(previewText.getText());
		int keyWordsCount = kewWordsList.size();
		StyleRange[] ranges = new StyleRange[keyWordsCount];
		int i = 0;
		for (KeyWords keywords : kewWordsList) {
			// ranges[i] = new StyleRange(keywords.getKeyWordsStart(), keywords
			// .getKeyWords().length(), getShell().getDisplay()
			// .getSystemColor(SWT.COLOR_BLUE), null);
			ranges[i] = new StyleRange(keywords.getKeyWordsStart(), keywords
					.getKeyWords().length(), keywords.getKeyWordsColor(), null);
			i++;
		}

		int textLength = previewText.getText().length();
		previewText.replaceStyleRanges(0, textLength, ranges);
	}

	private void createEvent() {
		// 列标签Composite创建事件
		columnModelComposite.createEvent();
		
		tableDataComposite.createEvent();
		
		folder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (folder.getSelection() == previewItem) {
					initPreviewData();
				} else if(folder.getSelection() == initTableItem) {
					tableDataComposite.redrawControl();
				}
				
				super.widgetSelected(e);
			}
		});
		
	}

	/**
	 * 初始化表格标签数据
	 * @throws DocumentException 
	 */
	private void initTableItemData() throws DocumentException {
		tableInfoComposite.setTableModel(tableModel);
		tableInfoComposite.setCloneTableModel(cloneTableModel);
		tableInfoComposite.initControlData();
	}

	/**
	 * 初始化列标签数据
	 */
	private void initColumnItemData() {
		undefinedDataTypeModel = new DataTypeModel();
		undefinedDataTypeModel.setName(DmConstants.UNDEFINED);
		undefinedDataTypeModel.setType(DmConstants.UNDEFINED);
		
		columnModelComposite.setColumnModelList(cloneTableModel.getColumnList());
		columnModelComposite.setTableModel(cloneTableModel);

		// 获取该数据库支持的数据类型
		PhysicalDiagramModel physicalDiagramModel = tableModel
				.getPhysicalDiagramModel();
		if (physicalDiagramModel == null
				|| physicalDiagramModel.getPackageModel() == null
				|| physicalDiagramModel.getPackageModel().getPhysicalDataModel()
						.getDatabaseTypeModel() == null) {
			setErrorMessage("无法找到该表格所属的数据库类型！");
			MessageDialog.openError(getShell(), I18nUtil.getMessage("MESSAGE"),
					"无法找到该表格所属的数据库类型！");
			return;
		}
		
		List<DataTypeModel> dataTypeList = new ArrayList<DataTypeModel>();
		dataTypeList.add(undefinedDataTypeModel); // 添加未定义数据类型
		// 添加该数据库所支持的所有数据类型
		dataTypeList.addAll(DatabaseManager
				.getDataTypeList(physicalDiagramModel.getPackageModel().getPhysicalDataModel()
								.getDatabaseTypeModel()));
		
		columnModelComposite.initColumnItemData();

	}

	@Override
	protected void okPressed() {
		if (!checkData() && !isProjectTable) {
			return;
		} 
		
		if(isProjectTable) {
			super.cancelPressed();
		}

		cloneTableModel.setTableName(tableInfoComposite.getNameTextValue());
		cloneTableModel.setTableDesc(tableInfoComposite.getDesc());
		cloneTableModel.setTableNote(tableInfoComposite.getNote());
		cloneTableModel.setTableType((String) tableInfoComposite.getTableTypeComboViewer().getSelectKey());

		command = new UpdateTableModelCommand();
		command.setNewTableModel(cloneTableModel);
		
		// 设置表格属于的新的模块
		Object[] selectObjs = tableInfoComposite.getModuleCheckboxTreeComposite()
				.getCheckedElements();
		Set<ModuleModel> moduleModelSet = new HashSet<ModuleModel>();
		for(Object obj : selectObjs) {
			if(obj instanceof TreeContent) {
				TreeContent treeContent = (TreeContent) obj;
				if(treeContent.getObj() instanceof ModuleModel)	{
					moduleModelSet.add((ModuleModel) treeContent.getObj());
				}
			}
		}
		command.setModuleModelSet(moduleModelSet);
		
		if (tableGefModel != null) {
			command.setTableGefModel(tableGefModel);
		} else {
			// 如果用户是从数据库上打开的该对话框，则tableGefModel为空
			command.setTableModel(tableModel);
		}
		
		command.setInitTableDataModel(tableDataComposite.getInitTableDataModel());

		super.okPressed();
	}

	/**
	 * 检查数据是否填写正确
	 * 
	 * @return
	 */
	private boolean checkData() {
		String message = tableInfoComposite.checkData();
		if(message != null) {
			setErrorMessage(message);
			return false;
		}
		
		setErrorMessage(null);
		setMessage(I18nUtil.getMessage("TABLEMODEL_ATTRI"));
		return true;
	}
	
	public void setProjectTable(boolean isProjectTable) {
		this.isProjectTable = isProjectTable;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}

	public void setTableGefModel(TableGefModel tableGefModel) {
		this.tableGefModel = tableGefModel;
		tableModel = tableGefModel.getDataObject();
	}

	public void setTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
	}

	public UpdateTableModelCommand getCommand() {
		return command;
	}

	public List<ColumnModel> getColumnModelList() {
		return columnModelList;
	}

	public static Map<TableModel, TableGefModelDialog> getTableDialogMap() {
		return tableDialogMap;
	}
}

/* 文件名：     ReferencePropertiesDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-10-18
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.DocumentException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

import cn.sunline.suncard.powerdesigner.command.UpdateReferenceCommand;
import cn.sunline.suncard.powerdesigner.db.DatabaseGeneration;
import cn.sunline.suncard.powerdesigner.db.DatabaseManager;
import cn.sunline.suncard.powerdesigner.db.KeyWordsManager;
import cn.sunline.suncard.powerdesigner.gef.model.LineGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableShortcutGefModel;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.KeyToKeyModel;
import cn.sunline.suncard.powerdesigner.model.LineModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.model.db.KeyWords;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.provider.KeyToKeyModelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmDictConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.ui.dialog.WindowsManagerDialog;
import cn.sunline.suncard.sde.bs.dict.BizDictManager;
import cn.sunline.suncard.sde.bs.dict.DictComboViewer;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;

/**
 * 引用连接线弹出对话框
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-10-18
 * @see
 * @since 1.0
 */
public class ReferencePropertiesDialog extends TitleAreaDialog {

	private Composite composite;
	private CTabFolder folder;
	private CTabItem previewItem;
	private Text nameText;
	private Text descText;
	private Text noteText;
	private Text parentTableText;
	private Text childTableText;
	private Table joinsTable;
	private Text constraintNameText;
	private StyledText previewText;

	private LineModel lineModel;
	private LineGefModel lineGefModel;

	private TableViewer joinsTableViewer;
	private DictComboViewer relationComboViewer;
	private UpdateReferenceCommand command;

	// 一个LineModel只能打开一个对话框
	private static Map<LineModel, ReferencePropertiesDialog> lineDialogMap = new HashMap<LineModel, ReferencePropertiesDialog>();

	private Log logger = LogManager.getLogger(ReferencePropertiesDialog.class
			.getName());
	private String oldConstraintName; // 记录对话框打开的时候的约束名称，便于校验

	/**
	 * @param parentShell
	 */
	public ReferencePropertiesDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.BORDER | SWT.CLOSE | SWT.MIN | SWT.MAX);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void configureShell(Shell newShell) {
		// 引用属性对话框
		newShell.setText(I18nUtil.getMessage("REFERENCE_PROPERTIES_DIALOG"));
		setDefaultImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.REFERENCE_16));
		super.configureShell(newShell);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(626, 468);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(I18nUtil.getMessage("REFERENCE_PROPERTIES"));
		setMessage(I18nUtil.getMessage("REFERENCE_PROPERTIES"));
		setTitleImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.REFERENCE_64));

		Control control = super.createDialogArea(parent);

		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		composite.setLayout(new FormLayout());

		createControl();
		try {
			initControlData();
			createEvent();
		} catch (DocumentException e) {
			logger.error("读取业务字典出错！" + e.getMessage());
			setErrorMessage("读取业务字典出错！" + e.getMessage());
			e.printStackTrace();

		} catch (CloneNotSupportedException e) {
			logger.error("克隆KeyToKeyModel失败！" + e.getMessage());
			setErrorMessage("克隆KeyToKeyModel失败！" + e.getMessage());
			e.printStackTrace();
		}

		return control;
	}

	/**
	 * 检查数据是否填写正确
	 * 
	 * @return
	 */
	private boolean checkData() {
		String str = nameText.getText().trim();
		if (str.isEmpty()) {
			// 列名称不能为空！
			setErrorMessage(I18nUtil
					.getMessage("COLUMNmODEL_NAME_IS_NOT_EMPTY"));
			return false;
		}

		str = descText.getText().trim();
		if (str.isEmpty()) {
			// 列描述不能为空！
			setErrorMessage(I18nUtil.getMessage("COLUMN_DESC_NOT_EMPTY"));
			return false;
		}
		
		str = constraintNameText.getText().trim();
		if(str.isEmpty()) {
			// 约束名称不能为空！
			setErrorMessage("约束名称不能为空！");
			return false;
		} else if(!str.equals(oldConstraintName)){
			// 获取文件中用过的所有约束名称
			Set<String> allConstraintNameSet = FileModel.getAllConstraintStr(FileModel
					.getFileModelFromObj(lineGefModel.getSource().getDataObject()));
			if(allConstraintNameSet.contains(str)) {
				// 此约束名称已经被使用！
				setErrorMessage("此约束名称已经被使用！");
				return false;
			}
		}

		setErrorMessage(null);
		setMessage(I18nUtil.getMessage("REFERENCE_PROPERTIES"));
		return true;
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		// 如果在打开该对话框之前，窗口管理对话框已经打开，这需要更新窗口管理对话框的数据
		WindowsManagerDialog.refreshData();
		return control;
	}

	@Override
	public int open() {
		ReferencePropertiesDialog referencePropertiesDialog = getLineDialogMap()
				.get(lineModel);
		if (referencePropertiesDialog != null) {
			referencePropertiesDialog.getShell().setMinimized(false);
			return Window.OK;
		} else {
			getLineDialogMap().put(lineModel, this);
			return super.open();
		}
	}

	@Override
	public boolean close() {
		getLineDialogMap().remove(lineModel);
		WindowsManagerDialog.refreshData();
		return super.close();
	}

	private void createControl() {
		folder = new CTabFolder(composite, SWT.NONE | SWT.BORDER);

		// 设置标签栏的高度
		folder.setTabHeight(20);
		folder.marginHeight = 2;
		folder.marginWidth = 2;
		folder.setMaximizeVisible(true);
		folder.setMinimizeVisible(true);

		// 设置圆角
		folder.setSimple(false);

		FormData folderData = new FormData();
		folderData.right = new FormAttachment(100, 0);
		folderData.top = new FormAttachment(0, 0);
		folderData.left = new FormAttachment(0, 0);
		folderData.bottom = new FormAttachment(100, 0);
		folder.setLayoutData(folderData);
		folder.setLayout(new FormLayout());

		createGeneralItem(); // 创建常规项标签
		createJoinsItem(); // 创建连接项标签
		createConstraintItem(); // 创建约束项标签
		createPreviewItem(); // 创建预览项标签
	}

	private void createPreviewItem() {
		previewItem = new CTabItem(folder, SWT.NONE);
		previewItem.setText("预览");
		Image image = CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.PREVIEW_ITEM_16);
		previewItem.setImage(image);

		Composite composite = new Composite(folder, SWT.NONE);
		previewItem.setControl(composite);
		composite.setLayout(new FormLayout());

		previewText = new StyledText(composite, SWT.BORDER | SWT.WRAP
				| SWT.MULTI);
		FormData fd_previewText = new FormData();
		fd_previewText.bottom = new FormAttachment(100, -5);
		fd_previewText.right = new FormAttachment(100, -5);
		fd_previewText.top = new FormAttachment(0, 5);
		fd_previewText.left = new FormAttachment(0, 5);
		previewText.setLayoutData(fd_previewText);

	}

	private void createConstraintItem() {
		// 约束标签项
		CTabItem constraintItem = new CTabItem(folder, SWT.NONE);
		constraintItem.setText("约束");
		Image image = CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.CONSTRAINT_16);
		constraintItem.setImage(image);

		Composite composite = new Composite(folder, SWT.NONE);
		composite.setLayout(new FormLayout());
		constraintItem.setControl(composite);

		Label lblNewLabel_6 = new Label(composite, SWT.RIGHT);
		FormData fd_lblNewLabel_6 = new FormData();
		fd_lblNewLabel_6.width = 60;
		fd_lblNewLabel_6.top = new FormAttachment(0, 10);
		fd_lblNewLabel_6.left = new FormAttachment(0, 10);
		lblNewLabel_6.setLayoutData(fd_lblNewLabel_6);
		lblNewLabel_6.setText("约束名称:");

		constraintNameText = new Text(composite, SWT.BORDER);
		FormData fd_constraintNameText = new FormData();
		fd_constraintNameText.right = new FormAttachment(100, -5);
		fd_constraintNameText.top = new FormAttachment(lblNewLabel_6, -3,
				SWT.TOP);
		fd_constraintNameText.left = new FormAttachment(lblNewLabel_6, 6);
		constraintNameText.setLayoutData(fd_constraintNameText);

		Label lblNewLabel_7 = new Label(composite, SWT.NONE);
		lblNewLabel_7.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_7 = new FormData();
		fd_lblNewLabel_7.width = 60;
		fd_lblNewLabel_7.top = new FormAttachment(lblNewLabel_6, 17);
		fd_lblNewLabel_7.right = new FormAttachment(lblNewLabel_6, 0, SWT.RIGHT);
		lblNewLabel_7.setLayoutData(fd_lblNewLabel_7);
		lblNewLabel_7.setText("关联关系:");

		Combo relationCombo = new Combo(composite, SWT.READ_ONLY);
		relationComboViewer = new DictComboViewer(relationCombo);
		FormData fd_relationCombo = new FormData();
		fd_relationCombo.right = new FormAttachment(50);
		fd_relationCombo.top = new FormAttachment(lblNewLabel_7, -3, SWT.TOP);
		fd_relationCombo.left = new FormAttachment(constraintNameText, 0,
				SWT.LEFT);
		relationCombo.setLayoutData(fd_relationCombo);
	}

	private void createJoinsItem() {
		// 连接标签项
		CTabItem joinsItem = new CTabItem(folder, SWT.NONE);
		joinsItem.setText("连接");
		Image image = CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.JOINS_16);
		joinsItem.setImage(image);

		Composite composite_1 = new Composite(folder, SWT.NONE);
		joinsItem.setControl(composite_1);
		composite_1.setLayout(new FormLayout());

		joinsTable = new Table(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		joinsTableViewer = new TableViewer(joinsTable);
		joinsTable.setLinesVisible(true);
		joinsTable.setHeaderVisible(true);
		FormData fd_joinsTable = new FormData();
		fd_joinsTable.top = new FormAttachment(0, 0);
		fd_joinsTable.bottom = new FormAttachment(100, 0);
		fd_joinsTable.left = new FormAttachment(0, 0);
		fd_joinsTable.right = new FormAttachment(100, 0);
		joinsTable.setLayoutData(fd_joinsTable);

		TableColumn tblclmnNewColumn = new TableColumn(joinsTable, SWT.NONE);
		tblclmnNewColumn.setWidth(230);
		tblclmnNewColumn.setText("父表列");

		TableColumn tableColumn_1 = new TableColumn(joinsTable, SWT.NONE);
		tableColumn_1.setWidth(231);
		tableColumn_1.setText("子表列");
	}

	private void createGeneralItem() {
		// 常规标签项
		CTabItem generalItem = new CTabItem(folder, SWT.NONE);
		// generalItem.setText(I18nUtil.getMessage("GENERAL"));
		generalItem.setText("常规");
		Image image = CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.GENERAL_16);
		generalItem.setImage(image);

		Composite composite = new Composite(folder, SWT.NONE);
		composite.setLayout(new FormLayout());
		generalItem.setControl(composite);

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.width = 40;
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("名称:");

		nameText = new Text(composite, SWT.BORDER);
		FormData fd_nameText = new FormData();
		fd_nameText.right = new FormAttachment(50);
		fd_nameText.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_nameText.left = new FormAttachment(lblNewLabel, 6);
		nameText.setLayoutData(fd_nameText);

		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.width = 40;
		fd_lblNewLabel_1.top = new FormAttachment(lblNewLabel, 16);
		fd_lblNewLabel_1.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("描述:");

		descText = new Text(composite, SWT.BORDER);
		FormData fd_descText = new FormData();
		fd_descText.right = new FormAttachment(100, -10);
		fd_descText.top = new FormAttachment(lblNewLabel_1, -3, SWT.TOP);
		fd_descText.left = new FormAttachment(nameText, 0, SWT.LEFT);
		descText.setLayoutData(fd_descText);

		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		fd_lblNewLabel_2.width = 60;
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText("备注:");

		noteText = new Text(composite, SWT.BORDER);
		fd_lblNewLabel_2.top = new FormAttachment(noteText, 3, SWT.TOP);
		FormData fd_noteText = new FormData();
		fd_noteText.height = 80;
		fd_noteText.right = new FormAttachment(100, -10);
		fd_noteText.top = new FormAttachment(descText, 10);
		fd_noteText.left = new FormAttachment(nameText, 0, SWT.LEFT);
		noteText.setLayoutData(fd_noteText);

		Label lblNewLabel_3 = new Label(composite, SWT.NONE);
		lblNewLabel_3.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_3 = new FormData();
		fd_lblNewLabel_3.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_lblNewLabel_3.width = 40;
		lblNewLabel_3.setLayoutData(fd_lblNewLabel_3);
		lblNewLabel_3.setText("父表:");

		parentTableText = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		fd_lblNewLabel_3.top = new FormAttachment(parentTableText, 3, SWT.TOP);
		FormData fd_parentTableText = new FormData();
		fd_parentTableText.right = new FormAttachment(50);
		fd_parentTableText.top = new FormAttachment(noteText, 10);
		fd_parentTableText.left = new FormAttachment(nameText, 0, SWT.LEFT);
		parentTableText.setLayoutData(fd_parentTableText);

		Label lblNewLabel_4 = new Label(composite, SWT.NONE);
		lblNewLabel_4.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_4 = new FormData();
		fd_lblNewLabel_4.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		fd_lblNewLabel_4.width = 40;
		lblNewLabel_4.setLayoutData(fd_lblNewLabel_4);
		lblNewLabel_4.setText("子表:");

		childTableText = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		fd_lblNewLabel_4.top = new FormAttachment(childTableText, 3, SWT.TOP);
		FormData fd_childTableText = new FormData();
		fd_childTableText.top = new FormAttachment(parentTableText, 8);
		fd_childTableText.right = new FormAttachment(50);
		fd_childTableText.left = new FormAttachment(nameText, 0, SWT.LEFT);
		childTableText.setLayoutData(fd_childTableText);
	}

	public void setLineGefModel(LineGefModel lineGefModel) {
		this.lineGefModel = lineGefModel;
		if (lineGefModel != null) {
			lineModel = lineGefModel.getDataObject();
		}
	}

	private void initControlData() throws DocumentException,
			CloneNotSupportedException {
		if (lineModel == null) {
			logger.error("传入的LineModel为空，无法初始化数据！");
			setErrorMessage("传入的LineModel为空，无法初始化数据！");
			return;
		}

		TableModel parentTableModel = null;
		TableShortcutModel parentTableShortcutModel = null;
		if(lineGefModel.getTarget() instanceof TableGefModel) {
			parentTableModel = (TableModel) lineGefModel.getTarget()
					.getDataObject();
		} else if(lineGefModel.getTarget() instanceof TableShortcutGefModel) {
			parentTableShortcutModel = (TableShortcutModel) lineGefModel.getTarget()
					.getDataObject();
		}
		
		
		TableModel childTableModel = (TableModel) lineGefModel.getSource()
				.getDataObject();

		nameText.setText(lineModel.getName() == null ? "" : lineModel.getName());
		descText.setText(lineModel.getDesc() == null ? "" : lineModel.getDesc());
		noteText.setText(lineModel.getNote() == null ? "" : lineModel.getNote());
		if(parentTableModel != null) {
			parentTableText.setText(parentTableModel.getTableName());
		} else if(parentTableShortcutModel != null) {
			parentTableText.setText(parentTableShortcutModel.getTargetTableModel().getTableName());
		}
		childTableText.setText(childTableModel.getTableName());

		// 让该表格可以编辑
		String[] columnProperties = new String[] { "parent", "child" };
		joinsTableViewer.setColumnProperties(columnProperties);

		List<ColumnModel> childColumnList = childTableModel.getColumnList();
		List<String> columnNameList = new ArrayList<String>(); // 用于给ComboBoxCellEditor显示
		List<ColumnModel> newColumnList = new ArrayList<ColumnModel>();
		columnNameList.add(DmConstants.JOINS_NONE); // 加入“未匹配”项
		for (ColumnModel columnModel : childColumnList) {
			columnNameList.add(columnModel.getColumnDesc());
			newColumnList.add(columnModel);
		}

		CellEditor[] cellEditors = new CellEditor[2];
		cellEditors[0] = new TextCellEditor(joinsTable);
		cellEditors[1] = new ComboBoxCellEditor(joinsTable,
				columnNameList.toArray(new String[0]));
		joinsTableViewer.setCellEditors(cellEditors);

		ICellModifier modifier = new ColumnCellModify(joinsTableViewer,
				newColumnList);
		joinsTableViewer.setCellModifier(modifier);

		joinsTableViewer.setContentProvider(new ArrayContentProvider());
		joinsTableViewer.setLabelProvider(new KeyToKeyModelProvider());

		List<KeyToKeyModel> keyList = new ArrayList<KeyToKeyModel>();
		List<ColumnModel> parentColumnModelList = null;
		if(parentTableModel != null) {
			parentColumnModelList = parentTableModel.getColumnList();
		} else if(parentTableShortcutModel != null) {
			parentColumnModelList = parentTableShortcutModel.getTargetTableModel().getColumnList();
		}
		
		if(parentColumnModelList != null) {
			for (ColumnModel columnModel : parentColumnModelList) {
				if (columnModel.isPrimaryKey()) {
					KeyToKeyModel keyModel = new KeyToKeyModel();
					keyModel.setPrimaryColumnModel(columnModel);
					
					for (ColumnModel childColumnModel : childColumnList) {
						if (childColumnModel.isForeignKey()) {
							ColumnModel parentColumnModel = ColumnModelFactory
									.getColumnModel(FileModel
											.getFileModelFromObj(childColumnModel),
											childColumnModel
											.getParentTableColumnId());
							if (parentColumnModel != null
									&& parentColumnModel.equals(columnModel)) {
								keyModel.setForeginColumnModel(childColumnModel);
								break;
							}
						}
					}
					
					keyList.add(keyModel);
				}
			}
		}

		joinsTableViewer.setInput(keyList);

		constraintNameText.setText(lineModel.getConstraintName() == null ? ""
				: lineModel.getConstraintName());
		oldConstraintName = constraintNameText.getText().trim();
		
		Map<String, String> map = BizDictManager
				.getDictIdValue(IDmDictConstants.RELATION);
		relationComboViewer.setMap(map);
		relationComboViewer.setSelect(lineModel.getIncidenceRelation());

		initPreviewData();
	}

	private void initPreviewData() {
		DatabaseManager databaseManager = new DatabaseManager();
		TableModel childTableModel = (TableModel) lineGefModel.getSource()
				.getDataObject();
		
		String sqlDropForeignKey = databaseManager.getSqlDropForeignKeyModel(
				childTableModel);
		if(!sqlDropForeignKey.isEmpty()){
			previewText.append(sqlDropForeignKey
					 + DmConstants.FILE_WRAP);
		}
		
		String sqlCreateForeignKeyModel = databaseManager.getSqlCreateForeignKeyModel(
				childTableModel).trim();
		if(!sqlCreateForeignKeyModel.isEmpty()){
			previewText.append(sqlCreateForeignKeyModel  + DmConstants.FILE_WRAP);
		}
		
		System.out.println("测试:" + previewText.getText());
		
		if(!previewText.getText().isEmpty()){
			previewText.setText(previewText.getText().trim());
		}
		
		// 关键字变色
		KeyWordsManager keyWordsManager = new KeyWordsManager();
		List<KeyWords> kewWordsList = keyWordsManager
				.getKeyWordsList(previewText.getText());
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

	@Override
	protected void okPressed() {
		if (!checkData()) {
			return;
		}

		command = new UpdateReferenceCommand();

		command.setLineModel(lineModel);

		command.setNewName(nameText.getText().trim());
		command.setNewDesc(descText.getText().trim());
		command.setNewNote(noteText.getText());

		command.setNewConstraintName(constraintNameText.getText().trim());
		command.setNewRelation((String) relationComboViewer.getSelectKey());
		command.setNewKeyList((List<KeyToKeyModel>) joinsTableViewer.getInput());

		command.setChildTableModel((TableModel) lineGefModel.getSource()
				.getDataObject());
		if(lineGefModel.getTarget() instanceof TableGefModel) {
			command.setParentTableModel((TableModel) lineGefModel.getTarget()
					.getDataObject());
		} else if(lineGefModel.getTarget() instanceof TableShortcutGefModel) {
			command.setParentTableShortcutModel((TableShortcutModel) lineGefModel.getTarget()
					.getDataObject());
		}
		
		command.setLineGefModel(lineGefModel);

		super.okPressed();
	}

	public UpdateReferenceCommand getCommand() {
		return command;
	}

	private void createEvent() {
		nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				checkData();
			}
		});

		descText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				checkData();
			}
		});
		
		constraintNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				checkData();
			}
		});

		folder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (folder.getSelection() == previewItem) {
					 initPreviewData();
				}
				super.widgetSelected(e);
			}
		});

	}

	public static Map<LineModel, ReferencePropertiesDialog> getLineDialogMap() {
		return lineDialogMap;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}
}

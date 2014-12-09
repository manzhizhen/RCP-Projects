/* 文件名：     DataSourceComposite.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-10
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog.composite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.DocumentException;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.KeyValueModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.provider.DataSourceColumnModelLabelProvider;
import cn.sunline.suncard.powerdesigner.provider.TableModelLabelProvider;
import cn.sunline.suncard.powerdesigner.provider.UserDefineTableLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmDictConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.ui.dialog.UserDefineTableCellModifier;
import cn.sunline.suncard.sde.bs.dict.BizDictManager;
import cn.sunline.suncard.sde.bs.dict.DictComboViewer;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 列属性对话框的数据来源标签的Composite
 * 
 * @author Manzhizhen
 * @version 1.0, 2013-1-10
 * @see
 * @since 1.0
 */
public class DataSourceComposite extends Composite implements ISubComposite {
	private ColumnModel columnModel;
	private String flag;
	private LinkedHashMap<String, KeyValueModel> customDataMap = new LinkedHashMap<String, KeyValueModel>();

	private DictComboViewer dataSourceTypeComboViewer;
	private Combo sourceCombo;
	private ComboViewer comboView_dataTableType;
	private Combo combo_dataTableType;
	private Combo comboDatasourceDesc;
	private ComboViewer comboViewerDatasourceDesc;
	private Text txtLimitCondition;
	private Text txtMatchDefaultValue;
	private Button downButton;
	private Button delButton;
	private Button upButton;
	private TableViewer userDefineTableViewer;
	private Button addButton;

	private ComboViewer comboView_dataTable;

	private Combo combo_dataTable;
	private Button btnSingleTable;
	private Button btnMultiTable;
	private Table tableUserDefine;

	public DataSourceComposite(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	@Override
	public void createControl() {
		setLayout(new FormLayout());
		// ----------------------------数据来源---------------------------------
		Label label = new Label(this, SWT.NONE);
		label.setAlignment(SWT.RIGHT);
		FormData fd_label = new FormData();
		fd_label.top = new FormAttachment(0, 10);
		fd_label.left = new FormAttachment(0, 6);
		fd_label.width = 60;
		label.setLayoutData(fd_label);
		label.setText("数据来源:");
		sourceCombo = new Combo(this, SWT.READ_ONLY);
		FormData fd_sourceCombo = new FormData();
		fd_sourceCombo.top = new FormAttachment(label, -3, SWT.TOP);
		fd_sourceCombo.left = new FormAttachment(label, 10);
		fd_sourceCombo.right = new FormAttachment(100, -5);
		sourceCombo.setLayoutData(fd_sourceCombo);
		dataSourceTypeComboViewer = new DictComboViewer(sourceCombo);

		// ------------------表数据源Group--------------------------------------
		Group group = new Group(this, SWT.NONE);
		group.setLayout(new FormLayout());
		FormData fd_group = new FormData();
		fd_group.height = 200;
		fd_group.top = new FormAttachment(label, 10);
		fd_group.left = new FormAttachment(0, 6);
		fd_group.right = new FormAttachment(100, -5);
		group.setLayoutData(fd_group);
		group.setText("表数据源");

		btnSingleTable = new Button(group, SWT.RADIO);
		btnSingleTable.setLayoutData(new FormData());
		btnSingleTable.setText("单表来源");
		FormData fd_btnSingleTable = new FormData();
		fd_btnSingleTable.width = 65;
		fd_btnSingleTable.top = new FormAttachment(0, 7);
		fd_btnSingleTable.left = new FormAttachment(0, 6);
		btnSingleTable.setLayoutData(fd_btnSingleTable);
		btnSingleTable.setEnabled(false);

		btnMultiTable = new Button(group, SWT.RADIO);
		btnMultiTable.setLayoutData(new FormData());
		btnMultiTable.setText("多表来源");
		FormData fd_btnMultiTable = new FormData();
		fd_btnMultiTable.width = 65;
		fd_btnMultiTable.top = new FormAttachment(btnSingleTable, 0, SWT.TOP);
		fd_btnMultiTable.left = new FormAttachment(btnSingleTable, 10);
		btnMultiTable.setLayoutData(fd_btnMultiTable);
		btnMultiTable.setEnabled(false);

		// 表数据源
		Label lblTable = new Label(group, SWT.NONE);
		lblTable.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.width = 65;
		fd_lblNewLabel.top = new FormAttachment(btnSingleTable, 15);
		fd_lblNewLabel.left = new FormAttachment(0, 6);
		lblTable.setLayoutData(fd_lblNewLabel);
		lblTable.setText("表数据源:");

		combo_dataTable = new Combo(group, SWT.READ_ONLY);
		FormData fd_combo_dataTable = new FormData();
		fd_combo_dataTable.right = new FormAttachment(50);
		fd_combo_dataTable.top = new FormAttachment(lblTable, -4, SWT.TOP);
		fd_combo_dataTable.left = new FormAttachment(lblTable, 10);
		combo_dataTable.setLayoutData(fd_combo_dataTable);
		comboView_dataTable = new ComboViewer(combo_dataTable);
		combo_dataTable.setEnabled(false);

		// 数据字段
		Label lblData = new Label(group, SWT.NONE);
		lblData.setAlignment(SWT.RIGHT);
		FormData fd_label_1 = new FormData();
		fd_label_1.width = 65;
		fd_label_1.top = new FormAttachment(lblTable, 0, SWT.TOP);
		fd_label_1.left = new FormAttachment(combo_dataTable, 10);
		lblData.setLayoutData(fd_label_1);
		lblData.setText("数据字段:");

		combo_dataTableType = new Combo(group, SWT.READ_ONLY);
		FormData fd_combo_dataTableType = new FormData();
		fd_combo_dataTableType.right = new FormAttachment(100, -5);
		fd_combo_dataTableType.top = new FormAttachment(combo_dataTable, 0,
				SWT.TOP);
		fd_combo_dataTableType.left = new FormAttachment(lblData, 10);
		combo_dataTableType.setLayoutData(fd_combo_dataTableType);
		comboView_dataTableType = new ComboViewer(combo_dataTableType);
		combo_dataTableType.setEnabled(false);

		// 数据描述字段
		Label lblDataDesc = new Label(group, SWT.NONE);
		lblDataDesc.setAlignment(SWT.RIGHT);
		FormData fd_label_2 = new FormData();
		fd_label_2.top = new FormAttachment(lblData, 17);
		fd_label_2.left = new FormAttachment(lblData, 0, SWT.LEFT);
		fd_label_2.width = 65;
		lblDataDesc.setLayoutData(fd_label_2);
		lblDataDesc.setText("数据描述:");

		comboDatasourceDesc = new Combo(group, SWT.READ_ONLY);
		FormData fd_combo = new FormData();
		fd_combo.right = new FormAttachment(100, -5);
		fd_combo.top = new FormAttachment(lblDataDesc, -4, SWT.TOP);
		fd_combo.left = new FormAttachment(lblDataDesc, 10);
		comboDatasourceDesc.setLayoutData(fd_combo);
		comboDatasourceDesc.setEnabled(false);
		comboViewerDatasourceDesc = new ComboViewer(comboDatasourceDesc);

		// "匹配默认值"标签
		Label lblMatchDefaultValue = new Label(group, SWT.NONE);
		lblMatchDefaultValue.setText("匹配默认值:");
		FormData fd_lblMatchDefaultValue = new FormData();
		fd_lblMatchDefaultValue.top = new FormAttachment(lblDataDesc, 17);
		fd_lblMatchDefaultValue.left = new FormAttachment(lblTable, 0, SWT.LEFT);
		fd_lblMatchDefaultValue.width = 65;
		lblMatchDefaultValue.setLayoutData(fd_lblMatchDefaultValue);

		txtMatchDefaultValue = new Text(group, SWT.BORDER);
		txtMatchDefaultValue.setEnabled(false);
		FormData fd_txtMatchDefaultValue = new FormData();
		fd_txtMatchDefaultValue.right = new FormAttachment(100, -6);
		fd_txtMatchDefaultValue.top = new FormAttachment(lblMatchDefaultValue,
				-4, SWT.TOP);
		fd_txtMatchDefaultValue.left = new FormAttachment(lblMatchDefaultValue,
				10);
		txtMatchDefaultValue.setLayoutData(fd_txtMatchDefaultValue);

		// "限制条件"标签
		Label lblLimit = new Label(group, SWT.NONE);
		lblLimit.setAlignment(SWT.RIGHT);
		lblLimit.setText("限制条件:");
		FormData fd_lblLimit = new FormData();
		fd_lblLimit.top = new FormAttachment(lblMatchDefaultValue, 17);
		fd_lblLimit.left = new FormAttachment(lblTable, 0, SWT.LEFT);
		fd_lblLimit.width = 65;
		lblLimit.setLayoutData(fd_lblLimit);

		txtLimitCondition = new Text(group, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		txtLimitCondition.setEnabled(false);
		FormData fd_txtLimit = new FormData();
		fd_txtLimit.right = new FormAttachment(100, -6);
		fd_txtLimit.top = new FormAttachment(lblLimit, -4, SWT.TOP);
		fd_txtLimit.left = new FormAttachment(lblLimit, 10);
		fd_txtLimit.bottom = new FormAttachment(100, -6);
		txtLimitCondition.setLayoutData(fd_txtLimit);

		// -----------------------自定义数据Group--------------------------------
		Group groupUserDefine = new Group(this, SWT.NONE);
		groupUserDefine.setLayout(new FormLayout());
		FormData fd_groupUserDefine = new FormData();
		fd_groupUserDefine.right = new FormAttachment(group, 0, SWT.RIGHT);
		fd_groupUserDefine.top = new FormAttachment(group, 6);
		fd_groupUserDefine.left = new FormAttachment(0, 6);
		fd_groupUserDefine.bottom = new FormAttachment(100, -6);
		groupUserDefine.setLayoutData(fd_groupUserDefine);
		groupUserDefine.setText("自定义数据:");

		delButton = new Button(groupUserDefine, SWT.NONE);
		FormData fd_delButton = new FormData();
		fd_delButton.width = 30;
		fd_delButton.top = new FormAttachment(0, 0);
		fd_delButton.right = new FormAttachment(100, -10);
		delButton.setLayoutData(fd_delButton);
		delButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_DEL));
		delButton.setToolTipText("删除");
		delButton.setEnabled(false);

		downButton = new Button(groupUserDefine, SWT.NONE);
		FormData fd_downButton = new FormData();
		fd_downButton.width = 30;
		fd_downButton.top = new FormAttachment(delButton, 0, SWT.TOP);
		fd_downButton.right = new FormAttachment(delButton, -6);
		downButton.setLayoutData(fd_downButton);
		downButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_DOWN));
		downButton.setToolTipText("下移一行");
		downButton.setEnabled(false);

		upButton = new Button(groupUserDefine, SWT.NONE);
		FormData fd_upButton = new FormData();
		fd_upButton.width = 30;
		fd_upButton.top = new FormAttachment(delButton, 0, SWT.TOP);
		fd_upButton.right = new FormAttachment(downButton, -6);
		upButton.setLayoutData(fd_upButton);
		upButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_UP));
		upButton.setToolTipText("上移一行");
		upButton.setEnabled(false);

		addButton = new Button(groupUserDefine, SWT.NONE);
		FormData fd_addButton = new FormData();
		fd_addButton.width = 30;
		fd_addButton.top = new FormAttachment(delButton, 0, SWT.TOP);
		fd_addButton.right = new FormAttachment(upButton, -6);
		addButton.setLayoutData(fd_addButton);
		addButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.A_NEW_IMAGE));
		addButton.setToolTipText("插入一行");
		addButton.setEnabled(false);

		userDefineTableViewer = new TableViewer(groupUserDefine, SWT.BORDER
				| SWT.FULL_SELECTION);
		tableUserDefine = userDefineTableViewer.getTable();
		FormData fd_tableUserDefine = new FormData();
		fd_tableUserDefine.top = new FormAttachment(delButton, 6);
		fd_tableUserDefine.left = new FormAttachment(0, 6);
		fd_tableUserDefine.bottom = new FormAttachment(100, -6);
		fd_tableUserDefine.right = new FormAttachment(100, -6);
		tableUserDefine.setLayoutData(fd_tableUserDefine);
		tableUserDefine.setTouchEnabled(true);
		tableUserDefine.setLinesVisible(true);
		tableUserDefine.setHeaderVisible(true);
		tableUserDefine.setEnabled(false);

		// 序号显示列
		TableViewerColumn tvColumnId = new TableViewerColumn(userDefineTableViewer,
				SWT.NONE);
		TableColumn columnId = tvColumnId.getColumn();
		columnId.setWidth(50);
		columnId.setText("");

		// 键ID列
		TableViewerColumn tvcKey = new TableViewerColumn(userDefineTableViewer, SWT.NONE);
		TableColumn columnKey = tvcKey.getColumn();
		columnKey.setWidth(250);
		columnKey.setText("键ID");

		// 键值列
		TableViewerColumn tvcValue = new TableViewerColumn(userDefineTableViewer,
				SWT.NONE);
		TableColumn columnValue = tvcValue.getColumn();
		columnValue.setWidth(250);
		columnValue.setText("键值");

		userDefineTableViewer.setContentProvider(new ArrayContentProvider());
		userDefineTableViewer.setLabelProvider(new UserDefineTableLabelProvider(
				customDataMap));

		// 让该表格可以编辑
		String[] columnProperties = new String[] {
				DmConstants.COLUMN_PROPERTY_INDEX,
				DmConstants.COLUMN_PROPERTY_KEY,
				DmConstants.COLUMN_PROPERTY_VALUE };
		userDefineTableViewer.setColumnProperties(columnProperties);

		CellEditor[] cellEditors = new CellEditor[] {
				new TextCellEditor(tableUserDefine),
				new TextCellEditor(tableUserDefine),
				new TextCellEditor(tableUserDefine) };
		userDefineTableViewer.setCellEditors(cellEditors);
		userDefineTableViewer.setCellModifier(new UserDefineTableCellModifier(
				userDefineTableViewer));
	}

	@Override
	public void initControlData() throws DocumentException {
		// 通过列模型,获取物理数据模型
		PhysicalDataModel physicalDataModel = columnModel.getTableModel()
				.getPhysicalDiagramModel().getPackageModel()
				.getPhysicalDataModel();

		if (!columnModel.isDomainColumnModel()) {
			Map<String, String> map = BizDictManager
					.getDictIdValue(IDmDictConstants.DATASOURCE_TYPE);
			dataSourceTypeComboViewer.setMap(map);

			// 表数据源
			comboView_dataTable.setLabelProvider(new TableModelLabelProvider());
			comboView_dataTable.setContentProvider(new ArrayContentProvider());
			// 列数据源
			comboView_dataTableType
					.setLabelProvider(new DataSourceColumnModelLabelProvider());
			comboView_dataTableType
					.setContentProvider(new ArrayContentProvider());

			// 列描述来源
			comboViewerDatasourceDesc
					.setLabelProvider(new DataSourceColumnModelLabelProvider());
			comboViewerDatasourceDesc
					.setContentProvider(new ArrayContentProvider());

			// 通过得到物理数据模型遍历得到所有表模型
			Set<TableModel> allTableModel = physicalDataModel.getAllTables();
			String selfTableName = columnModel.getTableModel().getTableName();
			TableModel needTableModel = null;
			for (TableModel tableModel : allTableModel) {
				// 不要把自己的表格也添加进来了
				if (!tableModel.getTableName().equals(selfTableName)) {
					needTableModel = tableModel;
					break;
				}
			}
			if (needTableModel != null) {
				allTableModel.remove(needTableModel);
			}
			comboView_dataTable.setInput(allTableModel);
		}

		if (DmConstants.COMMAND_MODIFY.equals(flag)) {
			if (!columnModel.isDomainColumnModel()) {
				String sourceType = columnModel.getDataSourceType() == null ? DmConstants.NULL_DATA_SOURCE
						: columnModel.getDataSourceType().trim();
				if (sourceType.trim().isEmpty()) {
					sourceType = DmConstants.NULL_DATA_SOURCE;
				}
				dataSourceTypeComboViewer.setSelect(sourceType);

				// 如果是表数据源，则需要找到对应的列
				if (DmConstants.TABLE_DATA_SOURCE
						.equals(dataSourceTypeComboViewer.getSelectKey())) {

					dataSourceChanged();

					// 赋值"单表来源"和"多表来源"单选按钮
					if (columnModel.isSingleTableSource()) {
						btnSingleTable.setSelection(true);
						btnMultiTable.setSelection(false);
						txtMatchDefaultValue.setEnabled(false);
						txtLimitCondition.setEnabled(false);
					} else {
						btnSingleTable.setSelection(false);
						btnMultiTable.setSelection(true);
						combo_dataTable.setEnabled(false);
						combo_dataTableType.setEnabled(false);
						comboDatasourceDesc.setEnabled(false);
					}

					// 如果是多表来源,则赋值匹配默认值及限制添加控件
					if (btnMultiTable.getSelection()) {
						txtMatchDefaultValue.setText(columnModel
								.getMatchDefaultValue());
						txtLimitCondition.setText(columnModel
								.getLimitCondition());
					}

					String columnId = columnModel.getDataSourceContent().trim();
					ColumnModel dataColumnModel = ColumnModelFactory
							.getColumnModel(
									FileModel.getFileModelFromObj(columnModel),
									columnId);
					if (dataColumnModel != null) {
						comboView_dataTable
								.setSelection(new StructuredSelection(
										dataColumnModel.getTableModel()));
						// 更新列数据源的数据
						tableDataSourceChanged();

						comboView_dataTableType
								.setSelection(new StructuredSelection(
										dataColumnModel));

					}

					// 赋值列描述来源
					String columnDatasourceDescId = columnModel
							.getDataSourceDescContent().trim();
					ColumnModel datasourdeDescColumnModel = ColumnModelFactory
							.getColumnModel(
									FileModel.getFileModelFromObj(columnModel),
									columnDatasourceDescId);

					if (datasourdeDescColumnModel != null) {
						comboViewerDatasourceDesc
								.setSelection(new StructuredSelection(
										datasourdeDescColumnModel));
					}

					// 如果是自定义数据
				} else if (DmConstants.CUSTOM_DATA_SOURCE
						.equals(dataSourceTypeComboViewer.getSelectKey())) {
					
					Set<String> keySet = columnModel.getCustomDataMap().keySet();
					for(String keyStr : keySet) {
						KeyValueModel keyValueModel = new KeyValueModel();
						keyValueModel.setKey(keyStr);
						keyValueModel.setValue(columnModel.getCustomDataMap().get(keyStr));
						customDataMap.put(keyStr, keyValueModel);
					}
					
					List<KeyValueModel> keyValueModels = new ArrayList<KeyValueModel>();
					Collection<KeyValueModel> list = (Collection<KeyValueModel>) customDataMap
							.values();
					for (KeyValueModel keyValueModel : list) {
						keyValueModels.add(keyValueModel);
					}
					userDefineTableViewer.setInput(keyValueModels);
				}
				
				dataSourceChanged();
			}
		}
	}

	@Override
	public void createControlEvent() {
		// 不是Domains键才会创建下面的事件
		if (columnModel.isDomainColumnModel()) {
			return;
		}

		dataSourceTypeComboViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						dataSourceChanged();
						checkData();
					}
				});

		comboView_dataTable
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						tableDataSourceChanged();
						checkData();
					}
				});

		// 单表来源单选按钮侦听器
		btnSingleTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnSingleTable.getSelection()) {
					combo_dataTable.setEnabled(true);
					combo_dataTableType.setEnabled(true);
					comboDatasourceDesc.setEnabled(true);
					txtMatchDefaultValue.setEnabled(false);
					txtLimitCondition.setEnabled(false);
				}
			}
		});

		// 单表来源单选按钮侦听器
		btnMultiTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnMultiTable.getSelection()) {
					combo_dataTable.setEnabled(false);
					combo_dataTableType.setEnabled(false);
					comboDatasourceDesc.setEnabled(false);
					txtMatchDefaultValue.setEnabled(true);
					txtLimitCondition.setEnabled(true);
				}
			}
		});

		// 插入按钮侦听器
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addButtonEvent();
			}
		});

		// 删除按钮侦听器
		delButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				delButtonEvent();
			}
		});

		// 上移按钮侦听器
		upButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveButtonEvent(true);
			}
		});

		// 下移按钮侦听器
		downButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveButtonEvent(false);
			}
		});
	}

	@Override
	public String checkData() {
		if (!columnModel.isDomainColumnModel()) {
			if (DmConstants.TABLE_DATA_SOURCE.equals(dataSourceTypeComboViewer
					.getSelectKey())) {

				if (btnSingleTable.getSelection()) {
					IStructuredSelection select = (IStructuredSelection) comboView_dataTableType
							.getSelection();
					if (select.isEmpty()) {
						return "列数据源不能为空！";
					}

					IStructuredSelection selectDefualtValue = (IStructuredSelection) comboViewerDatasourceDesc
							.getSelection();
					if (selectDefualtValue.isEmpty()) {
						return "列数据描述来源不能为空！";
					}
				}

				if (btnMultiTable.getSelection()) {
					if (txtMatchDefaultValue.getText().trim().isEmpty()) {
						return "默认匹配值不能为空！";
					}

					if (txtLimitCondition.getText().trim().isEmpty()) {
						return "限制条件不能为空！";
					}
				}

			} else if (DmConstants.CUSTOM_DATA_SOURCE
					.equals(dataSourceTypeComboViewer.getSelectKey())) {
				if (tableUserDefine.getColumnCount() <= 0) {
					return "自定义数据不能为空！";
				}
			}
		}

		return null;
	}

	/**
	 * 当数据源类型变化时需要刷新控件
	 */
	private void dataSourceChanged() {
		if (DmConstants.TABLE_DATA_SOURCE.equals(dataSourceTypeComboViewer
				.getSelectKey())) {
			setTableDataSourcGroupeWidgetEnable(true);
			setUserDefineGroupWidgetEnable(false);

		} else if (DmConstants.CUSTOM_DATA_SOURCE
				.equals(dataSourceTypeComboViewer.getSelectKey())) {
			setTableDataSourcGroupeWidgetEnable(false);
			setUserDefineGroupWidgetEnable(true);

		} else if (DmConstants.NULL_DATA_SOURCE
				.equals(dataSourceTypeComboViewer.getSelectKey())) {
			setTableDataSourcGroupeWidgetEnable(false);
			setUserDefineGroupWidgetEnable(false);
		}
	}

	/**
	 * 设置表数据源group控件的可用性
	 * 
	 * @param enable
	 *            true=控件可用;false=控件不可用
	 */
	private void setTableDataSourcGroupeWidgetEnable(boolean enabled) {
		btnSingleTable.setEnabled(enabled);
		btnMultiTable.setEnabled(enabled);

		combo_dataTable.setEnabled(enabled);
		combo_dataTableType.setEnabled(enabled);
		comboDatasourceDesc.setEnabled(enabled);

		txtMatchDefaultValue.setEnabled(enabled);
		txtLimitCondition.setEnabled(enabled);
	}

	/**
	 * 设置自定义数据源group控件的可用性
	 * 
	 * @param enable
	 *            true=控件可用;false=控件不可用
	 */
	private void setUserDefineGroupWidgetEnable(boolean enabled) {
		addButton.setEnabled(enabled);
		delButton.setEnabled(enabled);
		upButton.setEnabled(enabled);
		downButton.setEnabled(enabled);

		tableUserDefine.setEnabled(enabled);
	}

	/**
	 * 当表数据源类型变化时需要刷新列数据源
	 */
	private void tableDataSourceChanged() {
		// 得到选中的model的方法
		IStructuredSelection dataSourceselection = (IStructuredSelection) comboView_dataTable
				.getSelection();
		TableModel dataSourceTableModel = (TableModel) dataSourceselection
				.getFirstElement();

		comboView_dataTableType.setInput(dataSourceTableModel.getColumnList());
		comboViewerDatasourceDesc
				.setInput(dataSourceTableModel.getColumnList());

		// combo_dataTableType.setEnabled(true);
	}

	/**
	 * 插入一个自定义数据
	 */
	private void addButtonEvent() {
		int index = 1;

		KeyValueModel keyValueModel = new KeyValueModel();
		while (true) {
			if (!customDataMap.keySet().contains("key" + index)) {
				break;
			}
			index++;
		}

		keyValueModel.setKey("key" + index);
		keyValueModel.setValue("value" + index);
		customDataMap.put((String) keyValueModel.getKey(), keyValueModel);

		List<KeyValueModel> customDataMapList = Arrays.asList(customDataMap
				.values().toArray(new KeyValueModel[] {}));
		userDefineTableViewer.setInput(customDataMapList);
	}

	/**
	 * 删除自定义数据
	 */
	private void delButtonEvent() {
		List<KeyValueModel> list = getTableSelection();
		if (list == null) {
			return;
		}

		List<KeyValueModel> keyValueModels = (List<KeyValueModel>) userDefineTableViewer
				.getInput();

		customDataMap.clear();
		for (KeyValueModel keyValueModel : keyValueModels) {
			customDataMap.put((String) keyValueModel.getKey(), keyValueModel);
		}

		for (KeyValueModel keyValueModel : list) {
			customDataMap.remove(keyValueModel.getKey());
		}

		userDefineTableViewer.setInput(Arrays.asList(customDataMap.values().toArray(
				new KeyValueModel[] {})));
	}

	/**
	 * 创建移动按钮事件
	 * 
	 * @param boolean 如果为ture，表示上移，false表示下移
	 */
	private void moveButtonEvent(boolean isUpMove) {
		List<KeyValueModel> list = getTableSelection();
		if (list == null) {
			return;
		}

		List<KeyValueModel> keyValueModelList = (List<KeyValueModel>) userDefineTableViewer
				.getInput();

		Map<Integer, KeyValueModel> sortMap = new HashMap<Integer, KeyValueModel>();// 对上移顺序做个排序，先移动行号小的
		for (KeyValueModel key : list) {
			sortMap.put(new Integer(keyValueModelList.indexOf(key)), key);
		}

		if (isUpMove) {
			for (int i = 0; i < keyValueModelList.size(); i++) {
				Object obj = sortMap.get(new Integer(i));

				if (obj == null) {
					continue;
				} else if (i == 0) {
					// 最小的行号为0，无法上移
					break;
				}

				// 把该元素和前面的元素交换位置
				keyValueModelList.set(i, keyValueModelList.get(i - 1));
				keyValueModelList.set(i - 1, (KeyValueModel) obj);
			}
		} else {
			for (int i = keyValueModelList.size() - 1; i > -1; i--) {
				Object obj = sortMap.get(new Integer(i));

				if (obj == null) {
					continue;
				} else if (i == keyValueModelList.size() - 1) {
					// 最大的行号为columnModelList.size() - 1，无法下移
					break;
				}

				// 把该元素和前面的元素交换位置
				keyValueModelList.set(i, keyValueModelList.get(i + 1));
				keyValueModelList.set(i + 1, (KeyValueModel) obj);
			}
		}

		userDefineTableViewer.setInput(keyValueModelList);
		userDefineTableViewer.setSelection(new StructuredSelection(list));

	}

	/**
	 * 获取表格所选择的行
	 * 
	 * @return
	 */
	private List<KeyValueModel> getTableSelection() {
		IStructuredSelection select = (IStructuredSelection) userDefineTableViewer
				.getSelection();

		if (select.isEmpty()) {
			return null;
		}

		List<KeyValueModel> list = select.toList();

		return list;
	}

	public void setColumnModel(ColumnModel columnModel) {
		this.columnModel = columnModel;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public DictComboViewer getDataSourceTypeComboViewer() {
		return dataSourceTypeComboViewer;
	}

	public TableViewer getUserDefineTableViewer() {
		return userDefineTableViewer;
	}

}

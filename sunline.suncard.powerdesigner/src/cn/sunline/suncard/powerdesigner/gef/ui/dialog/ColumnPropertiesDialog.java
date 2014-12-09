/* 文件名：     ColumnPropertiesDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-10-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.ui.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.DocumentException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.powerdesigner.db.DatabaseManager;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.KeyValueModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.db.DataTypeModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.provider.DataSourceColumnModelLabelProvider;
import cn.sunline.suncard.powerdesigner.provider.DataTypeLabelProvider;
import cn.sunline.suncard.powerdesigner.provider.TableModelLabelProvider;
import cn.sunline.suncard.powerdesigner.provider.UserDefineTableLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmDictConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.ui.dialog.UserDefineTableCellModifier;
import cn.sunline.suncard.powerdesigner.ui.dialog.WindowsManagerDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.composite.DataFormatComposite;
import cn.sunline.suncard.powerdesigner.ui.dialog.composite.DataSourceComposite;
import cn.sunline.suncard.sde.bs.dict.BizDictManager;
import cn.sunline.suncard.sde.bs.dict.DictComboViewer;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 列属性对话框
 * 
 * @author Agree
 * @version 1.0, 2012-10-26
 * @see
 * @since 1.0
 */
public class ColumnPropertiesDialog extends TitleAreaDialog {
	private CTabFolder folder; // 文件夹选项卡
	private CTabItem generalItem; // Table属性表情
	private CTabItem detailItem;
	private CTabItem standardChecksItem;
	private CTabItem dataSourceItem;
	private Composite composite;

	// 属性
	// general标签
	private Label generalNameLabel;
	private Text nameText;
	private Text descText;
	private Text noteText;
	private Label generalCommentLabel;
	private Label generalCodeLabel;
	private Label generalTableLabel;
	private Text generalTableText;
	private Label generalDatatypeLabel;
	private Combo dataTypeCombo;
	private Label generalLengthLabel;
	private Text lengthText;
	private Label generalPrecisionLabel;
	private Text precisionText;
	// detail标签
	private Composite detailComposite;
	private Group groupColumnFillParameter;
	private Label detailNullLabel;
	private Combo detailNullCombo;
	private Label detailDistinctLabel;
	private Combo detailDistinctCombo;
	private Label detailAverageLabel;
	private Text detailAverageText;
	private Group groupTestDataParameters;
	private Label detaiTestDataParametersLabel;
	private Combo detailTestDataParametersCombo;
	private Composite standardChecksComposite;
	private Group groupValuesParameter;
	// standard标签
	private Label StandardChecksMinimumLabel;
	private Text minText;
	private Label StandardChecksMaxmumLabel;
	private Text maxText;
	private Label StandardChecksDefaultLabel;
	private Combo defaultCombo;

	private Button btnSingleTable;
	private Button btnMultiTable;

	private Button btnSystemDate;
	private Button btnSystemTime;
	private Button btnSystemUser;
	private Button btnSystemOrgId;
	private Button btnUserDefine;

	private Text txtLimitCondition;
	private Text txtMatchDefaultValue;

	private ColumnModel columnModel;
	
	private ComboViewer dataTypeComboView;
	private ComboViewer defaultComboView;

	private Text txtInitValue;
	private Text txtDefualtValue;
	private ComboViewer comboView_dataTableType;
	private ComboViewer comboViewerDatasourceDesc;
	private Label lblNewLabel_4;
	private Text minLengthText;
	private Group group_1;
	private Group group_3;
	private Button canMinButton;
	private Button canNotMinButton;
	private Group group_4;
	private Button canNotNumButton;
	private Button canNumButton;
	private Button canMaxButton;
	private Button canNotMaxButton;
	private Label lblNewLabel_2;
	private ComboViewer domainComboViewer;
	private Combo domainCombo;

	private String flag;

	// 一个ColumnModel ID只能打开一个列属性对话框
	private List<DataTypeModel> dataTypeModelList;
	private CTabItem dataFormatItem;
	
	private DataSourceComposite dataSourceComposite; // 数据来源标签的Composite
	private DataFormatComposite dataFormatComposite; // 数据格式
	
	private static Map<String, ColumnPropertiesDialog> columnDialogMap = new HashMap<String, ColumnPropertiesDialog>();
	private Log logger = LogManager.getLogger(ColumnPropertiesDialog.class
			.getName());

	public ColumnPropertiesDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.BORDER | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}

	@Override
	protected void configureShell(Shell newShell) {
		// 列属性对话框
		newShell.setText(I18nUtil.getMessage("COLUMN_DIALOG"));
		setDefaultImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.COLUMN_ITEM_16));
		super.configureShell(newShell);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(650, 642);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// 列属性设置
		setTitle(I18nUtil.getMessage("COLUMN_SET"));
		setMessage(I18nUtil.getMessage("COLUMN_SET"));
		setTitleImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.COLUMN_ITEM_64));

		Control control = super.createDialogArea(parent);

		composite = new Composite((Composite) control, SWT.None);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		composite.setLayout(new FormLayout());

		createControl();
		try {
			initControlValue();
			createEvent();
		} catch (DocumentException e) {
			setErrorMessage("读取业务字典错误，初始化数据源的Combos失败！" + e.getMessage());
			logger.error("读取业务字典错误，初始化数据源的Combos失败！" + e.getMessage());
			e.printStackTrace();
		}

		return control;
	}

	private void initControlValue() throws DocumentException {
		if (columnModel == null) {
			logger.error("传入的ColumnModel为空，初始化控件失败！");
			return;
		}

		/**
		 * ----------------初始化"常规"标签界面------------------------------
		 */
		dataTypeModelList = new ArrayList<DataTypeModel>();
		DataTypeModel undefinedDataTypeModel = new DataTypeModel();
		undefinedDataTypeModel.setName(DmConstants.UNDEFINED);
		undefinedDataTypeModel.setType(DmConstants.UNDEFINED);
		dataTypeModelList.add(undefinedDataTypeModel);
		dataTypeModelList.addAll(DatabaseManager.getDataTypeList(columnModel
				.getTableModel().getPhysicalDiagramModel().getPackageModel()
				.getPhysicalDataModel().getDatabaseTypeModel()));

		dataTypeComboView.setContentProvider(new ArrayContentProvider());
		dataTypeComboView.setLabelProvider(new DataTypeLabelProvider());
		dataTypeComboView.setInput(dataTypeModelList);

		// 通过列模型,获取物理图形模型
		PhysicalDataModel tempPhysicalDataModel = columnModel.getTableModel()
				.getPhysicalDiagramModel().getPackageModel()
				.getPhysicalDataModel();

		// 初始化Domains下拉列表
		List<Object> domainColumnModelList = new ArrayList<Object>();
		// 不是公共列对象才能给Domians赋值
		if (!columnModel.isDomainColumnModel()) {
			domainComboViewer.setContentProvider(new ArrayContentProvider());
			domainComboViewer
					.setLabelProvider(new DataSourceColumnModelLabelProvider());
			// 加一个空的对象
			domainColumnModelList.add(DmConstants.NONE);
			domainColumnModelList.addAll(tempPhysicalDataModel.getDomainList());
			domainComboViewer.setInput(domainColumnModelList);
		}

		// 赋值系统默认值的Group
		String systemDefaultValueType = columnModel.getSystemDefaultValueType();
		// 赋值单选按钮
		if (DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_C
				.equalsIgnoreCase(systemDefaultValueType)) {
			btnUserDefine.setSelection(true);
		} else if (DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_D
				.equalsIgnoreCase(systemDefaultValueType)) {
			btnSystemDate.setSelection(true);
		} else if (DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_O
				.equalsIgnoreCase(systemDefaultValueType)) {
			btnSystemOrgId.setSelection(true);
		} else if (DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_T
				.equalsIgnoreCase(systemDefaultValueType)) {
			btnSystemTime.setSelection(true);
		} else if (DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_U
				.equalsIgnoreCase(systemDefaultValueType)) {
			btnSystemUser.setSelection(true);
		}
		// 选中自定义数据时,赋值"初始化默认值"控件
		if (!btnUserDefine.getSelection()) {
			txtDefualtValue.setEditable(false);
		}

		// 赋值初始化默认值文本框
		txtInitValue.setText(columnModel.getInitDefaultValue() == null ? ""
				: columnModel.getInitDefaultValue());
		// 赋值系统默认值文本框
		txtDefualtValue
				.setText(columnModel.getSystemDefaultValue() == null ? ""
						: columnModel.getSystemDefaultValue());
		
		if (DmConstants.COMMAND_MODIFY.equals(flag)) {
			// 修改列对话框名称
			getShell().setText(
					I18nUtil.getMessage("COLUMN_DIALOG") + " - "
							+ columnModel.getColumnDesc());
	
			nameText.setText(columnModel.getColumnName() == null ? ""
					: columnModel.getColumnName());
			descText.setText(columnModel.getColumnDesc() == null ? ""
					: columnModel.getColumnDesc());
			noteText.setText(columnModel.getColumnNote() == null ? ""
					: columnModel.getColumnNote());
			generalTableText
					.setText(columnModel.getTableModel().getTableDesc());
	
			dataTypeComboView.setSelection(new StructuredSelection(columnModel
					.getDataTypeModel()));
	
			lengthText
					.setText(columnModel.getDataTypeModel().getLength() == -1 ? ""
							: columnModel.getDataTypeModel().getLength() + "");
	
			precisionText
					.setText(columnModel.getDataTypeModel().getPrecision() == -1 ? ""
							: columnModel.getDataTypeModel().getPrecision()
									+ "");
			
			// 如果该列对象引用了公共列对象，相关控件的可编辑状态需要设置
			if (columnModel.isRefDomainColumnModel()) {
				for (Object tempColumnModel : domainColumnModelList) {
					if (tempColumnModel instanceof ColumnModel) {
						ColumnModel dColumnModel = (ColumnModel) tempColumnModel;
						if (columnModel.getDomainId().equals(
								dColumnModel.getId())) {
							domainComboViewer
									.setSelection(new StructuredSelection(
											tempColumnModel));
						}
					}
				}

				setDomainState(true);
			}
		}
		

		/**
		 *  -----------------------初始化"标准检查"标签界面-------------------------
		 */
		// 建表语句中的默认值，已被黄总取消
//		defaultComboView.setContentProvider(new ArrayContentProvider());
//		defaultComboView.setInput(DatabaseManager
//				.getStandardChecksDefaultModel(columnModel.getTableModel()
//						.getPhysicalDiagramModel().getPackageModel()
//						.getPhysicalDataModel().getDatabaseTypeModel()));
//			// 设置默认值
//			if (columnModel.getDefaultValue() != null
//					&& !"".equals(columnModel.getDefaultValue().trim())) {
//				defaultComboView.setSelection(new StructuredSelection(
//						columnModel.getDefaultValue().trim()));
//			}

		minText.setText(columnModel.getMinValue() == null ? ""
				: columnModel.getMinValue());
		maxText.setText(columnModel.getMaxValue() == null ? ""
				: columnModel.getMaxValue());
		
		// 设置标准检查中值
		// 能否达到最大边界值
		if (columnModel.isCanGetMaxValue()) {
			canMaxButton.setSelection(true);
			canNotMaxButton.setSelection(false);
		} else {
			canMaxButton.setSelection(false);
			canNotMaxButton.setSelection(true);
		}

		// 能否达到最小边界值
		if (columnModel.isCanGetMinValue()) {
			canMinButton.setSelection(true);
			canNotMinButton.setSelection(false);
		} else {
			canMinButton.setSelection(false);
			canNotMinButton.setSelection(true);
		}

		// 必须是数字
		if (columnModel.isMustNumber()) {
			canNumButton.setSelection(true);
			canNotNumButton.setSelection(false);
		} else {
			canNumButton.setSelection(false);
			canNotNumButton.setSelection(true);
		}
		// 设置字符串长度的最小值
		minLengthText.setText(columnModel.getStrMinLength() + "");
		
		
		/**
		 *  ------------------初始化“数据格式”标签界面-----------------------------
		 */
		dataFormatComposite.setColumnModel(columnModel);
		dataFormatComposite.initControlData();
		
		/**
		 *  ------------------初始化“数据来源”标签界面-----------------------------
		 */
		if (!columnModel.isDomainColumnModel()) {
			dataSourceComposite.setColumnModel(columnModel);
			dataSourceComposite.setFlag(flag);
			dataSourceComposite.initControlData();
		}

		if (columnModel.isDomainColumnModel()) {
			// 给表格名称的文本框写个<无>
			generalTableText.setText(DmConstants.NONE);
		}

	}

	//数据格式与数据来源的按钮事件
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

		lengthText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				checkData();
			}
		});

		precisionText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				checkData();
			}
		});
		
		txtDefualtValue.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				checkData();
			}
		});

		//--------------创建数据格式的事件------------------------
		dataFormatComposite.createControlEvent();
		
		// 不是Domains键才会创建下面的事件
		if (columnModel.isDomainColumnModel()) {
			return;
		}

		domainComboViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						domainComboSelectionChanged();
					}
				});

		// "系统日期"单选按钮侦听器
		btnSystemDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!btnSystemDate.getSelection()) {
					txtDefualtValue.setText(DmConstants.EMPTY_STRING);
					txtInitValue.setText(DmConstants.EMPTY_STRING);
					return;
				}
				
				// 设置其他选择框为false
				btnSystemTime.setSelection(false);
				btnSystemUser.setSelection(false);
				btnSystemOrgId.setSelection(false);
				btnUserDefine.setSelection(false);

				txtDefualtValue.setEnabled(true);
				txtDefualtValue.setEditable(false);
				// txtInitValue.setEnabled(false);

				// 赋值系统日期
				String strDate = Calendar.getInstance().get(Calendar.YEAR)
						+ "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1) 
						+ "-"
						+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				txtDefualtValue.setText(strDate);
			}
		});

		// "系统时间"单选按钮侦听器
		btnSystemTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!btnSystemTime.getSelection()) {
					txtDefualtValue.setText(DmConstants.EMPTY_STRING);
					txtInitValue.setText(DmConstants.EMPTY_STRING);
					return;
				}
				
				// 设置其他选择框为false
				btnSystemDate.setSelection(false);
				btnSystemUser.setSelection(false);
				btnSystemOrgId.setSelection(false);
				btnUserDefine.setSelection(false);

				txtDefualtValue.setEnabled(true);
				txtDefualtValue.setEditable(false);
				// txtInitValue.setEnabled(false);

				// 赋值系统日期
				String strDate = Calendar.getInstance().get(Calendar.YEAR)
						+ "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1) 
						+ "-"
						+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
						+ " "
						+ Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
						+ ":" + Calendar.getInstance().get(Calendar.MINUTE)
						+ ":" + Calendar.getInstance().get(Calendar.SECOND);
				txtDefualtValue.setText(strDate);
			}
		});

		// "登录用户"单选按钮侦听器
		btnSystemUser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!btnSystemUser.getSelection()) {
					txtDefualtValue.setText(DmConstants.EMPTY_STRING);
					txtInitValue.setText(DmConstants.EMPTY_STRING);
					return;
				}
				
				// 设置其他选择框为false
				btnSystemDate.setSelection(false);
				btnSystemTime.setSelection(false);
				btnSystemOrgId.setSelection(false);
				btnUserDefine.setSelection(false);

				txtDefualtValue.setEnabled(true);
				txtDefualtValue.setEditable(false);
				txtDefualtValue.setText("user");
				// txtInitValue.setEnabled(false);
			}
		});

		// "机构代码"单选按钮侦听器
		btnSystemOrgId.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!btnSystemOrgId.getSelection()) {
					txtDefualtValue.setText(DmConstants.EMPTY_STRING);
					txtInitValue.setText(DmConstants.EMPTY_STRING);
					return;
				}
				
				// 设置其他选择框为false
				btnSystemDate.setSelection(false);
				btnSystemTime.setSelection(false);
				btnSystemUser.setSelection(false);
				btnUserDefine.setSelection(false);

				txtDefualtValue.setEnabled(true);
				txtDefualtValue.setEditable(false);
				txtDefualtValue.setText("2001");
				// txtInitValue.setEnabled(false);
			}
		});

		// "自定义数据"单选按钮侦听器
		btnUserDefine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!btnUserDefine.getSelection()) {
					txtDefualtValue.setText(DmConstants.EMPTY_STRING);
					txtInitValue.setText(DmConstants.EMPTY_STRING);
					return;
				}
				
				// 设置其他选择框为false
				btnSystemDate.setSelection(false);
				btnSystemTime.setSelection(false);
				btnSystemUser.setSelection(false);
				btnSystemOrgId.setSelection(false);

				txtDefualtValue.setEnabled(true);
				txtDefualtValue.setEditable(true);
				txtDefualtValue.setText("");
				// txtInitValue.setEnabled(true);
			}
		});
		
		// 创建数据来源标签事件
		if(!columnModel.isDomainColumnModel()) {
			dataSourceComposite.createControlEvent();
		}

	}

	private void createControl() {
		folder = new CTabFolder(composite, SWT.NONE | SWT.BORDER);
		FormData fd_folder = new FormData();
		fd_folder.top = new FormAttachment(0, 10);
		fd_folder.left = new FormAttachment(0, 10);
		fd_folder.bottom = new FormAttachment(100, -5);
		fd_folder.right = new FormAttachment(100, -5);
		folder.setLayoutData(fd_folder);

		// 设置标签栏的高度
		folder.setTabHeight(20);
		folder.marginHeight = 2;
		folder.marginWidth = 2;
		folder.setMaximizeVisible(true);
		folder.setMinimizeVisible(true);

		// 设置圆角
		folder.setSimple(false);

		createGeneralItem();
		// createDetailItem();
		createStandardChecksItem();
		// createMySQLItem();
		// createRulesItem();
		createDataFormatItem();

		// 创建数据来源标签
		if (!columnModel.isDomainColumnModel()) {
			createDatasourceItem();
		}
	}

	/**
	 * 创建general属性标签
	 */
	public void createGeneralItem() {
		// 常规
		generalItem = new CTabItem(folder, SWT.None);
		generalItem.setText(I18nUtil.getMessage("GENERAL"));
		Composite generalComposite = new Composite(folder, SWT.None);
		generalItem.setControl(generalComposite);
		generalComposite.setLayout(new FormLayout());

		// 名字
		generalNameLabel = new Label(generalComposite, SWT.None);
		generalNameLabel.setAlignment(SWT.RIGHT);
		FormData fd_generalNameLabel = new FormData();
		fd_generalNameLabel.width = 60;
		fd_generalNameLabel.top = new FormAttachment(0, 10);
		fd_generalNameLabel.left = new FormAttachment(0, 6);
		generalNameLabel.setLayoutData(fd_generalNameLabel);
		generalNameLabel.setText(I18nUtil.getMessage("NAME") + ":");
		nameText = new Text(generalComposite, SWT.BORDER);
		FormData fd_nameText = new FormData();
		fd_nameText.top = new FormAttachment(generalNameLabel, -3, SWT.TOP);
		fd_nameText.left = new FormAttachment(generalNameLabel, 10);
		fd_nameText.right = new FormAttachment(100, -5);
		nameText.setLayoutData(fd_nameText);

		// 描述
		generalCodeLabel = new Label(generalComposite, SWT.None);
		generalCodeLabel.setAlignment(SWT.RIGHT);
		FormData fd_generalCodeLabel = new FormData();
		fd_generalCodeLabel.width = 60;
		fd_generalCodeLabel.top = new FormAttachment(generalNameLabel, 15);
		fd_generalCodeLabel.left = new FormAttachment(0, 6);
		generalCodeLabel.setLayoutData(fd_generalCodeLabel);
		generalCodeLabel.setText(I18nUtil.getMessage("DESC") + ":");
		descText = new Text(generalComposite, SWT.BORDER);
		FormData fd_descText = new FormData();
		fd_descText.top = new FormAttachment(generalCodeLabel, -3, SWT.TOP);
		fd_descText.left = new FormAttachment(generalCodeLabel, 10);
		fd_descText.right = new FormAttachment(100, -5);
		descText.setLayoutData(fd_descText);

		// 备注
		generalCommentLabel = new Label(generalComposite, SWT.None);
		generalCommentLabel.setAlignment(SWT.RIGHT);
		FormData fd_generalCommentLabel = new FormData();
		fd_generalCommentLabel.top = new FormAttachment(generalCodeLabel, 15);
		fd_generalCommentLabel.width = 60;
		fd_generalCommentLabel.left = new FormAttachment(0, 6);
		generalCommentLabel.setLayoutData(fd_generalCommentLabel);
		generalCommentLabel.setText(I18nUtil.getMessage("NOTE") + ":");
		noteText = new Text(generalComposite, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		FormData fd_noteText = new FormData();
		fd_noteText.top = new FormAttachment(generalCommentLabel, -3, SWT.TOP);
		fd_noteText.left = new FormAttachment(generalCommentLabel, 10);
		fd_noteText.right = new FormAttachment(100, -5);
		fd_noteText.height = 50;
		noteText.setLayoutData(fd_noteText);

		// 表格
		generalTableLabel = new Label(generalComposite, SWT.None);
		generalTableLabel.setAlignment(SWT.RIGHT);
		FormData fd_generalTableLabel = new FormData();
		fd_generalTableLabel.top = new FormAttachment(noteText, 15);
		fd_generalTableLabel.width = 60;
		fd_generalTableLabel.left = new FormAttachment(0, 6);
		generalTableLabel.setLayoutData(fd_generalTableLabel);
		generalTableLabel.setText(I18nUtil.getMessage("TABLE") + ":");
		generalTableText = new Text(generalComposite, SWT.BORDER
				| SWT.READ_ONLY);
		FormData fd_generalTableText = new FormData();
		fd_generalTableText.top = new FormAttachment(generalTableLabel, -3,
				SWT.TOP);
		fd_generalTableText.left = new FormAttachment(generalTableLabel, 10);
		fd_generalTableText.right = new FormAttachment(100, -6);
		generalTableText.setLayoutData(fd_generalTableText);

		// 数据类型
		generalDatatypeLabel = new Label(generalComposite, SWT.None);
		generalDatatypeLabel.setAlignment(SWT.RIGHT);
		FormData fd_generalDatatypeLabel = new FormData();
		fd_generalDatatypeLabel.width = 60;
		fd_generalDatatypeLabel.top = new FormAttachment(generalTableLabel, 15);
		fd_generalDatatypeLabel.left = new FormAttachment(0, 6);
		generalDatatypeLabel.setLayoutData(fd_generalDatatypeLabel);
		generalDatatypeLabel.setText(I18nUtil.getMessage("DATA_TYPE") + ":");
		dataTypeCombo = new Combo(generalComposite, SWT.READ_ONLY);
		dataTypeComboView = new ComboViewer(dataTypeCombo);
		FormData fd_dataTypeCombo = new FormData();
		fd_dataTypeCombo.right = new FormAttachment(50);
		fd_dataTypeCombo.top = new FormAttachment(generalDatatypeLabel, -3,
				SWT.TOP);
		fd_dataTypeCombo.left = new FormAttachment(generalDatatypeLabel, 10);
		// fd_generalDatatypeCombo.right = new FormAttachment(100, -5);
		dataTypeCombo.setLayoutData(fd_dataTypeCombo);

		// 数据长度
		generalLengthLabel = new Label(generalComposite, SWT.None);
		FormData fd_generalLengthLabel = new FormData();
		fd_generalLengthLabel.top = new FormAttachment(generalDatatypeLabel, 0,
				SWT.TOP);
		fd_generalLengthLabel.left = new FormAttachment(dataTypeCombo, 10,
				SWT.RIGHT);
		generalLengthLabel.setLayoutData(fd_generalLengthLabel);
		generalLengthLabel.setText(I18nUtil.getMessage("DATA_LENGTH") + ":");
		lengthText = new Text(generalComposite, SWT.BORDER);
		FormData fd_lengthText = new FormData();
		fd_lengthText.right = new FormAttachment(75);
		fd_lengthText.top = new FormAttachment(generalLengthLabel, -3, SWT.TOP);
		fd_lengthText.left = new FormAttachment(generalLengthLabel, 15);
		lengthText.setLayoutData(fd_lengthText);

		// 数据精度
		generalPrecisionLabel = new Label(generalComposite, SWT.None);
		FormData fd_generalPrecisionLabel = new FormData();
		fd_generalPrecisionLabel.top = new FormAttachment(generalLengthLabel,
				0, SWT.TOP);
		fd_generalPrecisionLabel.left = new FormAttachment(lengthText, 10,
				SWT.RIGHT);
		generalPrecisionLabel.setLayoutData(fd_generalPrecisionLabel);
		generalPrecisionLabel.setText(I18nUtil.getMessage("DATA_PRECISION")
				+ ":");
		precisionText = new Text(generalComposite, SWT.BORDER);
		FormData fd_precisionText = new FormData();
		fd_precisionText.right = new FormAttachment(100, -6);
		fd_precisionText.top = new FormAttachment(generalPrecisionLabel, -3,
				SWT.TOP);
		fd_precisionText.left = new FormAttachment(generalPrecisionLabel, 15);
		precisionText.setLayoutData(fd_precisionText);

		// 是公共主键才会显示下面的控件
		if (!columnModel.isDomainColumnModel()) {
			// ---------------------------Domains----------------------------------
			lblNewLabel_2 = new Label(generalComposite, SWT.NONE);
			lblNewLabel_2.setAlignment(SWT.RIGHT);
			FormData fd_lblNewLabel_2 = new FormData();

			fd_lblNewLabel_2.left = new FormAttachment(0, 6);
			fd_lblNewLabel_2.width = 60;
			fd_lblNewLabel_2.top = new FormAttachment(generalDatatypeLabel, 15);
			fd_lblNewLabel_2.right = new FormAttachment(generalNameLabel, 0,
					SWT.RIGHT);
			lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
			lblNewLabel_2.setText("Domain:");
			domainComboViewer = new ComboViewer(generalComposite, SWT.NONE
					| SWT.READ_ONLY);
			domainCombo = domainComboViewer.getCombo();
			FormData fd_domainCombo = new FormData();

			fd_domainCombo.top = new FormAttachment(lblNewLabel_2, -3, SWT.TOP);
			fd_domainCombo.left = new FormAttachment(nameText, 0, SWT.LEFT);
			fd_domainCombo.right = new FormAttachment(dataTypeCombo, 0,
					SWT.RIGHT);
			domainCombo.setLayoutData(fd_domainCombo);
		}

		// ----------------------系统默认值Group--------------------------------
		Group groupDefualtValue = new Group(generalComposite, SWT.NONE);
		groupDefualtValue.setText("系统默认值");
		FormData fd_groupDefualtValue = new FormData();
		fd_groupDefualtValue.height = 100;
		if (!columnModel.isDomainColumnModel()) {
			fd_groupDefualtValue.top = new FormAttachment(domainCombo, 10);
		} else {
			fd_groupDefualtValue.top = new FormAttachment(precisionText, 10);
		}
		fd_groupDefualtValue.right = new FormAttachment(100, -6);
		fd_groupDefualtValue.left = new FormAttachment(0, 6);
		// fd_groupDefualtValue.bottom = new FormAttachment(100, -6);
		groupDefualtValue.setLayoutData(fd_groupDefualtValue);
		groupDefualtValue.setLayout(new FormLayout());

		// 系统日期选择按钮
		btnSystemDate = new Button(groupDefualtValue, SWT.CHECK);
		btnSystemDate.setText("系统日期");
		FormData fd_btnSystemDate = new FormData();
		fd_btnSystemDate.top = new FormAttachment(0, 9);
		fd_btnSystemDate.left = new FormAttachment(0, 6);
		btnSystemDate.setLayoutData(fd_btnSystemDate);

		// 系统时间选择按钮
		btnSystemTime = new Button(groupDefualtValue, SWT.CHECK);
		btnSystemTime.setText("系统时间");
		FormData fd_btnSystemTime = new FormData();
		fd_btnSystemTime.top = new FormAttachment(btnSystemDate, 0, SWT.TOP);
		fd_btnSystemTime.left = new FormAttachment(btnSystemDate, 6);
		btnSystemTime.setLayoutData(fd_btnSystemTime);

		// 登录用户选择按钮
		btnSystemUser = new Button(groupDefualtValue, SWT.CHECK);
		btnSystemUser.setText("登录用户");
		FormData fd_btnSystemUser = new FormData();
		fd_btnSystemUser.top = new FormAttachment(btnSystemTime, 0, SWT.TOP);
		fd_btnSystemUser.left = new FormAttachment(btnSystemTime, 6);
		btnSystemUser.setLayoutData(fd_btnSystemUser);

		// 机构代码选择按钮
		btnSystemOrgId = new Button(groupDefualtValue, SWT.CHECK);
		btnSystemOrgId.setText("机构代码");
		FormData fd_btnSystemOrgId = new FormData();
		fd_btnSystemOrgId.top = new FormAttachment(btnSystemUser, 0, SWT.TOP);
		fd_btnSystemOrgId.left = new FormAttachment(btnSystemUser, 6);
		btnSystemOrgId.setLayoutData(fd_btnSystemOrgId);

		// 自定义数据选择按钮
		btnUserDefine = new Button(groupDefualtValue, SWT.CHECK);
		btnUserDefine.setText("自定义数据");
		FormData fd_btnUserDefine = new FormData();
		fd_btnUserDefine.top = new FormAttachment(btnSystemOrgId, 0, SWT.TOP);
		fd_btnUserDefine.left = new FormAttachment(btnSystemOrgId, 6);
		btnUserDefine.setLayoutData(fd_btnUserDefine);

		// 系统默认值标签
		Label lblDefualtValue = new Label(groupDefualtValue, SWT.NONE);
		lblDefualtValue.setAlignment(SWT.RIGHT);
		lblDefualtValue.setText("系统默认值:");
		FormData fd_lblDefualtValue = new FormData();
		fd_lblDefualtValue.width = 80;
		fd_lblDefualtValue.top = new FormAttachment(btnSystemDate, 17);
		fd_lblDefualtValue.left = new FormAttachment(0);
		lblDefualtValue.setLayoutData(fd_lblDefualtValue);

		txtDefualtValue = new Text(groupDefualtValue, SWT.BORDER);
		FormData fd_txtDefaultValue = new FormData();
		fd_txtDefaultValue.right = new FormAttachment(100, -5);
		fd_txtDefaultValue.top = new FormAttachment(lblDefualtValue, -3,
				SWT.TOP);
		fd_txtDefaultValue.left = new FormAttachment(lblDefualtValue, 10);
		txtDefualtValue.setLayoutData(fd_txtDefaultValue);

		// 初始化默认值标签
		Label lblInitValue = new Label(groupDefualtValue, SWT.NONE);
		lblInitValue.setAlignment(SWT.RIGHT);
		lblInitValue.setText("初始化默认值:");
		FormData fd_lblInitValue = new FormData();
		fd_lblInitValue.top = new FormAttachment(lblDefualtValue, 17);
		fd_lblInitValue.left = new FormAttachment(0);
		fd_lblInitValue.right = new FormAttachment(lblDefualtValue, 0,
				SWT.RIGHT);
		lblInitValue.setLayoutData(fd_lblInitValue);

		txtInitValue = new Text(groupDefualtValue, SWT.BORDER);
		FormData fd_txtInitValue = new FormData();
		fd_txtInitValue.right = new FormAttachment(100, -5);
		fd_txtInitValue.top = new FormAttachment(lblInitValue, -3, SWT.TOP);
		fd_txtInitValue.left = new FormAttachment(lblInitValue, 10);
		txtInitValue.setLayoutData(fd_txtInitValue);
	}

	/**
	 * 创造数据格式标签
	 */
	private void createDataFormatItem() {
		dataFormatItem = new CTabItem(folder, SWT.None);
		dataFormatItem.setText("数据格式");
		FormLayout formLayout = new FormLayout();
		dataFormatComposite = new DataFormatComposite(folder, SWT.NONE);
		dataFormatComposite.setLayout(formLayout);
		dataFormatItem.setControl(dataFormatComposite);

	}

	/**
	 * 创建数据来源标签
	 */
	private void createDatasourceItem() {

		dataSourceItem = new CTabItem(folder, SWT.NONE);
		dataSourceItem.setText("数据来源");
		
		dataSourceComposite = new DataSourceComposite(folder, SWT.NONE);
		dataSourceItem.setControl(dataSourceComposite);

	}

	/**
	 * 创建Detail属性标签
	 */
	public void createDetailItem() {
		detailItem = new CTabItem(folder, SWT.None);
		detailItem.setText("细节");

		detailComposite = new Composite(folder, SWT.None);
		FormLayout fl_detailComosite = new FormLayout();
		detailComposite.setLayout(fl_detailComosite);
		detailItem.setControl(detailComposite);

		// 第一组
		groupColumnFillParameter = new Group(detailComposite, SWT.NONE);
		FormData gr_FromData = new FormData();
		gr_FromData.top = new FormAttachment(0, 6);
		gr_FromData.left = new FormAttachment(0, 6);
		gr_FromData.right = new FormAttachment(100, -6);
		gr_FromData.height = 120;
		groupColumnFillParameter.setLayoutData(gr_FromData);
		groupColumnFillParameter.setLayout(new FormLayout());
		groupColumnFillParameter.setText("列填充参数");

		detailNullLabel = new Label(groupColumnFillParameter, SWT.None);
		detailNullLabel.setAlignment(SWT.RIGHT);
		FormData fd_detailNullLabel = new FormData();
		fd_detailNullLabel.width = 60;
		fd_detailNullLabel.top = new FormAttachment(0, 10);
		fd_detailNullLabel.left = new FormAttachment(0, 6);
		detailNullLabel.setLayoutData(fd_detailNullLabel);
		detailNullLabel.setText("空值:");
		detailNullCombo = new Combo(groupColumnFillParameter, SWT.READ_ONLY);
		FormData fd_detailNullCombo = new FormData();
		fd_detailNullCombo.right = new FormAttachment(50);
		fd_detailNullCombo.top = new FormAttachment(detailNullLabel, -3,
				SWT.TOP);
		fd_detailNullCombo.left = new FormAttachment(detailNullLabel, 6);
		detailNullCombo.setLayoutData(fd_detailNullCombo);

		detailDistinctLabel = new Label(groupColumnFillParameter, SWT.None);
		detailDistinctLabel.setAlignment(SWT.RIGHT);
		FormData fd_detailDistinctLabel = new FormData();
		fd_detailDistinctLabel.width = 60;
		fd_detailDistinctLabel.top = new FormAttachment(detailNullLabel, 20);
		fd_detailDistinctLabel.left = new FormAttachment(0, 6);
		detailDistinctLabel.setLayoutData(fd_detailDistinctLabel);
		detailDistinctLabel.setText("不同的值:");
		detailDistinctCombo = new Combo(groupColumnFillParameter, SWT.READ_ONLY);
		FormData fd_detailDistinctCombo = new FormData();
		fd_detailDistinctCombo.right = new FormAttachment(50);
		fd_detailDistinctCombo.left = new FormAttachment(detailDistinctLabel, 6);
		fd_detailDistinctCombo.top = new FormAttachment(detailDistinctLabel,
				-3, SWT.TOP);
		detailDistinctCombo.setLayoutData(fd_detailDistinctCombo);

		detailAverageLabel = new Label(groupColumnFillParameter, SWT.None);
		detailAverageLabel.setAlignment(SWT.RIGHT);
		FormData fd_detailAverageLabel = new FormData();
		fd_detailAverageLabel.width = 60;
		fd_detailAverageLabel.top = new FormAttachment(detailDistinctLabel, 20);
		fd_detailAverageLabel.left = new FormAttachment(0, 6);
		detailAverageLabel.setLayoutData(fd_detailAverageLabel);
		detailAverageLabel.setText("平均长度:");
		detailAverageText = new Text(groupColumnFillParameter, SWT.BORDER);
		FormData fd_detailAverageText = new FormData();
		fd_detailAverageText.right = new FormAttachment(50);
		fd_detailAverageText.top = new FormAttachment(detailAverageLabel, -3,
				SWT.TOP);
		fd_detailAverageText.left = new FormAttachment(detailNullCombo, 0,
				SWT.LEFT);
		detailAverageText.setLayoutData(fd_detailAverageText);

		// 第二组
		groupTestDataParameters = new Group(detailComposite, SWT.NONE);
		groupTestDataParameters.setText("测试数据属性");
		FormData gr_TestFromData = new FormData();
		gr_TestFromData.top = new FormAttachment(groupColumnFillParameter, 10);
		gr_TestFromData.left = new FormAttachment(0, 6);
		gr_TestFromData.right = new FormAttachment(100, -6);
		gr_TestFromData.bottom = new FormAttachment(100, -6);
		groupTestDataParameters.setLayoutData(gr_TestFromData);
		groupTestDataParameters.setLayout(new FormLayout());

		detaiTestDataParametersLabel = new Label(groupTestDataParameters,
				SWT.None);
		detaiTestDataParametersLabel.setAlignment(SWT.RIGHT);
		FormData fd_detaiTestDataParametersLabel = new FormData();
		fd_detaiTestDataParametersLabel.width = 60;
		fd_detaiTestDataParametersLabel.top = new FormAttachment(0, 10);
		fd_detaiTestDataParametersLabel.left = new FormAttachment(0, 6);
		detaiTestDataParametersLabel
				.setLayoutData(fd_detaiTestDataParametersLabel);
		detaiTestDataParametersLabel.setText("外形:");
		detailTestDataParametersCombo = new Combo(groupTestDataParameters,
				SWT.READ_ONLY);
		FormData fd_detailTestDataParametersCombo = new FormData();
		fd_detailTestDataParametersCombo.right = new FormAttachment(50);
		fd_detailTestDataParametersCombo.top = new FormAttachment(
				detaiTestDataParametersLabel, -3, SWT.TOP);
		fd_detailTestDataParametersCombo.left = new FormAttachment(
				detaiTestDataParametersLabel, 6);
		detailTestDataParametersCombo
				.setLayoutData(fd_detailTestDataParametersCombo);
	}

	/**
	 * 创建Standard Checks属性标签
	 */
	public void createStandardChecksItem() {
		standardChecksItem = new CTabItem(folder, SWT.None);
		standardChecksItem.setText("标准检查");

		standardChecksComposite = new Composite(folder, SWT.None);
		FormLayout fl_standardChecksComosite = new FormLayout();
		standardChecksComposite.setLayout(fl_standardChecksComosite);
		standardChecksItem.setControl(standardChecksComposite);

		// 第一组
		groupValuesParameter = new Group(standardChecksComposite, SWT.NONE);
		FormData fd_groupValuesParameter = new FormData();
		fd_groupValuesParameter.height = 110;
		fd_groupValuesParameter.left = new FormAttachment(0, 10);
		fd_groupValuesParameter.top = new FormAttachment(0, 10);
		fd_groupValuesParameter.right = new FormAttachment(50, 0);
		groupValuesParameter.setLayoutData(fd_groupValuesParameter);
		groupValuesParameter.setLayout(new FormLayout());
		groupValuesParameter.setText("值");

		StandardChecksMinimumLabel = new Label(groupValuesParameter, SWT.None);
		FormData fd_StandardChecksMinimumLabel = new FormData();
		fd_StandardChecksMinimumLabel.top = new FormAttachment(
				groupValuesParameter, 10);
		fd_StandardChecksMinimumLabel.left = new FormAttachment(
				groupValuesParameter, 10);
		StandardChecksMinimumLabel.setLayoutData(fd_StandardChecksMinimumLabel);
		StandardChecksMinimumLabel.setText("最小值:");
		minText = new Text(groupValuesParameter, SWT.BORDER);
		FormData fd_minText = new FormData();
		fd_minText.top = new FormAttachment(StandardChecksMinimumLabel, -3,
				SWT.TOP);
		fd_minText.left = new FormAttachment(StandardChecksMinimumLabel, 10);
		fd_minText.right = new FormAttachment(100, -5);
		minText.setLayoutData(fd_minText);

		StandardChecksMaxmumLabel = new Label(groupValuesParameter, SWT.None);
		FormData fd_StandardChecksMaxmumLabel = new FormData();
		fd_StandardChecksMaxmumLabel.top = new FormAttachment(
				StandardChecksMinimumLabel, 15);
		fd_StandardChecksMaxmumLabel.left = new FormAttachment(
				groupValuesParameter, 10);
		StandardChecksMaxmumLabel.setLayoutData(fd_StandardChecksMaxmumLabel);
		StandardChecksMaxmumLabel.setText("最大值:");
		maxText = new Text(groupValuesParameter, SWT.BORDER);
		FormData fd_maxText = new FormData();
		fd_maxText.top = new FormAttachment(StandardChecksMaxmumLabel, -3,
				SWT.TOP);
		fd_maxText.left = new FormAttachment(StandardChecksMaxmumLabel, 10);
		fd_maxText.right = new FormAttachment(100, -5);
		maxText.setLayoutData(fd_maxText);

		StandardChecksDefaultLabel = new Label(groupValuesParameter, SWT.None);
		FormData fd_StandardChecksDefaultLabel = new FormData();
		fd_StandardChecksDefaultLabel.top = new FormAttachment(
				StandardChecksMaxmumLabel, 15);
		fd_StandardChecksDefaultLabel.left = new FormAttachment(
				groupValuesParameter, 10);
		StandardChecksDefaultLabel.setLayoutData(fd_StandardChecksDefaultLabel);
		StandardChecksDefaultLabel.setText("默认值:");
		StandardChecksDefaultLabel.setVisible(false);
		defaultCombo = new Combo(groupValuesParameter, SWT.READ_ONLY);
		defaultComboView = new ComboViewer(defaultCombo);
		FormData fd_defaultCombo = new FormData();
		fd_defaultCombo.top = new FormAttachment(StandardChecksDefaultLabel,
				-3, SWT.TOP);
		fd_defaultCombo.left = new FormAttachment(StandardChecksDefaultLabel,
				10);
		fd_defaultCombo.right = new FormAttachment(100, -5);
		defaultCombo.setLayoutData(fd_defaultCombo);
		defaultCombo.setVisible(false);

		Group otherGroup = new Group(standardChecksComposite, SWT.NONE);
		otherGroup.setText("其它");
		otherGroup.setLayout(new FormLayout());
		FormData fd_otherGroup = new FormData();
		fd_otherGroup.height = 210;
		fd_otherGroup.right = new FormAttachment(100, -10);
		fd_otherGroup.top = new FormAttachment(0, 10);
		fd_otherGroup.left = new FormAttachment(groupValuesParameter, 6);
		otherGroup.setLayoutData(fd_otherGroup);

		lblNewLabel_4 = new Label(otherGroup, SWT.NONE);
		lblNewLabel_4.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_4 = new FormData();
		fd_lblNewLabel_4.width = 95;
		lblNewLabel_4.setLayoutData(fd_lblNewLabel_4);
		lblNewLabel_4.setText("字符串最小长度:");

		minLengthText = new Text(otherGroup, SWT.BORDER);
		FormData fd_minLengthText = new FormData();
		fd_minLengthText.top = new FormAttachment(lblNewLabel_4, -3, SWT.TOP);
		fd_minLengthText.left = new FormAttachment(lblNewLabel_4, 6);
		fd_minLengthText.right = new FormAttachment(100, -5);
		fd_minLengthText.width = 80;
		minLengthText.setLayoutData(fd_minLengthText);

		group_1 = new Group(otherGroup, SWT.NONE);
		fd_lblNewLabel_4.left = new FormAttachment(group_1, 0, SWT.LEFT);
		group_1.setText("包含最大值边界");
		group_1.setLayout(new FormLayout());
		FormData fd_group_1 = new FormData();
		fd_group_1.right = new FormAttachment(100, -5);
		fd_group_1.left = new FormAttachment(0, 5);
		fd_group_1.height = 30;
		fd_group_1.top = new FormAttachment(0, 5);
		group_1.setLayoutData(fd_group_1);

		canMaxButton = new Button(group_1, SWT.RADIO);
		canMaxButton.setText("是");
		FormData fd_canMaxButton = new FormData();
		fd_canMaxButton.top = new FormAttachment(0, 10);
		fd_canMaxButton.left = new FormAttachment(0, 10);
		canMaxButton.setLayoutData(fd_canMaxButton);

		canNotMaxButton = new Button(group_1, SWT.RADIO);
		canNotMaxButton.setText("否");
		FormData fd_canNotMaxButton = new FormData();
		fd_canNotMaxButton.top = new FormAttachment(canMaxButton, 0, SWT.TOP);
		fd_canNotMaxButton.left = new FormAttachment(canMaxButton, 6);
		canNotMaxButton.setLayoutData(fd_canNotMaxButton);

		group_3 = new Group(otherGroup, SWT.NONE);
		group_3.setText("包含最小值边界");
		group_3.setLayout(new FormLayout());
		FormData fd_group_3 = new FormData();
		fd_group_3.right = new FormAttachment(100, -5);
		fd_group_3.height = 30;
		fd_group_3.top = new FormAttachment(group_1, 5);
		fd_group_3.left = new FormAttachment(0, 5);
		group_3.setLayoutData(fd_group_3);

		canMinButton = new Button(group_3, SWT.RADIO);
		canMinButton.setText("是");
		FormData fd_canMinButton = new FormData();
		fd_canMinButton.top = new FormAttachment(0, 10);
		fd_canMinButton.left = new FormAttachment(0, 10);
		canMinButton.setLayoutData(fd_canMinButton);

		canNotMinButton = new Button(group_3, SWT.RADIO);
		canNotMinButton.setText("否");
		FormData fd_canNotMinButton = new FormData();
		fd_canNotMinButton.top = new FormAttachment(canMinButton, 0, SWT.TOP);
		fd_canNotMinButton.left = new FormAttachment(canMinButton, 6);
		canNotMinButton.setLayoutData(fd_canNotMinButton);

		group_4 = new Group(otherGroup, SWT.NONE);
		fd_lblNewLabel_4.top = new FormAttachment(group_4, 10);
		group_4.setText("只能是数字");
		group_4.setLayout(new FormLayout());
		FormData fd_group_4 = new FormData();
		fd_group_4.right = new FormAttachment(100, -5);
		fd_group_4.left = new FormAttachment(0, 5);
		fd_group_4.height = 30;
		fd_group_4.top = new FormAttachment(group_3, 5);
		group_4.setLayoutData(fd_group_4);

		canNotNumButton = new Button(group_4, SWT.RADIO);
		canNotNumButton.setText("否");
		FormData fd_canNotNumButton = new FormData();
		canNotNumButton.setLayoutData(fd_canNotNumButton);

		canNumButton = new Button(group_4, SWT.RADIO);
		fd_canNotNumButton.top = new FormAttachment(canNumButton, 0, SWT.TOP);
		fd_canNotNumButton.left = new FormAttachment(canNumButton, 6);
		canNumButton.setText("是");
		FormData fd_canNumButton = new FormData();
		fd_canNumButton.top = new FormAttachment(0, 10);
		fd_canNumButton.left = new FormAttachment(0, 10);
		canNumButton.setLayoutData(fd_canNumButton);

	}

	public void setColumnModel(ColumnModel columnModel) {
		this.columnModel = columnModel;
	}


	/**
	 * 公共列字段Combo改变事件
	 */
	private void domainComboSelectionChanged() {
		IStructuredSelection select = (IStructuredSelection) domainComboViewer
				.getSelection();
		if (select.isEmpty()) {
			return;
		}

		Object obj = select.getFirstElement();
		if (obj instanceof String) {
			setDomainState(false);
			return;
		}

		ColumnModel domainColumnModel = (ColumnModel) obj;

		// 设置名称描述注释等
		nameText.setText(domainColumnModel.getColumnName());
		descText.setText(domainColumnModel.getColumnDesc());
		noteText.setText(domainColumnModel.getColumnNote());

		// 设置数据类型
		DataTypeModel domainDataTypeModel = domainColumnModel
				.getDataTypeModel();
		for (DataTypeModel dataTypeModel : dataTypeModelList) {
			if (dataTypeModel.getName().equals(domainDataTypeModel.getName())) {
				dataTypeComboView.setSelection(new StructuredSelection(
						dataTypeModel));

				break;
			}
		}
		lengthText.setText(domainDataTypeModel.getLength() == -1 ? ""
				: domainDataTypeModel.getLength() + "");

		precisionText.setText(domainDataTypeModel.getPrecision() == -1 ? ""
				: domainDataTypeModel.getPrecision() + "");

		// 设置系统默认值
		String systemDefaultValueType = domainColumnModel
				.getSystemDefaultValueType();
		if (DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_C
				.equalsIgnoreCase(systemDefaultValueType)) {
			btnUserDefine.setSelection(true);
			btnSystemDate.setSelection(false);
			btnSystemOrgId.setSelection(false);
			btnSystemTime.setSelection(false);
			btnSystemUser.setSelection(false);
		} else if (DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_D
				.equalsIgnoreCase(systemDefaultValueType)) {
			btnUserDefine.setSelection(false);
			btnSystemDate.setSelection(true);
			btnSystemOrgId.setSelection(false);
			btnSystemTime.setSelection(false);
			btnSystemUser.setSelection(false);
		} else if (DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_O
				.equalsIgnoreCase(systemDefaultValueType)) {
			btnUserDefine.setSelection(false);
			btnSystemDate.setSelection(false);
			btnSystemOrgId.setSelection(true);
			btnSystemTime.setSelection(false);
			btnSystemUser.setSelection(false);
		} else if (DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_T
				.equalsIgnoreCase(systemDefaultValueType)) {
			btnUserDefine.setSelection(false);
			btnSystemDate.setSelection(false);
			btnSystemOrgId.setSelection(false);
			btnSystemTime.setSelection(true);
			btnSystemUser.setSelection(false);
		} else if (DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_U
				.equalsIgnoreCase(systemDefaultValueType)) {
			btnUserDefine.setSelection(false);
			btnSystemDate.setSelection(false);
			btnSystemOrgId.setSelection(false);
			btnSystemTime.setSelection(false);
			btnSystemUser.setSelection(true);
		}
		// 选中自定义数据时,赋值"初始化默认值"控件
		if (!btnUserDefine.getSelection()) {
			txtDefualtValue.setEditable(false);
		}
		// 赋值初始化默认值文本框
		txtInitValue.setText(columnModel.getInitDefaultValue() == null ? ""
				: domainColumnModel.getInitDefaultValue());
		// 赋值系统默认值文本框
		txtDefualtValue
				.setText(columnModel.getSystemDefaultValue() == null ? ""
						: domainColumnModel.getSystemDefaultValue());

		// 设置标准检查
		maxText.setText(domainColumnModel.getMaxValue() == null ? ""
				: domainColumnModel.getMaxValue());
		minText.setText(domainColumnModel.getMinValue() == null ? ""
				: domainColumnModel.getMinValue());
		defaultComboView.setSelection(new StructuredSelection(domainColumnModel
				.getDefaultValue()));
		if (domainColumnModel.isCanGetMaxValue()) {
			canMaxButton.setSelection(true);
			canNotMaxButton.setSelection(false);
		} else {
			canMaxButton.setSelection(false);
			canNotMaxButton.setSelection(true);
		}
		if (domainColumnModel.isCanGetMinValue()) {
			canMinButton.setSelection(true);
			canNotMinButton.setSelection(false);
		} else {
			canMinButton.setSelection(false);
			canNotMinButton.setSelection(true);
		}
		if (domainColumnModel.isMustNumber()) {
			canNumButton.setSelection(true);
			canNotNumButton.setSelection(false);
		} else {
			canNumButton.setSelection(false);
			canNotNumButton.setSelection(true);
		}

		minLengthText.setText(domainColumnModel.getStrMinLength() < 1 ? ""
				: domainColumnModel.getStrMinLength() + "");
		
		// 设置数据格式
		dataFormatComposite.setDomainsContent(domainColumnModel);

		// 设置相关控件不可编辑
		setDomainState(true);
	}

	/**
	 * 设置控件的Domain状态，当用户选择公共主键时，相关控件不能编辑
	 * 
	 * @param flag
	 */
	private void setDomainState(boolean flag) {
		dataTypeCombo.setEnabled(!flag);
		lengthText.setEditable(!flag);
		precisionText.setEditable(!flag);
		maxText.setEditable(!flag);
		minText.setEditable(!flag);
		defaultCombo.setEnabled(!flag);
		canMaxButton.setEnabled(!flag);
		canNotMaxButton.setEnabled(!flag);
		canMinButton.setEnabled(!flag);
		canNumButton.setEnabled(!flag);
		canNotMinButton.setEnabled(!flag);
		minLengthText.setEditable(!flag);
		btnSystemDate.setEnabled(!flag);
		btnSystemTime.setEnabled(!flag);
		btnSystemUser.setEnabled(!flag);
		btnSystemOrgId.setEnabled(!flag);
		btnUserDefine.setEnabled(!flag);
		txtDefualtValue.setEditable(!flag);
		txtInitValue.setEditable(!flag);
		
		dataFormatComposite.setEnabled(!flag);

		if (!flag) {
			if (!btnUserDefine.getSelection()) {
				txtDefualtValue.setEditable(false);
			}
		}
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
		if (columnModel == null) {
			return Window.CANCEL;
		}

		// 对于那些暂时没有ID的ColumnModel，需要给其添加ID后再打开对话框
		if (columnModel.getId() == null || columnModel.getId().trim().isEmpty()) {
			ColumnModelFactory.addColumnModel(
					FileModel.getFileModelFromObj(columnModel), columnModel);
		}

		ColumnPropertiesDialog columnPropertiesDialog = getColumnDialogMap()
				.get(columnModel.getId());
		if (columnPropertiesDialog != null
				&& columnPropertiesDialog.getShell() != null
				&& !columnPropertiesDialog.getShell().isDisposed()) {
			columnPropertiesDialog.getShell().setMinimized(false);
			columnPropertiesDialog.getShell().setActive();
			return Window.OK;
		} else {
			getColumnDialogMap().put(columnModel.getId(), this);
			return super.open();
		}
	}

	@Override
	public boolean close() {
		getColumnDialogMap().remove(columnModel.getId());
		WindowsManagerDialog.refreshData();
		return super.close();
	}

	/**
	 * 检查数据是否填写正确
	 * 
	 * @return
	 */
	private boolean checkData() {
		
		String str = nameText.getText().trim();
		if ("".equals(str)) {
			// 列名称不能为空！
			setErrorMessage(I18nUtil
					.getMessage("COLUMNmODEL_NAME_IS_NOT_EMPTY"));
			return false;
		}

		// 同一个表格模型下列名称必须唯一
		if (columnModel == null) {
			// 列模型为空，无法校验数据！
			setErrorMessage(I18nUtil
					.getMessage("COLUMNMODEL_IS_EMPTY_CAN_NOT_CHECK_DATA"));
			return false;
		}

		TableModel tableModel = columnModel.getTableModel();
		if (tableModel == null) {
			// 找不到对应表格模型，无法校验数据！
			setErrorMessage(I18nUtil
					.getMessage("TABLEMODEL_IS_EMPTY_CAN_NOT_INIT_DATA"));
			return false;
		}

		List<ColumnModel> columnModelList = tableModel.getColumnList();
		for (ColumnModel column : columnModelList) {
			if (!column.equals(columnModel)
					&& str.equals(column.getColumnName())) {
				setErrorMessage(I18nUtil.getMessage("COLUMNMODEL_NAME_USED"));
				return false;
			}
		}

		str = descText.getText().trim();
		if ("".equals(str)) {
			// 列描述不能为空！
			setErrorMessage(I18nUtil.getMessage("COLUMN_DESC_NOT_EMPTY"));
			return false;
		}

		if (!lengthText.getText().trim().isEmpty()) {
			try {
				Integer num = new Integer(lengthText.getText().trim());
			} catch (NumberFormatException e) {
				// 填写的长度数据非法!
				setErrorMessage(I18nUtil.getMessage("LENGTH_IS_ERROR"));
				return false;
			}
		}

		if (!precisionText.getText().trim().isEmpty()) {
			try {
				Integer num = new Integer(precisionText.getText().trim());
			} catch (NumberFormatException e) {
				// 填写的精度数据非法!
				setErrorMessage(I18nUtil.getMessage("PRECISION_IS_ERROR"));
				return false;
			}
		}

		if (!minLengthText.getText().trim().isEmpty()) {
			try {
				Integer num = new Integer(minLengthText.getText().trim());
			} catch (NumberFormatException e) {
				// 字符串最小长度只能输入数字！
				setErrorMessage("字符串最小长度只能输入数字！");
				return false;
			}
		}

		// 校验系统默认值输入
//		if (btnUserDefine.getSelection()) {
//			if (txtDefualtValue.getText().trim().isEmpty()) {
//				setErrorMessage("系统默认值不能为空！");
//				return false;
//			}
//		}

		if(!columnModel.isDomainColumnModel()) {
			str = dataSourceComposite.checkData();
			if(str != null) {
				setErrorMessage(str);
				return false;
			}
		}
		
		setErrorMessage(null);
		setMessage(I18nUtil.getMessage("COLUMN_SET"));
		return true;
	}

	@Override
	protected void okPressed() {
		if (!checkData()) {
			return;
		}

		if (columnModel == null) {
			logger.error("传入的ColumnModel为空，设置值失败！");
			return;
		}

		columnModel.setColumnName(nameText.getText().trim());
		columnModel.setColumnDesc(descText.getText().trim());
		columnModel.setColumnNote(noteText.getText());

		IStructuredSelection select = (IStructuredSelection) dataTypeComboView
				.getSelection();
		if (!select.isEmpty()) {
			columnModel.setDataTypeModel((DataTypeModel) select
					.getFirstElement());
		}

		if (columnModel.getDataTypeModel().getName().contains("%")) {
			try {
				Integer num = new Integer(lengthText.getText().trim());
				if (num >= 0) {
					columnModel.getDataTypeModel().setLength(num);
				}
			} catch (NumberFormatException e) {
				logger.warn("填写的Length数据非法，已取消对Length的赋值。");
				e.printStackTrace();
			}
		}

		if (columnModel.getDataTypeModel().getName().contains(",")) {
			try {
				Integer num = new Integer(precisionText.getText().trim());
				if (num >= 0) {
					columnModel.getDataTypeModel().setPrecision(num);
				}
			} catch (NumberFormatException e) {
				logger.warn("填写的Precision数据非法，已取消对Precision的赋值。");
				e.printStackTrace();
			}
		}

		// 赋值系统默认值类型
		if (btnSystemDate.getSelection()) {
			// 系统日期
			columnModel
					.setSystemDefaultValueType(DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_D);

		} else if (btnSystemTime.getSelection()) {
			// 系统时间
			columnModel
					.setSystemDefaultValueType(DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_T);

		} else if (btnSystemUser.getSelection()) {
			// 登录用户
			columnModel
					.setSystemDefaultValueType(DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_U);

		} else if (btnSystemOrgId.getSelection()) {
			// 机构代码
			columnModel
					.setSystemDefaultValueType(DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_O);

		} else if (btnUserDefine.getSelection()) {
			// 用户自定义
			columnModel
					.setSystemDefaultValueType(DmConstants.SYSTEM_DEFAULT_VALUE_TYPE_C);
		}

		columnModel.setInitDefaultValue(txtInitValue.getText().trim());
		columnModel.setSystemDefaultValue(txtDefualtValue.getText().trim());

		// 赋值标准检查页面
		columnModel.setMinValue(minText.getText().trim());
		columnModel.setMaxValue(maxText.getText().trim());
		columnModel.setCanGetMaxValue(canMaxButton.getSelection());
		columnModel.setCanGetMinValue(canMinButton.getSelection());
		columnModel.setMustNumber(canNumButton.getSelection());
		if (minLengthText.getText().trim().isEmpty()) {
			columnModel.setStrMinLength(-1);
		} else {
			columnModel.setStrMinLength(new Integer(minLengthText.getText()
					.trim()));

		}

		select = (IStructuredSelection) defaultComboView.getSelection();
		if (!select.isEmpty()) {
			columnModel.setDefaultValue((String) select.getFirstElement());
		}

		// 赋值数据来源页面
		if (!columnModel.isDomainColumnModel()) {
			// 对数据源来进行赋值
			String type = (String) dataSourceComposite.getDataSourceTypeComboViewer().getSelectKey();
			columnModel.setDataSourceType(type);
			if (DmConstants.TABLE_DATA_SOURCE.equals(type)) {

				// 如果是单表数据来源
				if (btnSingleTable.getSelection()) {
					// 赋值表数据来源标记
					columnModel
							.setSingleTableSource(DmConstants.SINGLE_TABLE_SOURCE);

					// 赋值列数据源
					select = (IStructuredSelection) comboView_dataTableType
							.getSelection();
					columnModel.setDataSourceContent(((ColumnModel) select
							.getFirstElement()).getId());

					// 赋值列数据描述来源
					select = (IStructuredSelection) comboViewerDatasourceDesc
							.getSelection();
					columnModel.setDataSourceDescContent(((ColumnModel) select
							.getFirstElement()).getId());
				}

				// 如果是多表数据来源
				if (btnMultiTable.getSelection()) {
					// 赋值表数据来源标记
					columnModel
							.setSingleTableSource(DmConstants.MULTI_TABLE_SOURCE);

					columnModel.setMatchDefaultValue(txtMatchDefaultValue
							.getText().trim());
					columnModel.setLimitCondition(txtLimitCondition.getText()
							.trim());
				}

			} else if (DmConstants.CUSTOM_DATA_SOURCE.equals(type)) {
				
				LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
				List<KeyValueModel> keyValueModels = (List<KeyValueModel>)dataSourceComposite.getUserDefineTableViewer().getInput();
				for(KeyValueModel valueModel : keyValueModels){
					linkedHashMap.put((String)valueModel.getKey(), (String)valueModel.getValue());
				}
				
				columnModel
						.setCustomDataMap(linkedHashMap);
			}

			select = (IStructuredSelection) domainComboViewer.getSelection();
			if (select.isEmpty()) {
				columnModel.setDomainId(DmConstants.NONE);
			} else {
				Object obj = select.getFirstElement();
				// 如果是字符串类型，说明选的是“None”
				if (obj instanceof String) {
					columnModel.setDomainId(DmConstants.NONE);
				} else {
					columnModel.setDomainId(((ColumnModel) obj).getId());
				}
			}

			// 赋值列的默认值
			String defaultValue = txtDefualtValue.getText().trim();
			if (!defaultValue.isEmpty()) {
				columnModel.setDefaultValue(defaultValue);
			}
		}
		
		//数据格式选项卡里存储的内容
		if(dataFormatComposite.getButtonUnitDescription().getSelection()){
			columnModel.setUnitDesc(dataFormatComposite.getTextUnitDescription().getText());
		}
		if(dataFormatComposite.getButtonChiniseDescription().getSelection()){
			columnModel.setAutoChangeChDesc(true);
			
			if(dataFormatComposite.getButtonDatasourceTransform().getSelection()){
				columnModel.setPassDataSourceChange(true);
			} else{
				LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
				List<KeyValueModel> keyValueModels = (List<KeyValueModel>)dataFormatComposite.getDataFormatUserDefineTableViewer().getInput();
				for(KeyValueModel valueModel : keyValueModels){
					linkedHashMap.put((String)valueModel.getKey(), (String)valueModel.getValue());
				}
				
				columnModel.setDefaultSwitchMap(linkedHashMap);
			}
		}

		super.okPressed();
	}

	public static Map<String, ColumnPropertiesDialog> getColumnDialogMap() {
		return columnDialogMap;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public ColumnModel getColumnModel() {
		return columnModel;
	}
}

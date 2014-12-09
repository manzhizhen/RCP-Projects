/* 文件名：     ImportTableModelDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-24
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.provider.SelectTableLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 给产品引入表格的对话框
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-11-24
 * @see
 * @since 1.0
 */
public class ImportTableModelDialog extends TitleAreaDialog {
	private Composite composite;
	private Text physicalDataModelNameText;
	private CheckboxTableViewer tablesViewer;
	private Table tablesTable;

	private ModuleModel moduleModel;
	private List<TableModel> selectTableList = new ArrayList<TableModel>();

	private Log logger = LogManager.getLogger(ImportTableModelDialog.class
			.getName());

	/**
	 * @param parentShell
	 */
	public ImportTableModelDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}

	@Override
	protected void configureShell(Shell newShell) {
		// 引入表格模型对话框
		newShell.setText(I18nUtil.getMessage("IMPORT_TABLE_MODEL_DIALOG"));
		setDefaultImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.TABLE_16));
		super.configureShell(newShell);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(672, 479);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// 引入表格模型
		setTitle(I18nUtil.getMessage("IMPORT_TABLE_MODEL"));
		// 请选择需要引入的表格
		setMessage(I18nUtil.getMessage("PLEASE_CHOICE_TABLES"));
		setTitleImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.TABLE_64));

		Control control = super.createDialogArea(parent);

		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		composite.setLayout(new FormLayout());

		createControl();
		initControlValue();
		createEvent();

		return control;
	}

	private void createEvent() {
		tablesViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				Object obj = selection.getFirstElement();
				if (obj instanceof TableModel) {
					tablesViewer.setChecked(obj, !tablesViewer.getChecked(obj));
				}
			}
		});

	}

	private void initControlValue() {
		if (moduleModel == null || moduleModel.getProductModel() == null 
				|| moduleModel.getProductModel().getPhysicalDataModel() == null) {
			logger.error("传入的ModuleModel为空或信息不完整，无法初始化控件！");
			return;
		}

		PhysicalDataModel physicalDataModel =  moduleModel.getProductModel()
				.getPhysicalDataModel();

		physicalDataModelNameText
				.setText(physicalDataModel.getName() == null ? ""
						: physicalDataModel.getName());

		// 找到该物理数据模型下的所有不属于该产品的表格
		List<TableModel> tableModelList = new ArrayList<TableModel>();
		Set<TableModel> moduleTableModelSet = moduleModel.getTableModelSet();
		Set<TableModel> tableModelSet = physicalDataModel.getAllTables();
		for (TableModel tableModel : tableModelSet) {
			if (!moduleTableModelSet.contains(tableModel)) {
				tableModelList.add(tableModel);
			}
		}

		tablesViewer.setContentProvider(new ArrayContentProvider());
		tablesViewer.setLabelProvider(new SelectTableLabelProvider());
		tablesViewer.setInput(tableModelList);

	}

	private void createControl() {
		Label lblNewLabel = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("物理数据模型:");

		physicalDataModelNameText = new Text(composite, SWT.BORDER
				| SWT.READ_ONLY);
		FormData fd_text = new FormData();
		fd_text.right = new FormAttachment(100, -10);
		fd_text.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_text.left = new FormAttachment(lblNewLabel, 6);
		physicalDataModelNameText.setLayoutData(fd_text);

		tablesTable = new Table(composite, SWT.BORDER | SWT.CHECK
				| SWT.FULL_SELECTION | SWT.MULTI);
		tablesViewer = new CheckboxTableViewer(tablesTable);
		tablesTable.setHeaderVisible(true);
		tablesTable.setLinesVisible(true);
		FormData fd_tablesTable = new FormData();
		fd_tablesTable.top = new FormAttachment(physicalDataModelNameText, 10);
		fd_tablesTable.bottom = new FormAttachment(100, -10);
		fd_tablesTable.right = new FormAttachment(100, -10);
		fd_tablesTable.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		tablesTable.setLayoutData(fd_tablesTable);

		TableColumn tableColumn = new TableColumn(tablesTable, SWT.NONE);
		tableColumn.setWidth(24);

		TableColumn tableColumn_1 = new TableColumn(tablesTable, SWT.NONE);
		tableColumn_1.setWidth(100);
		tableColumn_1.setText("名称");

		TableColumn tableColumn_2 = new TableColumn(tablesTable, SWT.NONE);
		tableColumn_2.setWidth(100);
		tableColumn_2.setText("描述");

		TableColumn tableColumn_3 = new TableColumn(tablesTable, SWT.NONE);
		tableColumn_3.setWidth(202);
		tableColumn_3.setText("产品标签");

		TableColumn tableColumn_4 = new TableColumn(tablesTable, SWT.NONE);
		tableColumn_4.setWidth(205);
		tableColumn_4.setText("模块标签");
	}

	@Override
	protected void okPressed() {
		selectTableList = getSelection();

		super.okPressed();
	}

	/**
	 * 获得所选择的行
	 * 
	 * @return
	 */
	public List<TableModel> getSelection() {
		Object[] objects = tablesViewer.getCheckedElements();

		List<TableModel> list = new ArrayList<TableModel>();
		for (Object obj : objects) {
			if (obj instanceof TableModel) {
				list.add((TableModel) obj);
			}
		}

		return list;
	}

	public void setModuleModel(ModuleModel moduleModel) {
		this.moduleModel = moduleModel;
	}

	public List<TableModel> getSelectTableList() {
		return selectTableList;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}
}

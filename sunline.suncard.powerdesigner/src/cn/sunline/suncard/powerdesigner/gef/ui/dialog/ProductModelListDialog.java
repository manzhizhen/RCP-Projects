/* 文件名：     ProductModelListDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-27
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.provider.ProductModelTableLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 产品列表对话框
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-11-27
 * @see
 * @since 1.0
 */
public class ProductModelListDialog extends Dialog {
	private List<ProductModel> productModelList = new ArrayList<ProductModel>();
	private List<ProductModel> selectProductModelList = new ArrayList<ProductModel>();
	private Composite composite;
	private Table table;
	private CheckboxTableViewer checkboxTableViewer;

	protected ProductModelListDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		// 产品列表对话框
		newShell.setText(I18nUtil.getMessage("PRODUCT_LIST_DIALOG"));
		setDefaultImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.PRODUCT_IMAGE_16));

		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(470, 400);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control control = super.createDialogArea(parent);

		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		composite.setLayout(new FormLayout());

		createControl();
		initControlValue();
		// 创建表格的事件
		createTableEvent();

		return control;
	}

	private void createTableEvent() {
		checkboxTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				Object obj = selection.getFirstElement();
				if (obj instanceof ProductModel) {
					checkboxTableViewer.setChecked(obj,
							!checkboxTableViewer.getChecked(obj));
				}
			}
		});

	}

	private void initControlValue() {
		checkboxTableViewer.setContentProvider(new ArrayContentProvider());
		checkboxTableViewer
				.setLabelProvider(new ProductModelTableLabelProvider());
		checkboxTableViewer.setInput(productModelList);

	}

	private void createControl() {
		table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.CHECK | SWT.MULTI);
		checkboxTableViewer = new CheckboxTableViewer(table);

		FormData fd_table = new FormData();
		fd_table.bottom = new FormAttachment(100);
		fd_table.right = new FormAttachment(100);
		fd_table.top = new FormAttachment(0);
		fd_table.left = new FormAttachment(0);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(26);

		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(155);
		tblclmnNewColumn_1.setText("产品ID");

		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(234);
		tblclmnNewColumn_2.setText("产品名称");
	}

	@Override
	protected void okPressed() {
		selectProductModelList = getSelection();
		super.okPressed();
	}

	/**
	 * 返回表格中所选择的PeopleModel对象列表 返回的是复选框被勾选的行
	 * 
	 * @return List<PeopleModel>
	 */
	public List<ProductModel> getSelection() {
		Object[] objects = checkboxTableViewer.getCheckedElements();

		List<ProductModel> list = new ArrayList<ProductModel>();
		for (Object obj : objects) {
			if (obj instanceof ProductModel) {
				list.add((ProductModel) obj);
			}
		}

		return list;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}

	public void setProductModelList(List<ProductModel> productModelList) {
		this.productModelList = productModelList;
	}

	public List<ProductModel> getSelectProductModelList() {
		return selectProductModelList;
	}

	
	
}

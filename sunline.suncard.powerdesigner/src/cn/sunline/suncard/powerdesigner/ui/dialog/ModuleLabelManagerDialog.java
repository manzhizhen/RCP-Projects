/* 文件名：     NewFileDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.DocumentException;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import cn.sunline.suncard.powerdesigner.command.UpdateModuleLabelCommand;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.xml.ModuleXmlModel;
import cn.sunline.suncard.powerdesigner.provider.ModuleXmlModelLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.ui.dialog.modelattri.ModuleXmlModelDialog;
import cn.sunline.suncard.powerdesigner.xml.ModuleDataXmlManager;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 模块标签管理对话框
 * @author  Manzhizhen
 * @version 1.0, 2012-9-6
 * @see 
 * @since 1.0
 */
public class ModuleLabelManagerDialog extends TitleAreaDialog{

	private Composite composite;

	private Button addButton;
	private Button delButton;
	private Table labelTable;
	private TableViewer labelListViewer;
	
	private List<ModuleXmlModel> moduleXmlModelList;// 储存所用到的模块标签
	private TableColumn tblclmnid;
	private TableColumn tableColumn;
	private TableColumn tableColumn_1;

	private Log logger = LogManager.getLogger(ModuleLabelManagerDialog.class.getName());
	
	public ModuleLabelManagerDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("模块标签管理对话框");
		setDefaultImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.MODULE_LABEL_16));
		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(569, 425);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("模块标签管理");
		setMessage("模块标签管理");
		setTitleImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.MODULE_LABEL_64));
		
		Control control =   super.createDialogArea(parent);
		
		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new FormLayout());
		
		createControl();
		try {
			initControlValue();
			createEvent();
		} catch (DocumentException e) {
			logger.error("初始化数据失败！" + e.getMessage());
			e.printStackTrace();
		}
		
		return control;
	}
	
	private void createEvent() {
		delButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				delModelLabel();
				super.widgetSelected(e);
			}
		});
		
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addModelLabel();
				super.widgetSelected(e);
			}
		});
		
	}

	/**
	 * 删除标签
	 */
	protected void delModelLabel() {
		IStructuredSelection select = (IStructuredSelection) labelListViewer
				.getSelection();
		
		if(select.isEmpty()) {
			return ;
		}
		
		moduleXmlModelList.removeAll(select.toList());
		labelListViewer.setInput(moduleXmlModelList);
	}
	
	/**
	 * 新增标签
	 */
	protected void addModelLabel() {
		ModuleXmlModelDialog dialog = new ModuleXmlModelDialog(getShell());
		dialog.setModuleXmlModels((List<ModuleXmlModel>) labelListViewer.getInput());
		dialog.setFlag(DmConstants.COMMAND_ADD);
		
		if(IDialogConstants.OK_ID == dialog.open()) {
			moduleXmlModelList.add(dialog.getNewModuleXmlModel());
			labelListViewer.setInput(moduleXmlModelList);
		}
	}

	private void initControlValue() throws DocumentException {
		moduleXmlModelList = ModuleDataXmlManager.getAllModuleXmlModel();
		
		labelListViewer.setContentProvider(new ArrayContentProvider());
		labelListViewer.setLabelProvider(new ModuleXmlModelLabelProvider());
		
		labelListViewer.setInput(moduleXmlModelList);
	}

	private void createControl() {
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(0, 5);
		fd_lblNewLabel_1.left = new FormAttachment(0, 10);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("本地配置的模块标签:");
		labelTable = new Table(composite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		labelTable.setLinesVisible(true);
		labelTable.setHeaderVisible(true);
		labelListViewer = new TableViewer(labelTable);
		FormData fd_labelList = new FormData();
		fd_labelList.top = new FormAttachment(lblNewLabel_1, 5);
		fd_labelList.left = new FormAttachment(0, 10);
		fd_labelList.bottom = new FormAttachment(100, -40);
		fd_labelList.right = new FormAttachment(100, -10);
		labelTable.setLayoutData(fd_labelList);
		
		tblclmnid = new TableColumn(labelTable, SWT.NONE);
		tblclmnid.setWidth(100);
		tblclmnid.setText("模块ID");
		
		tableColumn = new TableColumn(labelTable, SWT.NONE);
		tableColumn.setWidth(128);
		tableColumn.setText("模块名称");
		
		tableColumn_1 = new TableColumn(labelTable, SWT.NONE);
		tableColumn_1.setWidth(309);
		tableColumn_1.setText("备注");
		
		delButton = new Button(composite, SWT.NONE);
		FormData fd_delButton = new FormData();
		fd_delButton.right = new FormAttachment(100, -10);
		fd_delButton.width = 30;
		fd_delButton.top = new FormAttachment(labelTable, 5);
		delButton.setLayoutData(fd_delButton);
		delButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.COLUMN_DEL));
		
		addButton = new Button(composite, SWT.NONE);
		FormData fd_addButton = new FormData();
		fd_addButton.width = 30;
		fd_addButton.bottom = new FormAttachment(delButton, 0, SWT.BOTTOM);
		fd_addButton.right = new FormAttachment(delButton, -6);
		addButton.setLayoutData(fd_addButton);
		addButton.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.ADD_LABEL_16));
	}
	
	@Override
	protected void okPressed() {
		UpdateModuleLabelCommand command = new UpdateModuleLabelCommand();
		command.setModuleXmlModelList(moduleXmlModelList);
		
		command.execute();
		
		super.okPressed();
	}
	
	/**
	 * 从一个FileModel中，获取所有用到的模块标签
	 */
	public static java.util.List<String> getModuleLabelFromFileModel(FileModel fileModel) {
//		Set<String> labelSet = new HashSet<String>();
//		java.util.List<TableModel> tableModelList = fileModel.getAllTableModel();
//		for(TableModel tableModel : tableModelList) {
//			String label = tableModel.getModuleLabel();
//			if("".equals(label.trim())) {
//				continue ;
//			}
//			
//			String[] labels = label.split(DmConstants.TABLE_MODEL_MODELLABEL_SEPARATOR);
//			labelSet.addAll(Arrays.asList(labels));
//		}
//		
//		java.util.List<String> labelList = new ArrayList<String>();
//		for(String label : labelSet) {
//			labelList.add(label);
//		}
//		
//		return labelList;
		
		return null;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}

}

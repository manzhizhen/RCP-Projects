/* 文件名：     FindObjectDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-7
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.gef.ui.dialog.ColumnPropertiesDialog;
import cn.sunline.suncard.powerdesigner.gef.ui.dialog.TableGefModelDialog;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.LineModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.model.WorkSpaceModel;
import cn.sunline.suncard.powerdesigner.provider.FindObjectLabelProvider;
import cn.sunline.suncard.powerdesigner.provider.FindObjectTableFilter;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeFilter;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;

/**
 * 查找对话框
 * @author  Manzhizhen
 * @version 1.0, 2012-11-7
 * @see 
 * @since 1.0
 */
public class FindObjectDialog extends TitleAreaDialog{
	private Composite composite;
	private Text nameText;
	private Text descText;
	private Text moduleIdText;
	private Table findTable;
	private TableViewer findTableViewer;
	private List<Object> allObjectList;
	
	private Log logger = LogManager.getLogger(FindObjectDialog.class.getName());
	private Text moduleNameText;
	
	public FindObjectDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.BORDER | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(550, 616);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("查找对象对话框");
		setDefaultImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.A_FIND));
		super.configureShell(newShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("查找对象");
		setMessage("查找对象");
		setTitleImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.FIND_LOGO_64));
		
		Control control =   super.createDialogArea(parent);
		
		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new FormLayout());
		
		createControl();
		initControlData();
		createEvent();
		
		return control;
	}


	private void createControl() {
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.width = 55;
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("名称:");
		
		nameText = new Text(composite, SWT.BORDER);
		FormData fd_nameText = new FormData();
		fd_nameText.right = new FormAttachment(100, -5);
		fd_nameText.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_nameText.left = new FormAttachment(lblNewLabel, 6);
		nameText.setLayoutData(fd_nameText);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(nameText, 13);
		fd_lblNewLabel_1.width = 55;
		fd_lblNewLabel_1.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("描述:");
		
		descText = new Text(composite, SWT.BORDER);
		FormData fd_descText = new FormData();
		fd_descText.left = new FormAttachment(nameText, 0, SWT.LEFT);
		fd_descText.top = new FormAttachment(nameText, 10);
		fd_descText.right = new FormAttachment(100, -5);
		descText.setLayoutData(fd_descText);
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_lblNewLabel_2.width = 55;
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText("模块ID:");
		moduleIdText = new Text(composite, SWT.BORDER);
		fd_lblNewLabel_2.top = new FormAttachment(moduleIdText, 3, SWT.TOP);
		FormData fd_moduleIdText = new FormData();
		fd_moduleIdText.right = new FormAttachment(50);
		fd_moduleIdText.top = new FormAttachment(descText, 10);
		fd_moduleIdText.left = new FormAttachment(nameText, 0, SWT.LEFT);
		moduleIdText.setLayoutData(fd_moduleIdText);
		
		Label lblNewLabel_3 = new Label(composite, SWT.NONE);
		lblNewLabel_3.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_3 = new FormData();
		fd_lblNewLabel_3.width = 55;
		fd_lblNewLabel_3.top = new FormAttachment(lblNewLabel_2, 0, SWT.TOP);
		fd_lblNewLabel_3.left = new FormAttachment(moduleIdText, 6);
		lblNewLabel_3.setLayoutData(fd_lblNewLabel_3);
		lblNewLabel_3.setText("模块名称:");
		moduleNameText = new Text(composite, SWT.BORDER);
		FormData fd_moduleNameText = new FormData();
		fd_moduleNameText.right = new FormAttachment(100, -5);
		fd_moduleNameText.bottom = new FormAttachment(moduleIdText, 0, SWT.BOTTOM);
		fd_moduleNameText.left = new FormAttachment(lblNewLabel_3, 6);
		moduleNameText.setLayoutData(fd_moduleNameText);
		
		findTableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		findTable = findTableViewer.getTable();
		FormData fd_findTable = new FormData();
		fd_findTable.top = new FormAttachment(moduleIdText, 10);
		fd_findTable.bottom = new FormAttachment(100, -5);
		fd_findTable.right = new FormAttachment(100, -5);
		fd_findTable.left = new FormAttachment(lblNewLabel, 5, SWT.LEFT);
		findTable.setLayoutData(fd_findTable);
		
		findTable.setHeaderVisible(true);
		findTable.setLinesVisible(true);
		
		TableColumn tableColumn = new TableColumn(findTable, SWT.NONE);
		tableColumn.setWidth(200);
		tableColumn.setText("名称");
		TableColumn tableColumn1 = new TableColumn(findTable, SWT.NONE);
		tableColumn1.setWidth(300);
		tableColumn1.setText("描述");

	}
	
	private void initControlData() {
		findTableViewer.setContentProvider(new ArrayContentProvider());
		findTableViewer.setLabelProvider(new FindObjectLabelProvider());
		
		// 把所有的表格，列和连接线查找出来
		List<TableModel> tableModelList = new ArrayList<TableModel>();
		List<TableShortcutModel> tableShortcutModelList = new ArrayList<TableShortcutModel>();
		List<ColumnModel> columnModelList = new ArrayList<ColumnModel>();
//		List<LineModel> lineModelList = new ArrayList<LineModel>();
		
		Set<FileModel> set = WorkSpaceModel.getFileModelSet();
		for(FileModel fileModel : set) {
			tableModelList.addAll(fileModel.getAllTableModel());
			tableShortcutModelList.addAll(fileModel.getAllTableShortcutModel());
			columnModelList.addAll(fileModel.getAllColumnModel());
			
			// 添加物理数据模型中的默认列对象和公共列对象
			Set<PhysicalDataModel> physicalDataModelSet = fileModel.getPhysicalDataSet();
			for(PhysicalDataModel physicalDataModel : physicalDataModelSet) {
				columnModelList.addAll(physicalDataModel.getDefaultColumnList());
				columnModelList.addAll(physicalDataModel.getDomainList());
			}
		}
		
//		for(TableModel tableModel : tableModelList) {
//			lineModelList.addAll(tableModel.getLineModelList());
//		}
		
		allObjectList = new ArrayList<Object>();
		allObjectList.addAll(tableModelList);
		allObjectList.addAll(tableShortcutModelList);
		allObjectList.addAll(columnModelList);
//		allObjectList.addAll(lineModelList);
		
		findTableViewer.setInput(allObjectList);
	}

	private void createEvent() {
		nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				textEvent();
			}
		});
		
		descText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				textEvent();
			}
		});
		
		moduleIdText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				textEvent();
			}
		});
		
		moduleNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				textEvent();
			}
		});
		
		findTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				tableEvent();
			}
		});
	}

	/**
	 * 表格双击来打开对应的对象
	 */
	protected void tableEvent() {
		IStructuredSelection select = (IStructuredSelection) findTableViewer.
				getSelection();
		
		if(select.isEmpty()) {
			logger.warn("没有所选的对象，查找对象失败！");
			return ;
		}
		
		Object obj = select.getFirstElement();
		
		close();
		if(obj instanceof TableModel) {
			TableModel tableModel = (TableModel) obj;
			
			TableGefModelDialog dialog = new TableGefModelDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell());
			dialog.setTableModel(tableModel);
			if (IDialogConstants.OK_ID == dialog.open()) {
				CommandStack commandStack = DatabaseTreeViewPart.getEditorCommandStack(tableModel.
						getPhysicalDiagramModel());
				if(commandStack != null) {
					commandStack.execute(dialog.getCommand());
				} else {
					commandStack = DefaultViewPart.getFileCommandFromObj(FileModel
							.getFileModelFromObj(tableModel));
					if(commandStack != null) {
						commandStack.execute(dialog.getCommand());
					}
				}
				
			}
			
		} else if(obj instanceof ColumnModel) {
			ColumnModel columnModel = (ColumnModel) obj;
			ColumnPropertiesDialog dialog = new ColumnPropertiesDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell());
			dialog.setColumnModel(columnModel);
			dialog.open();
			
			dialog.close();
		} else if(obj instanceof LineModel) {
			
			
		}
		
		
	}
	
	/**
	 * 文本框变化事件
	 */
	protected void textEvent() {
		String nameStr = nameText.getText().trim();
		String descStr = descText.getText().trim();
		String moduleIdStr = moduleIdText.getText().trim();
		String moduleNameStr = moduleNameText.getText().trim();


		findTableViewer.setFilters(new ViewerFilter[] { 
				new FindObjectTableFilter(nameStr, descStr, moduleIdStr, moduleNameStr),
		});
		
		findTableViewer.refresh();
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}
}

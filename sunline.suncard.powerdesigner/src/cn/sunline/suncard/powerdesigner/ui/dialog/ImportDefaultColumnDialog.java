/* 文件名：     ImportDefaultColumnDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-11
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.command.ImportDefaultColumnCommand;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.provider.ColumnModelLabelProvider;
import cn.sunline.suncard.powerdesigner.provider.TableAndColumnShowLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.CheckboxComposite;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Button;

/**
 * 给表格导入默认列的对话框
 * @author  Manzhizhen
 * @version 1.0, 2013-1-11
 * @see 
 * @since 1.0
 */
public class ImportDefaultColumnDialog extends TitleAreaDialog {
	private PhysicalDataModel physicalDataModel;
	private CheckboxComposite checkboxComposite;
	
	private Composite composite;
	private ComboViewer defaultComboViewer;
	private Combo defaultCombo;
	private Button importButton;
	
	private boolean isImportDefaultColumn = true; // 标记是否是导入默认列打开此对话框还是导入Domains
	
	private Log logger = LogManager.getLogger(ImportDefaultColumnDialog.class
			.getName());
	
	public ImportDefaultColumnDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		if(isImportDefaultColumn) {
			// 引入默认列对话框
			newShell.setText(I18nUtil.getMessage("IMPORT_DEFAULT_COLUMN_DIALOG"));
		} else {
			// 引入Domains对话框
			newShell.setText(I18nUtil.getMessage("IMPORT_DOMAINS_DIALOG"));
		}
		
		setDefaultImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.COLUMN_ITEM_64));
		setTitleImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.TABLE_64));
		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(750, 600);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		if(isImportDefaultColumn) {
			// 导入默认列
			setTitle(I18nUtil.getMessage("IMPORT_DEFAULT_COLUMN"));
			setMessage(I18nUtil.getMessage("IMPORT_DEFAULT_COLUMN"));
		} else {
			// 导入Domains
			setTitle(I18nUtil.getMessage("IMPORT_DOMAINS"));
			setMessage(I18nUtil.getMessage("IMPORT_DOMAINS"));
		}

		Control control = super.createDialogArea(parent);

		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		composite.setLayout(new FormLayout());

//		try {
			createControl();
			initControlData();
			createEvent();
//		} catch (CloneNotSupportedException e) {
//			logger.error("克隆TableModel失败！" + e.getMessage());
//			setErrorMessage("克隆TableModel失败！" + e.getMessage());
//			e.printStackTrace();
//		}

		return control;
	}

	private void createControl() {
		checkboxComposite = new CheckboxComposite(composite, SWT.NONE);
		FormData fd_checkboxComposite = new FormData();
		fd_checkboxComposite.top = new FormAttachment(0, 0);
		fd_checkboxComposite.bottom = new FormAttachment(100, -10);
		fd_checkboxComposite.right = new FormAttachment(70, 0);
		fd_checkboxComposite.left = new FormAttachment(0, 0);
		checkboxComposite.setLayoutData(fd_checkboxComposite);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(checkboxComposite, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		if(isImportDefaultColumn) {
			lblNewLabel.setText("默认字段:");
		} else {
			lblNewLabel.setText("Domains:");
		}
		
		defaultComboViewer = new ComboViewer(composite, SWT.NONE | SWT.READ_ONLY);
		defaultCombo = defaultComboViewer.getCombo();
		FormData fd_defaultCombo = new FormData();
		fd_defaultCombo.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_defaultCombo.left = new FormAttachment(lblNewLabel, 6);
		fd_defaultCombo.right = new FormAttachment(100, -5);
		defaultCombo.setLayoutData(fd_defaultCombo);
		
		importButton = new Button(composite, SWT.NONE);
		FormData fd_importButton = new FormData();
		fd_importButton.width = 80;
		fd_importButton.top = new FormAttachment(defaultCombo, 6);
		fd_importButton.right = new FormAttachment(defaultCombo, 0, SWT.RIGHT);
		importButton.setLayoutData(fd_importButton);
		importButton.setText("导入");
	}

	private void initControlData() {
		if(physicalDataModel == null) {
			logger.error("传入的物理数据模型为空，无法初始化控件数据！");
			setErrorMessage("传入的物理数据模型为空，无法初始化控件数据！");
			return ;
		}
		
		// 初始化控件的树，将该物理数据模型下的所有ColumnModel展现出来
		List<TreeContent> treeContentList = new ArrayList<TreeContent>();
		for(PackageModel packageModel : physicalDataModel.getPackageModelSet()) {
			TreeContent pacakgeModelTreeContent = new TreeContent();
			pacakgeModelTreeContent.setId(packageModel.getId());
			pacakgeModelTreeContent.setObj(packageModel);
			
			for(PhysicalDiagramModel physicalDiagramModel : packageModel.getPhysicalDiagramModelSet()) {
				TreeContent physicalDiagramModelTreeContent = new TreeContent();
				physicalDiagramModelTreeContent.setId(pacakgeModelTreeContent.getId() 
						+ DmConstants.SEPARATOR + physicalDiagramModel.getId());
				physicalDiagramModelTreeContent.setObj(physicalDiagramModel);
				
				pacakgeModelTreeContent.getChildrenList().add(physicalDiagramModelTreeContent);
				physicalDiagramModelTreeContent.setParent(pacakgeModelTreeContent);
				
				for(TableModel tableModel : physicalDiagramModel.getTableMap().values()) {
					TreeContent tableModelTreeContent = new TreeContent();
					tableModelTreeContent.setId(physicalDiagramModelTreeContent.getId() 
							+ DmConstants.SEPARATOR + tableModel.getId());
					tableModelTreeContent.setObj(tableModel);
					
					physicalDiagramModelTreeContent.getChildrenList().add(tableModelTreeContent);
					tableModelTreeContent.setParent(physicalDiagramModelTreeContent);
					
					for(ColumnModel columnModel : tableModel.getColumnList()) {
						TreeContent columnModelTreeContent = new TreeContent();
						columnModelTreeContent.setId(tableModelTreeContent.getId() 
								+ DmConstants.SEPARATOR + columnModel.getId());
						columnModelTreeContent.setObj(columnModel);
						
						tableModelTreeContent.getChildrenList().add(columnModelTreeContent);
						columnModelTreeContent.setParent(tableModelTreeContent);
						
					}
				}
			}
			
			treeContentList.add(pacakgeModelTreeContent);
		}
		checkboxComposite.initControlData(treeContentList);
		checkboxComposite.getTreeViewer().setLabelProvider(new TableAndColumnShowLabelProvider());
		checkboxComposite.getTreeViewer().expandAll();
		
		// 初始化默认列Combo的数据
		defaultComboViewer.setContentProvider(new ArrayContentProvider());
		defaultComboViewer.setLabelProvider(new ColumnModelLabelProvider());
		if(isImportDefaultColumn) {
			defaultComboViewer.setInput(physicalDataModel.getDefaultColumnList());
		} else {
			defaultComboViewer.setInput(physicalDataModel.getDomainList());
		}
	}

	private void createEvent() {
		checkboxComposite.createEvent();
		
		checkboxComposite.getTreeViewer().addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				// 一个表格下面最多只能允许一个列被选择
				if(event.getChecked()) {
					TreeContent checkTreeContent = (TreeContent) event.getElement();
					if(checkTreeContent.getObj() instanceof ColumnModel) {
						for(TreeContent columnTreeContent : checkTreeContent.getParent().getChildrenList()) {
							if(!columnTreeContent.equals(checkTreeContent)) {
								checkboxComposite.getTreeViewer().setChecked(columnTreeContent
										, false);
							}
						}
					}
				}
			}
		});
		
		importButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				importDefaultEvent();
				super.widgetSelected(e);
			}
		});
	}
	
	/**
	 * 给字段导入默认列
	 */
	protected void importDefaultEvent() {
		IStructuredSelection select = (IStructuredSelection) defaultComboViewer.getSelection();
		if(select.isEmpty()) {
			setErrorMessage("请选择一个列对象！");
			return ;
		} else {
			setErrorMessage(null);
			setMessage(I18nUtil.getMessage("IMPORT_DEFAULT_COLUMN"));
		}
		
		Set<TreeContent> columnTreeContentSet = checkboxComposite
				.getCheckedTreeContent(ColumnModel.class);
		Set<ColumnModel> columnModelSet = new HashSet<ColumnModel>();
		for(TreeContent treeContent : columnTreeContentSet) {
			columnModelSet.add((ColumnModel) treeContent.getObj());
		}
		
		ImportDefaultColumnCommand command = new ImportDefaultColumnCommand();
		command.setColumnModelSet(columnModelSet);
		command.setDefaultColumnModel((ColumnModel) select.getFirstElement());
		
		CommandStack commandStack = DefaultViewPart
				.getFileCommandFromObj((ColumnModel) select.getFirstElement());
		if (commandStack != null) {
			commandStack.execute(command);
			
			// 删除掉选中的树节点
			for(TreeContent columnModelTreeContent : columnTreeContentSet) {
				columnModelTreeContent.getParent().getChildrenList().remove(columnModelTreeContent);
			}
			checkboxComposite.getTreeViewer().refresh();
		} else {
			logger.debug("无法找到对应的CommandStack，执行导入默认列的命令失败！");
		}
		
	}

	public void setPhysicalDataModel(PhysicalDataModel physicalDataModel) {
		this.physicalDataModel = physicalDataModel;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}

	public void setImportDefaultColumn(boolean isImportDefaultColumn) {
		this.isImportDefaultColumn = isImportDefaultColumn;
	}
}

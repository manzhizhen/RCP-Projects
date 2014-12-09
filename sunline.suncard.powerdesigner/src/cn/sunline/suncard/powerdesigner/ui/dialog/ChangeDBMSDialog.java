/* 文件名：     ChangeDBMSDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-10-16
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.List;
import java.util.Set;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
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
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.powerdesigner.command.ChangeCurrentDatabaseCommand;
import cn.sunline.suncard.powerdesigner.db.DatabaseManager;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;
import cn.sunline.suncard.powerdesigner.provider.DatabaseTypeLabelProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 
 * @author  Agree
 * @version 1.0, 2012-10-16
 * @see 
 * @since 1.0
 */
public class ChangeDBMSDialog extends TitleAreaDialog{

	private Label labelDBMS;
	private Combo comboDBMSNew;
	private Button radioShare;
	private Button radioCopy;
	
	private Label labelDBMS_1;
	private Button radioShared;
	private Button radioCopied;
	
	private CTabFolder folder;	// 文件夹选项卡
	private CTabItem generalItem;	// Table属性表情
	private Composite composite;
	private Text currentDatabaseText;
	
	private DatabaseTypeModel currentDatabaseTypeModel;	// 当前的数据库类型
	private DatabaseTypeModel newDatabaseTypeModel;	// 转换后的数据库类型
	private PhysicalDataModel physicalDataModel;	// 要转换的物理数据模型
	
	private Log logger = LogManager.getLogger(ChangeDBMSDialog.class
			.getName());
	private ComboViewer comboDBMSNewViewer;
	
	/**
	 * @param parentShell
	 */
	public ChangeDBMSDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.BORDER | SWT.CLOSE | SWT.MIN | SWT.RESIZE);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("转换数据库类型对话框");
		setDefaultImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.DATABASESWITCH_16));
		super.configureShell(newShell);
	}
	@Override
	protected Point getInitialSize() {
		return new Point(550, 515);
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("转换数据类型属性");
		setMessage("转换数据类型属性");
		setTitleImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.DATABASESWITCH_64));
		
		Control control =   super.createDialogArea(parent);
		
		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new FormLayout());
		
		createControl();
		initControlData();
		createEvent();
		
		return control;
	}
	
	private void initControlData() {
		comboDBMSNewViewer.setContentProvider(new ArrayContentProvider());
		comboDBMSNewViewer.setLabelProvider(new DatabaseTypeLabelProvider());
		List<DatabaseTypeModel> allList = DatabaseManager.getDatabaseTypeList();
		allList.remove(currentDatabaseTypeModel);	// 从里面移除当前数据库
		comboDBMSNewViewer.setInput(allList);
		
		
		if(currentDatabaseTypeModel != null) {
			currentDatabaseText.setText(currentDatabaseTypeModel.getDatabaseName());
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
		
	}
	/**
	 * 创建general属性标签
	 */
	private void createGeneralItem() {
		generalItem = new CTabItem(folder, SWT.None);
		generalItem.setText("常规");
		Image tableItemImage = CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.GENERAL_16);
		generalItem.setImage(tableItemImage);
		
		Composite generalComposite = new Composite(folder, SWT.NONE);
		FillLayout fl_generalComposite = new FillLayout();
		fl_generalComposite.type = SWT.VERTICAL;
		generalComposite.setLayout(fl_generalComposite);
		generalItem.setControl(generalComposite);
		
		//第一组
		Group groupNew = new Group(generalComposite, SWT.NONE);
		groupNew.setLayout(new FormLayout());
		groupNew.setText("新的数据库");
		
		labelDBMS = new Label(groupNew, SWT.None);
		FormData fd_labelDBMS = new FormData();
		fd_labelDBMS.top = new FormAttachment(groupNew, 10);
		fd_labelDBMS.left = new FormAttachment(groupNew, 10);
		labelDBMS.setLayoutData(fd_labelDBMS);
		labelDBMS.setText("数据库管理系统:");
		
		comboDBMSNew = new Combo(groupNew, SWT.NONE | SWT.READ_ONLY);
		comboDBMSNewViewer = new ComboViewer(comboDBMSNew);
		FormData fd_comboDBMSNew = new FormData();
		fd_comboDBMSNew.top = new FormAttachment(labelDBMS, -3, SWT.TOP);
		fd_comboDBMSNew.left = new FormAttachment(labelDBMS, 6);
		fd_comboDBMSNew.right = new FormAttachment(100, -5);
		comboDBMSNew.setLayoutData(fd_comboDBMSNew);
		
		radioShare = new Button(groupNew, SWT.RADIO);
		FormData fd_radioShare = new FormData();
		fd_radioShare.top = new FormAttachment(comboDBMSNew, 20);
		fd_radioShare.left = new FormAttachment(labelDBMS, 10, SWT.LEFT);
		radioShare.setLayoutData(fd_radioShare);
		radioShare.setText("分享DBMS定义");
		
		radioCopy = new Button(groupNew, SWT.RADIO);
		FormData fd_radioCopy = new FormData();
		fd_radioCopy.top = new FormAttachment(radioShare, 20);
		fd_radioCopy.left = new FormAttachment(labelDBMS, 10, SWT.LEFT);
		radioCopy.setLayoutData(fd_radioCopy);
		radioCopy.setText("复制DBMS定义到模型");
		
		//第二组
		Group groupCurrent = new Group(generalComposite, SWT.NONE);
		groupCurrent.setText("当前数据库");
		groupCurrent.setLayout(new FormLayout());
		
		labelDBMS_1 = new Label(groupCurrent, SWT.None);
		FormData fd_labelDBMS_1 = new FormData();
		fd_labelDBMS_1.top = new FormAttachment(0, 10);
		fd_labelDBMS_1.left = new FormAttachment(0, 10);
		labelDBMS_1.setLayoutData(fd_labelDBMS_1);
		labelDBMS_1.setText("数据库管理系统:");
		radioShared = new Button(groupCurrent, SWT.RADIO);
		FormData fd_radioShared = new FormData();
		fd_radioShared.top = new FormAttachment(0, 44);
		fd_radioShared.left = new FormAttachment(0, 20);
		radioShared.setLayoutData(fd_radioShared);
		radioShared.setText("已分享的");
		radioShared.setEnabled(false);
		radioCopied = new Button(groupCurrent, SWT.RADIO);
		FormData fd_radioCopied = new FormData();
		fd_radioCopied.top = new FormAttachment(0, 81);
		fd_radioCopied.left = new FormAttachment(0, 20);
		radioCopied.setLayoutData(fd_radioCopied);
		radioCopied.setText("已复制的");
		radioCopied.setEnabled(false);
		
		currentDatabaseText = new Text(groupCurrent, SWT.BORDER | SWT.READ_ONLY);
		FormData fd_currentDatabaseText = new FormData();
		fd_currentDatabaseText.right = new FormAttachment(100, -5);
		fd_currentDatabaseText.top = new FormAttachment(labelDBMS_1, -3, SWT.TOP);
		fd_currentDatabaseText.left = new FormAttachment(labelDBMS_1, 6);
		currentDatabaseText.setLayoutData(fd_currentDatabaseText);
	}

	@Override
	protected void okPressed() {
		if(!checkData()) {
			return ;
		}
		
		// 转换数据库操作是不可逆的，是否继续转？
		if(!MessageDialog.openConfirm(getShell(), I18nUtil.getMessage("MESSAGE")
				, I18nUtil.getMessage("CHANG_DATABASE_IS_CAN_NOT_UNDO"))) {
			return ;
		}
		
		IStructuredSelection select = (IStructuredSelection) comboDBMSNewViewer.getSelection();
		
		ChangeCurrentDatabaseCommand command = new ChangeCurrentDatabaseCommand();
		command.setPhysicalDataModel(physicalDataModel);
		command.setDatabaseTypeModel((DatabaseTypeModel) select.getFirstElement());
		
		// 如果有关于此物理数据模型的Editor打开了，则转换当前数据需要把相关Editor都保存，
		// 并清空其CommandStack，这是为了防止他们使用undo造成错误。
		// 先保存该文件
		DefaultViewPart.saveFileModel(FileModel.getFileModelFromObj(physicalDataModel));
		// 开始清空其物理数据模型相关的Editor对应的CommandStack
		Set<PhysicalDiagramModel> diagramSet = physicalDataModel.getAllPhysicalDiagramModels();
		for(PhysicalDiagramModel physicalDiagramModel : diagramSet) {
			CommandStack commandStack = DatabaseTreeViewPart.getEditorCommandStack(
					physicalDiagramModel);
			// 说明存在其Editor，则清空
			if(commandStack != null) {
				commandStack.flush();
			}
		}
		
		CommandStack commandStack = DefaultViewPart.getFileCommandFromObj(physicalDataModel);
		if(commandStack != null) {
			commandStack.execute(command);
		}
		
		super.okPressed();
	}
	
	private boolean checkData() {
		IStructuredSelection select = (IStructuredSelection) comboDBMSNewViewer.getSelection();
		if(select.isEmpty()) {
			setErrorMessage("请选择一个数据库类型！");
			return false;
		} else {
			setErrorMessage(null);
			setMessage("转换数据类型属性");
			return true;
		}
	}
	
	private void createEvent() {
		comboDBMSNewViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				checkData();
			}
		});
	}

	public void setPhysicalDataModel(PhysicalDataModel physicalDataModel) {
		this.physicalDataModel = physicalDataModel;
		currentDatabaseTypeModel = physicalDataModel.getDatabaseTypeModel();
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}

}

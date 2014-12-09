/*
 * 文件名：UserManagerDialog.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：弹出用户编辑对话框
 * 修改人： heyong
 * 修改时间：2011-09-26
 * 修改内容：新增
 * 
 * 修改人：heyong
 * 修改时间：2011-10-18
 * 修改内容：增加初始化下拉框方法initComboviewer()
*/
package cn.sunline.suncard.sde.bs.ui.dailogs;




import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.sde.bs.biz.BsDepartmentBiz;
import cn.sunline.suncard.sde.bs.biz.BsUserBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsDepartment;
import cn.sunline.suncard.sde.bs.entity.BsDepartmentId;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.entity.BsUserId;
import cn.sunline.suncard.sde.bs.entity.UserStatus;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.ui.provider.BsDepartCvLabelProvider;
import cn.sunline.suncard.sde.bs.ui.provider.UserStatusCvLabelProvider;
import cn.sunline.suncard.sde.bs.ui.resource.IAppconstans;
import cn.sunline.suncard.sde.bs.ui.resource.IImageKey;
import cn.sunline.suncard.sde.bs.ui.resource.IWidgetId;
import cn.sunline.suncard.sde.bs.ui.resource.SWTResourceManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.bs.util.WidgetPermissionUtil;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

/**
 * 弹出用户编辑对话框
 * 该对话框用于用户的添加和修改操作
 * @author heyong
 * @version 1.0, 2011-9-26
 * @see 
 * @since 1.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class UserManagerDialog extends TitleAreaDialog {
	private Text txtBankorgId;
	private Text txtUserId;
	private Text txtUserName;
	
	private ComboViewer cvDepartment;
	private ComboViewer cvUserStatus;
	
	private BsUser user;
	private boolean editable ;
	private String initialUserStatus;
	private String departmentId;
	private String info;
	
	private Composite tem;
	
	private GC gc;
	/**
	 * Create the dialog.
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public UserManagerDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * 构造方法
	 * @param parentShell
	 * @param user 初始化成员变成user, 当user为null时，表示此对话框用于添加操作，否则用于修改操作
	 */
	public UserManagerDialog(Shell parentShell, BsUser user) {
		this(parentShell);
		this.user = (user == null ? new BsUser(new BsUserId()) : user);
		this.editable = (user == null ? true : false);
		this.initialUserStatus = (user == null ? Constants.INITIAL_USER_STATUS : user.getUserStatus());
		this.departmentId = (user == null ? Constants.INITIAL_USER_DEPARTMENT_ID : user.getDepartmentId());
		this.info = (user == null ? "CONFIRMADD" : "CONFIRMMODIFY");
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
//		Composite container = (Composite) super.createDialogArea(parent);
//		container.setLayout(null);
		
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.marginWidth = 1;
		gridLayout.marginHeight = 1;
		gridLayout.horizontalSpacing = 1;

		tem = new Composite(container, SWT.NONE);
		tem.setLayout(new FormLayout());
		
		tem.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		setTitleImage(CacheImage.getCacheImage().getImage(IAppconstans.APPLICATION_ID, IImageKey.USER_MANAGER_ICON));
		setTitle(I18nUtil.getMessage("USER_MANAGER"));
		setMessage(I18nUtil.getMessage("userinfo"));
		
		Group group = new Group(tem, SWT.NONE);
		group.setLayout(new FormLayout());
		FormData fd_group = new FormData();
		fd_group.top = new FormAttachment(0, 5);
		fd_group.left = new FormAttachment(0, 5);
		fd_group.right = new FormAttachment(100, -5);
		fd_group.bottom = new FormAttachment(100, -5);
		group.setLayoutData(fd_group);
		
		Label lblBankorgid = new Label(group, SWT.NONE);
		lblBankorgid.setAlignment(SWT.RIGHT);
		FormData fd_lblBankorgid = new FormData();
		fd_lblBankorgid.right = new FormAttachment(0, 85);
		fd_lblBankorgid.top = new FormAttachment(0, 5);
		fd_lblBankorgid.left = new FormAttachment(0, 17);
		lblBankorgid.setLayoutData(fd_lblBankorgid);
		lblBankorgid.setText(I18nUtil.getMessage("BANKORG_ID") + ":");
		
		txtBankorgId = new Text(group, SWT.BORDER);
		FormData fd_txtBankorgId = new FormData();
		fd_txtBankorgId.bottom = new FormAttachment(0, 27);
		fd_txtBankorgId.right = new FormAttachment(0, 267);
		fd_txtBankorgId.top = new FormAttachment(0, 2);
		fd_txtBankorgId.left = new FormAttachment(0, 91);
		txtBankorgId.setLayoutData(fd_txtBankorgId);
		String bankorgId = String.valueOf(Context.getSessionMap().get(Constants.BANKORG_ID));
		txtBankorgId.setText((user.getId().getBankorgId()==null?  bankorgId : user.getId().getBankorgId().toString()));
		txtBankorgId.setEditable(false);
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.right = new FormAttachment(lblBankorgid, 0, SWT.RIGHT);
		fd_lblNewLabel.left = new FormAttachment(0, 27);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText(I18nUtil.getMessage("USER_ID") + ":");
		
		txtUserId = new Text(group, SWT.BORDER);
		fd_lblNewLabel.top = new FormAttachment(txtUserId, 3, SWT.TOP);
		FormData fd_txtUserId = new FormData();
		fd_txtUserId.right = new FormAttachment(0, 267);
		fd_txtUserId.top = new FormAttachment(0, 36);
		fd_txtUserId.left = new FormAttachment(0, 91);
		txtUserId.setLayoutData(fd_txtUserId);
		txtUserId.setText(user.getId().getUserId()==null? "" : user.getId().getUserId());
		txtUserId.setTextLimit(10);
		txtUserId.setEditable(this.editable);
		txtUserId.setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_T_USER_USERID_TEXT));
		
		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.right = new FormAttachment(0, 85);
		fd_lblNewLabel_1.top = new FormAttachment(0, 70);
		fd_lblNewLabel_1.left = new FormAttachment(0, 29);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText(I18nUtil.getMessage("USER_NAME") + ":");
		
		txtUserName = new Text(group, SWT.BORDER);
		FormData fd_txtUserName = new FormData();
		fd_txtUserName.right = new FormAttachment(0, 267);
		fd_txtUserName.top = new FormAttachment(0, 67);
		fd_txtUserName.left = new FormAttachment(0, 91);
		txtUserName.setLayoutData(fd_txtUserName);
		txtUserName.setText(user.getUserName()==null? "" : user.getUserName());
		txtUserName.setTextLimit(40);
		txtUserName.setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_T_USER_USERAME_TEXT));
		
		Label lblNewLabel_2 = new Label(group, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.left = new FormAttachment(0, 10);
		fd_lblNewLabel_2.right = new FormAttachment(0, 85);
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText(I18nUtil.getMessage("DEPARTMENT") + ":");
		
		
		Label lblNewLabel_4 = new Label(group, SWT.NONE);
		lblNewLabel_4.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_4 = new FormData();
		fd_lblNewLabel_4.right = new FormAttachment(0, 85);
		fd_lblNewLabel_4.top = new FormAttachment(0, 135);
		fd_lblNewLabel_4.left = new FormAttachment(0, 29);
		lblNewLabel_4.setLayoutData(fd_lblNewLabel_4);
		lblNewLabel_4.setText(I18nUtil.getMessage("USER_STATUS") + ":");
		
		cvUserStatus = new ComboViewer(group, SWT.NONE | SWT.READ_ONLY);
		Combo comboUserStatus = cvUserStatus.getCombo();
		FormData fd_comboUserStatus = new FormData();
		fd_comboUserStatus.right = new FormAttachment(0, 267);
		fd_comboUserStatus.top = new FormAttachment(0, 132);
		fd_comboUserStatus.left = new FormAttachment(0, 91);
		comboUserStatus.setLayoutData(fd_comboUserStatus);
		cvUserStatus.setContentProvider(new ArrayContentProvider());
		cvUserStatus.setLabelProvider(new UserStatusCvLabelProvider());
		cvUserStatus.setInput(Arrays.asList(UserStatus.values()));
		comboUserStatus.setEnabled(!this.editable && WidgetPermissionUtil.checkPermission(IWidgetId.SDE_E_USER_USERSTATUS_TEXT));
		
		cvDepartment = new ComboViewer(group, SWT.NONE | SWT.READ_ONLY);
		Combo comboDepartment = cvDepartment.getCombo();
		fd_lblNewLabel_2.top = new FormAttachment(comboDepartment, 3, SWT.TOP);
		FormData fd_comboDepartment = new FormData();
		fd_comboDepartment.right = new FormAttachment(0, 267);
		fd_comboDepartment.top = new FormAttachment(0, 101);
		fd_comboDepartment.left = new FormAttachment(0, 90);
		comboDepartment.setLayoutData(fd_comboDepartment);
		cvDepartment.setContentProvider(new ArrayContentProvider());
		cvDepartment.setLabelProvider(new BsDepartCvLabelProvider());
		List datas = new BsDepartmentBiz().getAll();
		datas.add(0, Constants.INITIAL_USER_DEPARTMENT_ID);
		cvDepartment.setInput(datas);
		comboDepartment.setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_E_USER_DEPTID_TEXT));
		
		//初始化下拉框
		initComboviewer();
		
		//用户id文本框失去焦点监听器
		txtUserId.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				Text text = (Text) e.getSource();
				BsUser user = new BsUserBiz().findByUserId(text.getText().trim());
				
				if (text.getText() == null || text.getText().trim().length() == 0){
					setErrorMessage(I18nUtil.getMessage("USER_ID") + I18nUtil.getMessage("notnull"));
					drawColorBorder(txtUserId.getBounds(), SWTResourceManager.getColor(255, 0, 0));
				}else if (user != null){ 	//当用户存在
						setErrorMessage(I18nUtil.getMessage("userhasexisted", new Object[]{text.getText()}));
						drawColorBorder(txtUserId.getBounds(), SWTResourceManager.getColor(255, 0, 0));
				}else{
					drawColorBorder(txtUserId.getBounds(), txtUserId.getParent().getBackground());
					setErrorMessage(null);
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				Text text = (Text) e.getSource();
				//全选文本框中输入的内容
				text.selectAll();
			}
		});
		
		//创建gc,用于重画边框
		gc = new GC(group);
		
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("submit"),
				true).setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_B_USER_COMMIT));
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("cancle"), false).setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_B_USER_CANCEL));
	}

	
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(409, 368);
	}

	@Override
	protected void okPressed() {
		if (!validate()){
			return;
		}
		
		this.user.getId().setBankorgId(Long.parseLong(txtBankorgId.getText().trim()));
		this.user.getId().setUserId(txtUserId.getText().trim());
		this.user.setUserName(txtUserName.getText().trim());
		StructuredSelection selection = (StructuredSelection) cvDepartment.getSelection();
		String departmentId = Constants.INITIAL_USER_DEPARTMENT_ID;
		Object element = selection.getFirstElement();
		if (element instanceof BsDepartment){
			BsDepartment department = (BsDepartment) element;
			departmentId = department.getId().getDepartmentId();
		}
		this.user.setDepartmentId(String.valueOf(departmentId));
		StructuredSelection selection1 = (StructuredSelection) cvUserStatus.getSelection();
		this.user.setUserStatus(String.valueOf(selection1.getFirstElement()));
		
		if(MessageDialog.openConfirm(getShell(), I18nUtil.getMessage("CONFIRMINFO"), 
				I18nUtil.getMessage(info))){
			super.okPressed();
		}
	}
	
	/**
	 * 返回构造的实体类
	 * 
	 * @return BsUser
	 */
	public BsUser getResult(){
		return this.user;
	}
	
	private boolean validate(){
		//先用原色画框
		drawColorBorder(txtUserId.getBounds(), txtUserId.getParent().getBackground());
	    drawColorBorder(txtUserName.getBounds(), txtUserName.getParent().getBackground());
	    
		BsUser user = new BsUserBiz().findByUserId(txtUserId.getText().trim());
		
		if (txtUserId.getText() == null || txtUserId.getText().trim().length() == 0){
			setErrorMessage(I18nUtil.getMessage("USER_ID") + I18nUtil.getMessage("notnull"));
			drawColorBorder(txtUserId.getBounds(), SWTResourceManager.getColor(255, 0, 0));
			return false;
		} else if (user != null && this.editable){//如果用户存在，并且当前为新增操作
			setErrorMessage(I18nUtil.getMessage("userhasexisted", new Object[]{txtUserId.getText()}));
			drawColorBorder(txtUserId.getBounds(), SWTResourceManager.getColor(255, 0, 0));
			return false;
		}else if (txtUserName.getText() == null || txtUserName.getText().trim().length() == 0){
			setErrorMessage(I18nUtil.getMessage("USER_NAME")+I18nUtil.getMessage("notnull"));
			drawColorBorder(txtUserName.getBounds(), SWTResourceManager.getColor(255, 0, 0));
			return false;
		}
		setErrorMessage(null);
		return true;
	}

	/**
	 * 初始化下拉框
	 */
	private void initComboviewer(){
		//初始化用户状态下拉框
		cvUserStatus.setSelection(new StructuredSelection(UserStatus.valueOf(this.initialUserStatus)));
		//初始化用户部门下拉框，选中默认或用户所属部门
		Object element = this.departmentId;
		if (this.departmentId != null && !this.departmentId.equals(Constants.INITIAL_USER_DEPARTMENT_ID)){
			element = new BsDepartmentBiz().findByPk(
				new BsDepartmentId((Long) Context.getSessionMap().get(Constants.BANKORG_ID), 
						this.departmentId));
		}
		cvDepartment.setSelection(new StructuredSelection(element));
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(I18nUtil.getMessage("USER_MANAGER"));
		newShell.setImage(CacheImage.getCacheImage().getImage(IAppconstans.APPLICATION_ID, IImageKey.USER_MANAGER_ICON));
	}
	
	/**
	 * 重画边框
	 * @param rect 
	 * @param color
	 */
	private void drawColorBorder(Rectangle rect, Color color){
		gc.setForeground(color);  
		gc.drawRectangle(new Rectangle(rect.x - 1, rect.y - 1, rect.width + 1, rect.height + 1)); 
	}
}

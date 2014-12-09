/*
 * 文件名：DepartManagerDialog.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：弹出部门编辑对话框
 * 修改人：heyong
 * 修改时间：2011-9-25
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.dailogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsDepartment;
import cn.sunline.suncard.sde.bs.entity.BsDepartmentId;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.ui.resource.IAppconstans;
import cn.sunline.suncard.sde.bs.ui.resource.IImageKey;
import cn.sunline.suncard.sde.bs.ui.resource.IWidgetId;
import cn.sunline.suncard.sde.bs.ui.resource.SWTResourceManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.bs.util.WidgetPermissionUtil;

/**
 * 弹出部门编辑对话框
 * 该对话框用于部门的添加和修改操作
 * @author heyong
 * @version 1.0, 2011-9-26
 * @see 
 * @since 1.0
 */
public class DepartManagerDialog extends TitleAreaDialog {
	private Text txtBankorgId;
	private Text txtDepartId;
	private Text txtDepartName;

	private BsDepartment department;
	private boolean editable ;
	private String info;
	
	private GC gc;
	/**
	 * @wbp.parser.constructor
	 */
	public DepartManagerDialog(Shell parentShell) {
		super(parentShell);
	}
	
	public DepartManagerDialog(Shell parentShell, BsDepartment department){
		this(parentShell);
		this.department = (department == null? new BsDepartment(new BsDepartmentId()) : department);
		this.editable = (department == null? true : false);
		this.info = (department == null ? "CONFIRMADD" : "CONFIRMMODIFY");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(null);
		
		setTitleImage(CacheImage.getCacheImage().getImage(IAppconstans.APPLICATION_ID, IImageKey.DEPART_MANAGER_ICON));
		setTitle(I18nUtil.getMessage("DEPART_MANAGER"));
		setMessage(I18nUtil.getMessage("departinfo"));
		
		Group group = new Group(container, SWT.NONE);
		group.setBounds(10, 10, 413, 134);
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBounds(84, 23, 60, 15);
		lblNewLabel.setText(I18nUtil.getMessage("BANKORG_ID") + ":");
		
		txtBankorgId = new Text(group, SWT.BORDER);
		txtBankorgId.setBounds(150, 20, 166, 23);
		String bankorgId = String.valueOf(Context.getSessionMap().get(Constants.BANKORG_ID));
		txtBankorgId.setText((department.getId().getBankorgId()==null?  bankorgId : department.getId().getBankorgId().toString()));
		txtBankorgId.setEditable(false);
		
		Label lblNewLabel_2 = new Label(group, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		lblNewLabel_2.setBounds(94, 52, 50, 20);
		lblNewLabel_2.setText(I18nUtil.getMessage("DEPARTMENT_ID") + ":");
		
		txtDepartId = new Text(group, SWT.BORDER);
		txtDepartId.setBounds(150, 49, 166, 23);
		txtDepartId.setText(department.getId().getDepartmentId()==null? "" : department.getId().getDepartmentId());
		txtDepartId.setEditable(this.editable && WidgetPermissionUtil.checkPermission(IWidgetId.SDE_T_DEPT_DEPTID_TEXT));
		txtDepartId.setTextLimit(10);
		
		Label lblNewLabel_3 = new Label(group, SWT.NONE);
		lblNewLabel_3.setAlignment(SWT.RIGHT);
		lblNewLabel_3.setBounds(84, 81, 60, 20);
		lblNewLabel_3.setText(I18nUtil.getMessage("DEPARTMENT_NAME") + ":");
		
		txtDepartName = new Text(group, SWT.BORDER);
		txtDepartName.setBounds(150, 78, 166, 23);
		txtDepartName.setText(department.getDepartmentName()==null? "" : department.getDepartmentName());
		txtDepartName.setEditable(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_T_DEPT_DEPTNAME_TEXT));
		txtDepartName.setTextLimit(40);
		//创建gc,用于重画边框
		gc = new GC(group);
		
		return container;
	}

	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button buttonOk = createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("submit"), true);
		Button buttonCancel = createButton(parent, IDialogConstants.CANCEL_ID, I18nUtil.getMessage("cancle"), false);
		buttonOk.setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_B_DEPT_COMMIT));
		buttonCancel.setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_B_DEPT_CANCEL));
	}

	
	@Override
	protected void okPressed() {
		if(!validate()){
			return ;
		}
		
		this.department.getId().setBankorgId(new Long(txtBankorgId.getText().trim()));
		this.department.getId().setDepartmentId(txtDepartId.getText().trim());
		this.department.setDepartmentName(txtDepartName.getText().trim());
		
		if (MessageDialog.openConfirm(getShell(), I18nUtil.getMessage("CONFIRMINFO"), 
				I18nUtil.getMessage(info))){
			super.okPressed();
		}
	}

	/**
	 * 验证方法
	 */
	private boolean validate(){
		//先用原色画框
		drawColorBorder(txtDepartId.getBounds(), txtDepartId.getParent().getBackground());
	    drawColorBorder(txtDepartName.getBounds(), txtDepartName.getParent().getBackground());
	    
	    if (txtDepartId.getText() == null || txtDepartId.getText().trim().length() == 0){
	    	setErrorMessage(I18nUtil.getMessage("DEPARTMENT_ID") + I18nUtil.getMessage("notnull"));
	    	drawColorBorder(txtDepartId.getBounds(), SWTResourceManager.getColor(255, 0, 0));
			return false;
		}else if (txtDepartName.getText() == null || txtDepartName.getText().trim().length() == 0){
			setErrorMessage(I18nUtil.getMessage("DEPARTMENT_NAME")+I18nUtil.getMessage("notnull"));
			drawColorBorder(txtDepartName.getBounds(), SWTResourceManager.getColor(255, 0, 0));
			return false;
		}
	    setErrorMessage(null);
		return true;
	}
	
	/**
	 * 返回构造的实体类
	 * 
	 * @return BsDepartment
	 */
	public BsDepartment getResult(){
		return this.department;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(439, 298);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(I18nUtil.getMessage("DEPART_MANAGER"));
		newShell.setImage(CacheImage.getCacheImage().getImage(IAppconstans.APPLICATION_ID, IImageKey.DEPART_MANAGER_ICON));
	}

	@Override
	public boolean close() {
		
		return super.close();
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

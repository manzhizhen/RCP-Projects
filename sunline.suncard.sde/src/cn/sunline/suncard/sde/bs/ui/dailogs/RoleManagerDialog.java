/*
 * 文件名：RoleManagerDialog.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：角色编辑对话框
 * 修改人：heyong
 * 修改时间：2011-9-25
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.dailogs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.ActionGroup;

import cn.sunline.suncard.sde.bs.biz.BsFunctionBiz;
import cn.sunline.suncard.sde.bs.biz.BsRolemappingBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsFunction;
import cn.sunline.suncard.sde.bs.entity.BsRole;
import cn.sunline.suncard.sde.bs.entity.BsRoleId;
import cn.sunline.suncard.sde.bs.entity.BsRolemapping;
import cn.sunline.suncard.sde.bs.entity.BsRolemappingId;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.ui.actions.SelectActionGroup;
import cn.sunline.suncard.sde.bs.ui.provider.BsFuncTvLabelProvider;
import cn.sunline.suncard.sde.bs.ui.provider.TableViewerContentProvider;
import cn.sunline.suncard.sde.bs.ui.resource.IAppconstans;
import cn.sunline.suncard.sde.bs.ui.resource.IImageKey;
import cn.sunline.suncard.sde.bs.ui.resource.IWidgetId;
import cn.sunline.suncard.sde.bs.ui.resource.SWTResourceManager;
import cn.sunline.suncard.sde.bs.ui.sorter.BsFunctionViewerSorter;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.bs.util.WidgetPermissionUtil;

/**
 * 角色编辑对话框
 * 该对话框用于角色的添加和修改操作
 * @author heyong
 * @version 1.0, 2011-9-25
 * @see 
 * @since 1.0
 */
public class RoleManagerDialog extends TitleAreaDialog {
	
	private Text txtBankorgId;
	private Text txtRoleId;
	private Text txtPcId;
	private Text txtRoleName;
	
	private TableColumn tblclmnbankorgId;
	private TableColumn tblclmnpcId;
	private TableColumn tblclmnfuncId;
	private TableColumn tblclmnfuncName;

	private BsRole role;
	private boolean editable;
	private String info;
	private CheckboxTableViewer checkboxTableViewer;
	
	private GC gc;
	/**
	 * @wbp.parser.constructor
	 */
	public RoleManagerDialog(Shell parentShell) {
		super(parentShell);
	}
	
	public RoleManagerDialog(Shell parentShell, BsRole role){
		this(parentShell);
		this.role = (role == null? new BsRole(new BsRoleId()) : role);
		this.editable = (role == null? true : false);
		this.info = (role == null ? "CONFIRMADD" : "CONFIRMMODIFY");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(null);
		

		setTitleImage(CacheImage.getCacheImage().getImage(IAppconstans.APPLICATION_ID, IImageKey.ROLE_MANAGER_ICON));
		setTitle(I18nUtil.getMessage("ROLE_MANAGER"));
		setMessage(I18nUtil.getMessage("roleinfo"));
		
		Group group = new Group(container, SWT.NONE);
		group.setBounds(10, 0, 569, 75);
		
		Group group1 = new Group(container, SWT.NONE);
		group1.setText(I18nUtil.getMessage("funclist"));
		group1.setBounds(10, 61, 569, 230);
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		lblNewLabel.setBounds(1, 13, 69, 23);
		lblNewLabel.setText(I18nUtil.getMessage("BANKORG_ID") + ":");
		
		txtBankorgId = new Text(group, SWT.BORDER);
		txtBankorgId.setBounds(76, 10, 178, 26);
		String bankorgId = String.valueOf(Context.getSessionMap().get(Constants.BANKORG_ID));
		txtBankorgId.setText((role.getId().getBankorgId()==null?  bankorgId : role.getId().getBankorgId().toString()));
		txtBankorgId.setEditable(false);
		
		TableViewer tViewer = new TableViewer(group1, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		Table table = tViewer.getTable();
		table.setBounds(10, 20, 540, 200);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		this.checkboxTableViewer = new CheckboxTableViewer(table);
		
		tblclmnbankorgId = new TableColumn(table, SWT.NONE);
		tblclmnbankorgId.setWidth(100);
		tblclmnbankorgId.setText(I18nUtil.getMessage("BANKORG_ID"));
		
		tblclmnpcId = new TableColumn(table, SWT.NONE);
		tblclmnpcId.setWidth(120);
		tblclmnpcId.setText(I18nUtil.getMessage("PC_ID"));
		
		tblclmnfuncId = new TableColumn(table, SWT.NONE);
		tblclmnfuncId.setWidth(150);
		tblclmnfuncId.setText(I18nUtil.getMessage("FUNCTION_ID"));
		
		tblclmnfuncName = new TableColumn(table, SWT.NONE);
		tblclmnfuncName.setWidth(150);
		tblclmnfuncName.setText(I18nUtil.getMessage("FUNCTION_NAME"));
		
		this.checkboxTableViewer.setContentProvider(new TableViewerContentProvider());
		this.checkboxTableViewer.setLabelProvider(new BsFuncTvLabelProvider());
		
		this.checkboxTableViewer.setInput(new BsFunctionBiz().getAll());
		
		//添加排序器
		addViewerSorter();
				
		checkboxTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if(!selection.isEmpty()){
					Object element = selection.getFirstElement();
					boolean flag = true;
					if(checkboxTableViewer.getChecked(element)){
						flag = false;
					}
					checkboxTableViewer.setChecked(element, flag);
				}
			}
		});
		
		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		lblNewLabel_1.setBounds(305, 13, 60, 23);
		lblNewLabel_1.setText(I18nUtil.getMessage("PC_ID") + ":");
		
		txtPcId = new Text(group, SWT.BORDER);
		txtPcId.setBounds(371, 10, 178, 26);
		txtPcId.setText((String) (role.getId().getPcId()==null? Context.getSessionMap().get(Constants.PC_ID) : role.getId().getPcId()));
		txtPcId.setEditable(false);
		
		Label lblNewLabel_2 = new Label(group, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		lblNewLabel_2.setBounds(20, 45, 50, 20);
		lblNewLabel_2.setText(I18nUtil.getMessage("ROLE_ID") + ":");
		
		txtRoleId = new Text(group, SWT.BORDER);
		txtRoleId.setBounds(76, 42, 178, 23);
		txtRoleId.setText(role.getId().getRoleId()==null? "" : role.getId().getRoleId());
		txtRoleId.setTextLimit(10);
		txtRoleId.setEditable(this.editable);
		txtRoleId.setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_T_ROLE_ROLEID_TEXT));
		
		Label lblNewLabel_3 = new Label(group, SWT.NONE);
		lblNewLabel_3.setAlignment(SWT.RIGHT);
		lblNewLabel_3.setBounds(305, 45, 58, 20);
		lblNewLabel_3.setText(I18nUtil.getMessage("ROLE_NAME") + ":");
		
		txtRoleName = new Text(group, SWT.BORDER);
		txtRoleName.setBounds(371, 42, 178, 23);
		txtRoleName.setText(role.getRoleName()==null? "" : role.getRoleName());
		txtRoleName.setTextLimit(40);
		txtRoleName.setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_T_ROLE_ROLENAME_TEXT));
		
		ActionGroup actionGroup = new SelectActionGroup(this.checkboxTableViewer);
		actionGroup.fillContextMenu(new MenuManager());
		
		initFuncList();//实始化功能列表
		//创建gc,用于重画边框
		gc = new GC(group);
		
		return container;
	}

	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("submit"),
				true).setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_B_ROLE_COMMIT));
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("cancle"), false).setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_B_ROLE_CANCEL));
	}

	
	@Override
	protected void okPressed() {
		//如果验证没通过,直接返回
		if(!validate()){
			return ;
		}
		
		this.role.getId().setBankorgId(new Long(txtBankorgId.getText().trim()));
		this.role.getId().setRoleId(txtRoleId.getText().trim());
		this.role.getId().setPcId(txtPcId.getText().trim());

		this.role.setRoleName(txtRoleName.getText().trim());
		
		Set<BsRolemapping> bsRolemappings = new HashSet<BsRolemapping>();
		Object[] objs = checkboxTableViewer.getCheckedElements();
		BsRolemappingId mappingid;
		BsRolemapping bsRolemapping;
		for (int i=0; i<objs.length; i++){
			BsFunction function = (BsFunction)objs[i];
			mappingid = new BsRolemappingId();
			mappingid.setBankorgId(function.getId().getBankorgId());
			mappingid.setFunctionId(function.getId().getFunctionId());
			mappingid.setPcId(function.getId().getPcId());
			mappingid.setRoleId(this.role.getId().getRoleId());
			bsRolemapping = new BsRolemapping();
			bsRolemapping.setId(mappingid);
			bsRolemappings.add(bsRolemapping);
		}
		this.role.setBsRolemappings(bsRolemappings);
		
		if (MessageDialog.openConfirm(getShell(), I18nUtil.getMessage("CONFIRMINFO"), 
				I18nUtil.getMessage(info))){
			super.okPressed();
		}
	}

	private boolean validate(){
		//先用原色画框
		drawColorBorder(txtRoleId.getBounds(), txtRoleId.getParent().getBackground());
		drawColorBorder(txtRoleName.getBounds(), txtRoleName.getParent().getBackground());
		
		if (txtRoleId.getText() == null || txtRoleId.getText().trim().length() == 0){
			setErrorMessage(I18nUtil.getMessage("ROLE_ID") + I18nUtil.getMessage("notnull"));
			drawColorBorder(txtRoleId.getBounds(), SWTResourceManager.getColor(255, 0, 0));
			return false;	
		}else if (txtRoleName.getText() == null || txtRoleName.getText().trim().length() == 0){
			setErrorMessage(I18nUtil.getMessage("ROLE_NAME") + I18nUtil.getMessage("notnull"));
			drawColorBorder(txtRoleName.getBounds(), SWTResourceManager.getColor(255, 0, 0));
			return false;
		}
		setErrorMessage(null);
		return true;
	}

	/**
	 * 初始化功能列表
	 * 如果角色拥有相关功能时，选中该功能
	 * @param role 角色
	 */
	private void initFuncList(){
		List<BsRolemapping> bsRolemappings = new BsRolemappingBiz().getAllByRole(this.role);
		for (int i=0; i<checkboxTableViewer.getTable().getItemCount(); i++){
			BsFunction function = (BsFunction) checkboxTableViewer.getElementAt(i);
			
			BsRolemappingId id = new BsRolemappingId();
			id.setBankorgId(function.getId().getBankorgId());
			id.setFunctionId(function.getId().getFunctionId());
			id.setPcId(function.getId().getPcId());
			id.setRoleId(this.role.getId().getRoleId());
			BsRolemapping bsRolemapping = new BsRolemapping();
			bsRolemapping.setId(id);
			//如果存在，则标记为选中
			if (bsRolemappings.contains(bsRolemapping)){
				checkboxTableViewer.setChecked(function, true);
			}
		}
	}
	
	/**
	 * 返回构造的实体类
	 * 
	 * @return BsRole
	 */
	public BsRole getResult(){
		return this.role;
	}
	
	//添加排序器
	private void addViewerSorter(){
		tblclmnbankorgId.addSelectionListener(new SelectionAdapter() {
			//记录上一次排序是升序还是降序，默认为升序
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkboxTableViewer.setSorter(asc? BsFunctionViewerSorter.BANKORG_ID_ASC 
						: BsFunctionViewerSorter.BANKORG_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnpcId.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkboxTableViewer.setSorter(asc? BsFunctionViewerSorter.PC_ID_ASC 
						: BsFunctionViewerSorter.PC_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnfuncId.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkboxTableViewer.setSorter(asc? BsFunctionViewerSorter.FUNCTION_ID_ASC 
						: BsFunctionViewerSorter.FUNCTION_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnfuncName.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkboxTableViewer.setSorter(asc? BsFunctionViewerSorter.FUNCTION_NAME_ASC 
						: BsFunctionViewerSorter.FUNCTION_NAME_DESC);
				asc = !asc;
			}
		});
	}
		
	@Override
	protected Point getInitialSize() {
		return new Point(597, 452);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(I18nUtil.getMessage("ROLE_MANAGER"));
		newShell.setImage(CacheImage.getCacheImage().getImage(IAppconstans.APPLICATION_ID, IImageKey.ROLE_MANAGER_ICON));
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

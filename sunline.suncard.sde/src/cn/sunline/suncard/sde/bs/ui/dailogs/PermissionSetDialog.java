/*
 * 文件名：PermissionSetDialog.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：权限设置对话框
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.actions.ActionGroup;

import cn.sunline.suncard.sde.bs.biz.BsRoleBiz;
import cn.sunline.suncard.sde.bs.biz.BsUsermappingBiz;
import cn.sunline.suncard.sde.bs.entity.BsRole;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.entity.BsUsermapping;
import cn.sunline.suncard.sde.bs.entity.BsUsermappingId;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.ui.actions.SelectActionGroup;
import cn.sunline.suncard.sde.bs.ui.provider.BsRoleTvLabelProvider;
import cn.sunline.suncard.sde.bs.ui.provider.TableViewerContentProvider;
import cn.sunline.suncard.sde.bs.ui.resource.IAppconstans;
import cn.sunline.suncard.sde.bs.ui.resource.IImageKey;
import cn.sunline.suncard.sde.bs.ui.resource.IWidgetId;
import cn.sunline.suncard.sde.bs.ui.sorter.BsRoleViewerSorter;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.bs.util.WidgetPermissionUtil;

/**
 * 权限设置对话框
 * 权限设置对话框
 * @author heyong
 * @version 1.0, 2011-9-25
 * @see 
 * @since 1.0
 */
public class PermissionSetDialog extends TitleAreaDialog {

	private BsUser user;
	
	private CheckboxTableViewer cTableViewer;
	
	private TableColumn tblclmnbankorgId ;
	private TableColumn tblclmnpcId ;
	private TableColumn tblclmnroleId ;
	private TableColumn tblclmnroleName ;
	
	/**
	 * @wbp.parser.constructor
	 */
	public PermissionSetDialog(Shell parentShell) {
		super(parentShell);
	}
	
	public PermissionSetDialog(Shell parentShell, BsUser user){
		this(parentShell);
		this.user = user;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(null);
		
		setTitleImage(CacheImage.getCacheImage().getImage(IAppconstans.APPLICATION_ID, IImageKey.PERMISSION_MANAGER_ICON));
		setTitle(I18nUtil.getMessage("PEMISSION_SET"));
		setMessage(I18nUtil.getMessage("permissionsetinfo"));
		
		Group group = new Group(container, SWT.NONE);
		group.setText(I18nUtil.getMessage("rolelist"));
		group.setBounds(10, 0, 542, 241);
		
		TableViewer tableViewer = new TableViewer(group, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setBounds(10, 24, 524, 207);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		cTableViewer = new CheckboxTableViewer(table);
		
		tblclmnbankorgId = new TableColumn(table, SWT.NONE);
		tblclmnbankorgId.setWidth(100);
		tblclmnbankorgId.setText(I18nUtil.getMessage("BANKORG_ID"));
		
		tblclmnpcId = new TableColumn(table, SWT.NONE);
		tblclmnpcId.setWidth(120);
		tblclmnpcId.setText(I18nUtil.getMessage("PC_ID"));
		
		tblclmnroleId = new TableColumn(table, SWT.NONE);
		tblclmnroleId.setWidth(150);
		tblclmnroleId.setText(I18nUtil.getMessage("ROLE_ID"));
		
		tblclmnroleName = new TableColumn(table, SWT.NONE);
		tblclmnroleName.setWidth(150);
		tblclmnroleName.setText(I18nUtil.getMessage("ROLE_NAME"));
		
		cTableViewer.setContentProvider(new TableViewerContentProvider());
		cTableViewer.setLabelProvider(new BsRoleTvLabelProvider());
		
		cTableViewer.setInput(new BsRoleBiz().getAll());
		//添加排序器
		addViewerSorter();
		cTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if(!selection.isEmpty()){
					Object element = selection.getFirstElement();
					boolean flag = true;
					if(cTableViewer.getChecked(element)){
						flag = false;
					}
					cTableViewer.setChecked(element, flag);
				}
			}
		});
		
		ActionGroup actionGroup = new SelectActionGroup(this.cTableViewer);
		actionGroup.fillContextMenu(new MenuManager());
		
		initRoleList();
		
		return container;
	}

	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("submit"),
				true).setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_B_PERM_SET_CONFIRM));
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("cancle"), false).setEnabled(WidgetPermissionUtil.checkPermission(IWidgetId.SDE_B_PERM_SET_CANCEL));
	}

	
	@Override
	protected void okPressed() {
		if (MessageDialog.openConfirm(getShell(), I18nUtil.getMessage("CONFIRMINFO"), 
				I18nUtil.getMessage("CONFIRMADD"))){
			handlerSubmit();
		}
		
		super.okPressed();
	}

	/**
	 * 处理提交
	 * 
	 * @param biz 角色操作业务类
	 */
	private void handlerSubmit(){
		
		Set<BsUsermapping> bsUsermappings = new HashSet<BsUsermapping>();
		Object[] objs = cTableViewer.getCheckedElements();
		BsUsermappingId usermappingId;
		BsUsermapping bsUsermapping;
		for (int i=0; i<objs.length; i++){
			BsRole role = (BsRole)objs[i];
			usermappingId = new BsUsermappingId();
			usermappingId.setBankorgId(this.user.getId().getBankorgId());
			usermappingId.setPcId(role.getId().getPcId());
			usermappingId.setRoleId(role.getId().getRoleId());
			usermappingId.setUserId(this.user.getId().getUserId());
			bsUsermapping = new BsUsermapping();
			bsUsermapping.setId(usermappingId);
			bsUsermappings.add(bsUsermapping);
		}
		new BsUsermappingBiz().permissionSet(this.user, bsUsermappings);
	}
	
	/**
	 * 初始化功能列表，如果用户拥有某角色，则勾选某记录
	 */
	private void initRoleList(){
		List<BsUsermapping> bsUsermappings = new BsUsermappingBiz().getAllByUser(this.user);
		for (int i=0; i<cTableViewer.getTable().getItemCount(); i++){
			BsRole role = (BsRole) cTableViewer.getElementAt(i);
			
			BsUsermappingId id = new BsUsermappingId();
			id.setBankorgId(role.getId().getBankorgId());
			id.setPcId(role.getId().getPcId());
			id.setRoleId(role.getId().getRoleId());
			id.setUserId(this.user.getId().getUserId());
			BsUsermapping bsRolemapping = new BsUsermapping();
			bsRolemapping.setId(id);
			//如果存在，则标记为选中
			if (bsUsermappings.contains(bsRolemapping)){
				cTableViewer.setChecked(role, true);
			}
		}
	}
	
	private void addViewerSorter(){
		tblclmnbankorgId.addSelectionListener(new SelectionAdapter() {
			//记录上一次排序是升序还是降序，默认为升序
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				cTableViewer.setSorter(asc? BsRoleViewerSorter.BANKORG_ID_ASC 
						: BsRoleViewerSorter.BANKORG_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnpcId.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				cTableViewer.setSorter(asc? BsRoleViewerSorter.PC_ID_ASC 
						: BsRoleViewerSorter.PC_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnroleId.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				cTableViewer.setSorter(asc? BsRoleViewerSorter.ROLE_ID_ASC 
						: BsRoleViewerSorter.ROLE_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnroleName.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				cTableViewer.setSorter(asc? BsRoleViewerSorter.ROLE_NAME_ASC 
						: BsRoleViewerSorter.ROLE_NAME_DESC);
				asc = !asc;
			}
		});
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(568, 372);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(I18nUtil.getMessage("PEMISSION_SET"));
		newShell.setImage(CacheImage.getCacheImage().getImage(IAppconstans.APPLICATION_ID, IImageKey.PERMISSION_MANAGER_ICON));
	}
}

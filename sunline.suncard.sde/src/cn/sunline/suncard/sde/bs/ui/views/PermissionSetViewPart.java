/*
 * 文件名：PasswordmodifyViewPart.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：权限设置视图
 * 修改人：heyong
 * 修改时间：2011-9-25
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.views;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.part.ViewPart;

import cn.sunline.suncard.sde.bs.biz.BsUserBiz;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.ui.actions.PaginationActionGroup;
import cn.sunline.suncard.sde.bs.ui.dailogs.PermissionSetDialog;
import cn.sunline.suncard.sde.bs.ui.provider.BsUserTvLabelProvider;
import cn.sunline.suncard.sde.bs.ui.provider.TableViewerContentProvider;
import cn.sunline.suncard.sde.bs.ui.sorter.BsUserViewerSorter;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 
 * 权限设置视图
 * @author heyong
 * @version 1.0, 2011-9-25
 * @see 
 * @since 1.0
 */
public class PermissionSetViewPart extends ViewPart {
	public final static String ID = "cn.sunline.card.sde.ui.views.PermissionSetViewPart";
	
	private TableViewer tableViewer;
	
	private TableColumn tblclmnbankorgId;
	private TableColumn tblclmnuserId ;
	private TableColumn tblclmnuserName ;
	private TableColumn tblclmndepartId ;
	private TableColumn tblclmnuserStatus ;
	private TableColumn tblclmnlastloginDate ;

	public PermissionSetViewPart() {
	}
    //对权限界面进行排布
	@Override
	public void createPartControl(Composite parent) {
		ViewForm viewForm = new ViewForm(parent, SWT.FLAT);
		viewForm.setLayout(new FillLayout());
		
		tableViewer = new TableViewer(viewForm, SWT.BORDER | SWT.FULL_SELECTION );
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		tblclmnbankorgId = new TableColumn(table, SWT.NONE);
		tblclmnbankorgId.setWidth(80);
		tblclmnbankorgId.setText(I18nUtil.getMessage("BANKORG_ID"));
		
		tblclmnuserId = new TableColumn(table, SWT.NONE);
		tblclmnuserId.setWidth(100);
		tblclmnuserId.setText(I18nUtil.getMessage("USER_ID"));
		
		tblclmnuserName = new TableColumn(table, SWT.NONE);
		tblclmnuserName.setWidth(120);
		tblclmnuserName.setText(I18nUtil.getMessage("USER_NAME"));
		
		tblclmndepartId = new TableColumn(table, SWT.NONE);
		tblclmndepartId.setWidth(100);
		tblclmndepartId.setText(I18nUtil.getMessage("DEPARTMENT_ID"));
		
		tblclmnuserStatus = new TableColumn(table, SWT.NONE);
		tblclmnuserStatus.setWidth(100);
		tblclmnuserStatus.setText(I18nUtil.getMessage("USER_STATUS"));
		
		tblclmnlastloginDate = new TableColumn(table, SWT.NONE);
		tblclmnlastloginDate.setWidth(100);
		tblclmnlastloginDate.setText(I18nUtil.getMessage("LASET_LOGGIN_DATE"));
		
		//设置cTableViewer的内容提供者为new TableViewerContentProvider()
		tableViewer.setContentProvider(new TableViewerContentProvider());
		//设置cTableViewer的标签提供者
		tableViewer.setLabelProvider(new BsUserTvLabelProvider());
		//添加排序器
		addViewerSorter();
				
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				BsUser user = (BsUser) selection.getFirstElement();
				PermissionSetDialog dialog = new PermissionSetDialog(getSite().getShell(), user);
				dialog.open();
			}
		});
		
		//添加分页工具栏
		ToolBar toolBar = new ToolBar(viewForm, SWT.FLAT);
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		PaginationActionGroup<BsUser> actionGroup 
			= new PaginationActionGroup<BsUser>(tableViewer, new BsUserBiz()) ;
		actionGroup.fillActionToolBars(toolBarManager);
		//布局
		viewForm.setTopLeft(toolBar);
		viewForm.setContent(tableViewer.getControl());
		
		this.getViewSite().setSelectionProvider(tableViewer);
	}

	//添加排序器
	private void addViewerSorter(){
		tblclmnbankorgId.addSelectionListener(new SelectionAdapter() {
			//记录上一次排序是升序还是降序，默认为升序
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(asc? BsUserViewerSorter.BANKORG_ID_ASC 
						: BsUserViewerSorter.BANKORG_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnuserId.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(asc? BsUserViewerSorter.USER_ID_ASC 
						: BsUserViewerSorter.USER_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnuserName.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(asc? BsUserViewerSorter.USER_NAME_ASC 
						: BsUserViewerSorter.USER_NAME_DESC);
				asc = !asc;
			}
		});
		tblclmndepartId.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(asc? BsUserViewerSorter.DEPARTMENT_ID_ASC 
						: BsUserViewerSorter.DEPARTMENT_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnuserStatus.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(asc? BsUserViewerSorter.USER_STATUS_ASC 
						: BsUserViewerSorter.USER_STATUS_DESC);
				asc = !asc;
			}
		});
		tblclmnlastloginDate.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(asc? BsUserViewerSorter.LAST_LOGIN_DATE_ASC 
						: BsUserViewerSorter.LAST_LOGIN_DATE_DESC);
				asc = !asc;
			}
		});
	}
		
	@Override
	public void setFocus() {
	}

}

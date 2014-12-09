/*
 * 文件名：UserManagerViewPart.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：视图部分
 * 修改人： 周兵
 * 修改时间：2011-09-22
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.bs.ui.views;

import java.sql.Timestamp;
import java.util.Date;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import cn.sunline.suncard.sde.bs.biz.BsUserBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.tree.FunctionTree;
import cn.sunline.suncard.sde.bs.ui.actions.PaginationActionGroup;
import cn.sunline.suncard.sde.bs.ui.dailogs.UserManagerDialog;
import cn.sunline.suncard.sde.bs.ui.provider.BsUserTvLabelProvider;
import cn.sunline.suncard.sde.bs.ui.provider.TableViewerContentProvider;
import cn.sunline.suncard.sde.bs.ui.sorter.BsUserViewerSorter;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 视图部分
 * 点击用户管理按钮后弹出的视图
 * @author    周兵
 * @version   1.0  2011-09-22
 * @see       
 * @since     1.0 
 */
public class UserManagerViewPart extends ViewPart {
	Log log = LogManager.getLogger(UserManagerViewPart.class.getName());
	public static final String ID = "cn.sunline.card.sde.ui.views.UserManagerViewPart";
	
	private PaginationActionGroup<BsUser> actionGroup ;
	
	private CheckboxTableViewer cTableViewer;
	
	private TableColumn tblclmnbankorgId;
	private TableColumn tblclmnuserId ;
	private TableColumn tblclmnuserName ;
	private TableColumn tblclmndepartId ;
	private TableColumn tblclmnuserStatus ;
	private TableColumn tblclmnlastloginDate ;

	public UserManagerViewPart() {
	}
    //对用户管理进行排布
	@Override
	public void createPartControl(Composite parent) {
		ViewForm viewForm = new ViewForm(parent, SWT.NONE);
		viewForm.setLayout(new FillLayout());
		
		TableViewer tableViewer = new TableViewer(viewForm, SWT.BORDER | SWT.MULTI | SWT.CHECK | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		cTableViewer = new CheckboxTableViewer(table);
		
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
		tblclmndepartId.setText(I18nUtil.getMessage("DEPARTMENT"));
		
		tblclmnuserStatus = new TableColumn(table, SWT.NONE);
		tblclmnuserStatus.setWidth(150);
		tblclmnuserStatus.setText(I18nUtil.getMessage("USER_STATUS"));
		
		tblclmnlastloginDate = new TableColumn(table, SWT.NONE);
		tblclmnlastloginDate.setWidth(100);
		tblclmnlastloginDate.setText(I18nUtil.getMessage("LASET_LOGGIN_DATE"));
		
		//设置cTableViewer的内容提供者为new TableViewerContentProvider()
		cTableViewer.setContentProvider(new TableViewerContentProvider());
		//设置cTableViewer的标签提供者
		cTableViewer.setLabelProvider(new BsUserTvLabelProvider());
		//添加排序器
		addViewerSorter();
		//添加监听器
		addListener();
		//添加分页工具栏
		ToolBar toolBar = new ToolBar(viewForm, SWT.FLAT);
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		actionGroup = new PaginationActionGroup<BsUser>(cTableViewer, new BsUserBiz()) ;
		actionGroup.fillActionToolBars(toolBarManager);
		//布局
		viewForm.setTopLeft(toolBar);
		viewForm.setContent(cTableViewer.getControl());
		
		this.getViewSite().setSelectionProvider(cTableViewer);
	}
	
	/**
	 * 为viewer添加监听
	 * @param cTableViewer
	 */
	private void addListener() {
		//添加选择改变监听
		cTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				if(!selection.isEmpty()){
					Object element = selection.getFirstElement();
					boolean flag = true;
					//如果此元素已经勾对
					if (cTableViewer.getChecked(element)){
						flag = false;
					}
					cTableViewer.setChecked(element, flag);
				}
			}
		});
		//添加双击监听
		cTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				BsUser user = (BsUser) selection.getFirstElement();
				UserManagerDialog dialog = new UserManagerDialog(getSite().getShell(), user);
				dialog.open();
				if(dialog.getReturnCode() == Window.OK){
					//返回对话框构建的实体类对象
					user = dialog.getResult();
					//设置更新信息
					user.setModiDate(new Timestamp(new Date().getTime()));
					BsUser bsUser = (BsUser) Context.getSessionMap().get(Constants.CURRENT_USER);
					user.setModiUser(bsUser.getId().getUserId());
					//将版本设置当前版本+1
					user.setVersion(user.getVersion()==null? 1 : user.getVersion()+1);
					BsUserBiz biz = new BsUserBiz();
					biz.updateUser(user);
					//更新TableViewer的记录，并选中当前修改的记录
					cTableViewer.update(user, null);
					//刷新并展开相关功能树
					try {
						FunctionTreeViewPart viewPart = (FunctionTreeViewPart) getSite().getWorkbenchWindow().getActivePage().showView(FunctionTreeViewPart.ID);
						viewPart.refresh(new FunctionTree(I18nUtil.getMessage("USER_MANAGER"), BsUser.class));
					} catch (PartInitException e) {
						e.printStackTrace();
						log.error("刷新并展开相关功能树"+e.getMessage());
					}
				}
			}
		});
	}

	
	//添加排序器
	private void addViewerSorter(){
		tblclmnbankorgId.addSelectionListener(new SelectionAdapter() {
			//记录上一次排序是升序还是降序，默认为升序
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				cTableViewer.setSorter(asc? BsUserViewerSorter.BANKORG_ID_ASC 
						: BsUserViewerSorter.BANKORG_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnuserId.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				cTableViewer.setSorter(asc? BsUserViewerSorter.USER_ID_ASC 
						: BsUserViewerSorter.USER_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnuserName.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				cTableViewer.setSorter(asc? BsUserViewerSorter.USER_NAME_ASC 
						: BsUserViewerSorter.USER_NAME_DESC);
				asc = !asc;
			}
		});
		tblclmndepartId.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				cTableViewer.setSorter(asc? BsUserViewerSorter.DEPARTMENT_ID_ASC 
						: BsUserViewerSorter.DEPARTMENT_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnuserStatus.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				cTableViewer.setSorter(asc? BsUserViewerSorter.USER_STATUS_ASC 
						: BsUserViewerSorter.USER_STATUS_DESC);
				asc = !asc;
			}
		});
		tblclmnlastloginDate.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				cTableViewer.setSorter(asc? BsUserViewerSorter.LAST_LOGIN_DATE_ASC 
						: BsUserViewerSorter.LAST_LOGIN_DATE_DESC);
				asc = !asc;
			}
		});
	}
	
	public PaginationActionGroup<BsUser> getActionGroup(){
		return actionGroup;
	}
	
	@Override
	public void dispose() {
		this.actionGroup.dispose();
		super.dispose();
	}
	
	@Override
	public void setFocus() {
	}

	
}

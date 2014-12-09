/*
 * 文件名：DepartManagerViewPart.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：视图部分
 * 修改人： 周兵
 * 修改时间：2011-09-22
 * 修改内容：新增
 * 
 * 修改人：heyong
 * 修改时间：2011-10-21
 * 修改内容：添加排序器方法,addViewerSorter(final CheckboxTableViewer ctv)
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

import cn.sunline.suncard.sde.bs.biz.BsDepartmentBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsDepartment;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.tree.FunctionTree;
import cn.sunline.suncard.sde.bs.ui.actions.PaginationActionGroup;
import cn.sunline.suncard.sde.bs.ui.dailogs.DepartManagerDialog;
import cn.sunline.suncard.sde.bs.ui.provider.BsDepartTvLabelProvider;
import cn.sunline.suncard.sde.bs.ui.provider.TableViewerContentProvider;
import cn.sunline.suncard.sde.bs.ui.sorter.BsDepartmentViewerSorter;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 视图部分
 * 点击部门管理按钮后弹出的视图
 * @author    周兵
 * @version   1.0  2011-09-22
 * @see       
 * @since     1.0 
 */
public class DepartManagerViewPart extends ViewPart {
	Log log = LogManager.getLogger(DepartManagerViewPart.class.getName());
	public static final String ID = "cn.sunline.card.sde.ui.views.DepartManagerViewPart";
	
	private CheckboxTableViewer cTableViewer;
	
	private PaginationActionGroup<BsDepartment> actionGroup ;
	
	private TableColumn tblclmnbankorgId ;
	private TableColumn tblclmndepartId ;
	private TableColumn tblclmndepartName ;
	
	public DepartManagerViewPart() {
	}
   //对部门管理进行排布
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
		tblclmnbankorgId.setWidth(100);
		tblclmnbankorgId.setText(I18nUtil.getMessage("BANKORG_ID"));
		
		tblclmndepartId = new TableColumn(table, SWT.NONE);
		tblclmndepartId.setWidth(120);
		tblclmndepartId.setText(I18nUtil.getMessage("DEPARTMENT_ID"));
		
		tblclmndepartName = new TableColumn(table, SWT.NONE);
		tblclmndepartName.setWidth(150);
		tblclmndepartName.setText(I18nUtil.getMessage("DEPARTMENT_NAME"));
		
		//设置cTableViewer的内容提供者为new TableViewerContentProvider()
		cTableViewer.setContentProvider(new TableViewerContentProvider());
		//设置cTableViewer的标签提供者
		cTableViewer.setLabelProvider(new BsDepartTvLabelProvider());
		//添加排序器
		addViewerSorter();
		//添加监听
		addListener();
		
		//添加分页工具栏
		ToolBar toolbar = new ToolBar(viewForm, SWT.FLAT);
		ToolBarManager toolBarManager = new ToolBarManager(toolbar);
		actionGroup	= new PaginationActionGroup<BsDepartment>(cTableViewer, new BsDepartmentBiz());
		actionGroup.fillActionToolBars(toolBarManager);
		viewForm.setTopLeft(toolbar);
		viewForm.setContent(cTableViewer.getControl());
		
		this.getViewSite().setSelectionProvider(cTableViewer);
	}

	//添加排序器
	private void addViewerSorter(){
		tblclmnbankorgId.addSelectionListener(new SelectionAdapter() {
			//记录上一次排序是升序还是降序，默认为升序
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				cTableViewer.setSorter(asc? BsDepartmentViewerSorter.BANKORG_ID_ASC 
						: BsDepartmentViewerSorter.BANKORG_ID_DESC);
				asc = !asc;
			}
		});
		tblclmndepartId.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				cTableViewer.setSorter(asc? BsDepartmentViewerSorter.DEPARTMENT_ID_ASC 
						: BsDepartmentViewerSorter.DEPARTMENT_ID_DESC);
				asc = !asc;
			}
		});
		tblclmndepartName.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				cTableViewer.setSorter(asc? BsDepartmentViewerSorter.DEPARTMENT_NAME_ASC 
						: BsDepartmentViewerSorter.DEPARTMENT_NAME_DESC);
				asc = !asc;
			}
		});
	}
	
	/**
	 * 添加监听
	 */
	private void addListener(){
		cTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
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
		
		cTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				BsDepartment department = (BsDepartment) selection.getFirstElement();
				DepartManagerDialog dialog = new DepartManagerDialog(getSite().getShell(), department);
				dialog.open();
				if (dialog.getReturnCode() == Window.OK){
					department.setModiDate(new Timestamp(new Date().getTime()));
					BsUser user = (BsUser) Context.getSessionMap().get(Constants.CURRENT_USER);
					department.setModiUser(user.getId().getUserId());
					department.setVersion(department.getVersion()==null? 1 : department.getVersion()+1);
					BsDepartmentBiz biz = new BsDepartmentBiz();
					biz.update(department);
					//更新TableViewer的记录，并选中当前修改的记录
					cTableViewer.update(department, null);
					//刷新并展开相关功能树
					try {
						FunctionTreeViewPart viewPart = (FunctionTreeViewPart) getSite().getWorkbenchWindow().getActivePage().showView(FunctionTreeViewPart.ID);
						viewPart.refresh(new FunctionTree(I18nUtil.getMessage("DEPART_MANAGER"), BsDepartment.class));
					} catch (PartInitException e) {
						e.printStackTrace();
						log.error("刷新并展开相关功能树"+e.getMessage());
					}
				}
			}
		});
	}
	
	public PaginationActionGroup<BsDepartment> getActionGroup(){
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

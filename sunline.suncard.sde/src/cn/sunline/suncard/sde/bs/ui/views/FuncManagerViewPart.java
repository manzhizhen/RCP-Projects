/*
 * 文件名：FunManagerViewPart.java
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

import cn.sunline.suncard.sde.bs.biz.BsFunctionBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsFunction;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.tree.FunctionTree;
import cn.sunline.suncard.sde.bs.ui.actions.PaginationActionGroup;
import cn.sunline.suncard.sde.bs.ui.dailogs.FuncManagerDialog;
import cn.sunline.suncard.sde.bs.ui.provider.BsFuncTvLabelProvider;
import cn.sunline.suncard.sde.bs.ui.provider.TableViewerContentProvider;
import cn.sunline.suncard.sde.bs.ui.sorter.BsFunctionViewerSorter;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 视图部分
 * 点击功能管理按钮后弹出的视图
 * @author    周兵
 * @version   1.0  2011-09-22
 * @see       
 * @since     1.0 
 */
public class FuncManagerViewPart extends ViewPart {
	Log log = LogManager.getLogger(FuncManagerViewPart.class.getName());
	public static final String ID = "cn.sunline.card.sde.ui.views.FuncManagerViewPart";
	
	private PaginationActionGroup<BsFunction> actionGroup;
	
	private CheckboxTableViewer cTableViewer;
	
	private TableColumn tblclmnbankorgId;
	private TableColumn tblclmnpcId;
	private TableColumn tblclmnfuncId;
	private TableColumn tblclmnfuncName;

	public FuncManagerViewPart() {
	}
   //对功能管理界面进行排布
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
		
		tblclmnpcId = new TableColumn(table, SWT.NONE);
		tblclmnpcId.setWidth(120);
		tblclmnpcId.setText(I18nUtil.getMessage("PC_ID"));
		
		tblclmnfuncId = new TableColumn(table, SWT.NONE);
		tblclmnfuncId.setWidth(150);
		tblclmnfuncId.setText(I18nUtil.getMessage("FUNCTION_ID"));
		
		tblclmnfuncName = new TableColumn(table, SWT.NONE);
		tblclmnfuncName.setWidth(150);
		tblclmnfuncName.setText(I18nUtil.getMessage("FUNCTION_NAME"));
		
		//设置cTableViewer的内容提供者为new TableViewerContentProvider()
		cTableViewer.setContentProvider(new TableViewerContentProvider());
		//设置cTableViewer的标签提供者
		cTableViewer.setLabelProvider(new BsFuncTvLabelProvider());
		
		//添加排序器
		addViewerSorter();
		//添加监听
		addListener();
		
		//添加工具栏
		ToolBar toolbar = new ToolBar(viewForm, SWT.FLAT);
		ToolBarManager toolBarManager = new ToolBarManager(toolbar);
		actionGroup = new PaginationActionGroup<BsFunction>(cTableViewer, new BsFunctionBiz());
		actionGroup.fillActionToolBars(toolBarManager);
		//布局
		viewForm.setTopLeft(toolbar);
		viewForm.setContent(cTableViewer.getControl());
		
		this.getViewSite().setSelectionProvider(cTableViewer);
		
	}
	
	private void addListener() {
		cTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (!selection.isEmpty()){
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
				BsFunction function = (BsFunction) selection.getFirstElement();
				FuncManagerDialog dialog = new FuncManagerDialog(getSite().getShell(), function);
				dialog.open();
				if (dialog.getReturnCode() == Window.OK){
					//设置更新信息
					function.setModiDate(new Timestamp(new Date().getTime()));
					BsUser user = (BsUser) Context.getSessionMap().get(Constants.CURRENT_USER);
					function.setModiUser(user.getId().getUserId());
					function.setVersion(function.getVersion()==null? 1 : function.getVersion()+1);
					
					new BsFunctionBiz().update(function);
					cTableViewer.update(function, null);
					//刷新并展开相关功能树
					try {
						FunctionTreeViewPart viewPart = (FunctionTreeViewPart) getSite().getWorkbenchWindow().getActivePage().showView(FunctionTreeViewPart.ID);
						viewPart.refresh(new FunctionTree(I18nUtil.getMessage("FUNCTION_MANAGER"), BsFunction.class));
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
				cTableViewer.setSorter(asc? BsFunctionViewerSorter.BANKORG_ID_ASC 
						: BsFunctionViewerSorter.BANKORG_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnpcId.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				cTableViewer.setSorter(asc? BsFunctionViewerSorter.PC_ID_ASC 
						: BsFunctionViewerSorter.PC_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnfuncId.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				cTableViewer.setSorter(asc? BsFunctionViewerSorter.FUNCTION_ID_ASC 
						: BsFunctionViewerSorter.FUNCTION_ID_DESC);
				asc = !asc;
			}
		});
		tblclmnfuncName.addSelectionListener(new SelectionAdapter() {
			boolean asc = true;
			@Override
			public void widgetSelected(SelectionEvent e) {
				cTableViewer.setSorter(asc? BsFunctionViewerSorter.FUNCTION_NAME_ASC 
						: BsFunctionViewerSorter.FUNCTION_NAME_DESC);
				asc = !asc;
			}
		});
	}
	
	public PaginationActionGroup<BsFunction> getActionGroup(){
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

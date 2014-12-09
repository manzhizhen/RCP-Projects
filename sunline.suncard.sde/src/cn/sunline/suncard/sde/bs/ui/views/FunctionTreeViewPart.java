/*
 * 文件名：FunctionTreeViewPart.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：功能导航树视图
 * 修改人：周兵
 * 修改时间：2011-10-13
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.views;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import cn.sunline.suncard.sde.bs.entity.BsDepartment;
import cn.sunline.suncard.sde.bs.entity.BsFunction;
import cn.sunline.suncard.sde.bs.entity.BsRole;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.entity.BsUsermapping;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.tree.FunctionTree;
import cn.sunline.suncard.sde.bs.tree.FunctionTreeFactory;
import cn.sunline.suncard.sde.bs.ui.provider.FunctionTreeContentProvider;
import cn.sunline.suncard.sde.bs.ui.provider.FunctionTreeLabelProvider;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 功能导航树视图
 * @author zhoub
 * @version 1.0, 2011-10-24
 * @see 
 * @since 1.0
 */
public class FunctionTreeViewPart extends ViewPart {
	Log log = LogManager.getLogger(FunctionTreeViewPart.class.getName());
	public static final String ID = "cn.sunline.suncard.sde.ui.views.FunctionTreeViewPart";
	
	private TreeViewer tv;

	public FunctionTreeViewPart() {
	}

	@Override
	public void createPartControl(Composite parent) {
		tv = new TreeViewer(parent,SWT.BORDER);
		tv.setContentProvider(new FunctionTreeContentProvider());
		tv.setLabelProvider(new FunctionTreeLabelProvider());
		this.refresh(null);
		tv.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				Object element = selection.getFirstElement();
				String viewId = WelcomeDefaultViewPart.ID;
				if ( (element instanceof FunctionTree && ((FunctionTree)element).getType() == BsUser.class )
						|| element instanceof BsUser){
					viewId = UserManagerViewPart.ID;
				}else if ( (element instanceof FunctionTree && ((FunctionTree)element).getType() == BsDepartment.class)
						|| element instanceof BsDepartment){
					viewId = DepartManagerViewPart.ID;
				}else if((element instanceof FunctionTree && ((FunctionTree)element).getType() == BsRole.class )
						|| element instanceof BsRole){
					viewId = RoleManagerViewPart.ID;
				}else if((element instanceof FunctionTree && ((FunctionTree)element).getType() == BsFunction.class )
						|| element instanceof BsFunction){
					viewId = FuncManagerViewPart.ID;
				}else if(element instanceof FunctionTree && ((FunctionTree)element).getType() == BsUsermapping.class ){
					viewId = PermissionSetViewPart.ID;
				}
				
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					page.showView(viewId);
				} catch (PartInitException e) {
					e.printStackTrace();
					log.error("刷新并展开相关功能树"+e.getMessage());
				}
			}
		});
		this.getViewSite().setSelectionProvider(tv);
	}

	/**
	 * 刷新功能树列表
	 * 当进行增加，修改和删除操作时，会实时更新功能树中数据
	 * @param functionTree 父元素
	 */
	public void refresh(FunctionTree functionTree){
		tv.setInput(FunctionTreeFactory.createTreeData());
		//展开第一层
		tv.expandToLevel(new FunctionTree(I18nUtil.getMessage("funcnavigation"), null), 1);
		//如果function不为null，则展开第二层
		if (functionTree != null){
			tv.expandToLevel(functionTree, 2);
		}
	}
	
	@Override
	public void setFocus() {
	}
}



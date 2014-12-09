/*
 * 文件名：SelectActionGroup.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：选择上下文菜单
 * 修改人：heyong
 * 修改时间：2011-10-19
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionGroup;

import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 选择上下文菜单
 * 此类用于实现对记录的全选及取消全选上下文菜单栏
 * @author heyong
 * @version 1.0, 2011-10-23
 * @see 
 * @since 1.0
 */
public class SelectActionGroup extends ActionGroup {
	private CheckboxTableViewer cTableViewer;
	
	private IAction selectAllAction;
	private IAction cancleAllAction;
	
	public SelectActionGroup(CheckboxTableViewer cTableViewer) {
		this.cTableViewer = cTableViewer;
		makeActions();
	}

	private void makeActions(){
		selectAllAction = new Action(I18nUtil.getMessage("selectall")) {
			@Override
			public void run() {
				cTableViewer.setAllChecked(true);
			
			}
			
		}; 
		cancleAllAction = new Action(I18nUtil.getMessage("cancelselall")) {
			@Override
			public void run() {
				cTableViewer.setAllChecked(false);
			}
		};
	}
	
	@Override
	public void fillContextMenu(IMenuManager menu) {
		MenuManager menuManager = (MenuManager) menu;
		//当显示上下文菜单时，删除所有菜单
		menuManager.setRemoveAllWhenShown(true);
		//当菜单显示时，触发此监听
		menuManager.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				//初始化Action
				changeActionEnabled();
				manager.add(selectAllAction);
				manager.add(cancleAllAction);
			}
		});
		
		Table table = cTableViewer.getTable();
		Menu m = menuManager.createContextMenu(table);
		table.setMenu(m);
	
	}

	
	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
	}

	/**
	 * 根据选中记录数改变按钮enabled属性值
	 */
	private void changeActionEnabled(){
		//表记录数
		int itemCount = cTableViewer.getTable().getItemCount();
		//如果表记录数为0
		if (itemCount == 0){
			selectAllAction.setEnabled(false);
			cancleAllAction.setEnabled(false);
		}else if (cTableViewer.getCheckedElements().length == itemCount){//如果记录全部选中
			selectAllAction.setEnabled(false);
			cancleAllAction.setEnabled(true);
		}else if (cTableViewer.getCheckedElements().length == 0){//如果记录全部取消选中
			selectAllAction.setEnabled(true);
			cancleAllAction.setEnabled(false);
		}else{
			selectAllAction.setEnabled(true);
			cancleAllAction.setEnabled(true);
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
}

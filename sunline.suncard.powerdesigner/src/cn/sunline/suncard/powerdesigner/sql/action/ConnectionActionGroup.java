/* 文件名：     ConnectionActionGroup.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-14
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.sql.action;

import java.sql.SQLException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionGroup;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.sql.model.ConnectionModel;
import cn.sunline.suncard.powerdesigner.sql.model.ConnectionSpaceModel;
import cn.sunline.suncard.powerdesigner.sql.view.ConnectionTreeView;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;

/**
 * 数据库连接树的ActionGroup
 * @author  Manzhizhen
 * @version 1.0, 2013-1-14
 * @see 
 * @since 1.0
 */
public class ConnectionActionGroup extends ActionGroup{
	private ConnectionTreeView connectionTreeView;
	
	private IAction newConnectionAction;
	private IAction connectionDBAction;
	private IAction delConnectionAction;
	private IAction editConnectionAction;
	private IAction refreshConnectionAction;
	
	public ConnectionActionGroup(ConnectionTreeView connectionTreeView) {
		this.connectionTreeView = connectionTreeView;
	}
	
	@Override
	public void fillContextMenu(IMenuManager menu) {
		try {
			initAction();
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CanNotFoundNodeIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FoundTreeNodeNotUniqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	// 初始化Action
		
		MenuManager menuManager = (MenuManager) menu;
		menuManager.setRemoveAllWhenShown(true);
		
		menuManager.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				Object obj = getCurrentSelection();
				if(obj instanceof TreeContent) {
					TreeContent treeContent = (TreeContent) obj;
					Object dataObject = treeContent.getObj();
					
					if(dataObject instanceof ConnectionSpaceModel) {
						manager.add(newConnectionAction);
						
					} else if(dataObject instanceof ConnectionModel) {
						manager.add(connectionDBAction);
						manager.add(refreshConnectionAction);
						manager.add(delConnectionAction);
						manager.add(editConnectionAction);
					}
				}
				
			}
		});
		
		Tree tree = connectionTreeView.getTreeViewer().getTree();
		Menu treeMenu = menuManager.createContextMenu(tree);
		tree.setMenu(treeMenu);
		
		super.fillContextMenu(menu);
	}

	private void initAction() throws PartInitException, SQLException, CanNotFoundNodeIDException, FoundTreeNodeNotUniqueException {
		newConnectionAction = new ConnectionAction(DmConstants.NEW_CONNECTION
				, connectionTreeView);
		connectionDBAction = new ConnectionAction(DmConstants.CONNECTION_DATABASE
				, connectionTreeView);
		delConnectionAction = new ConnectionAction(DmConstants.DEL_CONNECTION
				, connectionTreeView);
		editConnectionAction = new ConnectionAction(DmConstants.EDIT_CONNECTION
				, connectionTreeView);
		refreshConnectionAction = new ConnectionAction(DmConstants.REFRESH_CONNECTION
				, connectionTreeView);
		
	}

	private TreeContent getCurrentSelection() {
		return connectionTreeView.getCurrentSelection();
	}
}

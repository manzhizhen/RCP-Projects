/* 文件名：     ConnectionTreeView.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-11
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.sql.view;

import java.util.List;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.sql.action.ConnectionActionGroup;
import cn.sunline.suncard.powerdesigner.sql.action.OpenSqlExecuteEditorAction;
import cn.sunline.suncard.powerdesigner.sql.model.ConnectionModel;
import cn.sunline.suncard.powerdesigner.sql.model.ConnectionSpaceModel;
import cn.sunline.suncard.powerdesigner.sql.model.SqlDatabaseModel;
import cn.sunline.suncard.powerdesigner.sql.model.SqlTableModel;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeViewComposite;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 管理数据库连接的View
 * 
 * @author Manzhizhen
 * @version 1.0, 2013-1-11
 * @see
 * @since 1.0
 */
public class ConnectionTreeView extends ViewPart {
	public final static String ID = "cn.sunline.suncard.powerdesigner.tree.ConnectionTreeView";
	private Tree tree;
	private TreeViewer treeViewer;

	private static Log logger = LogManager.getLogger(ConnectionTreeView.class
			.getName());

	private TreeViewComposite treeViewComposite;
	
	@Override
	public void createPartControl(Composite parent) {
		treeViewComposite = new TreeViewComposite(parent, true);

		tree = treeViewComposite.getTree();
		treeViewer = treeViewComposite.getTreeViewer();

		// 创建树的内容
		createContent();

		createEvent();

		// 创建树的右键菜单
		createTreeMenu();
		
		// 创建树的工具条菜单
		createTreeToolMenu();
		
	}

	/**
	 * 创建树的工具条菜单
	 */
	private void createTreeToolMenu() {
		IAction action = new OpenSqlExecuteEditorAction();
		IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
		manager.add(action);
	}

	/**
	 * 创建菜单
	 */
	private void createTreeMenu() {
		ConnectionActionGroup connectionActionGroup = new ConnectionActionGroup(
				this);
		connectionActionGroup.fillContextMenu(new MenuManager());
	}

	private void createContent() {
		Image image = CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.CONNECTION_SPACE_IMAGE);

		// 添加一棵树
		treeViewComposite.createTreeRootNode(DmConstants.DB_CONNECTION_TREE_ID,
				new ConnectionSpaceModel(), image);
	}

	private void createEvent() {
	}

	@Override
	public void setFocus() {
		if (tree != null) {
			tree.setFocus();
		}
	}

	/**
	 * 添加一个数据库连接模型
	 * 
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 * @throws AddFileModelToWorkSpaceModelFail
	 */
	public TreeContent addConnectionModel(ConnectionModel connectionModel)
			throws CanNotFoundNodeIDException, FoundTreeNodeNotUniqueException {
		if (connectionModel == null) {
			logger.error("传入的ConnectionModel为空，无法在ConnectionTreeView上添加该连接！");
			return null;
		}

		// 添加数据库连接节点
		TreeContent connectionTreeContent = treeViewComposite.addNode(
				DmConstants.DB_CONNECTION_TREE_ID,
				DmConstants.DB_CONNECTION_TREE_ID + DmConstants.SEPARATOR
						+ connectionModel.getDriverName(),
				connectionModel,
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID,
						IDmImageKey.CONNECTION_MODEL_16));

		// 在旗下添加数据库模型
		for (SqlDatabaseModel sqlDatabaseModel : connectionModel
				.getDatabaseModelMap().values()) {
			addDatabaseModel(sqlDatabaseModel, connectionTreeContent);
		}

		// 在数据库连接空间模型中添加此数据库连接模型
		ConnectionSpaceModel.getConnectionModelMap().put(
				connectionModel.getDriverName(), connectionModel);

		return connectionTreeContent;
	}

	/**
	 * 添加一个数据库模型
	 * 
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 * @throws AddFileModelToWorkSpaceModelFail
	 */
	public TreeContent addDatabaseModel(SqlDatabaseModel databaseModel,
			TreeContent parentTreeContent) throws CanNotFoundNodeIDException,
			FoundTreeNodeNotUniqueException {
		if (databaseModel == null || parentTreeContent == null
				|| !(parentTreeContent.getObj() instanceof ConnectionModel)) {
			logger.error("传入的数据不完整，无法在ConnectionTreeView上添加该数据库模型！");
			return null;
		}

		// 添加数据库模型节点
		TreeContent databaseTreeContent = treeViewComposite.addNode(
				parentTreeContent.getId(),
				DmConstants.DB_CONNECTION_TREE_ID + DmConstants.SEPARATOR
						+ databaseModel.getName(),
				databaseModel,
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID,
						IDmImageKey.DATABASE_MODEL_16));

		for (SqlTableModel sqlTableModel : databaseModel.getTableMap().values()) {
			addTableModel(sqlTableModel, databaseTreeContent);
		}

		return databaseTreeContent;
	}

	/**
	 * 添加一个数据库模型
	 * 
	 * @throws CanNotFoundNodeIDException
	 * @throws FoundTreeNodeNotUniqueException
	 * @throws AddFileModelToWorkSpaceModelFail
	 */
	public TreeContent addTableModel(SqlTableModel tableModel,
			TreeContent parentTreeContent) throws CanNotFoundNodeIDException,
			FoundTreeNodeNotUniqueException {
		if (tableModel == null || parentTreeContent == null
				|| parentTreeContent.getObj() instanceof SqlDatabaseModel) {
			logger.error("传入的数据不完整，无法在ConnectionTreeView上添加该表格模型！");
			return null;
		}

		// 添加数据库模型节点
		TreeContent tableTreeContent = treeViewComposite.addNode(
				parentTreeContent.getId(),
				parentTreeContent.getId() + DmConstants.SEPARATOR
						+ tableModel.getName(),
				tableModel,
				CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID,
						IDmImageKey.TABLE_16));

		return tableTreeContent;
	}

	/**
	 * 获得选择的树节点
	 * 
	 * @return
	 */
	public TreeContent getCurrentSelection() {
		IStructuredSelection select = (IStructuredSelection) treeViewer
				.getSelection();
		TreeContent treeContent = (TreeContent) select.getFirstElement();

		return treeContent;
	}
	
	/**
	 * 获得所选择的ConnectionModel对象
	 * @return
	 */
	public ConnectionModel getSelectConnectionModel()	 {
		TreeContent treeContent = getCurrentSelection();
		if(treeContent == null) {
			return null;
		}
		
		Object obj = null;
		// 从选择的树节点不断向上遍历，直到找到顶级树节点，也就是连接模型所绑定的树节点
		while(treeContent.getParent() != null) {
			treeContent = treeContent.getParent();
		}
		
		obj = treeContent.getObj();
		
		if(obj instanceof ConnectionModel) {
			return (ConnectionModel) obj;
		} else {
			logger.error("从ConnectionTreeView中获取当前选中的ConnectionModel树节点失败！");
			return null;
		}
	}
	
	/**
	 * 获得所选择的SqlDatabaseModel对象
	 * @return
	 */
	public SqlDatabaseModel getSelectDatabaseModel() {
		return null;
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

}

/* 文件名：     ConnectionAction.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-14
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.sql.action;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.handler.DatabaseConnectSetHandler;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.sql.model.ConnectionModel;
import cn.sunline.suncard.powerdesigner.sql.model.SqlDatabaseModel;
import cn.sunline.suncard.powerdesigner.sql.model.SqlTableModel;
import cn.sunline.suncard.powerdesigner.sql.view.ConnectionTreeView;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 数据库连接树的Action
 * @author  Manzhizhen
 * @version 1.0, 2013-1-14
 * @see 
 * @since 1.0
 */
public class ConnectionAction extends Action{
	private ConnectionTreeView connectionTreeView;
	private String actionFlag;
	private ConnectionModel connectionModel;
	
	private static Log logger = LogManager.getLogger(ConnectionAction.class);
	
	public ConnectionAction(String actionFlag, ConnectionTreeView connectionTreeView) throws PartInitException, SQLException, CanNotFoundNodeIDException, FoundTreeNodeNotUniqueException {
		this.actionFlag = actionFlag;
		this.connectionTreeView = connectionTreeView;
		
		if(DmConstants.NEW_CONNECTION.equals(actionFlag)) {
			setText("新建数据库连接");
		} else if(DmConstants.CONNECTION_DATABASE.equals(actionFlag)) {
			setText("连接数据库");
		} else if(DmConstants.REFRESH_CONNECTION.equals(actionFlag)) {
			setText("刷新");
		} else if(DmConstants.EDIT_CONNECTION.equals(actionFlag)) {
			setText("编辑");
		} else if(DmConstants.DEL_CONNECTION.equals(actionFlag)) {
			setText("删除数据库连接");
		}
	}
	
	@Override
	public void run() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if(window == null) {
			logger.error("找不到活跃的WorkbenchWindow，ProjectAction执行失败！");
			return ;
		}
		
		if(DmConstants.NEW_CONNECTION.equals(actionFlag)) {
			newConnection(window);
		} else if(DmConstants.CONNECTION_DATABASE.equals(actionFlag)) {
			try {
				connectionDatabase();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CanNotFoundNodeIDException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FoundTreeNodeNotUniqueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(DmConstants.REFRESH_CONNECTION.equals(actionFlag)) {
			refreshConnection();
		} else if(DmConstants.EDIT_CONNECTION.equals(actionFlag)) {
			editDatabaseConnection(window);
		} else if(DmConstants.DEL_CONNECTION.equals(actionFlag)) {
			delConnection(window);
		}
		
		super.run();
	}

	/**
	 * 连接数据库
	 * @throws SQLException 
	 * @throws PartInitException 
	 * @throws FoundTreeNodeNotUniqueException 
	 * @throws CanNotFoundNodeIDException 
	 */
	private void connectionDatabase() throws SQLException, PartInitException, CanNotFoundNodeIDException, FoundTreeNodeNotUniqueException {

//			try {
//				ConnectionModel connectionModel = getParentConnectionModel(databaseModel);
//	            Connection con = connectionModel.getConnection();
//	            DatabaseMetaData dbmd = con.getMetaData();
//
//	            // Verify if the database has catalogs
//	            String catalog = databaseModel.hasCatalog() ? databaseModel.getName() : null;
//
//	            /** Get Tables **/
//					String[] types = { "TABLE" };
//
//					if(databaseModel.getSchema() != null){
//						if(databaseModel.getSchema().equals("null")){
//							databaseModel.setSchema(null);
//						}
//					}
//
//					// Get all tables
//					ResultSet rsTables = dbmd.getTables(catalog, databaseModel.getSchema(), null, types);
//
//					boolean ok = fillTtables(databaseModel, rsTables);
//					if(!ok){
//						//TRY in UPPER CASE
//						rsTables.close();
//						String schema = StringUtils.upperCase(databaseModel.getSchema());
//						rsTables = dbmd.getTables(catalog, schema, null, types);
//						ok = fillTtables(databaseModel, rsTables);
//						if(ok){
//							databaseModel.setSchema(schema);
//						}
//					}
//					if(!ok && databaseModel.getSchema() != null && databaseModel.getSchema().trim().equals("?")){
//						//TRY with NULL
//						rsTables.close();
//						rsTables = dbmd.getTables(catalog, null, null, types);
//						ok = fillTtables(databaseModel, rsTables);
//						if(ok){
//							databaseModel.setSchema(null);
//						}
//					}
//
//					rsTables.close();
//
//	            /** Get Views **/
//	            if(StringUtils.equalsIgnoreCase(connectionModel.getShowViews(),"true")) {
//					String[] types = new String[] { "VIEW" };
//
//					// Get all Vies
//					ResultSet rsTables = dbmd.getTables(catalog, databaseModel.getSchema(), null, types);
//
//					while (rsTables.next()) {
//						ViewModel viewModel = new ViewModel();
//						String tableName = rsTables.getString("TABLE_NAME");
//						viewModel.setName(tableName);
//						databaseModel.addChild(viewModel);
//
//						// Add invisible root, just to show [+] expand tree
//						viewModel.addChild(new InvisibleModel("NONE"));
//					}
//
//					rsTables.close();
//				}
//
//			} catch (SQLException e) {
//				MessageView.getInstance().addMessage(e.getMessage());
//			}
		/*******************************************************/
		ConnectionTreeView connectionTreeView = new ConnectionTreeView();
		
		IWorkbenchPage iWorkbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		ConnectionTreeView treeView = (ConnectionTreeView) iWorkbenchPage.findView(connectionTreeView.ID);
		
		if(treeView == null){
			treeView = (ConnectionTreeView) iWorkbenchPage.showView(connectionTreeView.ID);
		}
		
		ResultSet catalog = null;
		ResultSet rsTables = null;
		
		TreeContent treeContent = getSelection();
		connectionModel = (ConnectionModel)getSelection().getObj();
		Connection con = connectionModel.getConnection();
		DatabaseMetaData dbmd = con.getMetaData();//得到数据库
		
		catalog = dbmd.getCatalogs();//得到数据库目录
		
		String[] types = { "TABLE" };
		
//		boolean hasCatalogs = false;

		treeView.addConnectionModel(connectionModel);
		// All Databases from Connection
		while (catalog.next()) {
			// Add Database
			SqlDatabaseModel databaseModel = new SqlDatabaseModel();
			
			String databaseName = catalog.getString(1);
			System.out.println(databaseName);
			databaseModel.setName(databaseName);
			
			rsTables = dbmd.getTables(databaseName, null, null, types);
			
			while(rsTables.next()){
				SqlTableModel tableModel = new SqlTableModel();
				String tableName = rsTables.getString("TABLE_NAME");
				System.out.println(tableName);
				tableModel.setName(tableName);
				databaseModel.getTableMap().put(tableName, tableModel);
				
			}
//			tableModel.setName(resultSetMetaData.getTableName(1));
			
			treeView.addDatabaseModel(databaseModel, treeContent);
//			hasCatalogs = true;
		}
		rsTables.close();
		catalog.close();
		
//		// don磘 have catalogs
//		if (!hasCatalogs) {//如果没有就设置为空
//			databaseModel.setName(connectionModel.getDriverName());
//			
//			SqlTableModel tableModel = new SqlTableModel();
//			databaseModel.getTableMap().put("", tableModel);
//
//			// Add invisible root, just to show [+] expand tree
////			databaseModel.addChild(new InvisibleModel("NONE"));
//		}
		
	}

	/**
	 * 删除数据库连接
	 * @param window
	 */
	private void delConnection(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 编辑数据库连接
	 * @param window
	 */
	private void editDatabaseConnection(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 刷新数据库连接
	 */
	private void refreshConnection() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 新建数据库连接
	 * @param window
	 */
	private void newConnection(IWorkbenchWindow window) {
		try {
			new DatabaseConnectSetHandler().execute(null);
		} catch (ExecutionException e) {
			logger.debug("新建数据库连接失败！" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 得到所选择的连接
	 */
	private TreeContent getSelection() {
		// TODO Auto-generated method stub
		StructuredSelection selection = (StructuredSelection) connectionTreeView.getTreeViewer().getSelection();
		TreeContent treeContent = (TreeContent) selection.getFirstElement();
		
		return treeContent;
	}
}

/* 文件名：     SqlResultView.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-2-16
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.sql.view;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.sql.SqlUtils;
import cn.sunline.suncard.powerdesigner.sql.editor.SqlExecuteEditor;
import cn.sunline.suncard.powerdesigner.sql.model.ConnectionModel;
import cn.sunline.suncard.sde.baseplugin.Activator;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * SQL执行结果的View
 * 
 * @author Manzhizhen
 * @version 1.0, 2013-2-16
 * @see
 * @since 1.0
 */
public class SqlResultView extends ViewPart {
	public final static String ID = "cn.sunline.suncard.powerdesigner.tree.SqlResultView";

	private static Log logger = LogManager.getLogger(SqlResultView.class
			.getName());

	private CTabFolder tablesTabFolder;
	private Composite composite;
    private boolean loaded = false;
	
    private static ResultSetMetaData resultSetMetaData;

	@Override
	public void createPartControl(Composite parent) {
		composite = parent;

		tablesTabFolder = new CTabFolder(parent, SWT.BORDER | SWT.CLOSE);

		// Refresh Action
		Action actionRefresh = new Action() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				if (page == null) {
					logger.debug("无法找到活跃的WorkbenchPage，无法执行SqlResultView的actionRefresh！");
					return;
				}

				ConnectionTreeView connectionTreeView = (ConnectionTreeView) page
						.findView(ConnectionTreeView.ID);
				SqlExecuteEditor sqlExecuteEditor = null;
				if (connectionTreeView == null
						|| !(page.getActiveEditor() instanceof SqlExecuteEditor)) {
					logger.debug("找不到ConnectionTreeView或者当前活跃的Editor不是SqlExecuteEdito，"
							+ "无法执行SqlResultView的actionRefresh！");
				} else {
					sqlExecuteEditor = (SqlExecuteEditor) page
							.getActiveEditor();
				}

				CTabItem cTabItem = tablesTabFolder.getSelection();
				if (cTabItem != null) {
					try {
						sqlExecuteEditor.executeQuery(
								cTabItem.getToolTipText(),
								connectionTreeView.getSelectConnectionModel());
					} catch (SQLException e) {
						MessageDialog.openError(getSite().getShell(),
								I18nUtil.getMessage("MESSAGE"), e.getMessage());
					}
				}
			}
		};

		actionRefresh.setImageDescriptor(AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IDmImageKey.REFRESH_16));
		actionRefresh.setToolTipText("刷新");

		// Close All Tabs
		Action closeAllTabsAction = new Action() {
			public void run() {
				CTabItem[] itens = tablesTabFolder.getItems();
				if (itens != null) {
					for (int i = 0; i < itens.length; i++) {
						itens[i].dispose();
					}
				}
			}
		};

		closeAllTabsAction.setImageDescriptor(AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IDmImageKey.A_CLOSE));
		closeAllTabsAction.setText("Close all Tabs");
		closeAllTabsAction.setToolTipText("Close all Tabs");

		// -- Create ToolBar --
		IActionBars bars = getViewSite().getActionBars();
		fillToolBar(bars.getToolBarManager(), new Action[] { actionRefresh });
		fillMenu(bars.getMenuManager(), new Action[] { closeAllTabsAction });

	}

	private static void fillToolBar(IToolBarManager manager,
			Action[] toolbarAction) {
		for (int i = 0; i < toolbarAction.length; i++) {
			manager.add(toolbarAction[i]);
		}
	}

	private static void fillMenu(IMenuManager manager, Action[] menuAction) {
		for (int i = 0; i < menuAction.length; i++) {
			manager.add(menuAction[i]);

			if (i < menuAction.length - 1) {
				manager.add(new Separator());
			}
		}
	}

	public void addResultSet(ResultSet rs, final String query)
			throws SQLException {

		if (tablesTabFolder == null) {
			MessageDialog.openWarning(getViewSite().getShell(), "EasySQL",
					"Please, open the Results View!");
			return;
		}

//		if (newTab == false) {
//			// close the previous tab
//			if (tablesTabFolder.getItemCount() != 0) {
//				tablesTabFolder.getSelection().dispose();
//			}
//		}

		final Table resultSetTable = new Table(tablesTabFolder, SWT.BORDER
				| SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

		// Table Name
		resultSetMetaData = rs.getMetaData();
		String tableName = configureTableName(query, resultSetTable);

		resultSetTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		resultSetTable.setLinesVisible(true);
		resultSetTable.setHeaderVisible(true);

		// Create new tab
		final CTabItem tabItem = new CTabItem(tablesTabFolder, SWT.NONE);

		tabItem.setText(tableName);
		tabItem.setToolTipText(query);
		tabItem.setControl(resultSetTable);

		bindResultSet(rs, resultSetTable, tableName);

		// Set focus on new tab
		tablesTabFolder.setSelection(tablesTabFolder.getItemCount() - 1);

		// Bring to front
		getSite().getPage().activate(this);
	}
	
    private void bindResultSet(ResultSet rs, final Table table, final String tableName) {
        try {
            resultSetMetaData = rs.getMetaData();

            int cols = resultSetMetaData.getColumnCount();

            // Add columns
            for (int i = 1; i <= cols; i++) {
                TableColumn column = new TableColumn(table, SWT.NONE);
                final String columnName = resultSetMetaData.getColumnName(i);
                column.setText(columnName);

                column.addListener(SWT.Selection, new Listener() {
                    public void handleEvent(Event event) {
                		IWorkbenchPage page = PlatformUI.getWorkbench()
                				.getActiveWorkbenchWindow().getActivePage();
                		if(page == null) {
                			logger.debug("无法找到活跃的WorkbenchPage，无法执行SqlResultView.bindResultSet！");
                			return ;
                		}

                		ConnectionTreeView connectionTreeView = (ConnectionTreeView) page.findView(ConnectionTreeView.ID);
                		SqlExecuteEditor sqlExecuteEditor = null;
                		if(connectionTreeView == null || !(page.getActiveEditor() instanceof SqlExecuteEditor)) {
                			logger.debug("无法找到ConnectionTreeView或当前活跃的Editor不为SqlExecuteEditor，无法执行SqlResultView.bindResultSet！");
                			return ;
                		} else {
                			sqlExecuteEditor = (SqlExecuteEditor) page.getActiveEditor();
                		}
                		
                        ConnectionModel connectionModel = connectionTreeView.getSelectConnectionModel();

                        if(connectionModel == null) {
                            MessageDialog.openInformation(getSite().getShell(), I18nUtil.getMessage("MESSAGE"), "请选择一个连接！");
                            return;
                        }

                        String query = getTableViewSqlQuery();

                        if(query != null) {
                            int idx = query.toUpperCase().indexOf("ORDER BY");
                            if(idx != -1) {
                                query = query.substring(0,idx)+ " ORDER BY " + columnName;
                            }
                            else {
                                query += " ORDER BY " + columnName;
                            }
                        }else {
                            query = "select * from " + tableName + " ORDER BY " + columnName;
                        }
                        try { 
//                          MessageView.getInstance().addMessage(query);
                        	sqlExecuteEditor.executeQuery(query, connectionTreeView.getSelectConnectionModel());
                        }
                        catch (SQLException e) {
                            MessageDialog.openError(getSite().getShell(), I18nUtil.getMessage("MESSAGE"), e.getMessage());
                        }
                    }
                });
            }

            // Add data
            while (rs.next()) {
                TableItem item = new TableItem(table, SWT.NONE);
                for (int i = 0; i < cols; i++) {
                    String value = rs.getString(i + 1);
                    if(value == null) {
                        item.setText(i, "<null>");
                    }
                    else {
                        item.setText(i, value.trim());
                    }
                }
            }

            // Resize columns
            for (int i = 0; i < cols; i++) {
                table.getColumn(i).pack();
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
    /**
     * @param query
     * @param resultSetTable
     * @return
     * @throws SQLException
     */
    private String configureTableName(final String query, final Table resultSetTable) throws SQLException {
        String tableName = resultSetMetaData.getTableName(1);

        if(tableName == null || (tableName != null && tableName.trim().length() == 0)) {
            tableName = SqlUtils.getTableNameFromQuery(query);
        }

        if(tableName == null || tableName.trim().length() == 0) {
            tableName = findTableName(query);
            if(tableName == null || tableName.trim().length() == 0) {
            	logger.error("无法找到表格名称！");
//                //MessageDialog.openWarning(getSite().getShell(),"","Can磘 find
//                // table name!");
//                MessageView.getInstance().addMessage("Can磘 find table name! ->" + query);
            }
        }
        resultSetTable.setToolTipText(tableName);
        return tableName;
    }
    

	private String getTableViewSqlQuery() {
		CTabItem cTabItem = tablesTabFolder.getSelection();
		if (cTabItem != null) {
			CTabItem localCTabItem = tablesTabFolder.getSelection();
			if (localCTabItem != null) {
				return localCTabItem.getToolTipText();
			}
		}

		return null;
	}

    /**
     * Return the table name of the query. The table name is necessary for
     * delete and update queries after.
     * 
     * @param query
     * @return
     */
    private String findTableName(String query) {
        char[] array = query.substring(query.toLowerCase().indexOf("from") + 4).toCharArray();
        String tableName = "";
        boolean start = false;
        for (int j = 0; j < array.length; j++) {
            if(array[j] == ' ') {
                if(!start) {
                    start = true;
                    continue;
                }
                else {
                    //end
                    break;
                }
            }
            else if(array[j] == ',') {
                break; // end
            }
            else {
                tableName += array[j];
            }
        }
        return tableName;
    }
	
    public boolean isLoaded(){
        return this.loaded;
    }
    
	@Override
	public void setFocus() {

	}

}

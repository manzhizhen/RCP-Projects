/* 文件名：     SqlUtils.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-2-16
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.sql.model.SqlDatabaseModel;
import cn.sunline.suncard.powerdesigner.sql.model.SqlTableModel;

/**
 * Sql脚本工具类
 * @author  Manzhizhen
 * @version 1.0, 2013-2-16
 * @see 
 * @since 1.0
 */
public class SqlUtils {
	public static void closeStatement(Statement stmt) {
		if(stmt != null) {
			try {
				stmt.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeResultSet(ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

//	public static SqlTableModel getTableModelByName(String tableName) {
//		tableName = tableName.trim();
//		
//		SqlDatabaseModel databaseModel = ConnectionContentProvider.getInstance().getSelectedDatabaseModel();
//
//		List children = databaseModel.getChildrenList();
//		for (int i = 0; i < children.size(); i++) {
//			TableModel table = (TableModel) children.get(i);
//			if(table.getName().equalsIgnoreCase(tableName)) {
//				return table;
//			}
//		}
//		return null;
//	}
//
//	public static ColumnModel getColumnModelByName(TableModel table, String columnName) {
//		columnName = columnName.trim();
//		List children = table.getChildrenList();
//		for (int i = 0; i < children.size(); i++) {
//			ColumnModel column = (ColumnModel) children.get(i);
//			if(column.getName().equalsIgnoreCase(columnName)) {
//				return column;
//			}
//		}
//		return null;
//	}

	/**
	 * This method return the Class of a specific SQL Type, defined in
	 * java.sql.Types.
	 * 
	 * @param sqlType
	 *            SQL Type.
	 * @return The Class relative to the SQL Type.
	 */
	public static Class getColumnClass(int sqlType) {
		switch (sqlType) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				return String.class;

			case Types.BIT:
			case Types.BOOLEAN:
				return boolean.class;

			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
				return Integer.class;

			case Types.BIGINT:
				return Long.class;

			case Types.FLOAT:
			case Types.DOUBLE:
			case Types.DECIMAL:
			case Types.NUMERIC:
			case Types.REAL:
				return Double.class;

			case Types.DATE:
				return java.util.Date.class;

			case Types.TIMESTAMP:
			case Types.TIME:
				return java.sql.Timestamp.class;

			default:
				return Object.class;

		/*
		 * types not recognized yet case Types.ARRAY : case Types.BINARY : case
		 * Types.BLOB : case Types.CLOB : case Types.DATALINK : case
		 * Types.DISTINCT : case Types.JAVA_OBJECT : case Types.LONGVARBINARY :
		 * case Types.NULL : case Types.OTHER : case Types.REF : case
		 * Types.STRUCT : case Types.VARBINARY :
		 */
		}
	}

	/**
	 * Returns the selected table if the ResultSetMetaData.getTableName(int)
	 * doesn磘 work
	 * 
	 * @param query
	 * @return
	 */
	public static String getTableNameFromQuery(String query) {
		if(query == null) {
			return "";
		}
		int idx = query.toUpperCase().indexOf("FROM");
		if(idx == -1) {
			return "";
		}

		return new StringTokenizer(query.substring(idx + 5).trim()).nextToken();
	}

	/**
	 * Bring view to front
	 * @throws PartInitException
	 */
	public static void showView(String viewId){
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if(page == null)
			return;

		try {
			page.showView(viewId);
		}
		catch (Exception e) {
			Logger.getLogger(SqlUtils.class).error(e,e);
		}
	}
}

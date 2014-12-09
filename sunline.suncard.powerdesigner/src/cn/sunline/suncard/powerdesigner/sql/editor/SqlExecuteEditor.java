/* 文件名：     SqlExcuteEditor.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-11
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.sql.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.codehaus.groovy.runtime.callsite.GetEffectivePogoFieldSite;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;

import cn.sunline.suncard.powerdesigner.sql.SqlUtils;
import cn.sunline.suncard.powerdesigner.sql.action.SqlExecuteAction;
import cn.sunline.suncard.powerdesigner.sql.model.ConnectionModel;
import cn.sunline.suncard.powerdesigner.sql.view.SqlResultView;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * SQL脚本执行的Editor
 * 
 * @author Manzhizhen
 * @version 1.0, 2013-1-11
 * @see
 * @since 1.0
 */
public class SqlExecuteEditor extends TextEditor {
	public static String ID = "cn.sunline.suncard.powerdesigner.gef.ui.editor.SqlExcuteEditor";
    public static String MENU_ID = "#" + ID;
    
	private static Log logger = LogManager.getLogger(SqlExecuteEditor.class);

	public SqlExecuteEditor() {
		super();
		
		setSourceViewerConfiguration(new SqlSourceViewerConfiguration());
        setDocumentProvider(new SqlDocumentProvider());
        setEditorContextMenuId(MENU_ID);
	}
	
	/**
	 * 获得Editor中所选择的文本
	 * @return
	 */
    public String getSelectedText() {
        String selected = getSourceViewer().getTextWidget().getSelectionText();

		return selected;
    }
    
	/**
	 * 获得Editor中的全部文本
	 * @return
	 */
    public String getText() {
        String text = getSourceViewer().getTextWidget().getText();

        return text;
    }
    
    /**
     * 执行SQL脚本
     * @param query
     * @param newTab
     * @return
     * @throws SQLException
     */
    public int executeQuery(String query, ConnectionModel connectionModel) throws SQLException {
    	int updateRow = 0;

		// SQL Parser
		String[] statements = parseSQL(query);

		// If no statements, don't execute
		if (statements == null || statements.length == 0) {
			return -1;
		}

		Connection con = connectionModel.getConnection();

		Statement stmt = null;
		ResultSet rs = null;
		try {

			stmt = con.createStatement();

			boolean flag = false;

			// Execute statements        
			for (int i = 0; i < statements.length; i++) {

				logger.info(statements[i]);

				// Execute query
				if (statements[i] != null && statements[i].trim().startsWith("@")) {
					File file = new File(statements[i].trim().substring(1));
					if (file.exists()) {
						try {
							// execute all statements in the file. (@c:/temp/statements.txt)
							executeQuery(file, connectionModel);

							MessageDialog.openInformation(getEditorSite().getShell(),
									I18nUtil.getMessage("MESSAGE"), "文件执行成功!");
						} catch (IOException e) {
							MessageDialog.openError(getEditorSite().getShell()
									, I18nUtil.getMessage("MESSAGE"), e.getMessage());
						}
					}
				} else {
					flag = stmt.execute(statements[i]);

					// No ResultSet, display update/insert/delete number of records effected
					if (flag) {
						rs = stmt.getResultSet();
						SqlResultView sqlResultView = (SqlResultView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(SqlResultView.ID);
						if(!sqlResultView.isLoaded()){
							SqlUtils.showView(SqlResultView.ID);
						}
						sqlResultView.addResultSet(rs, statements[i]);

					} else {
						int updateCount = stmt.getUpdateCount();
						if (updateCount == -1) {
							break;
						}
//						MessageView.getInstance().addMessage(updateCount + " records effected: " + statements[i]);
						updateRow += updateCount;
					}
				}
			}
		} catch (SQLException e) {
//			MessageView.getInstance().addMessage(e.getMessage());
			logger.error(e.getMessage());
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e1) {
//				MessageView.getInstance().addMessage(e1.getMessage());
				logger.error(e1, e1);
			}
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e2) {
//				MessageView.getInstance().addMessage(e2.getMessage());
				logger.error(e2, e2);
			}
		}
    	
		
		return updateRow;
    }
    
	/**
	 * 
	 * @param file
	 * @throws IOException
	 * @throws SQLException
	 */
	public void executeQuery(File file, ConnectionModel connectionModel) throws IOException, SQLException {
		if (file != null && file.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuffer buffer = new StringBuffer();
            String query = null;
			while ((query = reader.readLine()) != null) {
				buffer.append(query);
                buffer.append("\r\n");
			}
			reader.close();
            executeQuery(buffer.toString(), connectionModel);
		} else {
			MessageDialog.openInformation(getEditorSite().getShell(), I18nUtil.getMessage("MESSAGE"), "文件没有发现!");
		}
	}
    
    /**
     * 将SQL脚本转换成可执行的格式，包括去掉注释之类的
     * @param sql
     * @return
     */
	private static String[] parseSQL(String sql) {
		char[] c = sql.toCharArray();

		char stringChar = 'x';
		boolean inComment = false;

		StringBuffer sb = new StringBuffer();
		ArrayList statements = new ArrayList();

		for (int i = 0; i < c.length; i++) {

			if (!inComment) {
				// if in comment and escaping ' or ", via \' \", skip
				if (stringChar != 'x' && c[i] == '\\') {
					sb.append(c[i]);
					int nextChar = i + 1;
					if (c[nextChar] == '\'' || c[nextChar] == '"') {
						sb.append(c[i]);
						continue;
					}
				}

				if (c[i] == '\'' || c[i] == '"') {
					if (c[i] == stringChar) {
						stringChar = 'x';
						sb.append(c[i]);
					} else {
						stringChar = c[i];
						sb.append(c[i]);
					}
					continue;
				}

				if (c[i] == '-' && c[i + 1] == '-') {
					inComment = true;
					i++;
					//                    sb.append("--");
					continue;
				}

			}

			// if in comment and end of line, get out of comment
			if (inComment == true && c[i] == Character.LINE_SEPARATOR) {
				//System.out.println("char: " + i);
				inComment = false;
				continue;
			}

			if (inComment == true || c[i] == 13 || c[i] == 10) {
//				log.info("inComment: " + (int) c[i]);
				if (c[i] == 10) {
					sb.append(" ");
				}
				continue;
			}

			// if end of statement
			if (c[i] == ';') {
				statements.add(sb.toString());
				sb = new StringBuffer();
				continue;
			}

			if (!inComment) {
				sb.append(c[i]);
				//System.out.println("not inComment: " + (int)c[i]);
			}
		}

		if (statements.size() == 0) {
			statements.add(sb.toString());
		}

		String[] stmts = new String[statements.size()];
		statements.toArray(stmts);

		return stmts;
	}
    
	public boolean isDirty()  {
		// Only ask opened dirty files to save, not buffers
		if (getEditorInput() instanceof FileEditorInput) {
			return super.isDirty();
		}
		return false;
	}
	
	@Override
	public boolean isSaveAsAllowed()  {
		return true;
	}
    
    @Override
    public void setFocus() {
    	super.setFocus();
    }
}

/* 文件名：     ConnectionCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2013-1-15
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.derby.iapi.store.raw.Page;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Text;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.sql.model.ConnectionModel;
import cn.sunline.suncard.powerdesigner.sql.view.ConnectionTreeView;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

import com.lowagie.text.DocumentException;

/**
 * 
 * @author Agree
 * @version 1.0, 2013-1-15
 * @see
 * @since 1.0
 */
public class UpdateConnectionModelCommand extends Command {
	private ConnectionModel connectionModel;
	private Connection connection;
	private static Hashtable driverCache = new Hashtable();
	private static Document optionItemDocument;
	private Shell shell;
	
	private static Log logger = LogManager
			.getLogger(ChangeCurrentDatabaseCommand.class.getName());

	@Override
	public void execute() {

		try {
			writeJDBCFileConfig();
		} catch (org.dom4j.DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			createConnectionTree();
		} catch (CanNotFoundNodeIDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FoundTreeNodeNotUniqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.execute();
	}

	/**
	 * 新建连接树
	 * @throws FoundTreeNodeNotUniqueException 
	 * @throws CanNotFoundNodeIDException 
	 * @throws PartInitException 
	 */
	private void createConnectionTree() throws CanNotFoundNodeIDException, FoundTreeNodeNotUniqueException, PartInitException {
		ConnectionTreeView connectionTreeView = new ConnectionTreeView();
		
		IWorkbenchPage iWorkbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		ConnectionTreeView treeView = (ConnectionTreeView) iWorkbenchPage.findView(connectionTreeView.ID);
		
		if(treeView == null){
			treeView = (ConnectionTreeView) iWorkbenchPage.showView(connectionTreeView.ID);
		}
		
		treeView.addConnectionModel(connectionModel);
	}

	/**
	 * 初始化JDBCFileConfig文件的Document
	 * 
	 * @return
	 * @throws org.dom4j.DocumentException 
	 * @throws org.dom4j.DocumentException
	 */
	private Document createOptionItemConfigDocument() throws org.dom4j.DocumentException {
		if (optionItemDocument == null) {
			SAXReader saxReader = new SAXReader();

				optionItemDocument = saxReader.read(new File(
						SystemConstants.JDBC_CONFIG_FILE));
		}

		return optionItemDocument;
	}

	/**
	 * 写入XML，用于持久化
	 * @throws org.dom4j.DocumentException 
	 */
	private boolean writeJDBCFileConfig() throws org.dom4j.DocumentException {

		Document doc = createOptionItemConfigDocument();
		if (doc == null) {
			logger.error("初始化Document失败！");
			return false;
		}

		Element rootElement = doc.getRootElement();

		if (rootElement == null) {
			logger.error("Document根节点为空！");
			return false;
		}
		List<Element> elementList = rootElement.elements();
		for(Element element : elementList){
			if(element.attribute("name").equals(connectionModel.getDriverName())){
				MessageDialog.openWarning(shell, "提示", "已经存在");
			}
		};
		
		Element connectionNode = rootElement.addElement("driverName");

		connectionNode.addAttribute("name", connectionModel.getDriverName());

		connectionNode
				.addElement("driver", connectionModel.getDriverTemplate());

		connectionNode.addElement("connectionURL",
				connectionModel.getDriverURL());

		connectionNode.addElement("userName", connectionModel.getUserName());

		connectionNode.addElement("password", connectionModel.getPassword());

		connectionNode.addElement("JARsAddress",
				connectionModel.getJarAddress());

		connectionNode.addElement("driverClassName",
				connectionModel.getDriverClassName());

		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		XMLWriter writer;
		try {
			writer = new XMLWriter(new FileOutputStream(new File(
					SystemConstants.JDBC_CONFIG_FILE)), format);
			writer.write(doc);
			writer.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 数据库连接测试
	 * 
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SQLException
	 */
	public void connectionTest() throws MalformedURLException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		// 连接数据库核心代码
		URL urls[] = new URL[1];

		String driverFile = connectionModel.getJarAddress();
		URLClassLoader loader = (URLClassLoader) driverCache.get(driverFile);

		if (loader == null) {
			urls[0] = new File(driverFile).toURL();
			loader = new URLClassLoader(urls);
			driverCache.put(driverFile, loader);
		}

		Class driverClass = loader.loadClass(connectionModel
				.getDriverClassName());
		Driver driver = (Driver) driverClass.newInstance();

		Properties props = new Properties();
		props.put("user", connectionModel.getUserName());
		props.put("password", connectionModel.getPassword());

		connection = driver.connect(connectionModel.getDriverURL(), props);
		if (connection == null) {
			throw new SQLException("Connection Invalid, please verify URL.");
		}

		MessageDialog.openInformation(shell, "成功", "连接成功");
		// Class.forName(connectionModel.getDriverClassName()).newInstance();
		// String url = connectionModel.getDriverURL();
		// // orcl为数据库的SID
		// String user = connectionModel.getUserName();
		// String password = connectionModel.getPassword();
		// connection = DriverManager.getConnection(url, user, password);
		//
		// if (connection == null) {
		// throw new SQLException("Connection Invalid, please verify URL.");
		// }

		// connectionModel.setConnection(con);

		// PreparedStatement pstm = null;
		// ResultSet rs = null;
		// String sql = "select * from ";
		// try {
		// pstm = connection.prepareStatement(sql);
		// rs = pstm.executeQuery();
		// if(rs.next()) {
		// MessageDialog.openInformation(shell, "成功", "数据库连接成功");
		// }else{
		// MessageDialog.openInformation(shell, "失败", "数据库连接失败");
		// }
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }finally {
		// free(connection, pstm, rs);
		// }
	}


	/**
	 * 关闭连接
	 * 
	 * @param conn
	 * @param pstm
	 * @param rs
	 */
	public void free(Connection conn, PreparedStatement pstm, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (pstm != null) {
			try {
				pstm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	
	
	public Shell getShell() {
		return shell;
	}

	public void setShell(Shell shell) {
		this.shell = shell;
	}

	public void setConnectionModel(ConnectionModel connectionModel) {
		this.connectionModel = connectionModel;
	}

	public Connection getConnection() {
		return connection;
	}

}

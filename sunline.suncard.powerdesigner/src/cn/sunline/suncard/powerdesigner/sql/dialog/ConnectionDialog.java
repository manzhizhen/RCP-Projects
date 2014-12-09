/* 文件名：     JDBCDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2013-1-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.sql.dialog;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.powerdesigner.command.UpdateConnectionModelCommand;
import cn.sunline.suncard.powerdesigner.db.DatabaseTypeConfigXml;
import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.sql.model.ConnectionModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 连接数据库对话框
 * 
 * @author Agree
 * @version 1.0, 2013-1-5
 * @see
 * @since 1.0
 */
public class ConnectionDialog extends TitleAreaDialog {
	private Text textConnectionName;
	private Text textConnectionURL;
	private Text textUserName;
	private Text textPassword;
	private Combo comboJDBCTemplate;
	private List<ConnectionModel> attributeModelList;
	private Button btnJars;
	private String jarsAddress;
	private Text textAddress;
	private Text textClassName;
	private Button buttonConnectionTest;

	private static Document JDBCDocument = null;

	public static Log logger = LogManager
			.getLogger(DatabaseTypeConfigXml.class);

	/**
	 * @param parent
	 */
	public ConnectionDialog(Shell parent) {
		super(parent);
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE
				| SWT.PRIMARY_MODAL);
	}

	protected Control createDialogArea(Composite parent) {
		setTitle("新建数据库连接驱动");
		setMessage("新建数据库连接驱动");

		Control control = (Control) super.createDialogArea(parent);
		Composite composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new FormLayout());

		Label lblNewLabel = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("数据库连接名：");

		textConnectionName = new Text(composite, SWT.BORDER);
		FormData fd_text_4 = new FormData();
		fd_text_4.right = new FormAttachment(100, -6);
		fd_text_4.top = new FormAttachment(0, 2);
		fd_text_4.left = new FormAttachment(lblNewLabel, 6);
		textConnectionName.setLayoutData(fd_text_4);

		comboJDBCTemplate = new Combo(composite, SWT.NONE | SWT.READ_ONLY);
		FormData fd_combo = new FormData();
		fd_combo.top = new FormAttachment(lblNewLabel, 6);
		fd_combo.right = new FormAttachment(textConnectionName, 0, SWT.RIGHT);
		fd_combo.left = new FormAttachment(textConnectionName, 0, SWT.LEFT);
		comboJDBCTemplate.setLayoutData(fd_combo);

		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		fd_lblNewLabel_1.bottom = new FormAttachment(comboJDBCTemplate, 0,
				SWT.BOTTOM);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("驱动模版：");

		textConnectionURL = new Text(composite, SWT.BORDER);
		FormData fd_text_5 = new FormData();
		fd_text_5.right = new FormAttachment(comboJDBCTemplate, 0, SWT.RIGHT);
		fd_text_5.top = new FormAttachment(comboJDBCTemplate, 6);
		fd_text_5.left = new FormAttachment(comboJDBCTemplate, 0, SWT.LEFT);
		textConnectionURL.setLayoutData(fd_text_5);

		Label lblConnectionURL = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.bottom = new FormAttachment(textConnectionURL, 0,
				SWT.BOTTOM);
		fd_lblNewLabel_2.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblConnectionURL.setLayoutData(fd_lblNewLabel_2);
		lblConnectionURL.setText("连接URL：");

		textUserName = new Text(composite, SWT.BORDER);
		FormData fd_text_6 = new FormData();
		fd_text_6.right = new FormAttachment(comboJDBCTemplate, 0, SWT.RIGHT);
		fd_text_6.top = new FormAttachment(textConnectionURL, 6);
		fd_text_6.left = new FormAttachment(comboJDBCTemplate, 0, SWT.LEFT);
		textUserName.setLayoutData(fd_text_6);

		Label label = new Label(composite, SWT.NONE);
		FormData fd_label = new FormData();
		fd_label.bottom = new FormAttachment(textUserName, 0, SWT.BOTTOM);
		fd_label.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		label.setLayoutData(fd_label);
		label.setText("用户名：");

		textPassword = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		FormData fd_text_7 = new FormData();
		fd_text_7.right = new FormAttachment(comboJDBCTemplate, 0, SWT.RIGHT);
		fd_text_7.top = new FormAttachment(textUserName, 6);
		fd_text_7.left = new FormAttachment(comboJDBCTemplate, 0, SWT.LEFT);
		textPassword.setLayoutData(fd_text_7);

		Label label_1 = new Label(composite, SWT.NONE);
		FormData fd_label_1 = new FormData();
		fd_label_1.bottom = new FormAttachment(textPassword, 0, SWT.BOTTOM);
		fd_label_1.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		label_1.setLayoutData(fd_label_1);
		label_1.setText("密码：");

		Label label_2 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		FormData fd_label_2 = new FormData();
		fd_label_2.bottom = new FormAttachment(label_1, 8, SWT.BOTTOM);
		fd_label_2.right = new FormAttachment(comboJDBCTemplate, 0, SWT.RIGHT);
		fd_label_2.top = new FormAttachment(textPassword, 6);
		fd_label_2.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		label_2.setLayoutData(fd_label_2);

		Label lblJars = new Label(composite, SWT.NONE);
		FormData fd_lblJars = new FormData();
		fd_lblJars.top = new FormAttachment(label_2, 6);
		fd_lblJars.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblJars.setLayoutData(fd_lblJars);
		lblJars.setText("驱动 JARs");

		textAddress = new Text(composite, SWT.BORDER);
		FormData fd_list = new FormData();
		fd_list.top = new FormAttachment(lblJars, 6);
		fd_list.left = new FormAttachment(0, 19);
		textAddress.setLayoutData(fd_list);

		btnJars = new Button(composite, SWT.NONE);
		fd_list.right = new FormAttachment(btnJars, -6);
		FormData fd_btnJars = new FormData();
		fd_btnJars.top = new FormAttachment(label_2, 29);
		fd_btnJars.right = new FormAttachment(comboJDBCTemplate, 0, SWT.RIGHT);
		btnJars.setLayoutData(fd_btnJars);
		btnJars.setText("添加 JARs");

		textClassName = new Text(composite, SWT.NONE);
		FormData fd_combo_1 = new FormData();
		fd_combo_1.right = new FormAttachment(comboJDBCTemplate, 0, SWT.RIGHT);
		fd_combo_1.top = new FormAttachment(textAddress, 6);
		fd_combo_1.left = new FormAttachment(comboJDBCTemplate, 0, SWT.LEFT);
		textClassName.setLayoutData(fd_combo_1);

		Label lblNewLabel_3 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_3 = new FormData();
		fd_lblNewLabel_3.bottom = new FormAttachment(textClassName, 0,
				SWT.BOTTOM);
		fd_lblNewLabel_3.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblNewLabel_3.setLayoutData(fd_lblNewLabel_3);
		lblNewLabel_3.setText("驱动类名：");

		Label label_3 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		FormData fd_label_3 = new FormData();
		fd_label_3.top = new FormAttachment(textClassName, 6);
		fd_label_3.right = new FormAttachment(comboJDBCTemplate, 0, SWT.RIGHT);
		fd_label_3.bottom = new FormAttachment(textClassName, 8, SWT.BOTTOM);
		fd_label_3.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		label_3.setLayoutData(fd_label_3);

		 buttonConnectionTest = new Button(composite, SWT.NONE);
		FormData fd_button = new FormData();
		fd_button.top = new FormAttachment(label_3, 6);
		fd_button.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		buttonConnectionTest.setLayoutData(fd_button);
		buttonConnectionTest.setText("连接测试");

		createJDBCDocuemnt();

		initControl();

		return composite;
	}

	/**
	 * 填充界面内容
	 */
	private void initControl() {
		attributeModelList = getAllJDBCTemplate();
		for (ConnectionModel attributeModel : attributeModelList) {
			comboJDBCTemplate.add(attributeModel.getDriverTemplate());
			// if("0".equals(attributeModel.getDefaultState())){
			// textConnectionURL.setText(attributeModel.getDriverURL());
			// comboJDBCTemplate.select(attributeModelList.indexOf(attributeModel));
			// }
		}
		comboJDBCTemplate.select(0);
		textConnectionURL.setText(attributeModelList.get(0).getDriverURL());
		textClassName.setText(attributeModelList.get(0).getDriverClassName());

		createJDBCTemplateEvent();
		createBtnJarsEvent(getParentShell());
		createButtonConnectionTestEvent();
	}

	/**
	 * 制造选择jar包位置事件
	 */
	private void createBtnJarsEvent(final Shell shell) {
		// 得到地址的方法
		btnJars.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				FileDialog dialog = new FileDialog(shell);
				String path = dialog.open();
				if (path == null) {
					textAddress.setText("请输入地址");
				} else {
					textAddress.setText(path);
				}
			}
		});
	}

	/**
	 * 数据库连接测试
	 */
	private void createButtonConnectionTestEvent() {
		buttonConnectionTest.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseDown(MouseEvent e) {
				if (textClassName.getText().equals("")
						|| textConnectionName.getText().equals("")
						|| textConnectionURL.getText().equals("")
						|| textUserName.getText().equals("")
						|| textPassword.getText().equals("")
						|| textAddress.getText().equals("")) {
					MessageDialog.openWarning(getParentShell(), "提示", "请将内容填写完整");
					return;
				}

				try{
				ConnectionModel connectionModel = new ConnectionModel();
				connectionModel.setDriverClassName(textClassName.getText());
				connectionModel.setDriverName(textConnectionName.getText());
				connectionModel.setDriverTemplate(comboJDBCTemplate.getText());
				connectionModel.setDriverURL(textConnectionURL.getText());
				connectionModel.setJarAddress(textAddress.getText());
				connectionModel.setPassword(textPassword.getText());
				connectionModel.setUserName(textUserName.getText());
				
				UpdateConnectionModelCommand connectionCommand = new UpdateConnectionModelCommand();
				connectionCommand.setConnectionModel(connectionModel);
				connectionCommand.setShell(getParentShell());
				connectionCommand.connectionTest();
				}catch (Exception exception) {
					MessageDialog.openError(getParentShell(), "错误", "连接失败，请检查信息是否正确，数据库是否开启");
				}
			}
		});
	}
	
	/**
	 * 制造选择comboJDBCTemplate事件
	 */
	private void createJDBCTemplateEvent() {
		comboJDBCTemplate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String selectedTemplate = comboJDBCTemplate.getText();
				for (ConnectionModel attributeModel : attributeModelList) {
					if (selectedTemplate.equals(attributeModel
							.getDriverTemplate())) {
						textConnectionURL.setText(attributeModel.getDriverURL());// 设置连接URL
						textClassName.setText(attributeModel
								.getDriverClassName());
					}
				}
			}
		});
	}

	/**
	 * 初始化数据库类型定义文件的Document
	 * 
	 * @return
	 */
	private static void createJDBCDocuemnt() {
		if (JDBCDocument == null) {
			SAXReader saxReader = new SAXReader();

			try {
				JDBCDocument = saxReader.read(new File(
						SystemConstants.JDBC_CONFIG_Driver));

			} catch (DocumentException e) {
				logger.error("初始化Document失败！" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据选择的数据库选择classname
	 */
	private String getDriverClassName(String driver) {

		if (JDBCDocument == null) {
			logger.error("初始化Document失败！");
			return null;
		}

		Element rootElement = JDBCDocument.getRootElement();

		if (rootElement == null) {
			logger.error("Document根节点为空！");
			return null;
		}

		List<Element> databaseList = rootElement.elements("driver");
		if (databaseList == null) {
			return null;
		}

		for (Element JDBCElement : databaseList) {
			if (driver.equals(JDBCElement.attribute("template"))) {
				return JDBCElement.elementTextTrim("driverClassName");
			}
		}

		return null;
	}

	/**
	 * 得到所有JDBCTemplate
	 */
	public static List<ConnectionModel> getAllJDBCTemplate() {

		List<ConnectionModel> attributeModelList = new ArrayList<ConnectionModel>();

		if (JDBCDocument == null) {
			logger.error("初始化Document失败！");
			return null;
		}

		Element rootElement = JDBCDocument.getRootElement();

		if (rootElement == null) {
			logger.error("Document根节点为空！");
			return null;
		}

		List<Element> databaseList = rootElement.elements("driver");
		if (databaseList == null) {
			return null;
		}

		for (Element JDBCElement : databaseList) {
			ConnectionModel attributeModel = new ConnectionModel();
			attributeModel.setDriverTemplate(JDBCElement
					.attributeValue("template"));
			attributeModel.setDriverName(JDBCElement
					.elementTextTrim("driverName"));
			attributeModel.setDriverURL(JDBCElement
					.elementTextTrim("connectionURL"));
			attributeModel.setUserName(JDBCElement.elementTextTrim("userName"));
			attributeModel.setPassword(JDBCElement.elementTextTrim("password"));
			attributeModel.setDefaultState(JDBCElement
					.attributeValue("default"));
			attributeModel.setDriverClassName(JDBCElement
					.elementTextTrim("driverClassName"));
			attributeModelList.add(attributeModel);
		}

		return attributeModelList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		if (textClassName.getText().equals("")
				|| textConnectionName.getText().equals("")
				|| textConnectionURL.getText().equals("")
				|| textUserName.getText().equals("")
				|| textPassword.getText().equals("")
				|| textAddress.getText().equals("")) {
			MessageDialog.openWarning(getParentShell(), "提示", "请将内容填写完整");
			return;
		}
		
		ConnectionModel connectionModel = new ConnectionModel();
		connectionModel.setDriverClassName(textClassName.getText());
		connectionModel.setDriverName(textConnectionName.getText());
		connectionModel.setDriverTemplate(comboJDBCTemplate.getText());
		connectionModel.setDriverURL(textConnectionURL.getText());
		connectionModel.setJarAddress(textAddress.getText());
		connectionModel.setPassword(textPassword.getText());
		connectionModel.setUserName(textUserName.getText());
		
		UpdateConnectionModelCommand connectionCommand = new UpdateConnectionModelCommand();
		connectionCommand.setConnectionModel(connectionModel);
		connectionCommand.setShell(getParentShell());
		try {
			connectionCommand.connectionTest();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connectionModel.setConnection(connectionCommand.getConnection());
		connectionCommand.execute();
		
		super.okPressed();
	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 500);
	}
}

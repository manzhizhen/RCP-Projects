/* 文件名：     JDBCDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2013-1-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.Button;

import cn.sunline.suncard.powerdesigner.db.DatabaseTypeConfigXml;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 连接数据库对话框
 * @author  Agree
 * @version 1.0, 2013-1-5
 * @see 
 * @since 1.0
 */
public class JDBCDialog extends TitleAreaDialog{
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private Text text_4;
	private Text text_5;
	private Text text_6;
	private Text text_7;

	private static Document JDBCDocument = null;
	
	public static Log logger = LogManager
			.getLogger(DatabaseTypeConfigXml.class);
	/**
	 * @param parent
	 */
	public JDBCDialog(Shell parent) {
		super(parent);
		// TODO Auto-generated constructor stub
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE
				| SWT.PRIMARY_MODAL);
	}

	protected Control createDialogArea(Composite parent){
		setTitle("数据库连接驱动");
		setMessage("数据库连接驱动");
		
		Control control = (Control) super.createDialogArea(parent);
		Composite composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new FormLayout());
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("驱动模版：");
		
		Combo combo = new Combo(composite, SWT.NONE);
		FormData fd_combo = new FormData();
		fd_combo.right = new FormAttachment(100, -6);
		fd_combo.top = new FormAttachment(0, 2);
		fd_combo.left = new FormAttachment(lblNewLabel, 6);
		combo.setLayoutData(fd_combo);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("驱动名：");
		
		text_4 = new Text(composite, SWT.BORDER);
		fd_lblNewLabel_1.bottom = new FormAttachment(text_4, 0, SWT.BOTTOM);
		FormData fd_text_4 = new FormData();
		fd_text_4.top = new FormAttachment(lblNewLabel, 6);
		fd_text_4.right = new FormAttachment(combo, 0, SWT.RIGHT);
		fd_text_4.left = new FormAttachment(combo, 0, SWT.LEFT);
		text_4.setLayoutData(fd_text_4);
		
		text_5 = new Text(composite, SWT.BORDER);
		FormData fd_text_5 = new FormData();
		fd_text_5.right = new FormAttachment(combo, 0, SWT.RIGHT);
		fd_text_5.top = new FormAttachment(text_4, 6);
		fd_text_5.left = new FormAttachment(combo, 0, SWT.LEFT);
		text_5.setLayoutData(fd_text_5);
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.bottom = new FormAttachment(text_5, 0, SWT.BOTTOM);
		fd_lblNewLabel_2.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText("连接 URL：");
		
		text_6 = new Text(composite, SWT.BORDER);
		FormData fd_text_6 = new FormData();
		fd_text_6.right = new FormAttachment(combo, 0, SWT.RIGHT);
		fd_text_6.top = new FormAttachment(text_5, 6);
		fd_text_6.left = new FormAttachment(combo, 0, SWT.LEFT);
		text_6.setLayoutData(fd_text_6);
		
		Label label = new Label(composite, SWT.NONE);
		FormData fd_label = new FormData();
		fd_label.bottom = new FormAttachment(text_6, 0, SWT.BOTTOM);
		fd_label.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		label.setLayoutData(fd_label);
		label.setText("用户名：");
		
		text_7 = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		FormData fd_text_7 = new FormData();
		fd_text_7.right = new FormAttachment(combo, 0, SWT.RIGHT);
		fd_text_7.top = new FormAttachment(text_6, 6);
		fd_text_7.left = new FormAttachment(combo, 0, SWT.LEFT);
		text_7.setLayoutData(fd_text_7);
		
		Label label_1 = new Label(composite, SWT.NONE);
		FormData fd_label_1 = new FormData();
		fd_label_1.bottom = new FormAttachment(text_7, 0, SWT.BOTTOM);
		fd_label_1.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		label_1.setLayoutData(fd_label_1);
		label_1.setText("密码：");
		
		Label label_2 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		FormData fd_label_2 = new FormData();
		fd_label_2.bottom = new FormAttachment(label_1, 8, SWT.BOTTOM);
		fd_label_2.right = new FormAttachment(combo, 0, SWT.RIGHT);
		fd_label_2.top = new FormAttachment(text_7, 6);
		fd_label_2.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		label_2.setLayoutData(fd_label_2);
		
		Label lblJars = new Label(composite, SWT.NONE);
		FormData fd_lblJars = new FormData();
		fd_lblJars.top = new FormAttachment(label_2, 6);
		fd_lblJars.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblJars.setLayoutData(fd_lblJars);
		lblJars.setText("驱动 JARS");
		
		List list = new List(composite, SWT.BORDER);
		FormData fd_list = new FormData();
		fd_list.top = new FormAttachment(lblJars, 6);
		fd_list.left = new FormAttachment(0, 19);
		list.setLayoutData(fd_list);
		
		Button btnJars = new Button(composite, SWT.NONE);
		fd_list.right = new FormAttachment(btnJars, -6);
		FormData fd_btnJars = new FormData();
		fd_btnJars.top = new FormAttachment(label_2, 29);
		fd_btnJars.right = new FormAttachment(combo, 0, SWT.RIGHT);
		btnJars.setLayoutData(fd_btnJars);
		btnJars.setText("添加 JARS");
		
		Button btnJars_1 = new Button(composite, SWT.NONE);
		FormData fd_btnJars_1 = new FormData();
		fd_btnJars_1.top = new FormAttachment(btnJars, 14);
		fd_btnJars_1.right = new FormAttachment(combo, 0, SWT.RIGHT);
		btnJars_1.setLayoutData(fd_btnJars_1);
		btnJars_1.setText("删除 JARS");
		
		Combo combo_1 = new Combo(composite, SWT.NONE);
		FormData fd_combo_1 = new FormData();
		fd_combo_1.right = new FormAttachment(combo, 0, SWT.RIGHT);
		fd_combo_1.top = new FormAttachment(list, 6);
		fd_combo_1.left = new FormAttachment(combo, 0, SWT.LEFT);
		combo_1.setLayoutData(fd_combo_1);
		
		Label lblNewLabel_3 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_3 = new FormData();
		fd_lblNewLabel_3.bottom = new FormAttachment(combo_1, 0, SWT.BOTTOM);
		fd_lblNewLabel_3.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblNewLabel_3.setLayoutData(fd_lblNewLabel_3);
		lblNewLabel_3.setText("驱动类名：");
		
		Label label_3 = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		FormData fd_label_3 = new FormData();
		fd_label_3.top = new FormAttachment(combo_1, 6);
		fd_label_3.right = new FormAttachment(combo, 0, SWT.RIGHT);
		fd_label_3.bottom = new FormAttachment(combo_1, 8, SWT.BOTTOM);
		fd_label_3.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		label_3.setLayoutData(fd_label_3);
		
		
		
		return composite;
	}
	
	/**
	 * 初始化数据库类型定义文件的Document
	 * 
	 * @return
	 */
	private static void createDatabaseDocuemnt() {
		if (JDBCDocument == null) {
			SAXReader saxReader = new SAXReader();

			try {
				JDBCDocument = saxReader.read(new File(
						SystemConstants.DATABASE_TYPE_CONFIG_FILE));

			} catch (DocumentException e) {
				logger.error("初始化Document失败！" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * 得到所有JDBCTemplate
	 */
	private void getAllJDBCTemplate() {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	protected Point getInitialSize() {
		// TODO Auto-generated method stub
		return new Point(500, 500);
	}
}

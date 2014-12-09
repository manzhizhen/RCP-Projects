/* 文件名：     DatabaseGenerationSet.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-12-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.xml.sax.helpers.XMLFilterImpl;

import cn.sunline.suncard.powerdesigner.db.DatabaseGeneration;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 数据库生成设置
 * @author Agree
 * @version 1.0, 2012-12-5
 * @see
 * @since 1.0
 */
public class DatabaseGenerationSetDialog extends TitleAreaDialog {

	private static Document optionItemDocument;
	private Button buttonTableCreate;
	private DatabaseGeneration databaseGeneration;
	private Button buttonTableDrop;
	private Button buttonTableComment;
	private Button btnsql;
	private Button buttonColumnDefault;
	private Button buttonColumnCheck;
	private Button buttonCreatePrimaryKey;
	private Button buttonKeyDropPrimary;
	private Button buttonKeyCreateForegin;
	private Button buttonKeyDropForegin;
	private Group group;
	private Button buttonPhysicalModelSQL;
	private Button buttonStoredProcedure;
	private Button buttonIndex;

	private static Log logger = LogManager
			.getLogger(DatabaseGenerationSetDialog.class.getName());
	
	public DatabaseGenerationSetDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE
				| SWT.PRIMARY_MODAL);
	}

	@Override
	protected void configureShell(Shell newShell) {
		// 数据库生成
		newShell.setText(I18nUtil.getMessage("DATASET"));
		setDefaultImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.CREATE_DATABASE_SET_16));
		super.configureShell(newShell);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(636, 460);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// 数据库生成设置
		setTitle(I18nUtil.getMessage("DATABASE_GENERATING_SET"));
		setMessage(I18nUtil.getMessage("DATABASE_GENERATING_SET"));
		setTitleImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.CREATE_DATABASE_SET_64));

		Control control = super.createDialogArea(parent);

		Composite composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		composite.setLayout(new FormLayout());

		Group group_1 = new Group(composite, SWT.NONE);

		group_1.setText(I18nUtil.getMessage("ZC_TABLE"));
		group_1.setLayout(new FormLayout());
		FormData fd_group_1 = new FormData();
		fd_group_1.right = new FormAttachment(100, -10);
		fd_group_1.height = 35;
		fd_group_1.top = new FormAttachment(composite, 6);
		fd_group_1.left = new FormAttachment(composite, 6, SWT.LEFT);
		group_1.setLayoutData(fd_group_1);

		 buttonTableCreate = new Button(group_1, SWT.CHECK);
		FormData fd_buttonTableCreate = new FormData();
		fd_buttonTableCreate.width = 80;
		fd_buttonTableCreate.top = new FormAttachment(0, 11);
		fd_buttonTableCreate.left = new FormAttachment(0, 7);
		buttonTableCreate.setLayoutData(fd_buttonTableCreate);
		// 创建表格
		buttonTableCreate.setText(I18nUtil.getMessage("CREATE_TABLE"));
		buttonTableCreate.setSelection(databaseGeneration
				.isTableCreate());// 是否被选中

		buttonTableDrop = new Button(group_1, SWT.CHECK);
		FormData fd_buttonTableDrop = new FormData();
		fd_buttonTableDrop.top = new FormAttachment(0, 11);
		fd_buttonTableDrop.left = new FormAttachment(
				buttonTableCreate, 10);
		fd_buttonTableDrop.width = 80;
		buttonTableDrop.setLayoutData(fd_buttonTableDrop);

		// 删除表格
		buttonTableDrop.setText(I18nUtil.getMessage("DROP_TABLE"));
		buttonTableDrop.setSelection(databaseGeneration.isTableDrop());

		buttonTableComment = new Button(group_1, SWT.CHECK);
		FormData fd_buttonTableComment = new FormData();
		fd_buttonTableComment.width = 80;
		fd_buttonTableComment.top = new FormAttachment(0, 11);
		fd_buttonTableComment.left = new FormAttachment(
				buttonTableDrop, 10);
		buttonTableComment.setLayoutData(fd_buttonTableComment);
		// 注释
		buttonTableComment.setText(I18nUtil.getMessage("NOTE"));
		buttonTableComment.setSelection(databaseGeneration
				.isTableComment());

		Group group_2 = new Group(composite, SWT.NONE);
		// 列
		group_2.setText(I18nUtil.getMessage("ZC_LINE"));
		group_2.setLayout(new FormLayout());
		FormData fd_group_2 = new FormData();
		fd_group_2.right = new FormAttachment(100, -10);
		fd_group_2.height = 35;
		fd_group_2.top = new FormAttachment(group_1, 6);

		btnsql = new Button(group_1, SWT.CHECK);
		FormData fd_btnsql = new FormData();
		fd_btnsql.bottom = new FormAttachment(buttonTableCreate, 0,
				SWT.BOTTOM);
		fd_btnsql.left = new FormAttachment(buttonTableComment, 6);
		btnsql.setLayoutData(fd_btnsql);
		// 初始化
		btnsql.setText(I18nUtil.getMessage("INITIALIZE") + "SQL");
		fd_group_2.left = new FormAttachment(composite, 6, SWT.LEFT);
		group_2.setLayoutData(fd_group_2);
		btnsql.setSelection(databaseGeneration.isInitializeSQL());

		buttonColumnDefault = new Button(group_2, SWT.CHECK);
		FormData fd_buttonColumnDefault = new FormData();
		fd_buttonColumnDefault.top = new FormAttachment(0, 11);
		fd_buttonColumnDefault.left = new FormAttachment(0, 7);
		fd_buttonColumnDefault.width = 80;
		buttonColumnDefault
				.setLayoutData(fd_buttonColumnDefault);
		// 默认值
		buttonColumnDefault.setText(I18nUtil.getMessage("DEFAULT"));
		buttonColumnDefault.setSelection(databaseGeneration
				.isColumnDefault());

		buttonColumnCheck = new Button(group_2, SWT.CHECK);
		FormData fd_buttonColumnCheck = new FormData();
		fd_buttonColumnCheck.width = 80;
		fd_buttonColumnCheck.top = new FormAttachment(0, 11);
		fd_buttonColumnCheck.left = new FormAttachment(
				buttonColumnDefault, 10);
		buttonColumnCheck.setLayoutData(fd_buttonColumnCheck);
		// 检查
		buttonColumnCheck.setText(I18nUtil.getMessage("CHECK"));
		buttonColumnCheck.setSelection(databaseGeneration
				.isColumnCheck());

		Group group_3 = new Group(composite, SWT.NONE);
		// 键
		group_3.setText(I18nUtil.getMessage("KEY"));
		group_3.setLayout(new FormLayout());
		FormData fd_group_3 = new FormData();
		fd_group_3.right = new FormAttachment(100, -10);
		fd_group_3.height = 35;
		fd_group_3.top = new FormAttachment(group_2, 6);
		fd_group_3.left = new FormAttachment(composite, 6, SWT.LEFT);
		group_3.setLayoutData(fd_group_3);

		buttonCreatePrimaryKey = new Button(group_3, SWT.CHECK);
		buttonCreatePrimaryKey
				.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
					}
				});
		FormData fd_buttonCreatePrimaryKey = new FormData();
		fd_buttonCreatePrimaryKey.top = new FormAttachment(0, 11);
		fd_buttonCreatePrimaryKey.left = new FormAttachment(0, 7);
		fd_buttonCreatePrimaryKey.width = 80;
		buttonCreatePrimaryKey
				.setLayoutData(fd_buttonCreatePrimaryKey);
		// 创建主键
		buttonCreatePrimaryKey.setText(I18nUtil
				.getMessage("CREATE_PRIMARYKEY"));
		buttonCreatePrimaryKey.setSelection(databaseGeneration
				.isKeyCreatePrimary());

		buttonKeyDropPrimary = new Button(group_3, SWT.CHECK);
		FormData fd_buttonKeyDropPrimary = new FormData();
		fd_buttonKeyDropPrimary.top = new FormAttachment(0, 11);
		fd_buttonKeyDropPrimary.left = new FormAttachment(
				buttonCreatePrimaryKey, 10);
		fd_buttonKeyDropPrimary.width = 80;
		buttonKeyDropPrimary
				.setLayoutData(fd_buttonKeyDropPrimary);
		// 删除主键
		buttonKeyDropPrimary.setText(I18nUtil
				.getMessage("DROP_PRIMARYKEY"));
		buttonKeyDropPrimary.setSelection(databaseGeneration
				.isKeyDropPrimary());

		buttonKeyCreateForegin = new Button(group_3, SWT.CHECK);
		FormData fd_buttonKeyCreateForegin = new FormData();
		fd_buttonKeyCreateForegin.width = 80;
		fd_buttonKeyCreateForegin.top = new FormAttachment(0, 11);
		fd_buttonKeyCreateForegin.left = new FormAttachment(
				buttonKeyDropPrimary, 10);
		buttonKeyCreateForegin
				.setLayoutData(fd_buttonKeyCreateForegin);
		// 创建外键
		buttonKeyCreateForegin.setText(I18nUtil
				.getMessage("CREATE_FOREIGNKEY"));
		buttonKeyCreateForegin.setSelection(databaseGeneration
				.isKeyCreateForegin());

		buttonKeyDropForegin = new Button(group_3, SWT.CHECK);
		FormData fd_buttonKeyDropForegin = new FormData();
		fd_buttonKeyDropForegin.bottom = new FormAttachment(
				buttonCreatePrimaryKey, 0, SWT.BOTTOM);
		fd_buttonKeyDropForegin.left = new FormAttachment(
				buttonKeyCreateForegin, 6);
		buttonKeyDropForegin
				.setLayoutData(fd_buttonKeyDropForegin);
		// 删除外键
		buttonKeyDropForegin.setText(I18nUtil
				.getMessage("DROP_FOREIGNKEY"));
		buttonKeyDropForegin.setSelection(databaseGeneration
				.isKeyDropForegin());

		group = new Group(composite, SWT.NONE);
		FormData fd_group = new FormData();
		fd_group.bottom = new FormAttachment(group_3, 68, SWT.BOTTOM);
		fd_group.right = new FormAttachment(group_1, 0, SWT.RIGHT);
		fd_group.top = new FormAttachment(group_3, 13);
		fd_group.left = new FormAttachment(group_1, 0, SWT.LEFT);
		group.setLayoutData(fd_group);
		// 物理图形模型
		group.setText(I18nUtil.getMessage("PHYSICAL_GRAPHIC_MODEL"));
		
		 buttonIndex = new Button(group_1, SWT.CHECK);
		FormData fd_button = new FormData();
		fd_button.bottom = new FormAttachment(buttonTableCreate, 0, SWT.BOTTOM);
		fd_button.left = new FormAttachment(btnsql, 6);
		buttonIndex.setLayoutData(fd_button);
		buttonIndex.setText("索引");
		buttonIndex.setSelection(databaseGeneration.isIndexSQL());

		buttonPhysicalModelSQL = new Button(group, SWT.CHECK);
		buttonPhysicalModelSQL.setBounds(10, 28, 77, 17);
		// SQL语句
		buttonPhysicalModelSQL.setText(I18nUtil
				.getMessage("SQK_LANGUAGE"));
		buttonPhysicalModelSQL.setSelection(databaseGeneration
				.isPhysicalModelSQL());

		buttonStoredProcedure = new Button(group, SWT.CHECK);
		buttonStoredProcedure.setBounds(100, 28, 98, 17);
		// 存储过程
		buttonStoredProcedure.setText(I18nUtil
				.getMessage("STORED_PROCEDURE"));
		buttonStoredProcedure.setSelection(databaseGeneration
				.isStoredProcedure());
		return control;
	}

	/**
	 * 初始化数据库optionItemConfig文件的Document
	 * @return
	 */
	private Document createOptionItemConfigDocument() {
		if (optionItemDocument == null) {
			SAXReader saxReader = new SAXReader();

			try {
				optionItemDocument = saxReader.read(new File(
						SystemConstants.DATABASE_OPTIONITEM_CONFIG_FILE));

			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}

		return optionItemDocument;
	}
	
	/**
	 * 重写设置选项框的配置文件
	 * */
	private String writeConfig() {

		Document doc = createOptionItemConfigDocument();
		if (doc == null) {
			logger.error("初始化Document失败！");
			return null;
		}

		Element rootElement = doc.getRootElement();

		if (rootElement == null) {
			logger.error("Document根节点为空！");
			return null;
		}
		if (buttonTableCreate.getSelection()) {
			rootElement.element("createTable").setText("0");
		} else {
			rootElement.element("createTable").setText("1");
		}

		if (buttonTableDrop.getSelection()) {
			rootElement.element("dropTable").setText("0");
		} else {
			rootElement.element("dropTable").setText("1");
		}

		if (buttonTableComment.getSelection()) {
			rootElement.element("commentTable").setText("0");
		} else {
			rootElement.element("commentTable").setText("1");
		}

		if (buttonColumnDefault.getSelection()) {
			rootElement.element("defaultLine").setText("0");
		} else {
			rootElement.element("defaultLine").setText("1");
		}

		if (buttonCreatePrimaryKey.getSelection()) {
			rootElement.element("createPrimaryKey").setText("0");
		} else {
			rootElement.element("createPrimaryKey").setText("1");
		}

		if (buttonKeyDropPrimary.getSelection()) {
			rootElement.element("dropPrimaryKey").setText("0");
		} else {
			rootElement.element("dropPrimaryKey").setText("1");
		}

		if (buttonKeyCreateForegin.getSelection()) {
			rootElement.element("createForeignKey").setText("0");
		} else {
			rootElement.element("createForeignKey").setText("1");
		}

		if (buttonKeyDropForegin.getSelection()) {
			rootElement.element("dropForeignKey").setText("0");
		} else {
			rootElement.element("dropForeignKey").setText("1");
		}
		if (buttonColumnCheck.getSelection()) {
			rootElement.element("checkLine").setText("0");
		} else {
			rootElement.element("checkLine").setText("1");
		}
		if (btnsql.getSelection()) {
			rootElement.element("initializeSQL").setText("0");
		} else {
			rootElement.element("initializeSQL").setText("1");
		}
		if (buttonPhysicalModelSQL.getSelection()) {
			rootElement.element("physicalModelSQL").setText("0");
		} else {
			rootElement.element("physicalModelSQL").setText("1");
		}
		if (buttonStoredProcedure.getSelection()) {
			rootElement.element("storedProcedure").setText("0");
		} else {
			rootElement.element("storedProcedure").setText("1");
		}
		if(buttonIndex.getSelection()){
			rootElement.element("indexSQL").setText("0");
		} else {
			rootElement.element("indexSQL").setText("1");
		}

		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		XMLWriter writer;
		try {
			writer = new XMLWriter(new FileOutputStream(new File(
					SystemConstants.DATABASE_OPTIONITEM_CONFIG_FILE)), format);
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

		return null;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				I18nUtil.getMessage("CANCEL"), false);
	}
	
	@Override
	protected void okPressed() {
		writeConfig();
		super.okPressed();
	}
}

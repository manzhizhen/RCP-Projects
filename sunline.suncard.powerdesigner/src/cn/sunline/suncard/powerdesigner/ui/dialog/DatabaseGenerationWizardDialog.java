/* 文件名：     DatabaseGenerationWizardDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-12-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.wizard.DatabaseGenerationAddressPage;
import cn.sunline.suncard.powerdesigner.wizard.DatabaseGenerationPreviewPage;
import cn.sunline.suncard.powerdesigner.wizard.DatabaseGenerationWizard;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 生成数据库脚本向导对话框
 * @author Agree
 * @version 1.0, 2012-12-6
 * @see
 * @since 1.0
 */
public class DatabaseGenerationWizardDialog extends WizardDialog {

//	private DatabaseGenerationWizard databaseGenerationWizard;
	private static Log logger = LogManager
			.getLogger(DatabaseGenerationSetDialog.class.getName());

	/**
	 * @param parentShell
	 * @param newWizard
	 */
	public DatabaseGenerationWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
		// TODO Auto-generated constructor stub

//		databaseGenerationWizard = (DatabaseGenerationWizard) newWizard;

		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX);
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText(I18nUtil.getMessage("ACD_PARA_SET"));
		setDefaultImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.CREATE_DATABASE_16));
		setTitleImage(CacheImage.getCacheImage().getImage(DmConstants.PD_APPLICATION_ID, 
				IDmImageKey.CREATE_DATABASE_64));
		super.configureShell(newShell);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 600);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected Button getButton(int id) {
		return super.getButton(id);
	}

	@Override
	protected void finishPressed() {
		// TODO Auto-generated method stub
		// 得到指定Page
		DatabaseGenerationPreviewPage databaseGenerationPreviewPage = (DatabaseGenerationPreviewPage) getCurrentPage()
				.getWizard().getPage("预览窗口");
		DatabaseGenerationAddressPage databaseGenerationAddressPage = (DatabaseGenerationAddressPage) getCurrentPage()
				.getWizard().getPage("地址窗口");

		writeSQL(databaseGenerationPreviewPage.text.getText(),
				databaseGenerationAddressPage.dirText.getText(),
				databaseGenerationAddressPage.fileNameText.getText());
		super.finishPressed();
	}

	/**
	 * 生成.sql 文件
	 * */
	private void writeSQL(String text, String dirText, String fileNameText) {

		String destFileName = dirText + "\\" + fileNameText + ".sql";
		File file = new File(destFileName);
		try {
			if (file.createNewFile()) {
				// 创建单个文件成功！
				logger.warn(I18nUtil.getMessage("CREATE_FILE_SUCCESS")
						+ destFileName);
			} else {
				// 创建单个文件失败！
				logger.warn(I18nUtil.getMessage("CREATE_FILE_FAIL")
						+ destFileName);
			}
		} catch (IOException e) {
			e.printStackTrace();
			// 创建单个文件失败！
			logger.warn(I18nUtil.getMessage("CREATE_FILE_FAIL") + destFileName);
		}

		try {
			Writer writer = new FileWriter(destFileName);
			writer.write(text);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

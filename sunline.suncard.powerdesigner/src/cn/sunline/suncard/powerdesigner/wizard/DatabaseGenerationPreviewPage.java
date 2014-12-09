/* 文件名：     DatabaseGenerationPreviewPage.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-12-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.wizard;

import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

import cn.sunline.suncard.powerdesigner.db.CreateProductionSQLManager;
import cn.sunline.suncard.powerdesigner.db.DatabaseManager;
import cn.sunline.suncard.powerdesigner.db.KeyWordsManager;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.db.KeyWords;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 
 * @author  Agree
 * @version 1.0, 2012-12-6
 * @see 
 * @since 1.0
 */
public class DatabaseGenerationPreviewPage extends WizardPage{


	public StyledText text;
	private DatabaseManager databaseManager;

	/**
	 * @param pageName
	 */
	protected DatabaseGenerationPreviewPage(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		setTitle(I18nUtil.getMessage("PREVIEW"));
		setMessage(I18nUtil.getMessage("PREVIEW"));
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FormLayout());

		text = new StyledText(composite, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		FormData fd_text = new FormData();
		fd_text.top = new FormAttachment(0, 5);
		fd_text.bottom = new FormAttachment(100, -5);
		fd_text.left = new FormAttachment(0, 5);
		fd_text.right = new FormAttachment(100, -5);
		text.setLayoutData(fd_text);
		
		this.setControl(composite);
	}
	
	public void initControl(List<TableModel> childrenTableModel) {
		databaseManager = new DatabaseManager();

		text.setText("");

		CreateProductionSQLManager createProductionSQLManager = new CreateProductionSQLManager();
		String SQLPreview = createProductionSQLManager.createTableSQLs(childrenTableModel);
		text.setText(SQLPreview);
		
		if(!text.getText().isEmpty()){
			text.setText(text.getText().trim());
		}
		
		// 关键字变色
		int textLength = text.getText().length();
		text.replaceStyleRanges(0, textLength, databaseManager.changeColor(text));
	}
}

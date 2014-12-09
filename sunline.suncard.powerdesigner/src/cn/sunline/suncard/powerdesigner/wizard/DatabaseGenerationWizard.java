/* 文件名：     DatabaseGenerationWizard.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-12-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.wizard;

import org.eclipse.jface.wizard.Wizard;

import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;

/**
 * 数据库脚本生成向导
 * @author Agree
 * @version 1.0, 2012-12-6
 * @see
 * @since 1.0
 */
public class DatabaseGenerationWizard extends Wizard {

	private PhysicalDiagramModel physicalDiagramModel;
	private DatabaseGenerationProductTreePage databaseGenerationProductTreePage;
	private DatabaseGenerationPreviewPage databaseGenerationPreviewPage;
	private DatabaseGenerationAddressPage databaseGenerationAddressPage;

	
	public DatabaseGenerationWizard(PhysicalDiagramModel physicalDiagramModel){
		this.physicalDiagramModel = physicalDiagramModel;
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public void addPages() {
		databaseGenerationProductTreePage = new DatabaseGenerationProductTreePage(
				"设置窗口", this.physicalDiagramModel);
		
		 databaseGenerationPreviewPage = new DatabaseGenerationPreviewPage(
				"预览窗口");
		 
		 databaseGenerationAddressPage = new DatabaseGenerationAddressPage(
				"地址窗口", this.physicalDiagramModel);
		 
		addPage(databaseGenerationProductTreePage);
		addPage(databaseGenerationPreviewPage);
		addPage(databaseGenerationAddressPage);

		// 设置向导对话框标题
		setWindowTitle("数据库生成向导");
		super.addPages();
	}
}

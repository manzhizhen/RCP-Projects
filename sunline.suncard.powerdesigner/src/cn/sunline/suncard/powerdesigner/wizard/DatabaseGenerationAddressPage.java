/* 文件名：     DatabaseGenerationAddressPage.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2012-12-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 
 * @author  Agree
 * @version 1.0, 2012-12-6
 * @see 
 * @since 1.0
 */
public class DatabaseGenerationAddressPage extends WizardPage{

	private Text currentText;
	public Text dirText;
	public Text fileNameText;
	private PhysicalDiagramModel physicalDiagramModel;

	/**
	 * @param pageName
	 */
	protected DatabaseGenerationAddressPage(String pageName, PhysicalDiagramModel physicalDiagramModel ) {
		super(pageName);
		this.physicalDiagramModel = physicalDiagramModel;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		
		this.setTitle(I18nUtil.getMessage("CONVENTION"));
		this.setMessage(I18nUtil.getMessage("CONVENTION"));
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FormLayout());

		// 当前数据库管理系统
		Label lblNewLabel = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		// lblNewLabel.setText(I18nUtil.getMessage("DBMS") + ":");
		//数据库管理系统:
		lblNewLabel.setText(I18nUtil.getMessage("DBMS"));
		currentText = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		FormData fd_currentText = new FormData();
		fd_currentText.right = new FormAttachment(100, -10);
		fd_currentText.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_currentText.left = new FormAttachment(lblNewLabel, 6);
		currentText.setLayoutData(fd_currentText);
		currentText.setText(this.physicalDiagramModel.getPackageModel().getPhysicalDataModel()
				.getDatabaseTypeModel().getDatabaseName());

		// 目录
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText(I18nUtil.getMessage("CATALOGUE"));
		dirText = new Text(composite, SWT.BORDER);
		fd_lblNewLabel_1.top = new FormAttachment(dirText, 3, SWT.TOP);
		FormData fd_dirText = new FormData();
		fd_dirText.right = new FormAttachment(100, -100);
		fd_dirText.top = new FormAttachment(currentText, 8);
		fd_dirText.left = new FormAttachment(currentText, 0, SWT.LEFT);
		dirText.setLayoutData(fd_dirText);

		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		//文件名称:
		lblNewLabel_2.setText(I18nUtil.getMessage("FILE_NAME"));
		fileNameText = new Text(composite, SWT.BORDER);
		fd_lblNewLabel_2.top = new FormAttachment(fileNameText, 3, SWT.TOP);
		FormData fd_fileNameText = new FormData();
		fd_fileNameText.right = new FormAttachment(100, -10);
		fd_fileNameText.top = new FormAttachment(dirText, 10);
		fd_fileNameText.left = new FormAttachment(currentText, 0, SWT.LEFT);
		fileNameText.setLayoutData(fd_fileNameText);

		Button btnNewButton = new Button(composite, SWT.NONE);
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.width = 80;
		fd_btnNewButton.top = new FormAttachment(currentText, 6);
		fd_btnNewButton.left = new FormAttachment(dirText, 6);
		btnNewButton.setLayoutData(fd_btnNewButton);
		//浏览
		btnNewButton.setText(I18nUtil.getMessage("BROWSE"));
		// 搜索地址功能
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				dirText.setText(getLocation(getShell()));
				dirText.forceFocus();
			}
		});
		
		this.setControl(composite);
//		createEvent();
	}
	
	// 得到地址的方法
		protected String getLocation(Shell shell) {
			DirectoryDialog dialog = new DirectoryDialog(shell);
			String path = dialog.open();
			if (path == null) {
				//请输入地址
				String warn = I18nUtil.getMessage("ENTER_ADDRESS");
				return warn;
			} else {
				return path;
			}

		}
		
//		@Override
//		public boolean isPageComplete() {
//			// TODO Auto-generated method stub
//			if("".equals(dirText.getText()) && "".equals(fileNameText.getText())){
//				return false;
//			}
//			
//			return super.isPageComplete();
//		}
//		
//		/**
//		 * 创建监听方法
//		 */
//		private void createEvent() {
//			// TODO Auto-generated method stub
//			fileNameText.addMouseMoveListener(new MouseMoveListener() {
//				
//				@Override
//				public void mouseMove(MouseEvent e) {
//					// TODO Auto-generated method stub
//					isPageComplete();
//				}
//			});
//		}
}

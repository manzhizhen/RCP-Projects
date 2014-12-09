/* 文件名：     ModuleXmlModelDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-2
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog.modelattri;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.powerdesigner.model.xml.ModuleXmlModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.ui.dialog.ModuleLabelManagerDialog;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 模块XML对象对话框
 * 
 * @author Manzhizhen
 * @version 1.0, 2013-1-2
 * @see
 * @since 1.0
 */
public class ModuleXmlModelDialog extends TitleAreaDialog {
	private List<ModuleXmlModel> moduleXmlModels;
	private List<String> allUsedId = new ArrayList<String>();
	private ModuleXmlModel moduleXmlModel;
	private ModuleXmlModel newModuleXmlModel;
	private String flag;

	private Text idText;
	private Text nameText;
	private Text noteText;
	private Composite composite;
	private Log logger = LogManager.getLogger(ModuleLabelManagerDialog.class
			.getName());

	public ModuleXmlModelDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE
				| SWT.PRIMARY_MODAL);
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("模块标签对话框");
		setDefaultImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.MODULE_LABEL_16));
		super.configureShell(newShell);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(569, 425);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("模块标签属性");
		setMessage("模块标签属性");
		setTitleImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.MODULE_LABEL_64));

		Control control = super.createDialogArea(parent);

		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		composite.setLayout(new FormLayout());

		createControl();
		initControlValue();

		return control;
	}

	private void createControl() {
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.width = 60;
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("模块ID:");

		idText = new Text(composite, SWT.BORDER);
		FormData fd_idText = new FormData();
		fd_idText.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_idText.left = new FormAttachment(lblNewLabel, 6);
		fd_idText.right = new FormAttachment(100, -10);
		idText.setLayoutData(fd_idText);

		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_lblNewLabel_1.width = 60;
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("模块名称:");

		nameText = new Text(composite, SWT.BORDER);
		fd_lblNewLabel_1.top = new FormAttachment(nameText, 3, SWT.TOP);
		FormData fd_nameText = new FormData();
		fd_nameText.right = new FormAttachment(100, -10);
		fd_nameText.top = new FormAttachment(idText, 11);
		fd_nameText.left = new FormAttachment(idText, 0, SWT.LEFT);
		nameText.setLayoutData(fd_nameText);

		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_lblNewLabel_2.width = 60;
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText("备注:");

		noteText = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL
				| SWT.MULTI);
		fd_lblNewLabel_2.top = new FormAttachment(noteText, 3, SWT.TOP);
		FormData fd_noteText = new FormData();
		fd_noteText.height = 100;
		fd_noteText.right = new FormAttachment(100, -10);
		fd_noteText.top = new FormAttachment(nameText, 11);
		fd_noteText.left = new FormAttachment(idText, 0, SWT.LEFT);
		noteText.setLayoutData(fd_noteText);
	}

	private void initControlValue() {
		if (DmConstants.COMMAND_MODIFY.equals(flag)) {
			if (moduleXmlModel == null) {
				logger.error("传入的ModuleXmlModel为空，无法初始化ModuleXmlModelDialog！");
				setErrorMessage("传入的ModuleXmlModel为空，无法初始化ModuleXmlModelDialog！");
				return;
			}

		} else if (DmConstants.COMMAND_ADD.equals(flag)) {
		}
	}

	@Override
	protected void okPressed() {
		if(!checkData()) {
			return ;
		}
		
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			newModuleXmlModel = new ModuleXmlModel();
			newModuleXmlModel.setId(idText.getText().trim());
			newModuleXmlModel.setName(nameText.getText().trim());
			newModuleXmlModel.setNote(noteText.getText().trim());
		}
		
		
		super.okPressed();
	}

	/**
	 * 检查数据的正确性
	 */
	private boolean checkData() {
		if (DmConstants.COMMAND_ADD.equals(flag)) {
			if (moduleXmlModels == null) {
				logger.error("传入的moduleXmlModels为空，无法校验数据！");
				setErrorMessage("传入的moduleXmlModels为空，无法校验数据！");
				return false;
			}

			String str = idText.getText().trim();
			if (str.isEmpty()) {
				// ID不能为空！
				setErrorMessage(I18nUtil.getMessage("ID_NOT_EMPTY"));
				return false;
			}

			if (allUsedId.contains(str)) {
				// 此ID已经存在，请更换！
				setErrorMessage("此ID已经存在，请更换！");
				return false;
			}

			str = nameText.getText().trim();
			if ("".equals(str)) {
				setErrorMessage(I18nUtil.getMessage("NAME_NOT_EMPTY"));
				return false;
			}

		}

		setErrorMessage(null);
		setMessage("模块标签属性");

		return true;
	}

	public void setModuleXmlModels(List<ModuleXmlModel> moduleXmlModels) {
		this.moduleXmlModels = moduleXmlModels;
		if (moduleXmlModels != null) {
			for (ModuleXmlModel moduleXmlModel : moduleXmlModels) {
				allUsedId.add(moduleXmlModel.getId());
			}
		}
	}

	public ModuleXmlModel getNewModuleXmlModel() {
		return newModuleXmlModel;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
}

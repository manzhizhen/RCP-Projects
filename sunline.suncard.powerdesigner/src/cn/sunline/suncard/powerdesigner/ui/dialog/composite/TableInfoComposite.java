/* 文件名：     TableInfoComposite.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-7
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog.composite;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.DocumentException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.sunline.suncard.powerdesigner.manager.ProductSpaceManager;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmDictConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.sde.bs.dict.BizDictManager;
import cn.sunline.suncard.sde.bs.dict.DictComboViewer;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import org.eclipse.swt.widgets.Button;

/**
 * 表格对话框中表格基本信息的Composite
 * @author  Manzhizhen
 * @version 1.0, 2013-1-7
 * @see 
 * @since 1.0
 */
public class TableInfoComposite extends Composite implements ISubComposite{
	private TableModel cloneTableModel;
	private TableModel tableModel;
	
	private boolean isModuleCheckboxTreeCompositeShow = true; // 模块复选框树是否显示
	
	private Text nameText;
	private Text descText;
	private Text noteText;
	private ModuleCheckboxTreeComposite moduleCheckboxTreeComposite;
	private DictComboViewer tableTypeComboViewer;
	
	/**
	 * @param parent
	 * @param style
	 */
	public TableInfoComposite(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	@Override
	public void createControl() {
		setLayout(new FormLayout());
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.width = 60;
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("表格名称:");
		nameText = new Text(this, SWT.BORDER);
		FormData fd_nameText = new FormData();
		fd_nameText.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_nameText.left = new FormAttachment(lblNewLabel, 6);
		fd_nameText.right = new FormAttachment(100, -10);
		nameText.setLayoutData(fd_nameText);

		Label lblNewLabel_1 = new Label(this, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.width = 60;
		fd_lblNewLabel_1.top = new FormAttachment(nameText, 14);
		fd_lblNewLabel_1.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		lblNewLabel_1.setText("表格描述:");
		descText = new Text(this, SWT.BORDER);
		FormData fd_descText = new FormData();
		fd_descText.top = new FormAttachment(lblNewLabel_1, -3, SWT.TOP);
		fd_descText.left = new FormAttachment(nameText, 0, SWT.LEFT);
		fd_descText.right = new FormAttachment(100, -10);
		descText.setLayoutData(fd_descText);

		Label lblNewLabel_2 = new Label(this, SWT.RIGHT);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.width = 60;
		fd_lblNewLabel_2.top = new FormAttachment(lblNewLabel_1, 18);
		fd_lblNewLabel_2.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		lblNewLabel_2.setText("备注:");
		noteText = new Text(this, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		noteText.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_noteText = new FormData();
		fd_noteText.top = new FormAttachment(lblNewLabel_2, -3, SWT.TOP);
		fd_noteText.left = new FormAttachment(nameText, 0, SWT.LEFT);
		fd_noteText.right = new FormAttachment(100, -10);
		fd_noteText.height = 80;
		noteText.setLayoutData(fd_noteText);
		
		if(isModuleCheckboxTreeCompositeShow) {
			Label lblNewLabel_3 = new Label(this, SWT.NONE);
			lblNewLabel_3.setAlignment(SWT.RIGHT);
			FormData fd_lblNewLabel_3 = new FormData();
			fd_lblNewLabel_3.width = 60;
			fd_lblNewLabel_3.top = new FormAttachment(lblNewLabel_2, 84);
			fd_lblNewLabel_3.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
			lblNewLabel_3.setLayoutData(fd_lblNewLabel_3);
			lblNewLabel_3.setText("所属模块:");
			FormData fd_moduleTree = new FormData();
			fd_moduleTree.bottom = new FormAttachment(100, -50);
			fd_moduleTree.right = new FormAttachment(100, -10);
			fd_moduleTree.top = new FormAttachment(noteText, 13);
			fd_moduleTree.left = new FormAttachment(nameText, 0, SWT.LEFT);
			moduleCheckboxTreeComposite = new ModuleCheckboxTreeComposite(this, SWT.BORDER);
			moduleCheckboxTreeComposite.setLayoutData(fd_moduleTree);
		}
		
		Label lblNewLabel_4 = new Label(this, SWT.NONE);
		FormData fd_lblNewLabel_4 = new FormData();
		fd_lblNewLabel_4.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		lblNewLabel_4.setLayoutData(fd_lblNewLabel_4);
		lblNewLabel_4.setText("表格类型:");
		Combo tableTypeCombo = new Combo(this, SWT.READ_ONLY);
		fd_lblNewLabel_4.top = new FormAttachment(tableTypeCombo, 3, SWT.TOP);
		tableTypeComboViewer = new DictComboViewer(tableTypeCombo);
		FormData fd_tableTypeCombo = new FormData();
		if(isModuleCheckboxTreeCompositeShow) {
			fd_tableTypeCombo.top = new FormAttachment(100, -40);
		} else {
			fd_tableTypeCombo.top = new FormAttachment(noteText, 12, SWT.BOTTOM);
		}
		fd_tableTypeCombo.left = new FormAttachment(nameText, 0, SWT.LEFT);
		fd_tableTypeCombo.right = new FormAttachment(50, 0);
		tableTypeCombo.setLayoutData(fd_tableTypeCombo);
	}

	@Override
	public void initControlData() throws DocumentException {
		nameText.setText(cloneTableModel.getTableName() == null ? ""
				: cloneTableModel.getTableName());
		descText.setText(cloneTableModel.getTableDesc() == null ? ""
				: cloneTableModel.getTableDesc());
		noteText.setText(cloneTableModel.getTableNote() == null ? ""
				: cloneTableModel.getTableNote());
		
		Map<String, String> map = BizDictManager.getDictIdValue(IDmDictConstants
				.TABLE_TYPE);
		tableTypeComboViewer.setMap(map);
		tableTypeComboViewer.setSelect(cloneTableModel.getTableType());
		
		// 初始化模块树数据
		if(isModuleCheckboxTreeCompositeShow) {
			FileModel fileModel = FileModel.getFileModelFromObj(cloneTableModel);
			List<ProductModel> fileProductList = ProductSpaceManager.getProductModelList(fileModel);
			moduleCheckboxTreeComposite.setProductModelList(fileProductList);
			moduleCheckboxTreeComposite.setTableModel(tableModel);
			moduleCheckboxTreeComposite.initControlData();
		}
	}

	@Override
	public void createControlEvent() {
		nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				checkData();
			}
		});
		
		descText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				checkData();
			}
		});
		
		if(isModuleCheckboxTreeCompositeShow) {
			moduleCheckboxTreeComposite.createControlEvent();
		}
	}

	@Override
	public String checkData() {
		String str = nameText.getText().trim();
		if ("".equals(str)) {
			return "表格名称不能为空！";
		}

		// 同一个物理数据模型下表格必须唯一
		if (cloneTableModel == null) {
			// 表模型为空，无法校验数据！
			return I18nUtil.getMessage("TABLEMODEL_IS_EMPTY_CAN_NOT_CHECK_DATA");
		}
		
		FileModel fileModel = FileModel.getFileModelFromObj(cloneTableModel);
		if(fileModel == null) {
			// 找不到对应文件模型，无法校验数据！
			return I18nUtil.getMessage("FILEMODEL_IS_EMPTY_CAN_NOT_CHECK");
		}
		
		Set<TableModel> tableModels = fileModel.getAllTableModel();
		for(TableModel tableModel : tableModels) {
			if(!tableModel.equals(this.tableModel) && str.equals(tableModel.getTableName())) {
				return I18nUtil.getMessage("TABLEMODEL_NAME_IS_ALREADY");
			}
		}

		str = descText.getText().trim();
		if ("".equals(str)) {
			// 表格描述不能为空！
			return I18nUtil.getMessage("TABLE_DESC_NOT_EMPTY");
		}
		
		IStructuredSelection select = (IStructuredSelection) tableTypeComboViewer
				.getSelection();
		if(select.isEmpty()) {
			// 表格类型不能为空！
			return I18nUtil.getMessage("TABLE_TYPE_NOT_EMPTY");
		}
		
		return null;
	}

	public ModuleCheckboxTreeComposite getModuleCheckboxTreeComposite() {
		return moduleCheckboxTreeComposite;
	}

	public String getNameTextValue() {
		return nameText.getText().trim();
	}

	public String getDesc() {
		return descText.getText().trim();
	}

	public String getNote() {
		return noteText.getText().trim();
	}
	
	public DictComboViewer getTableTypeComboViewer() {
		return tableTypeComboViewer;
	}
	
	public void setModuleCheckboxTreeCompositeShow(
			boolean isModuleCheckboxTreeCompositeShow) {
		this.isModuleCheckboxTreeCompositeShow = isModuleCheckboxTreeCompositeShow;
	}

	public void setTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
	}

	public void setCloneTableModel(TableModel cloneTableModel) {
		this.cloneTableModel = cloneTableModel;
	}
	
	
}

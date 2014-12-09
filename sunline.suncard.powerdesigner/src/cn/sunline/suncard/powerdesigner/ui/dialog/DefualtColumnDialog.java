/* 文件名：     DefualtColumnDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     wzx
 * 修改时间：2012-11-23
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.command.UpdateDefualtColumnCommand;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.ui.dialog.composite.ColumnModelComposite;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 
 * @author  wzx
 * @version 1.0, 2012-11-23
 * @see 
 * @since 1.0
 */
public class DefualtColumnDialog extends TitleAreaDialog {
	private Log logger = LogManager.getLogger(DefualtColumnDialog.class.getName());
	
	private ColumnModelComposite columnModelComposite;
	private PhysicalDataModel physicalDataModel;
	private UpdateDefualtColumnCommand command;
	private TreeContent defaultColumnsNodeTreeContent;
	private DatabaseTreeViewPart databaseTreeViewPart;

	public DefualtColumnDialog(Shell parentShell, PhysicalDataModel physicalDataModel) {
		super(parentShell);
		this.physicalDataModel = physicalDataModel;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage(I18nUtil.getMessage("DEFUALT_COLUMN_SET"));
		setTitle(I18nUtil.getMessage("DEFUALT_COLUMN_SET"));
	
		Composite area = (Composite) super.createDialogArea(parent);
		
		columnModelComposite = new ColumnModelComposite(area, SWT.NONE);
		columnModelComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		columnModelComposite.setPhysicalDataModel(physicalDataModel);
		// 设置显示的默认字段
		List<ColumnModel> columnModels = physicalDataModel.getDefaultColumnList();
		List<ColumnModel> cloneColumnModels = new ArrayList<ColumnModel>();
		for (ColumnModel columnModel : columnModels) {
			try {
				cloneColumnModels.add(columnModel.clone());
			} catch (CloneNotSupportedException e) {
				logger.error("复制列对象时出错！" + e.getMessage());
			}
		}
		
		columnModelComposite.setColumnModelList(cloneColumnModels);
		
		columnModelComposite.initColumnItemData();	// 初始化数据
		columnModelComposite.createEvent();	// 创建事件
		
		return area;
	}

	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"), true);
		createButton(parent, IDialogConstants.CANCEL_ID, I18nUtil.getMessage("CANCEL"), false);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(730, 600);
	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText(I18nUtil.getMessage("DEFUALT_COLUMN"));
		setDefaultImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.A_DEFAULT_COLUMN));
		setTitleImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.DEFAULT_COLUMN_64));
		super.configureShell(newShell);
	}
	
	@Override
	protected void okPressed() {
		List<ColumnModel> columnModels = columnModelComposite.getColumnModelList();
		command = new UpdateDefualtColumnCommand();
		command.setDefaultColumnsNodeTreeContent(defaultColumnsNodeTreeContent);
		command.setNewColumnModels(columnModels);
		command.setDatabaseTreeViewPart(databaseTreeViewPart);
		
		super.okPressed();
	}
	
	public UpdateDefualtColumnCommand getCommand() {
		return command;
	}
	
	public void setDatabaseTreeViewPart(DatabaseTreeViewPart databaseTreeViewPart) {
		this.databaseTreeViewPart = databaseTreeViewPart;
	}

	public void setDefaultColumnsNodeTreeContent(
			TreeContent defaultColumnsNodeTreeContent) {
		this.defaultColumnsNodeTreeContent = defaultColumnsNodeTreeContent;
	}
	
}

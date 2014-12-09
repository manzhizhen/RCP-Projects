/* 文件名：     ChangeCurrentDatabaseCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-10-22
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.db.DatabaseManager;
import cn.sunline.suncard.powerdesigner.gef.editpart.DatabaseDiagramModelEditPart;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.db.DataTypeModel;
import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 转换当前数据库的Command
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-10-22
 * @see
 * @since 1.0
 */
public class ChangeCurrentDatabaseCommand extends Command {
	private PhysicalDataModel physicalDataModel;
	private DatabaseTypeModel databaseTypeModel;
	private DatabaseTypeModel oldDatabaseTypeModel;

	private static Log logger = LogManager
			.getLogger(ChangeCurrentDatabaseCommand.class.getName());

	@Override
	public void execute() {
		if (physicalDataModel == null || databaseTypeModel == null) {
			logger.error("传入的Editor或DatabaseTypeModel为空，无法转换当前数据库！");
			return;
		}

		// 获得物理数据模型
		oldDatabaseTypeModel = physicalDataModel.getDatabaseTypeModel();
		physicalDataModel.setDatabaseTypeModel(databaseTypeModel);

		// 改变该物理数据模型下所有的物理图形模型的数据库类型
		Set<PhysicalDiagramModel> physicalDiagramModelSet = physicalDataModel.getAllPhysicalDiagramModels();
		for (PhysicalDiagramModel physicalDiagramModel : physicalDiagramModelSet) {
			Collection<TableModel> tableModels = physicalDiagramModel
					.getTableMap().values();
			for (TableModel tableModel : tableModels) {
				List<ColumnModel> columnModelList = tableModel
						.getColumnList();
				for (ColumnModel columnModel : columnModelList) {
					DataTypeModel dataTypeModel = DatabaseManager
							.getSwtichDataTypeModel(
									columnModel.getDataTypeModel(),
									databaseTypeModel);
					if (dataTypeModel != null) {
						columnModel.setDataTypeModel(dataTypeModel);
					} else {
						logger.error("数据库类型转换出错，无法找到匹配类型:"
								+ columnModel.getDataTypeModel().getName());
						MessageDialog.openError(PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getShell(),
								I18nUtil.getMessage("MESSAGE"),
								"数据库类型转换出错，无法找到匹配类型:"
										+ columnModel.getDataTypeModel()
												.getName());

						return;
					}
				}
			}
		}

		// 刷新已经打开的相关Editor中的数据
		// IEditorReference[] editors =
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow().
		// getActivePage().getEditorReferences();
		// for(IEditorReference editorReference : editors) {
		// if(editorReference.getEditor(false) instanceof DatabaseDiagramEditor)
		// {
		// DatabaseDiagramEditor editor = (DatabaseDiagramEditor)
		// editorReference.
		// getEditor(false);
		//
		// if(physicalDataModel.equals(editor.getPhysicalDiagramModel().getPhysicalDataModel()))
		// {
		// List<AbstractGefModel> gefModelList =
		// editor.getDatabaseDiagramGefModel().getChildren();
		// for(AbstractGefModel gefModel : gefModelList) {
		// // 为了刷新界面而已
		// gefModel.setDataObject(gefModel.getDataObject());
		// }
		// }
		// }
		// }

		DatabaseDiagramModelEditPart.refreshEditor();

		super.execute();
	}

	@Override
	public void undo() {
		if (physicalDataModel == null || databaseTypeModel == null) {
			logger.error("传入的Editor或DatabaseTypeModel为空，无法转换当前数据库！");
			return;
		}

		// 获得物理数据模型
		physicalDataModel.setDatabaseTypeModel(oldDatabaseTypeModel);

		// 改变该物理数据模型下所有的物理图形模型的数据库类型
		Set<PhysicalDiagramModel> physicalDiagramModelSet = physicalDataModel.getAllPhysicalDiagramModels();
		for (PhysicalDiagramModel physicalDiagramModel : physicalDiagramModelSet) {
			Collection<TableModel> tableModels = physicalDiagramModel
					.getTableMap().values();
			for (TableModel tableModel : tableModels) {
				List<ColumnModel> columnModelList = tableModel
						.getColumnList();
				for (ColumnModel columnModel : columnModelList) {
					DataTypeModel dataTypeModel = DatabaseManager
							.getSwtichDataTypeModel(
									columnModel.getDataTypeModel(),
									oldDatabaseTypeModel);
					if (dataTypeModel != null) {
						columnModel.setDataTypeModel(dataTypeModel);
					} else {
						logger.error("数据库类型转换出错，无法找到匹配类型:"
								+ columnModel.getDataTypeModel().getName());
						MessageDialog.openError(PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getShell(),
								I18nUtil.getMessage("MESSAGE"),
								"数据库类型转换出错，无法找到匹配类型:"
										+ columnModel.getDataTypeModel()
												.getName());

						return;
					}
				}
			}
		}

		// 刷新已经打开的相关Editor中的数据
		IEditorReference[] editors = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage()
				.getEditorReferences();
		for (IEditorReference editorReference : editors) {
			if (editorReference.getEditor(false) instanceof DatabaseDiagramEditor) {
				DatabaseDiagramEditor editor = (DatabaseDiagramEditor) editorReference
						.getEditor(false);

				// 如果是该物理数据模型下的Editor，则需要变换里面的TableModel
				if (physicalDataModel.equals(editor.getPhysicalDiagramModel()
						.getPackageModel())) {
					List<AbstractGefModel> gefModelList = editor
							.getDatabaseDiagramGefModel().getChildren();
					for (AbstractGefModel gefModel : gefModelList) {
						// 为了刷新界面而已
						gefModel.setDataObject(gefModel.getDataObject());
					}
				}
			}
		}

		super.undo();
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	public void setPhysicalDataModel(PhysicalDataModel physicalDataModel) {
		this.physicalDataModel = physicalDataModel;
	}

	public void setDatabaseTypeModel(DatabaseTypeModel databaseTypeModel) {
		this.databaseTypeModel = databaseTypeModel;
	}

}

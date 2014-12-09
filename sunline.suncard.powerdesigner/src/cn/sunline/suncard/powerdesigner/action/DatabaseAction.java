/* 文件名：     DatabaseAction.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-5
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dom4j.DocumentException;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.powerdesigner.Activator;
import cn.sunline.suncard.powerdesigner.command.UpdateCommonColumnModelCommand;
import cn.sunline.suncard.powerdesigner.command.UpdatePackageModelCommand;
import cn.sunline.suncard.powerdesigner.command.UpdatePhysicalDataCommand;
import cn.sunline.suncard.powerdesigner.command.UpdatePhysicalDiagramCommand;
import cn.sunline.suncard.powerdesigner.db.DatabaseManager;
import cn.sunline.suncard.powerdesigner.exception.CanNotFoundFolderFromWorkSpaceException;
import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.file.SwitchObjectAndFile;
import cn.sunline.suncard.powerdesigner.gef.command.DelTableModelCommand;
import cn.sunline.suncard.powerdesigner.gef.command.DelTableShortcutModelCommand;
import cn.sunline.suncard.powerdesigner.gef.command.PasteModelCommand;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.DatabaseDiagramGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableShortcutGefModel;
import cn.sunline.suncard.powerdesigner.gef.ui.dialog.ColumnPropertiesDialog;
import cn.sunline.suncard.powerdesigner.gef.ui.dialog.TableGefModelDialog;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditorInput;
import cn.sunline.suncard.powerdesigner.handler.ImportHandler;
import cn.sunline.suncard.powerdesigner.handler.NewHandler;
import cn.sunline.suncard.powerdesigner.handler.OpenHandler;
import cn.sunline.suncard.powerdesigner.manager.WorkSpaceManager;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.model.WorkSpaceModel;
import cn.sunline.suncard.powerdesigner.model.db.DataTypeModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.models.DefaultColumnsNodeModel;
import cn.sunline.suncard.powerdesigner.models.DomainsNodeModel;
import cn.sunline.suncard.powerdesigner.models.TablesNodeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeViewComposite;
import cn.sunline.suncard.powerdesigner.ui.dialog.DefualtColumnDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.ImportDefaultColumnDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.IndexManagerDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.SQLManagerDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.StoredProcedureManagerDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.TableDataDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.modelattri.PackageModelDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.modelattri.PhysicalDataDialog;
import cn.sunline.suncard.powerdesigner.ui.dialog.modelattri.PhysicalDiagramDialog;
import cn.sunline.suncard.powerdesigner.xml.SwitchObjAndXml;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 数据库树的Action
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-9-5
 * @see
 * @since 1.0
 */
public class DatabaseAction extends Action {
	private TreeViewer treeViewer; // 树视图
	private String actionFlag; // Action标记
	

	private static Log logger = LogManager.getLogger(DatabaseAction.class);

	public DatabaseAction(String actionFlag, TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
		this.actionFlag = actionFlag;

		if (DmConstants.ADD_PHYSICAL_DATA_FLAG.equals(actionFlag)) {
			setText("新增物理数据模型");
		} else if (DmConstants.ADD_PHYSICAL_DIAGRAM_FLAG.equals(actionFlag)) {
			setText("新增表空间");
		} else if (DmConstants.OPEN_PHYSICAL_DIAGRAM_FLAG.equals(actionFlag)) {
			setText("打开");
		} else if (DmConstants.ATTRI_PHYSICAL_DATA_FLAG.equals(actionFlag)) {
			setText("属性");
		} else if (DmConstants.ATTRI_PHYSICAL_DIAGRAM_FLAG.equals(actionFlag)) {
			setText("属性");
		} else if (DmConstants.DEL_PHYSICAL_DATA_FLAG.equals(actionFlag)) {
			setText("删除");
		} else if (DmConstants.DEL_PHYSICAL_DIAGRAM_FLAG.equals(actionFlag)) {
			setText("删除");
		} else if (DmConstants.FIND_IN_DIAGRAM.equals(actionFlag)) {
			setText("在图中查找");
		} else if (DmConstants.DEL_TABLE_MODEL.equals(actionFlag)) {
			setText("删除");
		} else if (DmConstants.ATTRI_TABLE_MODEL.equals(actionFlag)) {
			setText("属性");
		} else if (DmConstants.CLOSE_FILE_MODEL.equals(actionFlag)) {
			setText("关闭文件");
		} else if (DmConstants.NEW_FILE_MODEL.equals(actionFlag)) {
			setText("新建文件");
		} else if (DmConstants.OPEN_FILE_MODEL.equals(actionFlag)) {
			setText("打开文件");
		} else if (DmConstants.DEFAULT_COLUMN_FLAG.equals(actionFlag)) {
			setText("默认字段设置");
		} else if (DmConstants.EDIT_SQL_FLAG.equals(actionFlag)) {
			setText("编辑SQL脚本");
		} else if (DmConstants.EDIT_STOREDPROCEDURE_FLAG.equals(actionFlag)) {
			setText("编辑存储过程");
		} else if (DmConstants.MODIFY_COLUMN_MODEL.equals(actionFlag)) {
			setText("属性");
		} else if (DmConstants.DEL_COLUMN_MODEL.equals(actionFlag)) {
			setText("删除");
		} else if (DmConstants.ADD_DOMAINS_COLUMN_MODEL.equals(actionFlag)) {
			setText("新增");
		} else if (DmConstants.INIT_TABLE_DATA.equals(actionFlag)) {
			setText("初始化数据");
		} else if (DmConstants.IMPORT_PDM_FLAG.equals(actionFlag)) {
			setText("导入PDM文件");
		} else if (DmConstants.DEL_PACKAGE_MODEL.equals(actionFlag)) {
			setText("删除");
		} else if (DmConstants.ADD_PACKAGE_MODEL.equals(actionFlag)) {
			setText("新增包模型");
		} else if (DmConstants.ATTRI_PACKAGE_MODEL.equals(actionFlag)) {
			setText("属性");
		} else if (DmConstants.TABLE_INDEX_FLAG.equals(actionFlag)) {
			setText("编辑表格索引");
		} else if (DmConstants.IMPORT_DEFAULT_COLUMN_FLAG.equals(actionFlag)) {
			setText("导入默认列");
		} else if (DmConstants.IMPORT_DOMAINS_FLAG.equals(actionFlag)) {
			setText("导入Domains");
		} else if (DmConstants.PASTE_TABLE_FLAG.equals(actionFlag)) {
			setText("粘贴表格");
		}
	}

	@Override
	public void run() {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();

		if (window == null || window.getActivePage() == null) {
			logger.error("找不到活跃的WorkbenchWindow或WorkbenchPage，Action执行失败！");
			return;
		}

		if (DmConstants.ADD_PHYSICAL_DATA_FLAG.equals(actionFlag)) {
			addPhysicalData(window);

		} else if (DmConstants.ADD_PHYSICAL_DIAGRAM_FLAG.equals(actionFlag)) {
			addPhysicalDiagram(window);

		} else if (DmConstants.OPEN_PHYSICAL_DIAGRAM_FLAG.equals(actionFlag)) {
			openPhysicalDiagram(window);

		} else if (DmConstants.ATTRI_PHYSICAL_DATA_FLAG.equals(actionFlag)) {
			modifyPhysicalData(window);

		} else if (DmConstants.ATTRI_PHYSICAL_DIAGRAM_FLAG.equals(actionFlag)) {
			modifyPhysicalDiagram(window);

		} else if (DmConstants.DEL_PHYSICAL_DATA_FLAG.equals(actionFlag)) {
			delPhysicalData(window);

		} else if (DmConstants.DEL_PHYSICAL_DIAGRAM_FLAG.equals(actionFlag)) {
			delPhysicalDiagram(window);

		} else if (DmConstants.FIND_IN_DIAGRAM.equals(actionFlag)) {
			findInDiagram(window);

		} else if (DmConstants.DEL_TABLE_MODEL.equals(actionFlag)) {
			delTableModel(window);

		} else if (DmConstants.ATTRI_TABLE_MODEL.equals(actionFlag)) {
			modifyTableModel(window);

		} else if (DmConstants.CLOSE_FILE_MODEL.equals(actionFlag)) {
			closeFileModel(window);

		} else if (DmConstants.NEW_FILE_MODEL.equals(actionFlag)) {
			newFileModel(window);

		} else if (DmConstants.OPEN_FILE_MODEL.equals(actionFlag)) {
			openFileModel(window);

		} else if (DmConstants.DEFAULT_COLUMN_FLAG.equals(actionFlag)) {
			defaultColumnSet(window);

		} else if (DmConstants.EDIT_SQL_FLAG.equals(actionFlag)) {
			editSqlScript(window);

		} else if (DmConstants.EDIT_STOREDPROCEDURE_FLAG.equals(actionFlag)) {
			editStoredProcedure(window);

		} else if (DmConstants.DEL_COLUMN_MODEL.equals(actionFlag)) {
			delDomainColumnModel(window);

		} else if (DmConstants.MODIFY_COLUMN_MODEL.equals(actionFlag)) {
			attriDomainColumnModel(window);

		} else if (DmConstants.ADD_DOMAINS_COLUMN_MODEL.equals(actionFlag)) {
			addDomainColumnModel(window);

		} else if (DmConstants.INIT_TABLE_DATA.equals(actionFlag)) {
			initTableData(window);

		} else if (DmConstants.IMPORT_PDM_FLAG.equals(actionFlag)) {
			importPdmFile(window);

		} else if (DmConstants.DEL_PACKAGE_MODEL.equals(actionFlag)) {
			delPackageModel(window);

		} else if (DmConstants.ADD_PACKAGE_MODEL.equals(actionFlag)) {
			addPackageModel(window);

		} else if (DmConstants.ATTRI_PACKAGE_MODEL.equals(actionFlag)) {
			attriPackageModel(window);
			
		} else if (DmConstants.TABLE_INDEX_FLAG.equals(actionFlag)) {
			editTableIndex(window);
			
		} else if (DmConstants.IMPORT_DEFAULT_COLUMN_FLAG.equals(actionFlag)) {
			importDefaultColumn(window, true);
			
		} else if (DmConstants.IMPORT_DOMAINS_FLAG.equals(actionFlag)) {
			importDefaultColumn(window, false);
			
		} else if (DmConstants.PASTE_TABLE_FLAG.equals(actionFlag)) {
			pasteTable(window, false);
		}

	}

	/**
	 * 给物理图形下面添加表格
	 * @param window
	 * @param b
	 */
	private void pasteTable(IWorkbenchWindow window, boolean b) {
		TreeContent treeContent = getSelect();
		if(treeContent != null ) {
			if(treeContent.getObj() instanceof TablesNodeModel) {
				treeContent = treeContent.getParent();
			}
			
			Object clipObject = Clipboard.getDefault().getContents();
			
			if(clipObject instanceof List<?>) {
				if(((List<Object>)clipObject).size() > 0) {
					Object firstObject = ((List<Object>)clipObject).get(0);
					if(firstObject instanceof AbstractGefModel) {
						List<TableModel> gefModelList = new ArrayList<TableModel>();
						for(Object obj : (List<Object>)clipObject) {
							if(obj instanceof TableGefModel) {
								gefModelList.add(((TableGefModel) obj).getDataObject());
							}
						}
						
						PasteModelCommand command = new PasteModelCommand();
						command.setPoint(new Point(100, 100));
						command.setDragTableModelList(gefModelList);
					}
				}
			}
			
			
		}
	}

	/**
	 * 给一个物理数据模型下面的表格引入默认列字段
	 * @param window
	 */
	private void importDefaultColumn(IWorkbenchWindow window, boolean isImprortDefaultColumn) {
		TreeContent treeContent = getSelect();
		if (treeContent == null || !(treeContent.getObj() instanceof PhysicalDataModel)) {
			return;
		}
		
		ImportDefaultColumnDialog dialog = new ImportDefaultColumnDialog(window.getShell());
		dialog.setPhysicalDataModel((PhysicalDataModel) treeContent.getObj());
		dialog.setImportDefaultColumn(isImprortDefaultColumn);
		dialog.open();
		
	}

	/**
	 * 编辑表格索引
	 * @param window
	 */
	private void editTableIndex(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null) {
			return;
		}
		
		IndexManagerDialog indexManagerDialog = new IndexManagerDialog(window.getShell(), (TableModel) treeContent.getObj());
		if(IDialogConstants.OK_ID == indexManagerDialog.open()) {
			Command command = indexManagerDialog.getCommand();
			CommandStack commandStack = DefaultViewPart
					.getFileCommandFromObj(FileModel.getFileModelFromObj(treeContent.getObj()));
			if (commandStack != null) {
				commandStack.execute(command);
				DefaultViewPart.refreshFileModelTreeContent();
			}
		}
		
		
	}

	/**
	 * 查看一个包模型的属性
	 * 
	 * @param window
	 */
	private void attriPackageModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null || treeContent.getObj() == null
				|| !(treeContent.getObj() instanceof PackageModel)) {
			return;
		}

		PackageModelDialog dialog = new PackageModelDialog(window.getShell());
		dialog.setPackageModelTreeContent(treeContent);
		dialog.setFlag(DmConstants.COMMAND_MODIFY);
		dialog.open();
	}

	/**
	 * 添加一个包模型
	 * 
	 * @param window
	 */
	private void addPackageModel(IWorkbenchWindow window) {
		PackageModelDialog dialog = new PackageModelDialog(window.getShell());
		dialog.setFlag(DmConstants.COMMAND_ADD);
		dialog.setPhysicalDataTreeContent(getSelect());
		dialog.open();

	}

	/**
	 * 删除一个包模型
	 * 
	 * @param window
	 */
	private void delPackageModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null
				|| !(treeContent.getObj() instanceof PackageModel)) {
			return;
		}

		PackageModel model = (PackageModel) treeContent.getObj();
		if (MessageDialog.openConfirm(window.getShell(),
				I18nUtil.getMessage("MESSAGE"), "是否删除此包模型： " + model.getName()
						+ " ？")) {

			UpdatePackageModelCommand command = new UpdatePackageModelCommand();
			command.setFlag(DmConstants.COMMAND_DEL);
			command.setPackageModelTreeContent(treeContent);

			CommandStack commandStack = DefaultViewPart
					.getFileCommandFromObj(FileModel.getFileModelFromObj(model));
			if (commandStack != null) {
				commandStack.execute(command);
			}

			// 删除DatabaseTreeViewPart中Map中对应的物理图形模型的CommandStack
			Set<PhysicalDiagramModel> physicalDiagramModelSet = model
					.getPhysicalDiagramModelSet();
			for (PhysicalDiagramModel physicalDiagramModel : physicalDiagramModelSet) {
				DatabaseTreeViewPart
						.removeEditorCommandStack(physicalDiagramModel);
			}
		}
	}

	/**
	 * 导入一个PDM文件
	 * 
	 * @param window
	 */
	private void importPdmFile(IWorkbenchWindow window) {
		try {
			new ImportHandler().execute(null);
		} catch (ExecutionException e) {
			logger.error("导入PDM文件失败！");
			e.printStackTrace();
		}
	}

	/**
	 * 初始化表格数据
	 * 
	 * @param window
	 */
	private void initTableData(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null || treeContent.getObj() == null
				|| !(treeContent.getObj() instanceof TableModel)) {
			return;
		}

		TableModel tableModel = (TableModel) treeContent.getObj();

		// 如果表格不存在列,则提示不能插入数据,直接返回
		if (tableModel.getColumnList() == null
				|| tableModel.getColumnList().isEmpty()) {
			MessageDialog.openWarning(
					window.getShell(),
					I18nUtil.getMessage("MESSAGE"),
					tableModel.getTableName()
							+ " - "
							+ I18nUtil
									.getMessage("ERROR_TABLE_COLUMN_IS_EMPTY"));
			return;
		}

		TableDataDialog tableDataDialog;
		try {
			tableDataDialog = new TableDataDialog(window.getShell(), tableModel
					.getInitTableDataModel().clone());

			int returnCode = tableDataDialog.open();

			if (IDialogConstants.OK_ID == returnCode) {
				CommandStack commandStack = DefaultViewPart
						.getFileCommandFromObj(FileModel
								.getFileModelFromObj(tableModel));
				if (commandStack != null) {
					commandStack.execute(tableDataDialog.getCommand());
					// 刷新树上的文件节点
					DefaultViewPart.refreshFileModelTreeContent();
				}

			}
		} catch (CloneNotSupportedException e) {
			logger.error("克隆InitTableDataModel失败，无法编辑表格初始化数据！" + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * 添加一个公共列对象
	 * 
	 * @param window
	 */
	private void addDomainColumnModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null
				|| (!(treeContent.getObj() instanceof DomainsNodeModel) && !(treeContent
						.getObj() instanceof ColumnModel))) {
			return;
		}

		if (treeContent.getObj() instanceof ColumnModel) {
			treeContent = treeContent.getParent();
		}

		DomainsNodeModel domainsNodeModel = (DomainsNodeModel) treeContent
				.getObj();
		List<DataTypeModel> dataTypeList = new ArrayList<DataTypeModel>();

		dataTypeList.addAll(DatabaseManager.getDataTypeList(domainsNodeModel
				.getPhysicalDataModel().getDatabaseTypeModel()));

		// 初始化信息，以便能从该ColumnModel向上遍历到FileModel
		PackageModel packageModel = new PackageModel();
		packageModel.setPhysicalDataModel(domainsNodeModel
				.getPhysicalDataModel());
		PhysicalDiagramModel physicalDiagramModel = new PhysicalDiagramModel();
		physicalDiagramModel.setPackageModel(packageModel);
		TableModel tableModel = new TableModel();
		tableModel.setPhysicalDiagramModel(physicalDiagramModel);
		ColumnModel columnModel = new ColumnModel();
		columnModel.setTableModel(tableModel);
		// 标记为公共列对象
		columnModel.setDomainColumnModel(true);

		ColumnPropertiesDialog dialog = new ColumnPropertiesDialog(
				window.getShell());
		dialog.setColumnModel(columnModel);
		dialog.setFlag(DmConstants.COMMAND_ADD);
		if (IDialogConstants.OK_ID == dialog.open()) {
			UpdateCommonColumnModelCommand command = new UpdateCommonColumnModelCommand();
			command.setFlag(DmConstants.COMMAND_ADD);
			command.setDomainsTreeContent(treeContent);
			command.setColumnModel(columnModel);
			command.setDatabaseTreeViewPart((DatabaseTreeViewPart) window
					.getActivePage().findView(DatabaseTreeViewPart.ID));

			CommandStack commandStack = DefaultViewPart
					.getFileCommandFromObj(columnModel);
			if (commandStack != null) {
				commandStack.execute(command);
				// 刷新树上的文件节点
				DefaultViewPart.refreshFileModelTreeContent();
			}
		}

	}

	/**
	 * 修改一个公共列对象
	 * 
	 * @param window
	 */
	private void attriDomainColumnModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null
				|| !(treeContent.getObj() instanceof ColumnModel)) {
			return;
		}

		ColumnPropertiesDialog dialog = new ColumnPropertiesDialog(
				window.getShell());
		dialog.setColumnModel((ColumnModel) treeContent.getObj());
		dialog.setFlag(DmConstants.COMMAND_MODIFY);

		if (IDialogConstants.OK_ID == dialog.open()) {
			UpdateCommonColumnModelCommand command = new UpdateCommonColumnModelCommand();
			command.setFlag(DmConstants.COMMAND_MODIFY);
			command.setColumnModel((ColumnModel) treeContent.getObj());
			command.setDatabaseTreeViewPart((DatabaseTreeViewPart) window
					.getActivePage().findView(DatabaseTreeViewPart.ID));
			command.setDomainsTreeContent(treeContent.getParent());

			CommandStack commandStack = DefaultViewPart
					.getFileCommandFromObj(treeContent.getObj());
			if (commandStack != null) {
				commandStack.execute(command);
				// 刷新树上的文件节点
				DefaultViewPart.refreshFileModelTreeContent();
			}
		}

	}

	/**
	 * 删除一个公共列对象
	 * 
	 * @param window
	 */
	private void delDomainColumnModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null
				|| !(treeContent.getObj() instanceof ColumnModel)) {
			return;
		}

		if (!MessageDialog.openConfirm(window.getShell(),
				I18nUtil.getMessage("MESSAGE"), "是否确定删除该公共列对象？")) {
			return;
		}

		UpdateCommonColumnModelCommand command = new UpdateCommonColumnModelCommand();
		command.setFlag(DmConstants.COMMAND_DEL);
		command.setColumnModelTreeContent(treeContent);
		command.setDomainsTreeContent(treeContent.getParent());
		command.setDatabaseTreeViewPart((DatabaseTreeViewPart) window
				.getActivePage().findView(DatabaseTreeViewPart.ID));

		CommandStack commandStack = DefaultViewPart
				.getFileCommandFromObj(treeContent.getObj());
		if (commandStack != null) {
			commandStack.execute(command);
			// 刷新树上的文件节点
			DefaultViewPart.refreshFileModelTreeContent();
		}

	}

	/**
	 * 编辑存储过程
	 * 
	 * @param window
	 */
	private void editStoredProcedure(IWorkbenchWindow window) {
//		PhysicalDataModel physicalDataModel = new PhysicalDataModel();
//		physicalDataModel = (PhysicalDataModel) getSelect().getObj();
//		StoredProcedureManagerDialog dialog = new StoredProcedureManagerDialog(
//				window.getShell(), physicalDataModel);
//		int returnCode = dialog.open();
//
//		if (IDialogConstants.OK_ID == returnCode) {
//			CommandStack commandStack = DefaultViewPart
//					.getFileCommandFromObj(physicalDataModel);
//			if (commandStack != null) {
//				commandStack.execute(dialog.getCommand());
//				// 刷新树上的文件节点
//				DefaultViewPart.refreshFileModelTreeContent();
//			}
//		}
	}

	/**
	 * 编辑SQL脚本
	 * 
	 * @param window
	 */
	private void editSqlScript(IWorkbenchWindow window) {
//		PhysicalDataModel physicalDataModel = new PhysicalDataModel();
//		physicalDataModel = (PhysicalDataModel) getSelect().getObj();
//		SQLManagerDialog sqlManagerDialog = new SQLManagerDialog(
//				window.getShell(), physicalDataModel);
//		int returnCode = sqlManagerDialog.open();
//
//		if (IDialogConstants.OK_ID == returnCode) {
//			CommandStack commandStack = DefaultViewPart
//					.getFileCommandFromObj(physicalDataModel);
//			if (commandStack != null) {
//				commandStack.execute(sqlManagerDialog.getCommand());
//				// 刷新树上的文件节点
//				DefaultViewPart.refreshFileModelTreeContent();
//			}
//		}
	}

	/**
	 * 默认字段设置
	 * 
	 * @param window
	 */
	private void defaultColumnSet(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null
				|| 	!(treeContent.getObj() instanceof DefaultColumnsNodeModel)) {
			return;
		}
		
		PhysicalDataModel physicalDataModel = ((DefaultColumnsNodeModel)treeContent
				.getObj()).getPhysicalDataModel();
		
		DefualtColumnDialog defualtColumnDialog = new DefualtColumnDialog(
				window.getShell(), physicalDataModel);
		DatabaseTreeViewPart databaseTreeViewPart = (DatabaseTreeViewPart) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(DatabaseTreeViewPart.ID);
		defualtColumnDialog.setDatabaseTreeViewPart(databaseTreeViewPart);
		defualtColumnDialog.setDefaultColumnsNodeTreeContent(treeContent);
		
		int returnCode = defualtColumnDialog.open();

		if (IDialogConstants.OK_ID == returnCode) {
			CommandStack commandStack = DefaultViewPart
					.getFileCommandFromObj(physicalDataModel);
			if (commandStack != null) {
				commandStack.execute(defualtColumnDialog.getCommand());
				// 刷新树上的文件节点
				DefaultViewPart.refreshFileModelTreeContent();
			}
		}
	}

	/**
	 * 打开一个FileModel
	 * 
	 * @param page
	 */
	private void openFileModel(IWorkbenchWindow window) {
		try {
			new OpenHandler().execute(null);
		} catch (ExecutionException e) {
			logger.error("打开文件出错:" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 新建一个FileModel
	 * 
	 * @param page
	 */
	private void newFileModel(IWorkbenchWindow window) {
		try {
			new NewHandler().execute(null);
		} catch (ExecutionException e) {
			logger.error("新建文件出错:" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 关闭一个FileModel
	 * 
	 * @param page
	 */
	private void closeFileModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null || !(treeContent.getObj() instanceof FileModel)) {
			return;
		}

		closeFileTreeContent(window, treeContent);
	}

	/**
	 * @param window
	 * @param treeContent
	 */
	public static void closeFileTreeContent(IWorkbenchWindow window,
			TreeContent treeContent) {
		FileModel fileModel = (FileModel) treeContent.getObj();
		IEditorReference[] editorReferences = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage()
				.getEditorReferences();

		boolean isDirty = false;

		// 先检查该文件是否变脏
		CommandStack commandStack = DefaultViewPart
				.getFileCommandFromObj(fileModel);
		if (commandStack != null && commandStack.isDirty()) {
			isDirty = true;
		} else {
			for (IEditorReference editorReference : editorReferences) {
				if (editorReference.getEditor(false) instanceof DatabaseDiagramEditor) {
					DatabaseDiagramEditor databaseDiagramEditor = (DatabaseDiagramEditor) editorReference
							.getEditor(false);
					if (fileModel.equals(FileModel
							.getFileModelFromObj(databaseDiagramEditor
									.getPhysicalDiagramModel()))) {
						if (databaseDiagramEditor.isDirty()) {
							isDirty = true;
							break;
						}
					}
				}
			}
		}

		if (isDirty) {
			if (MessageDialog.openConfirm(window.getShell(),
					I18nUtil.getMessage("MESSAGE"), "该文件已被修改，是否保存该变动？")) {
				DefaultViewPart.saveFileModel(fileModel);
			}
		}

		// 从数据库树中删除该FileModel
		DatabaseTreeViewPart databaseTreeViewPart = (DatabaseTreeViewPart) window
				.getActivePage().findView(DatabaseTreeViewPart.ID);

		ProductTreeViewPart productTreeViewPart = (ProductTreeViewPart) window
				.getActivePage().findView(ProductTreeViewPart.ID);
		try {
			databaseTreeViewPart.closeFileModel(treeContent);
			productTreeViewPart.closeFileModel(treeContent);

		} catch (CanNotFoundNodeIDException e) {
			logger.error("文件关闭失败！" + e.getMessage());
			MessageDialog.openError(window.getShell(),
					I18nUtil.getMessage("MESSAGE"), "文件关闭失败！" + e.getMessage());
			e.printStackTrace();
			return;
		} catch (FoundTreeNodeNotUniqueException e) {
			logger.error("文件关闭失败！" + e.getMessage());
			MessageDialog.openError(window.getShell(),
					I18nUtil.getMessage("MESSAGE"), "文件关闭失败！" + e.getMessage());
			e.printStackTrace();
			return;
		} catch (PartInitException e) {
			logger.error("关闭相关Editor失败！" + e.getMessage());
			MessageDialog.openError(window.getShell(),
					I18nUtil.getMessage("MESSAGE"),
					"关闭相关Editor失败！" + e.getMessage());
			e.printStackTrace();
			return ;
		}
		
		// 先把该文件模型对应的文件夹中的内容覆盖掉该文件模型所指向的压缩文件，成功后会把该文件夹从工作空间移除
		try {
			WorkSpaceManager.removeFromWorkSpace(fileModel);
		} catch (CanNotFoundFolderFromWorkSpaceException e1) {
			logger.error("关闭该文件模型失败:" + e1.getMessage());
			e1.printStackTrace();
			return ;
		} catch (IOException e1) {
			logger.error("关闭该文件模型失败:" + e1.getMessage());
			e1.printStackTrace();
			return ;
		} catch (Throwable e1) {
			logger.error("关闭该文件模型失败:" + e1.getMessage());
			e1.printStackTrace();
			return ;
		}
		
	}
	
	/**
	 * 修改表格模型
	 * @param page
	 */
	private void modifyTableModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null || treeContent.getObj() == null) {
			return;
		}

		if(treeContent.getObj() instanceof TableModel) {
			TableModel tableModel = (TableModel) treeContent.getObj();
			TableGefModelDialog dialog = new TableGefModelDialog(window.getShell());
			dialog.setTableModel(tableModel);
			if (IDialogConstants.OK_ID == dialog.open()) {
				CommandStack commandStack = DatabaseTreeViewPart
						.getEditorCommandStack(tableModel.getPhysicalDiagramModel());
				if (commandStack != null) {
					commandStack.execute(dialog.getCommand());
				} else {
					commandStack = DefaultViewPart.getFileCommandFromObj(FileModel
							.getFileModelFromObj(tableModel));
					if (commandStack != null) {
						commandStack.execute(dialog.getCommand());
						// 刷新树上的文件节点
						DefaultViewPart.refreshFileModelTreeContent();
					}
				}
			}
		} else if(treeContent.getObj() instanceof TableShortcutModel) {
			TableShortcutModel tableShortcutModel = (TableShortcutModel) treeContent.getObj();
			TableGefModelDialog dialog = new TableGefModelDialog(window.getShell());
			dialog.setTableModel(tableShortcutModel.getTargetTableModel());
			if (IDialogConstants.OK_ID == dialog.open()) {
				CommandStack commandStack = DatabaseTreeViewPart
						.getEditorCommandStack(tableShortcutModel.getPhysicalDiagramModel());
				if (commandStack != null) {
					commandStack.execute(dialog.getCommand());
				} else {
					commandStack = DefaultViewPart.getFileCommandFromObj(FileModel
							.getFileModelFromObj(tableShortcutModel));
					if (commandStack != null) {
						commandStack.execute(dialog.getCommand());
						// 刷新树上的文件节点
						DefaultViewPart.refreshFileModelTreeContent();
					}
				}
			}
		}
	}

	/**
	 * 删除该表格模型
	 * 
	 * @param page
	 */
	private void delTableModel(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null) {
			return;
		}

		// 如果是删除一个表格模型
		if (treeContent.getObj() instanceof TableModel) {
			TableModel tableModel = (TableModel) treeContent.getObj();

			DelTableModelCommand deleteCommand = new DelTableModelCommand();
			deleteCommand.setTableTreeContent(treeContent);

			CommandStack commandStack = DatabaseTreeViewPart
					.getEditorCommandStack(tableModel.getPhysicalDiagramModel());
			if (commandStack != null) {
				commandStack.execute(deleteCommand);
			} else {

				commandStack = DatabaseTreeViewPart
						.getFileCommandFromObj(FileModel
								.getFileModelFromObj(tableModel));
				if (commandStack != null) {
					commandStack.execute(deleteCommand);
				}
			}

		} else if (treeContent.getObj() instanceof TableShortcutModel) {
			TableShortcutModel tableShortcutModel = (TableShortcutModel) treeContent.getObj();

			DelTableShortcutModelCommand deleteCommand = new DelTableShortcutModelCommand();
			deleteCommand.setTableShortcutModel(tableShortcutModel);

			CommandStack commandStack = DatabaseTreeViewPart
					.getEditorCommandStack(tableShortcutModel.getPhysicalDiagramModel());
			if (commandStack != null) {
				commandStack.execute(deleteCommand);
			} else {

				commandStack = DatabaseTreeViewPart
						.getFileCommandFromObj(FileModel
								.getFileModelFromObj(tableShortcutModel));
				if (commandStack != null) {
					commandStack.execute(deleteCommand);
				}
			}
		}
	}

	/**
	 * 在图中找到该表格模型
	 * 
	 * @param page
	 */
	private void findInDiagram(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null) {
			return;
		}

		DatabaseTreeViewPart databaseTreeViewPart = (DatabaseTreeViewPart) window
				.getActivePage().findView(DatabaseTreeViewPart.ID);
		if (databaseTreeViewPart != null) {
			findInDiagram(
					treeContent.getObj(),
					databaseTreeViewPart
							.getPhysicalDiagramTreeContentFromTableModelOrTableShortcutModel(treeContent
									.getObj()));
		} else {
			logger.warn("找不到DatabaseTreeViewPart树，在图中找到该表格模型失败！");
		}

	}

	/**
	 * 通过表格模型来找到在Editor中对应的图形
	 * 
	 * @param findModel
	 *            需要找寻的模型，一般为表格模型或者表格快捷方式模型
	 */
	public static void findInDiagram(Object findModel,
			TreeContent physicalDiagramTreeContent) {
		if (findModel == null
				|| physicalDiagramTreeContent == null
				|| !(physicalDiagramTreeContent.getObj() instanceof PhysicalDiagramModel)) {
			logger.warn("传入的数据不完整，通过表格模型来找到在Editor中对应的图形失败！");
			return;
		}

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();

		if (window == null || window.getActivePage() == null) {
			logger.error("找不到活跃的WorkbenchWindow或WorkbenchPage，Action执行失败！");
			return;
		}

		PhysicalDiagramModel physicalDiagramModel = (PhysicalDiagramModel) physicalDiagramTreeContent
				.getObj();
		boolean flag = false; // 是否已经实现的标记

		// 如果用户已经打开了对应表格的Editor，则先找到该Editor
		IEditorReference[] editorReferences = window.getActivePage()
				.getEditorReferences();
		for (IEditorReference editorReference : editorReferences) {
			IEditorPart editorPart = editorReference.getEditor(false);
			if (editorPart instanceof DatabaseDiagramEditor) {
				DatabaseDiagramEditor databaseDiagramEditor = (DatabaseDiagramEditor) editorPart;

				// 首先找到该表格对应的Editor
				if (physicalDiagramModel.equals(databaseDiagramEditor
						.getPhysicalDiagramModel())) {
					DatabaseDiagramGefModel databaseDiagramGefModel = databaseDiagramEditor
							.getDatabaseDiagramGefModel();
					List<AbstractGefModel> getModelList = databaseDiagramGefModel
							.getChildren();
					for (AbstractGefModel abstractGefModel : getModelList) {
						if (abstractGefModel instanceof TableGefModel) {
							((TableGefModel) abstractGefModel)
									.setSelected(false);
							if (findModel.equals(abstractGefModel
									.getDataObject())) {
								if (abstractGefModel instanceof TableGefModel) {
									((TableGefModel) abstractGefModel)
											.setSelected(true);
									flag = true;
								}
							}
						} else if (abstractGefModel instanceof TableShortcutGefModel) {
							((TableShortcutGefModel) abstractGefModel)
									.setSelected(false);
							if (findModel.equals(abstractGefModel
									.getDataObject())) {
								if (abstractGefModel instanceof TableShortcutGefModel) {
									((TableShortcutGefModel) abstractGefModel)
											.setSelected(true);
									flag = true;
								}
							}
						}
					}
				}
			}

			if (flag) {
				break;
			}
		}

		// 说明用户还没有打开其对应的Editor
		if (!flag) {
			if (findModel instanceof TableModel) {
				if (findModel.equals(physicalDiagramModel.getTableMap().get(
						((TableModel) findModel).getTableName()))) {
					DatabaseDiagramEditor editor = openPhysicalDiagram(physicalDiagramTreeContent);
					if (editor != null) {
						DatabaseDiagramGefModel databaseDiagramGefModel = editor
								.getDatabaseDiagramGefModel();
						List<AbstractGefModel> getModelList = databaseDiagramGefModel
								.getChildren();

						for (AbstractGefModel abstractGefModel : getModelList) {
							((TableGefModel) abstractGefModel)
									.setSelected(false);
							if (findModel.equals(abstractGefModel
									.getDataObject())) {
								((TableGefModel) abstractGefModel)
										.setSelected(true);
								flag = true;
								break;
							}
						}
					}

				}
			} else if (findModel instanceof TableShortcutModel) {
				if (findModel.equals(physicalDiagramModel.getTableShortcutMap().get(
						((TableShortcutModel) findModel).getId()))) {
					DatabaseDiagramEditor editor = openPhysicalDiagram(physicalDiagramTreeContent);
					if (editor != null) {
						DatabaseDiagramGefModel databaseDiagramGefModel = editor
								.getDatabaseDiagramGefModel();
						List<AbstractGefModel> getModelList = databaseDiagramGefModel
								.getChildren();

						for (AbstractGefModel abstractGefModel : getModelList) {
							((TableShortcutGefModel) abstractGefModel)
									.setSelected(false);
							if (findModel.equals(abstractGefModel
									.getDataObject())) {
								((TableShortcutGefModel) abstractGefModel)
										.setSelected(true);
								flag = true;
								break;
							}
						}
					}

				}
			}
		}
	}

	/**
	 * 删除一个物理图形模型
	 * 
	 * @param page
	 */
	private void delPhysicalDiagram(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null || treeContent.getObj() == null
				|| !(treeContent.getObj() instanceof PhysicalDiagramModel)) {
			return;
		}

		PhysicalDiagramModel model = (PhysicalDiagramModel) treeContent
				.getObj();
		if (MessageDialog.openConfirm(window.getShell(),
				I18nUtil.getMessage("MESSAGE"),
				"是否删除此物理图形模型： " + model.getName() + " ？")) {

			UpdatePhysicalDiagramCommand command = new UpdatePhysicalDiagramCommand();
			command.setFlag(DmConstants.COMMAND_DEL);
			command.setPhysicalDiagramTreeContent(treeContent);

			CommandStack commandStack = DefaultViewPart
					.getFileCommandFromObj(FileModel.getFileModelFromObj(model));
			if (commandStack != null) {
				commandStack.execute(command);
			}

			DatabaseTreeViewPart.removeEditorCommandStack(model);
		}
	}

	/**
	 * 打开物理图形模型属性对话框
	 * 
	 * @param page
	 */
	private void modifyPhysicalDiagram(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null
				|| !(treeContent.getObj() instanceof PhysicalDiagramModel)) {
			return;
		}

		PhysicalDiagramDialog dialog = new PhysicalDiagramDialog(
				window.getShell());
		dialog.setPhysicalDiagramTreeContent(getSelect());
		dialog.setFlag(DmConstants.COMMAND_MODIFY);
		dialog.open();

	}

	/**
	 * 删除一个物理数据模型
	 * 
	 * @param page
	 */
	private void delPhysicalData(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null
				|| !(treeContent.getObj() instanceof PhysicalDataModel)) {
			return;
		}

		PhysicalDataModel model = (PhysicalDataModel) treeContent.getObj();
		if (MessageDialog.openConfirm(window.getShell(),
				I18nUtil.getMessage("MESSAGE"),
				"是否删除此物理数据模型： " + model.getName() + " ？")) {

			UpdatePhysicalDataCommand command = new UpdatePhysicalDataCommand();
			command.setFlag(DmConstants.COMMAND_DEL);
			command.setPhysicalDataModelTreeContent(treeContent);

			CommandStack commandStack = DefaultViewPart
					.getFileCommandFromObj(FileModel.getFileModelFromObj(model));
			if (commandStack != null) {
				commandStack.execute(command);
			}

			// 删除DatabaseTreeViewPart中Map中对应的物理图形模型的CommandStack
			Set<PhysicalDiagramModel> physicalDiagramModelSet = model
					.getAllPhysicalDiagramModels();
			for (PhysicalDiagramModel physicalDiagramModel : physicalDiagramModelSet) {
				DatabaseTreeViewPart
						.removeEditorCommandStack(physicalDiagramModel);
			}
		}

	}

	/**
	 * 打开物理数据模型属性对话框
	 * 
	 * @param page
	 */
	private void modifyPhysicalData(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null || treeContent.getObj() == null
				|| !(treeContent.getObj() instanceof PhysicalDataModel)) {
			return;
		}

		PhysicalDataDialog dialog = new PhysicalDataDialog(window.getShell());
		dialog.setFlag(DmConstants.COMMAND_MODIFY);
		dialog.setPhysicalDataModelContent(treeContent);
		dialog.open();

	}

	/**
	 * 打开物理图形模型的Editor
	 * 
	 * @param page
	 */
	private void openPhysicalDiagram(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent == null
				|| !(treeContent.getObj() instanceof PhysicalDiagramModel)) {
			return;
		}

		openPhysicalDiagram(treeContent);
	}

	private static DatabaseDiagramEditor openPhysicalDiagram(
			TreeContent physicalDiagramTreeModel) {
		if (!(physicalDiagramTreeModel.getObj() instanceof PhysicalDiagramModel)) {
			logger.error("传入的PhysicalDiagramModel为空，无法打开对应的Editor!");
			return null;
		}

		DatabaseDiagramEditor databaseDiagramEditor = null;
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();

		DatabaseDiagramEditorInput input = new DatabaseDiagramEditorInput();
		input.setEditorName(((PhysicalDiagramModel) physicalDiagramTreeModel
				.getObj()).getName());
		input.setPhysicalDiagramTreeContent(physicalDiagramTreeModel);

		try {
			databaseDiagramEditor = (DatabaseDiagramEditor) window
					.getActivePage()
					.openEditor(input, DatabaseDiagramEditor.ID);
		} catch (PartInitException e) {
			// 写日志
			logger.error("打开物理图形视图失败!");
			MessageDialog.openError(window.getShell(),
					I18nUtil.getMessage("MESSAGE"), "打开物理图形视图失败!");
			e.printStackTrace();
		}

		return databaseDiagramEditor;
	}

	/**
	 * 添加物理图形模型
	 * 
	 * @param page
	 */
	private void addPhysicalDiagram(IWorkbenchWindow window) {
		TreeContent treeContent = getSelect();
		if (treeContent != null) {
			PhysicalDiagramDialog dialog = new PhysicalDiagramDialog(
					window.getShell());
			Object obj = treeContent.getObj();
			if (obj instanceof PackageModel) {
				dialog.setPackageModelTreeContent(getSelect());
				dialog.setFlag(DmConstants.COMMAND_ADD);
				dialog.open();
			} else if (obj instanceof PhysicalDiagramModel) {
				dialog.setPackageModelTreeContent(getSelect().getParent());
				dialog.setFlag(DmConstants.COMMAND_ADD);
				dialog.open();
			}

		}

	}

	/**
	 * 通过TableModel来在可能打开的Editor中找寻对应的TableGefModel
	 * 
	 * @param tableModel
	 * @return
	 */
	public static TableGefModel getTableGefModel(TableModel tableModel) {
		IEditorReference[] editorReferences = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage()
				.getEditorReferences();

		for (IEditorReference editorReference : editorReferences) {
			if (editorReference.getEditor(false) instanceof DatabaseDiagramEditor) {
				DatabaseDiagramEditor databaseDiagramEditor = (DatabaseDiagramEditor) editorReference
						.getEditor(false);
				DatabaseDiagramGefModel databaseDiagramGefModel = databaseDiagramEditor
						.getDatabaseDiagramGefModel();
				List<AbstractGefModel> gefModelList = databaseDiagramGefModel
						.getChildren();
				for (AbstractGefModel abstractGefModel : gefModelList) {
					if (abstractGefModel instanceof TableGefModel) {
						TableGefModel tableGefModel = (TableGefModel) abstractGefModel;
						if (tableGefModel.getDataObject().equals(tableModel)) {
							return tableGefModel;
						}
					}
				}
			}
		}

		return null;

	}

	/**
	 * 通过TableShortcutModel来在可能打开的Editor中找寻对应的TableShortcutGefModel
	 * 
	 * @param tableShortcutModel
	 * @return
	 */
	public static TableShortcutGefModel getTableShortcutGefModel(
			TableShortcutModel tableShortcutModel) {
		IEditorReference[] editorReferences = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage()
				.getEditorReferences();

		for (IEditorReference editorReference : editorReferences) {
			if (editorReference.getEditor(false) instanceof DatabaseDiagramEditor) {
				DatabaseDiagramEditor databaseDiagramEditor = (DatabaseDiagramEditor) editorReference
						.getEditor(false);
				DatabaseDiagramGefModel databaseDiagramGefModel = databaseDiagramEditor
						.getDatabaseDiagramGefModel();
				List<AbstractGefModel> gefModelList = databaseDiagramGefModel
						.getChildren();
				for (AbstractGefModel abstractGefModel : gefModelList) {
					if (abstractGefModel instanceof TableShortcutGefModel) {
						TableShortcutGefModel tableShortcutGefModel = (TableShortcutGefModel) abstractGefModel;
						if (tableShortcutGefModel.getDataObject().equals(
								tableShortcutModel)) {
							return tableShortcutGefModel;
						}
					}
				}
			}
		}

		return null;

	}

	/**
	 * 添加物理数据模型
	 */
	private void addPhysicalData(IWorkbenchWindow window) {
		PhysicalDataDialog dialog = new PhysicalDataDialog(window.getShell());
		dialog.setFlag(DmConstants.COMMAND_ADD);
		dialog.setFileModelTreeContent(getSelect());
		dialog.open();

	}

	/**
	 * 获得所选择的树节点
	 * 
	 * @return
	 */
	public TreeContent getSelect() {
		StructuredSelection selection = (StructuredSelection) treeViewer
				.getSelection();
		TreeContent select = (TreeContent) selection.getFirstElement();

		return select;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		ImageDescriptor descriptor = null;

		if (actionFlag.equals(DmConstants.ADD_PHYSICAL_DATA_FLAG)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_NEW_IMAGE);

		} else if (actionFlag.equals(DmConstants.ADD_PHYSICAL_DIAGRAM_FLAG)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_NEW_IMAGE);

		} else if (actionFlag.equals(DmConstants.DEL_PHYSICAL_DATA_FLAG)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_DELETE_IMAGE);

		} else if (actionFlag.equals(DmConstants.DEL_PHYSICAL_DIAGRAM_FLAG)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_DELETE_IMAGE);

		} else if (actionFlag.equals(DmConstants.ATTRI_PHYSICAL_DATA_FLAG)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_MODIFY_IMAGE);

		} else if (actionFlag.equals(DmConstants.ATTRI_PHYSICAL_DIAGRAM_FLAG)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_MODIFY_IMAGE);

		} else if (actionFlag.equals(DmConstants.OPEN_PHYSICAL_DIAGRAM_FLAG)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_VIEW_IMAGE);

		} else if (actionFlag.equals(DmConstants.FIND_IN_DIAGRAM)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_FIND);

		} else if (actionFlag.equals(DmConstants.DEL_TABLE_MODEL)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_DELETE_IMAGE);

		} else if (actionFlag.equals(DmConstants.ATTRI_TABLE_MODEL)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_MODIFY_IMAGE);

		} else if (actionFlag.equals(DmConstants.CLOSE_FILE_MODEL)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_CLOSE);

		} else if (actionFlag.equals(DmConstants.NEW_FILE_MODEL)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_NEW_FILE);

		} else if (actionFlag.equals(DmConstants.OPEN_FILE_MODEL)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_OPEN_FILE);

		} else if (actionFlag.equals(DmConstants.DEFAULT_COLUMN_FLAG)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_DEFAULT_COLUMN);

		} else if (actionFlag.equals(DmConstants.EDIT_SQL_FLAG)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_EDIT_SQL);

		} else if (actionFlag.equals(DmConstants.EDIT_STOREDPROCEDURE_FLAG)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_EDIT_STROEDPROCEDURE);

		} else if (actionFlag.equals(DmConstants.DEL_COLUMN_MODEL)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_DELETE_IMAGE);

		} else if (actionFlag.equals(DmConstants.MODIFY_COLUMN_MODEL)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_MODIFY_IMAGE);

		} else if (actionFlag.equals(DmConstants.ADD_DOMAINS_COLUMN_MODEL)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_NEW_IMAGE);

		} else if (actionFlag.equals(DmConstants.INIT_TABLE_DATA)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_INIT_DATA);

		} else if (actionFlag.equals(DmConstants.IMPORT_PDM_FLAG)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_IMPORT_IMAGE);

		} else if (actionFlag.equals(DmConstants.ATTRI_PACKAGE_MODEL)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_MODIFY_IMAGE);

		} else if (actionFlag.equals(DmConstants.ADD_PACKAGE_MODEL)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_NEW_IMAGE);

		} else if (actionFlag.equals(DmConstants.DEL_PACKAGE_MODEL)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_DELETE_IMAGE);

		} else if (actionFlag.equals(DmConstants.TABLE_INDEX_FLAG)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_INDEX_IMAGE);

		} else if (actionFlag.equals(DmConstants.IMPORT_DEFAULT_COLUMN_FLAG)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_IMPORT_IMAGE);

		} else if (actionFlag.equals(DmConstants.IMPORT_DOMAINS_FLAG)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.A_IMPORT_IMAGE);

		}  else if (actionFlag.equals(DmConstants.PASTE_TABLE_FLAG)) {
			descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					Activator.PLUGIN_ID, IDmImageKey.COLUMN_PASTE);

		}

		return descriptor;
	}

}

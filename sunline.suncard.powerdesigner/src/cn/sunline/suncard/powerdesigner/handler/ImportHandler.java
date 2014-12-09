package cn.sunline.suncard.powerdesigner.handler;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.dom4j.DocumentException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.manager.WorkSpaceManager;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.model.factory.PhysicalDiagramModelFactory;
import cn.sunline.suncard.powerdesigner.model.factory.TableModelFactory;
import cn.sunline.suncard.powerdesigner.model.factory.TableShortcutModelFactory;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeViewComposite;
import cn.sunline.suncard.powerdesigner.xml.PdmReader;
import cn.sunline.suncard.powerdesigner.xml.SwitchObjAndXml;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

public class ImportHandler extends AbstractHandler {
	private Log logger = LogManager.getLogger(ImportHandler.class.getName());

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window == null) {
			logger.error("找不到活跃的WorkbenchWindow，ImportHandler执行失败！");
			return null;
		}
		
		FileDialog fileDialog = new FileDialog(window.getShell(), SWT.OPEN);
		fileDialog.setFilterExtensions(new String[] { "*."
				+ SystemConstants.FILE_EXTEND_NAME_PDM });
		fileDialog.setText("导入PDM文件");

		String filePath = fileDialog.open();

		// 如果用户取消,则直接返回
		if (filePath == null) {
			logger.debug("user return! ");
			return null;
		}

		logger.debug("user select pdm file path : " + filePath);

		// Document document;
		PdmReader pdmReder = new PdmReader();
		try {
			pdmReder.loadPdm(filePath);
			PhysicalDataModel dataModel = pdmReder.getPhysicalDataModel();
			if (dataModel == null) {
				logger.error("无法从pdm文件中获取物理数据模型，导入PDM失败！");
				MessageDialog.openError(window.getShell(), I18nUtil
						.getMessage("MESSAGE"), I18nUtil
						.getMessage("FIND_NOT_PHYSICALDATAMODEL_FROM_PDM"));
				return null;
			}

			fileDialog = new FileDialog(window.getShell(), SWT.SAVE);
			fileDialog.setFilterExtensions(new String[] { "*."
					+ SystemConstants.ZIP_FILE_EXTEND_NAME });
			fileDialog.setText("新建数据库设计文件");

			filePath = fileDialog.open();

			if (filePath != null) {
				File file = new File(filePath);
				if (file.exists()) {
					if (!MessageDialog.openConfirm(window.getShell(),
							I18nUtil.getMessage("MESSAGE"),
							I18nUtil.getMessage("IS_COVER_FILE"))) {
						return null;
					}
				}

//				file.createNewFile();
				file = WorkSpaceManager.createNewFile(filePath);
				if(file == null) {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), I18nUtil.getMessage("MESSAGE"), "创建新文件失败！");
					return null;
				}
				
				FileModel fileModel;
				try {
					fileModel = WorkSpaceManager.addFileToWorkSpace(file);
				} catch (Exception e) {
					logger.error("将设计文件添加到工作空间失败！" + e.getMessage());
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), I18nUtil.getMessage("MESSAGE"), "将设计文件添加到工作空间失败！" + e.getMessage());
					e.printStackTrace();
					return null;
				}
				
				if(fileModel == null) {
					logger.error("解析到的FileModel为空，添加该文件到工作空间失败！");
					return null;
				}
				
//				FileModel fileModel = new FileModel();
				fileModel.getPhysicalDataSet().add(dataModel);
				dataModel.setFileModel(fileModel);
				
				// 组装TableModelFactory
				Set<TableModel> allTableModelSet = fileModel.getAllTableModel();
				for (TableModel tableModel : allTableModelSet) {
					TableModelFactory.addTableModel(fileModel, tableModel.getId(),
							tableModel);
				}
				
				// 组装TableShortcutModelFactory
				Set<TableShortcutModel> allTableShortcutModelSet = fileModel.getAllTableShortcutModel();
				for (TableShortcutModel tableShortcutModel : allTableShortcutModelSet) {
					TableShortcutModelFactory.addTableShortcutModel(fileModel, tableShortcutModel.getId(),
							tableShortcutModel);
				}
		
				// 组装ColumnModelFactory
				Set<ColumnModel> allColumnModelSet = fileModel.getAllColumnModel();
				for (ColumnModel columnModel : allColumnModelSet) {
					ColumnModelFactory.addColumnModel(fileModel, columnModel.getId(),
							columnModel);
				}
				
				// 组装PhysicalDiagramModelFactory
				Set<PhysicalDiagramModel> allPhysicalDiagramModelSet = fileModel.getAllPhysicalDiagramModel();
				for(PhysicalDiagramModel physicalDiagramModel : allPhysicalDiagramModelSet) {
					PhysicalDiagramModelFactory.addPhysicalDiagramModel(fileModel, physicalDiagramModel.getId(), physicalDiagramModel);
				}
				
				// 初始化文件模型的成员到各个工厂中
				SwitchObjAndXml.initFileModelInfo(fileModel);
				
				DatabaseTreeViewPart databaseViewPart = (DatabaseTreeViewPart) window.getActivePage().findView(DatabaseTreeViewPart.ID);
				if(databaseViewPart != null) {
					TreeViewComposite.setExpand(false);
					databaseViewPart.addFileModel(fileModel);
					TreeViewComposite.setExpand(true);
				} else {
					logger.error("找不到DatabaseTreeViewPart，添加FileModel失败！");
					MessageDialog.openError(window.getShell(), I18nUtil
							.getMessage("MESSAGE"), I18nUtil
							.getMessage("CAN_NOT_FIND_DATABASETREEVIEW"));
				}
				
				ProductTreeViewPart productViewPart = (ProductTreeViewPart) window.getActivePage().findView(ProductTreeViewPart.ID);
				if(productViewPart != null) {
					productViewPart.addFileModel(fileModel);
				} else {
					logger.error("找不到ProductTreeViewPart，添加FileModel失败！");
					MessageDialog.openError(window.getShell(), I18nUtil
							.getMessage("MESSAGE"), I18nUtil
							.getMessage("CAN_NOT_FIND_PRODUCTTREEVIEWPART"));
				}
				
				// 最后记得保存一下，这样才能覆盖文件
				DefaultViewPart.saveFileModel(fileModel);
			}
		} catch (DocumentException e) {
			logger.error("解析PDM文件失败！" + e.getMessage());
			MessageDialog.openError(window.getShell(), I18nUtil
					.getMessage("MESSAGE"), I18nUtil
					.getMessage("ANALYSIS_PDM_FAIL"));
			e.printStackTrace();
			
			return null;
		} catch (IOException e) {
			logger.error("新建文件失败！" + e.getMessage());
			MessageDialog.openError(window.getShell(), I18nUtil
					.getMessage("MESSAGE"), I18nUtil
					.getMessage("CREATE_FILE_FAIL"));
			e.printStackTrace();
			
			return null;
		} catch (CanNotFoundNodeIDException e) {
			MessageDialog.openError(window.getShell(), I18nUtil
					.getMessage("MESSAGE"), I18nUtil
					.getMessage("CAN_NOT_FIND_TREE_NODE_ID"));
			e.printStackTrace();
			
			return null;
		} catch (FoundTreeNodeNotUniqueException e) {
			MessageDialog.openError(window.getShell(), I18nUtil
					.getMessage("MESSAGE"), I18nUtil
					.getMessage("TREE_NODE_ID_NOT_UNIQUE"));
			e.printStackTrace();
			
			return null;
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}

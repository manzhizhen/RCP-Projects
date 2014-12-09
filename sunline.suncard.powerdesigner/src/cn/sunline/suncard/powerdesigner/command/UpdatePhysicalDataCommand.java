/* 文件名：     UpdatePhysicalDataCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-10-9
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.manager.ProductSpaceManager;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProductSpaceModel;
import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 增加、删除和修改物理数据模型的Command
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-10-9
 * @see
 * @since 1.0
 */
public class UpdatePhysicalDataCommand extends Command {
	private String flag;
	private TreeContent fileModelTreeContent;
	private TreeContent physicalDataModelTreeContent;
	private PhysicalDataModel physicalDataModel;
	
	private String oldName;
	private String newName;
	private String newNote;
	private String oldNote;
	private DatabaseTypeModel newDatabaseTypeModel;
	private DatabaseTypeModel oldDatabaseTypeModel;
	
	private Log logger = LogManager.getLogger(UpdatePhysicalDataCommand.class.getName());
	
	@Override
	public void execute() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if(page == null) {
			logger.error("找不到活跃的WorkbenchPage,无法对物理数据模型进行更新！");
			return ;
		}
		
		DatabaseTreeViewPart viewPart = (DatabaseTreeViewPart) page.findView(DatabaseTreeViewPart.ID);
		if(viewPart == null) {
			logger.error("无法找到活跃的DatabaseTreeViewPart，更新物理数据节点失败！");
			
			return ;
		}
		
		// 添加一个物理数据模型
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			// 向数据库数添加该物理数据节点
			try {
				physicalDataModelTreeContent = viewPart.addPhysicalDataModel(physicalDataModel, fileModelTreeContent);
			} catch (CanNotFoundNodeIDException e) {
				logger.error("向数据库树中添加物理数据节点失败！");
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
						I18nUtil.getMessage("MESSAGE"), 
						"向数据库树中添加物理数据节点失败！");
				
				e.printStackTrace();
				return ;
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.error("向数据库树中添加物理数据节点失败！");
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
						I18nUtil.getMessage("MESSAGE"), 
						"向数据库树中添加物理数据节点失败！");
				
				e.printStackTrace();
				return ;
			}
			
		// 删除一个物理数据模型
		} else if(DmConstants.COMMAND_DEL.equals(flag)) {
			try {
				// 从树上删除该节点
				viewPart.removePhysicalDataModel(physicalDataModelTreeContent);
				
				// 关闭相关打开的物理图形模型的Editor
				IEditorReference[] editors = page.getEditorReferences();
				for(IEditorReference editor : editors) {
					if(editor.getEditor(false) instanceof DatabaseDiagramEditor) {
						DatabaseDiagramEditor databaseDiagramEditor = (DatabaseDiagramEditor) editor.getEditor(false);
						if(physicalDataModelTreeContent.getObj().equals(databaseDiagramEditor.
								getPhysicalDiagramModel().getPackageModel())) {
							page.closeEditor(databaseDiagramEditor, false);
						}
					}
				}
				
				// 删除产品树中的相关产品
				ProductTreeViewPart productTreeViewPart = (ProductTreeViewPart) page
						.findView(ProductTreeViewPart.ID);
				PhysicalDataModel dataModel = (PhysicalDataModel) physicalDataModelTreeContent.getObj();
				List<ProductModel> productList = ProductSpaceManager.getProductModelList(dataModel.getFileModel());
				List<ProductModel> needDelProdcutList = new ArrayList<ProductModel>();
				if(productList != null) {
					for(ProductModel productModel : productList) {
						if(dataModel.equals(productModel.getPhysicalDataModel())) {
							needDelProdcutList.add(productModel);
						}
					}
					
					for(ProductModel productModel : needDelProdcutList) {
						productTreeViewPart.removeProductModel(productTreeViewPart
								.getTreeContentFromProductModel(productModel));
					}
				} else {
					logger.error("无法获得产品列表，删除产品树上相关产品失败！");
				}
				
				
			} catch (CanNotFoundNodeIDException e) {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
						I18nUtil.getMessage("MESSAGE"), 
						"删除物理数据节点失败！");
				e.printStackTrace();
			} catch (FoundTreeNodeNotUniqueException e) {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
						I18nUtil.getMessage("MESSAGE"), 
						"删除物理数据节点失败！");
				e.printStackTrace();
			}
			
			
			
			
		 // 修改一个物理数据模型
		} else if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			oldName = physicalDataModel.getName();
			oldNote = physicalDataModel.getNote();
			oldDatabaseTypeModel = physicalDataModel.getDatabaseTypeModel();
			
			physicalDataModel.setName(newName);
			physicalDataModel.setNote(newNote);
			physicalDataModel.setDatabaseTypeModel(newDatabaseTypeModel);
			viewPart.refreshTree();
		}
		super.execute();
	}
	
	@Override
	public boolean canUndo() {
		return false;
	}
	
	@Override
	public void undo() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if(page == null) {
			logger.error("找不到活跃的WorkbenchPage,UpdatePhysicalDataCommand的Undo失败！");
			return ;
		}
		
		DatabaseTreeViewPart viewPart = (DatabaseTreeViewPart) page.findView(DatabaseTreeViewPart.ID);
		if(viewPart == null) {
			logger.error("无法找到活跃的DatabaseTreeViewPart，UpdatePhysicalDataCommand的Undo失败！");
			
			return ;
		}
		
		// 添加一个物理数据模型的undo
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			// 向数据库数添加该物理数据节点
			if(physicalDataModelTreeContent != null) {
				try {
					viewPart.removePhysicalDataModel(physicalDataModelTreeContent);
				} catch (CanNotFoundNodeIDException e) {
					logger.error("删除物理数据节点失败！" + e.getMessage());
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
							I18nUtil.getMessage("MESSAGE"), 
							"删除物理数据节点失败！");
					
					e.printStackTrace();
					return ;
				} catch (FoundTreeNodeNotUniqueException e) {
					logger.error("删除物理数据节点失败！" + e.getMessage());
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
							I18nUtil.getMessage("MESSAGE"), 
							"删除物理数据节点失败！");
					
					e.printStackTrace();
					return ;
				}
			}
			
		// 删除一个物理数据模型的undo
		} else if(DmConstants.COMMAND_DEL.equals(flag)) {
			// 从树上添加该节点
			try {
				viewPart.addPhysicalDataModel((PhysicalDataModel) physicalDataModelTreeContent.getObj(), 
						physicalDataModelTreeContent.getParent());
			} catch (CanNotFoundNodeIDException e) {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
						I18nUtil.getMessage("MESSAGE"), 
						"添加物理数据节点失败！");
				e.printStackTrace();
			} catch (FoundTreeNodeNotUniqueException e) {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
						I18nUtil.getMessage("MESSAGE"), 
						"添加物理数据节点失败！");
				e.printStackTrace();
			}
				
			
		 // 修改一个物理数据模型
		} else if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			physicalDataModel.setName(oldName);
			physicalDataModel.setNote(oldNote);
			physicalDataModel.setDatabaseTypeModel(oldDatabaseTypeModel);
			viewPart.refreshTree();
		}
		super.undo();
	}
	
	
	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setFileModelTreeContent(TreeContent fileModelTreeContent) {
		this.fileModelTreeContent = fileModelTreeContent;
	}

	public void setPhysicalDataModel(PhysicalDataModel physicalDataModel) {
		this.physicalDataModel = physicalDataModel;
	}

	public void setPhysicalDataModelTreeContent(
			TreeContent physicalDataModelTreeContent) {
		this.physicalDataModelTreeContent = physicalDataModelTreeContent;
		this.physicalDataModel = (PhysicalDataModel) physicalDataModelTreeContent.getObj();
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}
	
	public void setNewNote(String newNote) {
		this.newNote = newNote;
	}

	public void setNewDatabaseTypeModel(DatabaseTypeModel newDatabaseTypeModel) {
		this.newDatabaseTypeModel = newDatabaseTypeModel;
	}
	
	
	
	

}

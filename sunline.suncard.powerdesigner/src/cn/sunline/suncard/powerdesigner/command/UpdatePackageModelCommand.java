/* 文件名：     UpdatePhysicalDataCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-10-9
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.util.ArrayList;
import java.util.Collection;
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
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProductSpaceModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
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
public class UpdatePackageModelCommand extends Command {
	private String flag;
	private TreeContent physicalDataModelTreeContent;
	private TreeContent packageModelTreeContent;
	private PackageModel packageModel;
	
	private String oldName;
	private String newName;
	private String newNote;
	private String oldNote;
	
	private Log logger = LogManager.getLogger(UpdatePackageModelCommand.class.getName());
	
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
			if(packageModel == null || physicalDataModelTreeContent == null || !(physicalDataModelTreeContent.getObj() instanceof PhysicalDataModel)) {
				logger.error("传入的数据不正确，无法进行包模型的添加！");
				return ;
			}
			
			// 向数据库数添加该包节点
			try {
				packageModelTreeContent = viewPart.addPackageModel(packageModel, physicalDataModelTreeContent);
			} catch (CanNotFoundNodeIDException e) {
				logger.error("向数据库树中添加包节点失败！");
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
						I18nUtil.getMessage("MESSAGE"), 
						"向数据库树中添加包节点失败！");
				
				e.printStackTrace();
				return ;
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.error("向数据库树中添加包节点失败！");
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
						I18nUtil.getMessage("MESSAGE"), 
						"向数据库树中添加包节点失败！");
				
				e.printStackTrace();
				return ;
			}
			
		// 删除一个包模型
		} else if(DmConstants.COMMAND_DEL.equals(flag)) {
			if(packageModelTreeContent == null || !(packageModelTreeContent.getObj() instanceof PackageModel)) {
				logger.error("传入的数据不正确，无法进行包模型的删除！");
				return ;
			}
			
			try {
				// 从树上删除该节点
				viewPart.removePackageModel(packageModelTreeContent);
				
				// 从产品树中删除相关表格
				ProductTreeViewPart productTreeViewPart = (ProductTreeViewPart) page
						.findView(ProductTreeViewPart.ID);
				Collection<TableModel> tableModels = packageModel.getAllTableModel();
				for(TableModel tableModel : tableModels) {
					List<TreeContent> tableTreeContents = productTreeViewPart
							.getTreeContentFromTableModel(tableModel);
					if(tableTreeContents != null) {
						for(TreeContent tableTreeContent : tableTreeContents) {
							productTreeViewPart.removeTableModel(tableTreeContent);
						}
					}
				}
				
				
			} catch (CanNotFoundNodeIDException e) {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
						I18nUtil.getMessage("MESSAGE"), 
						"删除包模型节点失败！");
				e.printStackTrace();
			} catch (FoundTreeNodeNotUniqueException e) {
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
						I18nUtil.getMessage("MESSAGE"), 
						"删除包模型节点失败！");
				e.printStackTrace();
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
		 // 修改一个物理数据模型
		} else if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			oldName = packageModel.getName();
			oldNote = packageModel.getNote();
			
			packageModel.setName(newName);
			packageModel.setNote(newNote);
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
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		if(page == null) {
//			logger.error("找不到活跃的WorkbenchPage，UpdatePackageModelCommand的Undo失败！");
//			return ;
//		}
//		
//		DatabaseTreeViewPart viewPart = (DatabaseTreeViewPart) page.findView(DatabaseTreeViewPart.ID);
//		if(viewPart == null) {
//			logger.error("无法找到活跃的DatabaseTreeViewPart，UpdatePackageModelCommand的Undo失败！");
//			
//			return ;
//		}
//		
//		// 添加一个物理数据模型的undo
//		if(DmConstants.COMMAND_ADD.equals(flag)) {
//			// 向数据库数添加该物理数据节点
//			if(physicalDataModelTreeContent != null) {
//				try {
//					viewPart.removePackageModel(packageModelTreeContent);
//				} catch (CanNotFoundNodeIDException e) {
//					logger.error("删除物理数据节点失败！" + e.getMessage());
//					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
//							I18nUtil.getMessage("MESSAGE"), 
//							"删除物理数据节点失败！");
//					
//					e.printStackTrace();
//					return ;
//				} catch (FoundTreeNodeNotUniqueException e) {
//					logger.error("删除物理数据节点失败！" + e.getMessage());
//					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
//							I18nUtil.getMessage("MESSAGE"), 
//							"删除物理数据节点失败！");
//					
//					e.printStackTrace();
//					return ;
//				} catch (PartInitException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			
//		// 删除一个物理数据模型的undo
//		} else if(DmConstants.COMMAND_DEL.equals(flag)) {
//			// 从树上添加该节点
//			try {
//				viewPart.addPhysicalDataModel((PhysicalDataModel) physicalDataModelTreeContent.getObj(), 
//						physicalDataModelTreeContent.getParent());
//			} catch (CanNotFoundNodeIDException e) {
//				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
//						I18nUtil.getMessage("MESSAGE"), 
//						"添加物理数据节点失败！");
//				e.printStackTrace();
//			} catch (FoundTreeNodeNotUniqueException e) {
//				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
//						I18nUtil.getMessage("MESSAGE"), 
//						"添加物理数据节点失败！");
//				e.printStackTrace();
//			}
//				
//			
//		 // 修改一个物理数据模型
//		} else if(DmConstants.COMMAND_MODIFY.equals(flag)) {
//				packageModel.setName(oldName);
//				packageModel.setNote(oldNote);
//				viewPart.refreshTree();
//		}
		super.undo();
	}
	
	
	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setPackageModelTreeContent(TreeContent packageModelTreeContent) {
		this.packageModelTreeContent = packageModelTreeContent;
		this.packageModel = (PackageModel) packageModelTreeContent.getObj();
	}

	public void setPackageModel(PackageModel packageModel) {
		this.packageModel = packageModel;
	}

	public void setPhysicalDataModelTreeContent(
			TreeContent physicalDataModelTreeContent) {
		this.physicalDataModelTreeContent = physicalDataModelTreeContent;
		
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}
	
	public void setNewNote(String newNote) {
		this.newNote = newNote;
	}

}

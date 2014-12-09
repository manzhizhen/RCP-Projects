/* 文件名：     UpdatePhysicalDiagramCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-10-9
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

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
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.factory.PhysicalDiagramModelFactory;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 增加、删除和修改物理图形模型的Command
 * @author  Manzhizhen
 * @version 1.0, 2012-10-9
 * @see 
 * @since 1.0
 */
public class UpdatePhysicalDiagramCommand extends Command {
	private String flag;
	private TreeContent physicalDataTreeContent;
	private TreeContent physicalDiagramTreeContent;
	private PhysicalDiagramModel physicalDiagramModel;
	
	private String newName;
	private String oldName;
	private String newNote;
	private String oldNote;

	private Log logger = LogManager.getLogger(UpdatePhysicalDiagramCommand.class.getName());
	
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
				physicalDiagramTreeContent = viewPart.addPhysicalDiagramModel(physicalDiagramModel, physicalDataTreeContent);
			} catch (CanNotFoundNodeIDException e) {
				logger.error("向数据库树中添加物理图形节点失败！");
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						I18nUtil.getMessage("MESSAGE"), 
						"向数据库树中添加物理图形节点失败！");
				
				e.printStackTrace();
				return ;
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.error("向数据库树中添加物理图形节点失败！");
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						I18nUtil.getMessage("MESSAGE"), 
						"向数据库树中添加物理图形节点失败！");
				
				e.printStackTrace();
				return ;
			}
			
		// 删除一个物理图形模型
		} else if(DmConstants.COMMAND_DEL.equals(flag)) {
			if(physicalDiagramTreeContent != null) {
				try {
					viewPart.removePhysicalDiagramModel(physicalDiagramTreeContent);
					
					// 从产品树中删除相关表格
					ProductTreeViewPart productTreeViewPart = (ProductTreeViewPart) page
							.findView(ProductTreeViewPart.ID);
					PhysicalDiagramModel diagramModel = (PhysicalDiagramModel) 
							physicalDiagramTreeContent.getObj();
					Collection<TableModel> tableModels = diagramModel.getTableMap().values();
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
					logger.error("删除物理图形节点失败！" + e.getMessage());
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							I18nUtil.getMessage("MESSAGE"), 
							"删除物理图形节点失败！");
					
					e.printStackTrace();
					return ;
				} catch (PartInitException e) {
					logger.error("关闭物理图形相关Editor失败！" + e.getMessage());
					
					e.printStackTrace();
					return ;
				} catch (FoundTreeNodeNotUniqueException e) {
					logger.error("删除物理图形节点失败！" + e.getMessage());
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							I18nUtil.getMessage("MESSAGE"), 
							"删除物理图形节点失败！");
					
					e.printStackTrace();
					return ;
				}
			}
			
		} else if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			if(physicalDiagramTreeContent != null) {
				physicalDiagramModel = (PhysicalDiagramModel) physicalDiagramTreeContent.getObj();
				
				oldName = physicalDiagramModel.getId();
				oldName = physicalDiagramModel.getName();
				oldNote = physicalDiagramModel.getNote();
				physicalDiagramModel.setId(newName);
				physicalDiagramModel.setName(newName);
				physicalDiagramModel.setNote(newNote);
				
				viewPart.refreshTree();
				
				// 如果有打开的Editor，还需要修改Editor的标题
				IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().
					getEditorReferences();
				for(IEditorReference editor : editors) {
					if(editor.getEditor(false) instanceof DatabaseDiagramEditor) {
						DatabaseDiagramEditor databaseDiagramEditor = (DatabaseDiagramEditor) editor.getEditor(false);
						if(physicalDiagramModel.equals(databaseDiagramEditor.getPhysicalDiagramModel())) {
							databaseDiagramEditor.setPartName(newName);
							break ;
						}
					}
				}
			}
		}
		
		super.execute();
	}
	
	@Override
	public boolean canUndo() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void undo() {
//		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		if(page == null) {
//			logger.error("找不到活跃的WorkbenchPage,无法对物理数据模型进行更新！");
//			return ;
//		}
//		
//		DatabaseTreeViewPart viewPart = (DatabaseTreeViewPart) page.findView(DatabaseTreeViewPart.ID);
//		if(viewPart == null) {
//			logger.error("无法找到活跃的DatabaseTreeViewPart，更新物理数据节点失败！");
//			
//			return ;
//		}
//		
//		// 添加一个物理数据模型undo
//		if(DmConstants.COMMAND_ADD.equals(flag)) {
//			// 向数据库数添加该物理数据节点undo
//			if(physicalDataTreeContent != null) {
//				try {
//					viewPart.removePhysicalDiagramModel(physicalDiagramTreeContent);
//					
//				} catch (CanNotFoundNodeIDException e) {
//					logger.error("删除物理图形节点失败！");
//					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
//							I18nUtil.getMessage("MESSAGE"), 
//							"删除物理图形节点失败！");
//					
//					e.printStackTrace();
//					return ;
//				} catch (PartInitException e) {
//					logger.error("关闭物理图形相关Editor失败！" + e.getMessage());
//					
//					e.printStackTrace();
//				} catch (FoundTreeNodeNotUniqueException e) {
//					logger.error("删除物理图形节点失败！");
//					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
//							I18nUtil.getMessage("MESSAGE"), 
//							"删除物理图形节点失败！");
//					
//					e.printStackTrace();
//					return ;
//				}
//			}
//			
//		// 删除一个物理数据模型undo
//		} else if(DmConstants.COMMAND_DEL.equals(flag)) {
//			if(physicalDiagramTreeContent != null) {
//				try {
//					viewPart.addPhysicalDiagramModel((PhysicalDiagramModel) physicalDiagramTreeContent.getObj(), 
//							physicalDiagramTreeContent.getParent());
//				} catch (CanNotFoundNodeIDException e) {
//					logger.error("添加物理图形节点失败！");
//					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
//							I18nUtil.getMessage("MESSAGE"), 
//							"添加物理图形节点失败！");
//					
//					e.printStackTrace();
//					return ;
//				} catch (FoundTreeNodeNotUniqueException e) {
//					logger.error("添加物理图形节点失败！");
//					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
//							I18nUtil.getMessage("MESSAGE"), 
//							"添加物理图形节点失败！");
//					
//					e.printStackTrace();
//					return ;
//				}
//			}
//		
//		// 修改一个物理数据模型的undo
//		} else if(DmConstants.COMMAND_MODIFY.equals(flag)) {
//			if(physicalDiagramModel != null) {
//				physicalDiagramModel.setId(oldName);
//				physicalDiagramModel.setName(oldName);
//				physicalDiagramModel.setNote(oldNote);
//				
//				viewPart.refreshTree();
//				
//				// 如果有打开的Editor，还需要修改Editor的标题
//				IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().
//					getEditorReferences();
//				for(IEditorReference editor : editors) {
//					if(editor instanceof DatabaseDiagramEditor) {
//						DatabaseDiagramEditor databaseDiagramEditor = (DatabaseDiagramEditor) editor;
//						if(physicalDiagramModel.equals(databaseDiagramEditor.getPhysicalDiagramModel())) {
//							databaseDiagramEditor.setPartName(newName);
//							break ;
//						}
//					}
//				}
//				
////				try {
////					viewPart.updateNode(physicalDiagramTreeContent.getId(), physicalDiagramModel);
////				} catch (CanNotFoundNodeIDException e) {
////					logger.error("修改物理图形节点失败！");
////					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
////							I18nUtil.getMessage("MESSAGE"), 
////							"修改物理图形节点失败！");
////					
////					e.printStackTrace();
////					return ;
////				} catch (FoundTreeNodeNotUniqueException e) {
////					logger.error("修改物理图形节点失败！");
////					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
////							I18nUtil.getMessage("MESSAGE"), 
////							"修改物理图形节点失败！");
////					
////					e.printStackTrace();
////					return ;
////				}
//			}
//		}
//		
		super.undo();
	}
	
	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setPhysicalDataTreeContent(TreeContent physicalDataTreeContent) {
		this.physicalDataTreeContent = physicalDataTreeContent;
	}

	public void setPhysicalDiagramModel(PhysicalDiagramModel physicalDiagramModel) {
		this.physicalDiagramModel = physicalDiagramModel;
	}

	public void setPhysicalDiagramTreeContent(TreeContent physicalDiagramTreeContent) {
		this.physicalDiagramTreeContent = physicalDiagramTreeContent;
	}


	public void setNewName(String newName) {
		this.newName = newName;
	}

	public void setNewNote(String newNote) {
		this.newNote = newNote;
	}
	
	
	
}

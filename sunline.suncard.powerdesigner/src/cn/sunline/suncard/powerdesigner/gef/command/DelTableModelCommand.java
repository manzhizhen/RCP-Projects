/*
 * 文件名：.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述： SUNCARD决策引擎系统
 * 修改人： 周兵
 * 修改时间：2001-02-16
 * 修改内容：新增
 */
package cn.sunline.suncard.powerdesigner.gef.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.action.DatabaseAction;
import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.gef.editpart.DatabaseDiagramModelEditPart;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractLineGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.DatabaseDiagramGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableShortcutGefModel;
import cn.sunline.suncard.powerdesigner.gef.ui.dialog.TableGefModelDialog;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.gef.xml.LineXmlProperty;
import cn.sunline.suncard.powerdesigner.gef.xml.NodeXmlProperty;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;


/**
 * 删除TableModel模型的Command
 * @author 	易振强
 * @version [1.0, 2011-11-01]
 * @see
 * @since 1.0
 */
public class DelTableModelCommand extends Command {
	private DatabaseDiagramGefModel diagramGefModel;
	private TableGefModel gefModel;

	private List<AbstractLineGefModel> sourceConnections = new ArrayList<AbstractLineGefModel>();
	private List<AbstractLineGefModel> targetConnections = new ArrayList<AbstractLineGefModel>();
	
	private List<DeleteLineCommand> deleteLineCommandList = new ArrayList<DeleteLineCommand>();
	private List<DelTableShortcutModelCommand> deleteShortcutModelCommandList = new ArrayList<DelTableShortcutModelCommand>();
	private NodeXmlProperty delNodeXmlProperty;
	private List<LineXmlProperty> delLineXmlPropertyList;
	private List<TreeContent> delTreeContentList; // 执行命令时被删除的表格的树节点
	
	public static Log logger = LogManager.getLogger(DelTableModelCommand.class.getName());
	private TableModel tableModel;
	private TreeContent tableTreeModel;

	@Override
	public void execute() {
		// model为空，则diagramGefModel也应该没设置，说明用户是在树上删除该TableModel
		if(gefModel == null) {
			// 看打开该对话框后用户是否打开了相关的Editor
			gefModel = DatabaseAction.getTableGefModel(tableModel);
		}
		
		if(tableModel == null) {
			logger.error("传入的TableModel为空，删除失败！");
			return ;
		}
		
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		// model不为空，说明是在Editor删除该TableModel的，所以要在Editor中删除该模型
		if(gefModel != null) {
			
			if(page == null) {
				logger.error("找不到活跃的WorkbenchPage，无法删除图元模型！");
				return ;
			}
			
			if(diagramGefModel == null) {
				diagramGefModel = (DatabaseDiagramGefModel) gefModel.getParentGefModel();
			}
			
			// 删除这个连接模型，再删除一个对象前，搜索把这个模型对象作为起点和终点的所有连接
			sourceConnections.addAll(gefModel.getSourceConnections());
			targetConnections.addAll(gefModel.getTargetConnections());
			
			// 删除该模型对应的source
			for (int i = 0; i < sourceConnections.size(); i++) {
				AbstractLineGefModel sourceModel = (AbstractLineGefModel) sourceConnections
						.get(i);
				
				DeleteLineCommand deleteLineCommand = new DeleteLineCommand();
				deleteLineCommand.setConnectionModel(sourceModel);
				deleteLineCommand.execute();
				
				deleteLineCommandList.add(deleteLineCommand);
			}
			
			// 删除该模型对应的target
			for (int i = 0; i < targetConnections.size(); i++) {
				AbstractLineGefModel targetModel = (AbstractLineGefModel) targetConnections
						.get(i);
				
				DeleteLineCommand deleteLineCommand = new DeleteLineCommand();
				deleteLineCommand.setConnectionModel(targetModel);
				deleteLineCommand.execute();
				
				deleteLineCommandList.add(deleteLineCommand);
			}
			
			// 从GEF集合模型中删除该模型
			diagramGefModel.removeChild(gefModel);
		}
		

		// 从对应的PhysicalDiagramModel中移除该TableModel
		PhysicalDiagramModel physicalDiagramModel = tableModel.getPhysicalDiagramModel();
		physicalDiagramModel.getTableMap().remove(tableModel.getTableName());
		
		List<NodeXmlProperty> nodeXmlList = physicalDiagramModel.getNodeXmlPropertyList();
		delNodeXmlProperty = null;
		for(NodeXmlProperty nodeXmlProperty : nodeXmlList) {
			if(tableModel.getId().equals(nodeXmlProperty.getId())) {
				delNodeXmlProperty = nodeXmlProperty;
				break ;
			}
		}
		nodeXmlList.remove(delNodeXmlProperty);
		
		List<LineXmlProperty> lineXmlList = physicalDiagramModel.getLineXmlPropertyList();
		delLineXmlPropertyList = new ArrayList<LineXmlProperty>();
		for(LineXmlProperty lineXmlProperty : lineXmlList) {
			if(tableModel.getId().equals(lineXmlProperty.getSourceNodeId()) 
					|| tableModel.getId().equals(lineXmlProperty.getTargetNodeId())) {
				delLineXmlPropertyList.add(lineXmlProperty);
			}
		}
		lineXmlList.removeAll(delLineXmlPropertyList);
		
		// 从数据库树上删除该TableModel
		try {
			DatabaseTreeViewPart databaseTreeViewPart = (DatabaseTreeViewPart) page
					.findView(DatabaseTreeViewPart.ID);
			if(databaseTreeViewPart != null) {
				delTreeContentList = databaseTreeViewPart.removeTableModel(tableModel);
				
			} else {
				logger.error("从数据树上删除该TableModel失败！找不到DatabaseTreeViewPart！");
			}
			
			// 从产品树上删除相关的表格
			ProductTreeViewPart productTreeViewPart = (ProductTreeViewPart) page
					.findView(ProductTreeViewPart.ID);
			List<TreeContent> tableTreeContents = productTreeViewPart
					.getTreeContentFromTableModel(tableModel);
			if(tableTreeContents != null) {
				for(TreeContent tableTreeContent : tableTreeContents) {
					productTreeViewPart.removeTableModel(tableTreeContent);
				}
			}
		} catch (CanNotFoundNodeIDException e) {
			logger.error("在产品树上删除表格节点失败！" + e.getMessage());
			e.printStackTrace();
			return ;
		} catch (FoundTreeNodeNotUniqueException e) {
			logger.error("在产品树上删除表格节点失败！" + e.getMessage());
			e.printStackTrace();
			return ;
		}
		
		
		// 删除引用该表格的快捷方式对象
		FileModel fileModel = FileModel.getFileModelFromObj(tableModel);
		if(fileModel == null) {
			logger.error("删除与该表格相关的表格快捷方式失败！" + tableModel.getTableName());
		} else {
			Set<TableShortcutModel> allShortcutModel = fileModel.getAllTableShortcutModel();
			for(TableShortcutModel shortcutModel : allShortcutModel) {
				if(shortcutModel.getTargetTableModel().equals(tableModel)) {
					DelTableShortcutModelCommand command = new DelTableShortcutModelCommand();
					command.setTableShortcutModel(shortcutModel);
					command.execute();
					deleteShortcutModelCommandList.add(command);
				}
			}
		}
		
		// 如果有对应的对话框打开了，则需要关闭
		TableGefModelDialog dialog = TableGefModelDialog.getTableDialogMap()
				.get(tableModel);
		if(dialog != null) {
			dialog.close();
		}
		
		// 关闭对应的对话框，包括ColumnModel、LineModel和TableModel的对话框
		TableGefModelDialog tableGefModelDialog = TableGefModelDialog.getTableDialogMap().get(tableModel);
		if(tableGefModelDialog != null) {
			tableGefModelDialog.close();
		}
	}

	@Override
	public void undo() {
		if(tableModel == null) {
			logger.error("传入的TableModel为空，删除Undo失败！");
			return ;
		}
		
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		// model不为空，说明是在Editor删除该TableModel的，所以要在Editor中删除该模型
		if(gefModel != null) {
			
			if(page == null) {
				logger.error("找不到活跃的WorkbenchPage，无法创建图元模型！");
				return ;
			}
			
			
			for(int i = deleteLineCommandList.size() - 1; i >= 0; i--) {
				deleteLineCommandList.get(i).undo();
			}
			
			diagramGefModel.addChild(gefModel);
			
			//清除记录，这些记录用于恢复
			sourceConnections.clear();
			targetConnections.clear();
			
		}
		
		tableModel.getPhysicalDiagramModel().getTableMap().put(tableModel.getTableName(), 
				tableModel);
		PhysicalDiagramModel physicalDiagramModel = tableModel.getPhysicalDiagramModel();
		if(delNodeXmlProperty != null) {
			physicalDiagramModel.getNodeXmlPropertyList().add(delNodeXmlProperty);
		}
		if(delLineXmlPropertyList != null) {
			physicalDiagramModel.getLineXmlPropertyList().addAll(delLineXmlPropertyList);
		}
		
		// 从数据库树上添加该TableModel
		DatabaseTreeViewPart databaseTreeViewPart = (DatabaseTreeViewPart) page.findView(DatabaseTreeViewPart.ID);
		if(databaseTreeViewPart != null) {
			if(delTreeContentList != null) {
				for(TreeContent treeContent : delTreeContentList) {
					// 将删除的表格树节点还原到父节点下面
					treeContent.getParent().getChildrenList().add(treeContent);
				}
				
				databaseTreeViewPart.refreshTree();
			}
		}
		
		for(int i = deleteShortcutModelCommandList.size() - 1; i >= 0; i--) {
			deleteShortcutModelCommandList.get(i).undo();
		}
	}
		
		
	
	public void setDatabaseDiagramGefModel(Object model) {
		diagramGefModel = (DatabaseDiagramGefModel) model;
	}

	public void setGefModel(TableGefModel gefModel) {
		this.gefModel = gefModel;
		if(this.gefModel != null && gefModel instanceof TableGefModel) {
			tableModel = (TableModel) this.gefModel.getDataObject();
		}
	}
	
	public void setTableTreeContent(TreeContent tableTreeModel) {
		this.tableTreeModel = tableTreeModel;
		if(tableTreeModel != null) {
			this.tableModel = (TableModel) tableTreeModel.getObj();
		}
	}

}

/* 文件名：     CreateCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.DatabaseDiagramGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableShortcutGefModel;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.gef.xml.GefFigureSwitchXml;
import cn.sunline.suncard.powerdesigner.manager.SystemSetManager;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.model.factory.TableModelFactory;
import cn.sunline.suncard.powerdesigner.model.factory.TableShortcutModelFactory;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;



/**
 * 创建模型的Command
 * @author  易振强
 * @version 1.0, 2012-3-26
 * @see 
 * @since 1.0
 */
public class CreateModelCommand extends Command {
	private DatabaseDiagramGefModel diagramGefModel;	// 集合模型
	private TreeContent tableModelTreeContent;
	private AbstractGefModel model;			// 要添加的图元模型
	private String parentId = null;				// 表格集合节点的ID
	private boolean isPaste = false; // 判断是否是粘贴时调用的该Command
	
	// 新的表格名称和描述，这是考虑到复制和粘贴时需要保证原表名
	private String newTableName = null;
	private String newTableDesc = null;
	
	private DatabaseDiagramEditor databaseDiagramEditor;
	public static Log logger = LogManager.getLogger(CreateModelCommand.class.getName());
	
	@Override
	public boolean canExecute() {
		return true;
	}
	
	@Override
	public void execute() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		if(page == null) {
			logger.error("找不到活跃的WorkbenchPage，无法创建图元模型！");
			return ;
		}
		
		IEditorPart editorPart = page.getActiveEditor();
		if(editorPart instanceof DatabaseDiagramEditor) {
			databaseDiagramEditor = (DatabaseDiagramEditor) editorPart;
			
			if(model instanceof TableGefModel) {
				TableModel tableModel = (TableModel) model.getDataObject();
				tableModel.setPhysicalDiagramModel(databaseDiagramEditor.getPhysicalDiagramModel());
				
				// 如果配置了添加默认列，则需要向表格里面添加默认列
				if(DmConstants.YES.equals(SystemSetManager.getIsAddDefaultColumns()) && !isPaste) {
					PhysicalDataModel physicalDataModel = tableModel.getPhysicalDiagramModel()
							.getPackageModel().getPhysicalDataModel();
					if(physicalDataModel != null) {
						List<ColumnModel> defaultColumnList = physicalDataModel.getDefaultColumnList();
						List<ColumnModel> cloneColumnList = new ArrayList<ColumnModel>();
						try {
							for(ColumnModel columnModel : defaultColumnList) {
								cloneColumnList.add(columnModel.clone());
							}
							
							tableModel.setColumnList(cloneColumnList);
						} catch (CloneNotSupportedException e) {
							logger.error("克隆ColumnModel失败，向表格添加默认列失败！");
							e.printStackTrace();
						}
					} else {
						logger.error("从TableModel向上遍历时获得PhysicalDataModel为空，无法添加默认列！");
					}
				}
				
				// 添加到表格模型的工厂中
				TableModelFactory.addTableModel(FileModel.getFileModelFromObj(tableModel), tableModel);
				
				// 设置模型的xml信息
				GefFigureSwitchXml.initNodeProperty(model, databaseDiagramEditor.getPhysicalDiagramModel(), diagramGefModel, newTableName, newTableDesc); 
				
				// 添加到该PhysicalDiagramModel中
				PhysicalDiagramModel physicalDiagramModel = databaseDiagramEditor.getPhysicalDiagramModel();
				physicalDiagramModel.getTableMap().put(tableModel.getTableName(), tableModel);
				
			} else if(model instanceof TableShortcutGefModel){
				TableShortcutModel tableShortcutModel = (TableShortcutModel) model.getDataObject();
				tableShortcutModel.setPhysicalDiagramModel(databaseDiagramEditor.getPhysicalDiagramModel());
				
				// 添加到表格快捷方式模型的工厂中
				TableShortcutModelFactory.addTableShortcutModel(FileModel.getFileModelFromObj(tableShortcutModel), tableShortcutModel);
				
				// 设置模型的xml信息
				GefFigureSwitchXml.initNodeProperty(model, databaseDiagramEditor.getPhysicalDiagramModel(), diagramGefModel, null, null); 
				
				PhysicalDiagramModel physicalDiagramModel = databaseDiagramEditor.getPhysicalDiagramModel();
				physicalDiagramModel.getTableShortcutMap().put(tableShortcutModel.getId(), tableShortcutModel);
				
			}
			
			
			// 添加到GEF集合模型
			diagramGefModel.addChild(model);
			
			// 给树上添加该表格节点
			try {
				DatabaseTreeViewPart databaseTreeViewPart = (DatabaseTreeViewPart) page.findView(DatabaseTreeViewPart.ID);
				if(databaseTreeViewPart != null) {
					if(model instanceof TableGefModel) {
						databaseTreeViewPart.addTableModel((TableModel)model.getDataObject(), 
								databaseDiagramEditor.getPhysicalDiagramTreeContent());
					} else if(model instanceof TableShortcutGefModel) { 
						databaseTreeViewPart.addTableShortcutModel((TableShortcutModel)model.getDataObject(), 
								databaseDiagramEditor.getPhysicalDiagramTreeContent().getId());
					}
				}
			} catch (CanNotFoundNodeIDException e) {
				logger.error("添加表格节点失败！" + e.getMessage());
				e.printStackTrace();
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.error("添加表格节点失败！" + e.getMessage());
				e.printStackTrace();
			}
		}
		
		
	}
	
	public void setPaste(boolean isPaste) {
		this.isPaste = isPaste;
	}

	@Override
	public void undo() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		if(page == null) {
			logger.error("找不到活跃的WorkbenchPage，无法创建图元模型！");
			return ;
		}
		
		IEditorPart editorPart = page.getActiveEditor();
		if(editorPart instanceof DatabaseDiagramEditor) {
			diagramGefModel.removeChild(model);
			// 从该FileModel中移除该TableModel
			databaseDiagramEditor.getPhysicalDiagramModel().getTableMap().
			remove(((TableModel) model.getDataObject()).getTableName());
			
			// 从树上移除该表格节点
			try {
				DatabaseTreeViewPart databaseTreeViewPart = (DatabaseTreeViewPart) page.showView(DatabaseTreeViewPart.ID);
				if(databaseTreeViewPart != null && parentId != null) {
					databaseTreeViewPart.removeTableModel((TableModel) tableModelTreeContent.getObj());
				}
			} catch (PartInitException e) {
				logger.error("删除表格节点失败！" + e.getMessage());
				e.printStackTrace();
			} catch (CanNotFoundNodeIDException e) {
				logger.error("删除表格节点失败！" + e.getMessage());
				e.printStackTrace();
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.error("删除表格节点失败！" + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public void setDatabaseDiagramGefModel(DatabaseDiagramGefModel diagramGefModel) {
		this.diagramGefModel = diagramGefModel;
	}
	
	public void setModel(AbstractGefModel model) {
		this.model = model;
	}

	public void setNewTableName(String newTableName) {
		this.newTableName = newTableName;
	}

	public void setNewTableDesc(String newTableDesc) {
		this.newTableDesc = newTableDesc;
	}
}

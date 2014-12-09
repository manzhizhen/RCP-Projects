/* 文件名：     ModifyTableModelCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-11
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.action.DatabaseAction;
import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.gef.editpart.DatabaseDiagramModelEditPart;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.xml.LineXmlProperty;
import cn.sunline.suncard.powerdesigner.gef.xml.NodeXmlProperty;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.InitTableDataModel;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.TableDataModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 通过弹出表格对话框修改TableModel属性所调用的命令
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-9-11
 * @see
 * @since 1.0
 */
public class UpdateTableModelCommand extends Command {
//	private String newName;
//	private String oldName;
//	private String newDesc;
//	private String oldDesc;
//	private String newNote;
//	private String oldNote;
//
//	private List<ColumnModel> newColumnModelList;
//	private List<ColumnModel> oldColumnModelList;
	
	// 用于TableModel还原的Map
	private Map<TableModel, List<ColumnModel>> tableRollBackMap = new HashMap<TableModel, List<ColumnModel>>();
	
	private TableGefModel tableGefModel;
	private TableModel tableModel;
	private TableModel newTableModel; // 新设置的值。
	private TableModel oldTableModel; // 储存旧值，便于还原
	private Set<ModuleModel> moduleModelSet = new HashSet<ModuleModel>(); // 包含该表格的模块
	private InitTableDataModel initTableDataModel = new InitTableDataModel();	// 表格初始化信息

	private Log logger = LogManager.getLogger(UpdateTableModelCommand.class.getName());

	private List<TreeContent> oldTreeContentList; // 更新一个表格的产品标签时，需要先把该表格从产品树中完全删除，然后再根据新的产品标签来添加
	
	@Override
	public void execute() {
		if(tableModel == null || newTableModel == null) {
			logger.error("传入的TableModel或新值的TableModel为空，无法执行ModifyTableModelCommand！");
			return;
		}
		
		try {
			oldTableModel = tableModel.clone(false);
		} catch (CloneNotSupportedException e1) {
			logger.error("克隆TableModel失败！" + e1.getMessage());
			
			e1.printStackTrace();
			return ;
		}
		
		tableModel.setTableName(newTableModel.getTableName());
		tableModel.setTableDesc(newTableModel.getTableDesc());
		tableModel.setTableNote(newTableModel.getTableNote());
		tableModel.setTableType(newTableModel.getTableType());
		
		//-----------------开始更新列模型列表信息
		tableModel.setColumnList(newTableModel.getColumnList());
		
		// 找出原ColumnModel的List中的主键
		List<ColumnModel> oldKeyColumnModelList = new ArrayList<ColumnModel>();
		List<String> oldKeyColumnModelIdList = new ArrayList<String>();
		List<ColumnModel> oldColumnModelList = oldTableModel.getColumnList();
		for(ColumnModel columnModel : oldColumnModelList) {
			if(columnModel.isPrimaryKey()) {
				oldKeyColumnModelList.add(columnModel);
				oldKeyColumnModelIdList.add(columnModel.getId());
			}
		}
		
		// 找出新ColumnModel的List中的主键
		List<ColumnModel> newKeyColumnModelList = new ArrayList<ColumnModel>();
		List<String> newKeyColumnModelIdList = new ArrayList<String>();
		List<ColumnModel> newColumnModelList = newTableModel.getColumnList();
		for(ColumnModel columnModel : newColumnModelList) {
			// 该ColumnModel的ID为空，说明是新添加的列
			if(columnModel.getId() == null || "".equals(columnModel.getId().trim()) || 
					ColumnModelFactory.getColumnModel(FileModel.getFileModelFromObj(columnModel), 
							columnModel.getId()) == null) {
				ColumnModelFactory.addColumnModel(FileModel.getFileModelFromObj(columnModel) , 
						columnModel);
			}
			
			if(columnModel.isPrimaryKey()) {
				newKeyColumnModelList.add(columnModel);
				newKeyColumnModelIdList.add(columnModel.getId());
			}
		}
		
		List<ColumnModel> allKeyColumnModelLsit = new ArrayList<ColumnModel>();
		allKeyColumnModelLsit.addAll(oldKeyColumnModelList);
		allKeyColumnModelLsit.addAll(newColumnModelList);
		for(ColumnModel columnModel : allKeyColumnModelLsit) {
			// 如果新旧列表都包含该ColumnModel，说明该列只是被修改，没有被删除，所以不处理
			if(oldKeyColumnModelIdList.contains(columnModel.getId()) && newKeyColumnModelIdList.
					contains(columnModel.getId())) {
				continue ;
				
			// 如果旧列表包含该ColumnModel，新列表没有，说明主键已经被删除或被取消了主键属性，
			// 所以如果该主键有外键，则外键需要处理	
			} else if(oldKeyColumnModelIdList.contains(columnModel.getId()) && !newKeyColumnModelIdList.
					contains(columnModel)) {
				List<ColumnModel> childColumnList = ColumnModelFactory.findAllForeginKeyChildColumn(FileModel.
						getFileModelFromObj(columnModel), columnModel.getId());
				
				if(childColumnList == null) {
					continue ;
				}
				
				for(ColumnModel childColumnModel : childColumnList) {
					// 先备份数据再处理
					TableModel tableModel = childColumnModel.getTableModel();
					try {
						if(tableRollBackMap.get(tableModel) == null) {
							tableRollBackMap.put(tableModel, tableModel.backUpTableColumnList());
						}
					} catch (CloneNotSupportedException e) {
						logger.error("备份表格:" + tableModel.getTableName() + "数据失败！" + e.getMessage());
						e.printStackTrace();
						continue ;
					}
					
					childColumnModel.setParentTableColumnId(null);
					
					// 如果该外键不是主键，则需要删除
					if(!childColumnModel.isPrimaryKey()) {
						childColumnModel.getTableModel().getColumnList().remove(childColumnModel);
					}
				}  
				
			// 如果旧列表没有，新列表包含该ColumnModel，说明主键是新的，暂时也不处理
			} else if(!oldKeyColumnModelIdList.contains(columnModel.getId()) && newKeyColumnModelIdList.
						contains(columnModel.getId())) {
					
			}
		}
		
		// 更新产品相关信息
		ProductTreeViewPart productTreeViewPart = (ProductTreeViewPart) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(ProductTreeViewPart.ID);
		if(productTreeViewPart == null) {
			logger.error("找不到ProductTreeViewPart，更新产品树上表格信息失败！");
		} else {
			oldTreeContentList = productTreeViewPart.getTreeContentFromTableModel(tableModel);
			if(oldTreeContentList != null) {
				// 先把该表格从产品树上全部删除
				for(TreeContent treeContent : oldTreeContentList) {
					try {
						productTreeViewPart.removeTableModel(treeContent);
					} catch (CanNotFoundNodeIDException e) {
						logger.error("从产品树上删除一个表格失败！" + ((TableModel)treeContent
								.getObj()).getTableName() + " " + e.getMessage());
						e.printStackTrace();
					} catch (FoundTreeNodeNotUniqueException e) {
						logger.error("从产品树上删除一个表格失败！" + ((TableModel)treeContent
								.getObj()).getTableName() + " " + e.getMessage());
					}
				}
			}
			
			// 把表格添加到对应的模块下面
			for(ModuleModel moduleModel : moduleModelSet) {
				TreeContent moduleTreeContent = productTreeViewPart
						.getTreeContentFromModuleModel(moduleModel);
				if(moduleTreeContent != null) {
					try {
						ProductTreeViewPart.addTableModel(tableModel, moduleTreeContent, productTreeViewPart.getTreeViewComposite());
					} catch (CanNotFoundNodeIDException e) {
						logger.error("从产品树上添加一个表格失败！" + tableModel.getTableName() + " " + e.getMessage());
						e.printStackTrace();
					} catch (FoundTreeNodeNotUniqueException e) {
						logger.error("从产品树上添加一个表格失败！" + tableModel.getTableName() + " " + e.getMessage());
						e.printStackTrace();
					}
				} else {
					logger.error("把表格添加到对应模块下失败！" + moduleModel.getName());
				}
			}
			
		}
		
		
		// 更新PhysicalDiagramModel中的表格信息
		PhysicalDiagramModel physicalDiagramModel = tableModel.getPhysicalDiagramModel();
		physicalDiagramModel.getTableMap().remove(oldTableModel.getTableName());
		physicalDiagramModel.getTableMap().put(tableModel.getTableName(), tableModel);
		
		// 更新InitTableDataModel对象
		Set<ColumnModel> newColumnSet = new HashSet<ColumnModel>(); // 用于储存新的ColumnModel的List中所用到的id
		Set<ColumnModel> allColumnSet = new HashSet<ColumnModel>(); // 用于储存新的ColumnModel的List中所用到的id和旧的InitTableDataModel对象中所用到的所有ColumnModel的Id
		List<ColumnModel> columnModelList = tableModel.getColumnList();
		for(ColumnModel tempColumnModel : columnModelList) {
			newColumnSet.add(tempColumnModel);
			allColumnSet.add(tempColumnModel);
		}
		List<TableDataModel> keyValueMapList = tableModel.getInitTableDataModel()
				.getInitDataList();
		for(TableDataModel tableDataModel : keyValueMapList) {
			// 因为每一个Map的Key都是相同的，所以添加一次即可
			allColumnSet.addAll(tableDataModel.getDataMap().keySet());
			break ;
		}
		for(TableDataModel tableDataModelMap : keyValueMapList) {
			for(ColumnModel columnModel : allColumnSet) {
				if(newColumnSet.contains(columnModel) && !tableDataModelMap.getDataMap().containsKey(columnModel)) {
					tableDataModelMap.getDataMap().put(columnModel, "");
				} else if(!newColumnSet.contains(columnModel) && tableDataModelMap.getDataMap().containsKey(columnModel)) {
					tableDataModelMap.getDataMap().remove(columnModel);
				}
			}
		}
		
		// 更新表格初始化数据
		tableModel.setInitTableDataModel(initTableDataModel);
		
		// 如果用户是从树上的TableModel打开该对话框的，则tableGefModel为空
		if(tableGefModel == null) {
			// 有可能用户在打开了此对话框后又打开了对应的Editor
			tableGefModel = DatabaseAction.getTableGefModel(tableModel);
		}
		
		// 如果此时tableGefModel还为空，则说明没打开相关Editor
		if(tableGefModel != null) {
			// 更新界面
			DatabaseDiagramModelEditPart.refreshEditor();
		}
		
		// 刷新树上的信息
		DefaultViewPart.refreshFileModelTreeContent();
		
		super.execute();
	}

	@Override
	public void undo() {
		if(oldTableModel == null) {
			return ;
		}
		
		tableModel.setTableName(oldTableModel.getTableName());
		tableModel.setTableDesc(oldTableModel.getTableDesc());
		tableModel.setTableNote(oldTableModel.getTableNote());
		tableModel.setTableType(oldTableModel.getTableType());
		tableModel.setColumnList(oldTableModel.getColumnList());
		tableModel.setInitTableDataModel(oldTableModel.getInitTableDataModel());
		
		// 还原可能被修改过的表
		Set<TableModel> tableModelSet = tableRollBackMap.keySet();
		for(TableModel tableModel : tableModelSet) {
			tableModel.setColumnList(tableRollBackMap.get(tableModel));
		}
		
		// 更新PhysicalDiagramModel中的表格信息
		PhysicalDiagramModel physicalDiagramModel = tableModel.getPhysicalDiagramModel();
		physicalDiagramModel.getTableMap().remove(newTableModel.getTableName());
		physicalDiagramModel.getTableMap().put(tableModel.getTableName(), tableModel);
		
//		// 更新PhysicalDiagramModel中与表格相关的GEF信息
//		List<NodeXmlProperty> nodeList = physicalDiagramModel.getNodeXmlPropertyList();
//		for(NodeXmlProperty nodeXmlProperty : nodeList) {
//			if(newTableModel.getTableName().equals(nodeXmlProperty.getId())) {
//				nodeXmlProperty.setId(tableModel.getTableName());
//				break ;
//			}
//		}
//		List<LineXmlProperty> lineList = physicalDiagramModel.getLineXmlPropertyList();
//		for(LineXmlProperty lineXmlProperty : lineList) {
//			if(newTableModel.getTableName().equals(lineXmlProperty.getSourceNodeId())) {
//				lineXmlProperty.setSourceNodeId(tableModel.getTableName());
//			}
//			if(newTableModel.getTableName().equals(lineXmlProperty.getTargetNodeId())) {
//				lineXmlProperty.setTargetNodeId(tableModel.getTableName());
//			}
// 		}
		
		
		// 如果对于Editor打开了，则需要更新GEF界面
		if(tableGefModel != null) {
//			tableGefModel.getNodeXmlProperty().setId(tableModel.getTableName());
			// 这样才能更新界面
//			tableGefModel.setDataObject(tableModel);
			DatabaseDiagramModelEditPart.refreshEditor();
		}
		
		// 刷新树上的信息
		try {
			DatabaseTreeViewPart databaseTreeViewPart = (DatabaseTreeViewPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().
				showView(DatabaseTreeViewPart.ID);
			
			if(databaseTreeViewPart != null) {
				databaseTreeViewPart.refreshTree();
			}
		} catch (PartInitException e) {
			logger.error("更新数据库树失败！" + e.getMessage());
			e.printStackTrace();
		}
		
		super.undo();
	}
	
//	public void setNewName(String newName) {
//		this.newName = newName;
//	}
//
//	public void setNewDesc(String newDesc) {
//		this.newDesc = newDesc;
//	}
//
//	public void setNewNote(String newNote) {
//		this.newNote = newNote;
//	}
//
//	public void setNewColumnModelList(List<ColumnModel> newColumnModelList) {
//		this.newColumnModelList = newColumnModelList;
//	}

	public void setTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
	}
	
	public void setNewTableModel(TableModel newTableModel) {
		this.newTableModel = newTableModel;
	}

	public void setTableGefModel(TableGefModel tableGefModel) {
		this.tableGefModel = tableGefModel;
		if(tableGefModel != null) {
			tableModel = tableGefModel.getDataObject();
		}
	}

	public void setModuleModelSet(Set<ModuleModel> moduleModelSet) {
		this.moduleModelSet = moduleModelSet;
	}

	/**
	 * @param initTableDataModel the initTableDataModel to set
	 */
	public void setInitTableDataModel(InitTableDataModel initTableDataModel) {
		this.initTableDataModel = initTableDataModel;
	}
}

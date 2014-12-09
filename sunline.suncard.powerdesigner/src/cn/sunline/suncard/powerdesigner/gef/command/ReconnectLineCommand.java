/* 文件名：     ReconnectConnectionCommand.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	 重定向连接线连接的Command
 * 修改人：     易振强
 * 修改时间：2011-11-15
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.command;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.Connection;
import org.eclipse.gef.commands.Command;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.gef.editpart.DatabaseDiagramModelEditPart;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractLineGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.DatabaseDiagramGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableShortcutGefModel;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.JoinModel;
import cn.sunline.suncard.powerdesigner.model.KeyToKeyModel;
import cn.sunline.suncard.powerdesigner.model.LineModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;


/**
 *  重定向连接线连接的Command
 * @author    易振强
 * @version   [1.0, 2011-11-15]
 * @since     1.0
 */
public class ReconnectLineCommand extends Command {
	private AbstractGefModel oldModel;
	private AbstractGefModel source;
	private AbstractGefModel target;
	private AbstractLineGefModel connection;
	private DatabaseDiagramGefModel diagramGefModel;
	
	private String oldTargetValue;
	private String oldNameValue;
	
	private String oldDecisionId;
	private String oldTarget;
	
	private List<ColumnModel> oldChildColumnList; // 用于备份原源节点的Column数据
	private List<ColumnModel> newChildColumnList; // 用于备份新的源节点的Column数据
	private String oldConstraintName; // 约束名称
	private String oldIncidenceRelation; // 关联关系
	
	
	public static Log logger = LogManager.getLogger(ReconnectLineCommand.class.getName());
	
	/**
	 *  用于标记该Command是由于移动连接线的开始端还是移动连接线的结束端产生的。
	 *  1为开始端，2为结束端
	 */
	private int flag = -1;
	public final static int START = 1;
	public final static int END = 2;

	/**
	 * 
	 */
	public ReconnectLineCommand() {
		IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(part instanceof DatabaseDiagramEditor) {
			DatabaseDiagramEditor gefPart =  (DatabaseDiagramEditor) part;
			diagramGefModel = gefPart.getDatabaseDiagramGefModel();
		}
	}
	
	/**
	 *  首先判断是否能执行连接
	 */
	@Override
	public boolean canExecute() {
		// 开始节点和结束节点都不能为空
		if (source == null || target == null) {
			return false;
		}
		
		// 开始节点和结束节点不能相等,快捷方式对象只能做为目标节点
		if (source.equals(target) || source instanceof TableShortcutGefModel) {
			return false;
		}
		
		List<AbstractLineGefModel> list;
		
		if(START == flag) {
			list = target.getTargetConnections();
			
			for(AbstractLineGefModel temp : list) {
				if(temp.getSource().equals(source)) {
					return false;
				}
			}
		} else if(END == flag) {
			list = source.getSourceConnections();
			
			for(AbstractLineGefModel temp : list) {
				if(temp.getTarget().equals(target)) {
					return false;
				}
			}
		}
		
		return true;
	}

	@Override
	public void execute() {
		logger.info("void execute() start...");
		
		LineModel lineModel = (LineModel) connection.getDataObject();
		
		if(lineModel == null) {
			logger.error("连接线上的LineModel为空！无法在创建时继续初始化信息！");
			return ;
		}
		
		
		// 不管是重定向目标节点还是源节点，都需要备份原源节点和源目标节点的列List数据
		TableModel sourceTableModel;	// 老的源节点
		TableModel targetTableModel = null;	// 老的表格目标节点
		TableShortcutModel targetTableShortcutModel = null; // 老的表格快捷方式目标节点
		if(flag == START) {
			sourceTableModel = (TableModel) oldModel.getDataObject();
			if(target.getDataObject() instanceof TableModel) {
				targetTableModel = (TableModel) target.getDataObject();
			} else if(target.getDataObject() instanceof TableShortcutModel) {
				targetTableShortcutModel = (TableShortcutModel) target.getDataObject();
			}
		} else {
			sourceTableModel = (TableModel) source.getDataObject();
			if(oldModel.getDataObject() instanceof TableModel) {
				targetTableModel = (TableModel) oldModel.getDataObject();
			} else if(target.getDataObject() instanceof TableShortcutModel) {
				targetTableShortcutModel = (TableShortcutModel) oldModel.getDataObject();
			}
		}
		// 克隆子表的列的List，便于修改后还原
		try {
			oldChildColumnList = sourceTableModel.backUpTableColumnList();
			
		} catch (CloneNotSupportedException e) {
			logger.error("克隆ColumnModel失败！" + e.getMessage());
			e.printStackTrace();
			
			return ;
		}
		
		// 除去老的源节点的相关的外键信息
		List<ColumnModel> childColumnList = sourceTableModel.getColumnList();
		List<ColumnModel> needDelColumnList = new ArrayList<ColumnModel>();
		for(ColumnModel columnModel : childColumnList) {
			if(columnModel.isForeignKey()) {
				ColumnModel parentTableColumnModel = ColumnModelFactory.getColumnModel(FileModel.
						getFileModelFromObj(columnModel), columnModel.getParentTableColumnId());
				if(parentTableColumnModel != null) {
					String tableName = null;
					if(targetTableModel != null) {
						tableName = targetTableModel.getTableName();
					} else if(targetTableShortcutModel != null) {
						tableName = targetTableShortcutModel.getTargetTableModel().getTableName();
					}
					if((parentTableColumnModel.getTableModel().getTableName().equals(tableName))) {
						columnModel.setParentTableColumnId(null);
					}
					
					// 如果该键仅仅是他的外键，则需要删除
					if(!columnModel.isPrimaryKey()) {
						needDelColumnList.add(columnModel);
					}
				}
			}
		}
		childColumnList.removeAll(needDelColumnList);
		
		// 如果是重定向连接线的起点
		if(flag == START) {
			// 从老的源节点中删除该LineModel
			sourceTableModel.getLineModelList().remove(lineModel);
			
			// 获取新的源节点
			TableModel newSourceTableModel = (TableModel) source.getDataObject();
			// 备份新的源节点的Column数据
			try {
				newChildColumnList = newSourceTableModel.backUpTableColumnList();
			} catch (CloneNotSupportedException e) {
				logger.error("克隆ColumnModel失败！" + e.getMessage());
				e.printStackTrace();
				
				return ;
			}
			
			// 在新的源节点中添加这个LineModel
			newSourceTableModel.getLineModelList().add(lineModel);
			try {
				if(targetTableModel != null) {
					CreateLineCommand.createRelation(targetTableModel, newSourceTableModel);
				} else if(targetTableShortcutModel != null) {
					CreateLineCommand.createRelation(targetTableShortcutModel.getTargetTableModel()
							, newSourceTableModel);
				}
			} catch (CloneNotSupportedException e) {
				logger.error("克隆ColumnModel失败！" + e.getMessage());
				e.printStackTrace();
			}
			
			connection.setSource(source);
			connection.attachSource();// 连接开始节点
			oldModel.removeSourceConnection(connection);
			
		} else if(flag == END) {
			
			// 获取新的目标节点
			TableModel newTargetTableModel = (TableModel) target.getDataObject();
			
			try {
				CreateLineCommand.createRelation(newTargetTableModel, sourceTableModel);
			} catch (CloneNotSupportedException e) {
				logger.error("克隆ColumnModel失败！" + e.getMessage());
				e.printStackTrace();
			}
			
//			oldConstraintName = lineModel.getConstraintName();
//			oldIncidenceRelation = lineModel.getIncidenceRelation();
			// 更新LineModel中的父表的ID
			lineModel.setParentTableModelId(newTargetTableModel.getId());
			
			connection.setTarget(target);
			connection.attachTarget();// 连接结束节点
			oldModel.removeTargetConnection(connection);
		}
		
		// 刷新界面
		DatabaseDiagramModelEditPart.refreshEditor();
	}

	// 撤销
	@Override
	public void undo() {	
		LineModel lineModel = (LineModel) connection.getDataObject();
		
		if(lineModel == null) {
			logger.error("连接线上的LineModel为空！无法在创建时继续初始化信息！");
			return ;
		}
		
		if(flag == START) {
			TableModel oldSourceTableModel = (TableModel) oldModel.getDataObject();
			oldSourceTableModel.setColumnList(oldChildColumnList);
			
			TableModel newSourceTableModel = (TableModel) source.getDataObject();
			newSourceTableModel.setColumnList(newChildColumnList);
			
			oldSourceTableModel.getLineModelList().add(lineModel);
			newSourceTableModel.getLineModelList().remove(lineModel);
			
			AbstractGefModel model = connection.getSource();
			connection.setSource(oldModel);
			connection.attachSource();// 连接开始节点
			model.removeSourceConnection(connection);
			
			// 刷新Editor
//			oldModel.setDataObject(oldModel.getDataObject());
//			source.setDataObject(source.getDataObject());
			
		} else if(flag == END) {
			TableModel sourceTableModel = (TableModel) source.getDataObject();
			sourceTableModel.setColumnList(oldChildColumnList);
			
//			lineModel.setConstraintName(oldConstraintName);
//			lineModel.setIncidenceRelation(oldIncidenceRelation);
			
			TableModel oldTargetTableModel = null; 
			TableShortcutModel oldTargetTableShortcutModel = null;
			if(oldModel.getDataObject() instanceof TableModel) {
				oldTargetTableModel = (TableModel)oldModel.getDataObject();
			} else if(oldModel.getDataObject() instanceof TableShortcutModel) {
				oldTargetTableShortcutModel = (TableShortcutModel)oldModel.getDataObject();
			}
			
			// 更新LineModel中的父表的ID
			if(oldTargetTableModel != null) {
				lineModel.setParentTableModelId(oldTargetTableModel.getId());
			} else if(oldTargetTableShortcutModel != null) {
				lineModel.setParentTableModelId(oldTargetTableShortcutModel.getId());
			}
			
			AbstractGefModel model = connection.getTarget();
			connection.setTarget(oldModel);
			connection.attachTarget();// 连接结束节点
			model.removeTargetConnection(connection);
			
			
		}
		
		// 刷新界面
		DatabaseDiagramModelEditPart.refreshEditor();
	}
	
	/**
	 * 设置连接线对象
	 * @param  Object 连接线对象
	 */
	public void setConnectionModel(Object model) {
		logger.info("void setConnectionModel(Object model) start...");
		
		connection = (AbstractLineGefModel) model;
		
		if(flag == START) {
			oldModel = connection.getSource();
			target = connection.getTarget();
			
			return ;
		} 
		
		if(flag == END) {
			oldModel = connection.getTarget();
			source = connection.getSource();
			
			return ;
		}
	}

	public void setNewSource(Object model) {
		logger.info("void setNewSource(Object model) start...");
		
		// 设置标记
		flag = START;
		
		source = (AbstractGefModel) model;
	}

	public void setNewTarget(Object model) {
		logger.info("void setNewTarget(Object model) start...");
	
		// 设置标记
		flag = END;
		
		target = (AbstractGefModel) model;
	}


	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	
}

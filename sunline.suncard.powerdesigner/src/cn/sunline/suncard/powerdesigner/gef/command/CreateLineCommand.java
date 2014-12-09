/* 文件名：     CreateLineCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	创建连接线的Command
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.gef.editpart.DatabaseDiagramModelEditPart;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractLineGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractListenerGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.DatabaseDiagramGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.LineGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableShortcutGefModel;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.gef.xml.GefFigureSwitchXml;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.JoinModel;
import cn.sunline.suncard.powerdesigner.model.KeyToKeyModel;
import cn.sunline.suncard.powerdesigner.model.LineModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;


/**
 * 创建连接线的Command
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */
public class CreateLineCommand extends Command {
	private AbstractGefModel source;
	private AbstractGefModel target;
	private AbstractLineGefModel connection;
	
//	private String oldConstraintName;
	private List<ColumnModel> oldChildColumnList;
	
	private Log logger = LogManager.getLogger(CreateLineCommand.class
			.getName());


	public CreateLineCommand() {
		IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(part instanceof DatabaseDiagramEditor) {
			DatabaseDiagramEditor diagramPart = (DatabaseDiagramEditor) part;
		}
	}

	// 首先判断是否能执行连接
	@Override
	public boolean canExecute() {
		if (source == null || target == null) {
			return false;
		}
		
		if (source.equals(target)) {
			return false;
		}
		
		if(!(target instanceof TableGefModel || target instanceof TableShortcutGefModel) 
				|| !(source instanceof TableGefModel)) {
			return false;
		}
		
		// 两个图形之间只能画一条连接线
		if(target instanceof TableGefModel) {
			List<AbstractLineGefModel> lineGefModels = target.getSourceConnections();
			for(AbstractLineGefModel abstractLineGefModel : lineGefModels) {
				if(abstractLineGefModel.getTarget().equals(target)) {
					return false;
				}
			}
		}
		
		IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(part instanceof DatabaseDiagramEditor) {
			DatabaseDiagramEditor diagramPart = (DatabaseDiagramEditor) part;
			GefFigureSwitchXml.initLineProperty(connection, diagramPart
					.getPhysicalDiagramModel());
			
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void execute() {
		LineModel lineModel = (LineModel) connection.getDataObject();
		if(lineModel == null) {
			logger.error("连接线上的LineModel为空！无法在创建时继续初始化信息！");
			return ;
		}
		
		// 执行的时候分两步。连接起点和终点
		connection.attachSource();
		connection.attachTarget();
		
		TableModel sourceTableModel = (TableModel) source.getDataObject();
		TableModel targetTableModel = null;
		TableShortcutModel targetShortcutTableModel = null;
		
		String constraintName = "";
		if(target instanceof TableGefModel) {
			targetTableModel = (TableModel) target.getDataObject();	
			// 设置约束名称
			constraintName = new String("PK_" + sourceTableModel.getTableName() + 
					"_" + targetTableModel.getTableName());
		} else if(target instanceof TableShortcutGefModel) {
			targetShortcutTableModel = (TableShortcutModel) target.getDataObject();	
			// 设置约束名称
			constraintName = new String("PK_" + sourceTableModel.getTableName() + 
					"_" + targetShortcutTableModel.getTargetTableModel().getTableName());
		}
		// 创建唯一的约束名称
		Set<String> usedConstraintNameSet = FileModel.getAllConstraintStr(FileModel.getFileModelFromObj(sourceTableModel));
		if(usedConstraintNameSet.contains(constraintName)) {
			for(int i = 1; ; i++) {
				if(!usedConstraintNameSet.contains(constraintName + i)) {
					constraintName = constraintName + i;
					break ;
				}
			}
		}
		lineModel.setConstraintName(constraintName);
		
		
		// 设置关联关系，默认为多对多
		lineModel.setIncidenceRelation(DmConstants.MANY_TO_MANY);
		
		// 设置目标表格的id
		if(targetTableModel != null) {
			lineModel.setParentTableModelId(targetTableModel.getId());
		} else if(targetShortcutTableModel != null) {
			lineModel.setParentTableModelId(targetShortcutTableModel.getId());
		}
		
		// 设置name和desc
		List<String> usedNameList = new ArrayList<String>(); // 用来储存当前Editor中连接线已经用过的引用名字
		AbstractListenerGefModel parentGefModel = source.getParentGefModel();
		if(parentGefModel instanceof DatabaseDiagramGefModel) {
			DatabaseDiagramGefModel databaseDiagramGefModel = (DatabaseDiagramGefModel) parentGefModel;
			List<AbstractGefModel> childList = databaseDiagramGefModel.getChildren();
			for(AbstractGefModel gefModel : childList) {
				List<AbstractLineGefModel> lineGefModelList = gefModel.getSourceConnections();
				for(AbstractLineGefModel abstractLineGefModel : lineGefModelList) {
					if(abstractLineGefModel instanceof LineGefModel) {
						LineGefModel lineGefModel = (LineGefModel) abstractLineGefModel;
						usedNameList.add(lineGefModel.getDataObject().getName());
					}
				}
			}
		}
		// 设置新的引用名称和引用描述
		for(int startIndex = usedNameList.size() + 1; ; startIndex++) {
			if(!usedNameList.contains(DmConstants.REFERENCE_NAME_PREFIX + startIndex)) {
				lineModel.setName(DmConstants.REFERENCE_NAME_PREFIX + startIndex);
				lineModel.setDesc(DmConstants.REFERENCE_DESC_PREFIX + startIndex);
				break ;
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
		
		// 设置关联字段的新值
		try {
			if(targetTableModel != null) {
				createRelation(targetTableModel, sourceTableModel);
			} else if(targetShortcutTableModel != null) {
				createRelation(targetShortcutTableModel.getTargetTableModel(), sourceTableModel);
			}
			// 更新GEF子表的图形界面
			source.setDataObject(source.getDataObject());
		} catch (CloneNotSupportedException e) {
			logger.error("克隆ColumnModel失败！" + e.getMessage());
			e.printStackTrace();
		}
		
		// 把该LineModel添加到子表中
		sourceTableModel.getLineModelList().add(lineModel);
		
		// 更新GEF界面
		DatabaseDiagramModelEditPart.refreshEditor();
	}
	
	@Override
	public void undo() {
		connection.detachSource();
		connection.detachTarget();
		
		LineModel lineModel = (LineModel) connection.getDataObject();
		if(lineModel == null) {
			logger.error("连接线上的LineModel为空！无法在创建时继续初始化信息！");
			return ;
		}
		
		TableModel sourceTableModel = (TableModel) source.getDataObject();
		sourceTableModel.setColumnList(oldChildColumnList);
		sourceTableModel.getLineModelList().remove(lineModel);
		
		// 更新GEF界面
		DatabaseDiagramModelEditPart.refreshEditor();
	}
	
	/**
	 * 根据两个表来建立约束的字段
	 * @throws CloneNotSupportedException 
	 */
	public static void createRelation(TableModel parentTableModel, TableModel childTableModel) throws CloneNotSupportedException {
		if(parentTableModel == null || childTableModel == null || parentTableModel.getColumnList() == 
				null || childTableModel.getColumnList() == null ) {
			return ;
		}
		
		List<ColumnModel> parentColumnList = parentTableModel.getColumnList();
		List<ColumnModel> childColumnList = childTableModel.getColumnList();
		
		
		// 把子表的列模型放入Map中，便于查询
		Map<String, ColumnModel> childMap = new HashMap<String, ColumnModel>();
		for(ColumnModel columnModel : childColumnList) {
			childMap.put(columnModel.getColumnName(), columnModel);
		}
		
		// 通过父表的主键来匹配外键
		for(ColumnModel columnModel : parentColumnList) {
			if(columnModel.isPrimaryKey()) {
				String keyName = columnModel.getColumnName();
				ColumnModel childColumnModel = childMap.get(keyName);
				// 如果从子表中找不到对应的列，则需要在子表新建一个列来作为外键
				if(childColumnModel == null ) {
					ColumnModel newColumnModel = columnModel.clone();
					newColumnModel.setTableModel(childTableModel);
					newColumnModel.setPrimaryKey(false);	
					newColumnModel.setParentTableColumnId(columnModel.getId());
					childTableModel.getColumnList().add(newColumnModel);
					
					// 把新增的列放入列工厂中
					ColumnModelFactory.addColumnModel(FileModel
							.getFileModelFromObj(columnModel), newColumnModel);
					
				// 子表中有对应的列，但数据类型不同，或者即使数据类型相同，但已经是别的表的外键了，也算匹配失败
				} else if(childColumnModel != null && (!(childColumnModel.getDataTypeModel().
						equals(columnModel.getDataTypeModel())) || (childColumnModel.getDataTypeModel().
								equals(columnModel.getDataTypeModel()) && childColumnModel.isForeignKey()))) {
					ColumnModel newColumnModel = columnModel.clone();
					newColumnModel.setTableModel(childTableModel);
					newColumnModel.setPrimaryKey(false);	
					newColumnModel.setParentTableColumnId(columnModel.getId());
					
					// 设置新的名字和描述
					newColumnModel.setColumnDesc(parentTableModel.getTableDesc() + "_" + 
							newColumnModel.getColumnDesc());
					// 看子表中是否存在该名字的列
					String newName = "PK_" + newColumnModel.getColumnName();
					List<ColumnModel> columnModels = childTableModel.getColumnList();
					List<String> columnNameList = new ArrayList<String>(); // 储存子表所有列的名称
					for(ColumnModel tempColumnModel : columnModels) {
						columnNameList.add(tempColumnModel.getColumnName());
					}
					if(columnNameList.contains(newName)) {
						for(int i = 2; ; i++) {
							if(!columnNameList.contains(newName + i)) {
								newColumnModel.setColumnName(newName + i);
								break ;
							}
						}
					} else {
						newColumnModel.setColumnName(newName);
					}
					
					childTableModel.getColumnList().add(newColumnModel);
					
					// 把新增的列放入列工厂中
					ColumnModelFactory.addColumnModel(FileModel
							.getFileModelFromObj(columnModel), newColumnModel);
					
				} else {
					childColumnModel.setParentTableColumnId(columnModel.getId());
				}
				
			}
		}
	}

	public void setConnection(Object model) {
		connection = (AbstractLineGefModel) model;
	}

	public void setSource(Object model) {
		source = (AbstractGefModel) model;
		connection.setSource(source);
	}

	public void setTarget(Object model) {
		target = (AbstractGefModel) model;
		connection.setTarget(target);
	}


}

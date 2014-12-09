/* 文件名：     UpdateReferenceDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-10-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.powerdesigner.gef.command.DeleteLineCommand;
import cn.sunline.suncard.powerdesigner.gef.editpart.DatabaseDiagramModelEditPart;
import cn.sunline.suncard.powerdesigner.gef.model.LineGefModel;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.KeyToKeyModel;
import cn.sunline.suncard.powerdesigner.model.LineModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 连接线弹出对话框更新的Command
 * 
 * @author Manzhizhen
 * @version 1.0, 2012-10-26
 * @see
 * @since 1.0
 */
public class UpdateReferenceCommand extends Command {
	private String oldName;
	private String newName;
	private String oldDesc;
	private String newDesc;
	private String oldNote;
	private String newNote;
	private String oldConstraintName;
	private String newConstraintName;
	private String oldRelation;
	private String newRelation;
	private List<ColumnModel> oldColumnModelList; // 用来储存修改之前该子表中对应该父表的外键
	private List<KeyToKeyModel> newKeyList;

	private LineModel lineModel;
	private TableModel childTableModel;
	private TableModel parentTableModel;
	private TableShortcutModel parentTableShortcutModel;
	private LineGefModel lineGefModel;

	private Log logger = LogManager.getLogger(UpdateReferenceCommand.class
			.getName());
	
	@Override
	public boolean canExecute() {
		return lineModel != null;
	}
	
	@Override
	public void execute() {
		oldName = lineModel.getName();
		oldDesc = lineModel.getDesc();
		oldNote = lineModel.getNote();
		oldConstraintName = lineModel.getConstraintName();
		oldRelation = lineModel.getIncidenceRelation();
		oldColumnModelList = new ArrayList<ColumnModel>();
		List<ColumnModel> childColumnModelList = null;
		try {
			oldColumnModelList = childTableModel.backUpTableColumnList();
			childColumnModelList = childTableModel.getColumnList();
		} catch (CloneNotSupportedException e) {
			logger.error("克隆ColumnModel失败！");
			e.printStackTrace();
		}	
		
		lineModel.setName(newName);
		lineModel.setDesc(newDesc);
		lineModel.setNote(newNote);
		lineModel.setConstraintName(newConstraintName);
		lineModel.setIncidenceRelation(newRelation);
		
		List<String> modifyChildColumnModelList = new ArrayList<String>();
		List<ColumnModel> parentColumnModelList =  new ArrayList<ColumnModel>();
		for(KeyToKeyModel keyModel : newKeyList) {
			if(keyModel.getForeginColumnModel() != null) {
				parentColumnModelList.add(keyModel.getPrimaryColumnModel());
				modifyChildColumnModelList.add(keyModel.getForeginColumnModel().getId());
			}
		}
		
		List<ColumnModel> needDelModelList =  new ArrayList<ColumnModel>();
		for(ColumnModel columnModel : childColumnModelList) {
			if(columnModel.isForeignKey()) {
				ColumnModel parentTableColumn = ColumnModelFactory.getColumnModel(FileModel.
						getFileModelFromObj(columnModel), columnModel.getParentTableColumnId());
				
				// 如果子表中该列以前是该父表的外键
				TableModel parentModel = null;
				if(parentTableModel != null) {
					parentModel = parentTableModel;
				} else if(parentTableShortcutModel != null) {
					parentModel = parentTableShortcutModel.getTargetTableModel();
				}
				if(parentTableColumn != null && parentTableColumn.getTableModel().equals(parentModel)) {
					// 现在却不是了，而且该键不是主键，则需要删除
					if(!modifyChildColumnModelList.contains(columnModel.getId()) && !columnModel.isPrimaryKey()) {
						needDelModelList.add(columnModel);
						
					// 现在却不是了，而且该键是主键，则需要删除外键属性
					} else if(!modifyChildColumnModelList.contains(columnModel.getId()) && columnModel.isPrimaryKey()){
						columnModel.setParentTableColumnId(null);
					}
					
				}
			} else if(modifyChildColumnModelList.contains(columnModel.getId())) {
				columnModel.setParentTableColumnId(parentColumnModelList.get(modifyChildColumnModelList.
						indexOf(columnModel.getId())).getId());
				
			}
		}
		childColumnModelList.removeAll(needDelModelList);
		
		// 更新界面
		DatabaseDiagramModelEditPart.refreshEditor();
		
		super.execute();
	}
	
	@Override
	public void undo() {
		lineModel.setName(oldName);
		lineModel.setDesc(oldDesc);
		lineModel.setNote(oldNote);
		lineModel.setConstraintName(oldConstraintName);
		lineModel.setIncidenceRelation(oldRelation);
		childTableModel.setColumnList(oldColumnModelList);
		
		// 更新界面
		DatabaseDiagramModelEditPart.refreshEditor();
		
		super.undo();
	}
	
	/**
	 * 根据传入的KeyToKeyModelList来改变TableModel的外键信息
	 * @param lineModel
	 */
	public void updateTableModelForeginKey(TableModel childTableModel, List<KeyToKeyModel> keyList) {
		
	}
	
	public void setLineModel(LineModel lineModel) {
		this.lineModel = lineModel;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public void setNewDesc(String newDesc) {
		this.newDesc = newDesc;
	}

	public void setNewNote(String newNote) {
		this.newNote = newNote;
	}

	public void setNewConstraintName(String newConstraintName) {
		this.newConstraintName = newConstraintName;
	}

	public void setNewRelation(String newRelation) {
		this.newRelation = newRelation;
	}

	public void setNewKeyList(List<KeyToKeyModel> newKeyList) {
		this.newKeyList = newKeyList;
	}

	public void setChildTableModel(TableModel childTableModel) {
		this.childTableModel = childTableModel;
	}

	public void setParentTableModel(TableModel parentTableModel) {
		this.parentTableModel = parentTableModel;
	}
	
	public void setParentTableShortcutModel(
			TableShortcutModel parentTableShortcutModel) {
		this.parentTableShortcutModel = parentTableShortcutModel;
	}

	public void setLineGefModel(LineGefModel lineGefModel) {
		this.lineGefModel = lineGefModel;
	}
	
	

}

/* 文件名：     DeleteConnectionCommand.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	删除控制线的Command
 * 修改人：     易振强
 * 修改时间：2011-11-15
 * 修改内容：创     建
 */
package cn.sunline.suncard.powerdesigner.gef.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.gef.editpart.DatabaseDiagramModelEditPart;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractLineGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.DatabaseDiagramGefModel;
import cn.sunline.suncard.powerdesigner.gef.ui.dialog.ReferencePropertiesDialog;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.KeyToKeyModel;
import cn.sunline.suncard.powerdesigner.model.LineModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;


/**
 * 删除连接线的Command
 * @author  易振强
 * @version 1.0, 2012-3-26
 * @see 
 * @since 1.0
 */
public class DeleteLineCommand extends Command {
	private AbstractLineGefModel connection;
//	private DatabaseDiagramGefModel diagramGefModel;

	private List<ColumnModel> oldChildColumnList;
	
	private Log logger = LogManager.getLogger(DeleteLineCommand.class
			.getName());
	private LineModel lineModel;
	
	public DeleteLineCommand() {
		IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(part instanceof DatabaseDiagramEditor) {
			DatabaseDiagramEditor diagramEditor = (DatabaseDiagramEditor) part;
//			diagramGefModel = diagramEditor.getDatabaseDiagramGefModel();
		} 
	}
	
	@Override
	public void execute() {
		lineModel = (LineModel) connection.getDataObject();
		if(lineModel == null) {
			logger.error("连接线上的LineModel为空！无法删除该连接线！");
			return ;
		}
		
		AbstractGefModel sourceGefModel = connection.getSource();
		AbstractGefModel targetGefModel = connection.getTarget();
		
		
		// 在开始节点上删除该连接线
		connection.detachSource();
		// 在结束节点上删除该连接线
		connection.detachTarget();
		
		TableModel sourceTableModel = (TableModel) sourceGefModel.getDataObject();
		TableModel targetTableModel = (TableModel) targetGefModel.getDataObject();
		
		sourceTableModel.getLineModelList().remove(lineModel);
		
		 // 克隆子表的列的List，便于修改后还原
		List<ColumnModel> newChildColumnList;
		try {
			
			oldChildColumnList = sourceTableModel.backUpTableColumnList();
			newChildColumnList = sourceTableModel.getColumnList();
		} catch (CloneNotSupportedException e) {
			logger.error("克隆ColumnModel失败！" + e.getMessage());
			e.printStackTrace();
			
			return ;
		}
		
		// 除去相关的外键信息
		List<ColumnModel> needDelColumnList = new ArrayList<ColumnModel>();
		for(ColumnModel columnModel : newChildColumnList) {
			if(columnModel.isForeignKey()) {
				ColumnModel parentTableColulmnModel = ColumnModelFactory.getColumnModel(FileModel.
						getFileModelFromObj(columnModel), columnModel.getParentTableColumnId());
				if(parentTableColulmnModel != null) {
					if(parentTableColulmnModel.getTableModel().getId().equals(targetTableModel.getId())) {
						columnModel.setParentTableColumnId(null);
						// 如果该键仅仅是他的外键，则需要删除
						if(!columnModel.isPrimaryKey()) {
							needDelColumnList.add(columnModel);
						}
					}
					
					
				}
			}
		}
		
		newChildColumnList.removeAll(needDelColumnList);
		
		// 如果有该连接线的对话框，也需要关闭
		ReferencePropertiesDialog dialog = ReferencePropertiesDialog.getLineDialogMap()
				.get(lineModel);
		if(dialog != null) {
			dialog.close();
		}
		
		// 刷新Editor
		DatabaseDiagramModelEditPart.refreshEditor();
		
	}
	
	/**
	 *  设置连接线对象
	 * @param  Object 连接线对象
	 */
	public void setConnectionModel(Object model) {
		this.connection = (AbstractLineGefModel) model;
	}
	
	/**
	 *  撤销动作
	 */
	@Override
	public void undo() {
		if(lineModel == null) {
			logger.error("连接线上的LineModel为空！无法删除该连接线！");
			return ;
		}
		
		connection.attachSource(); 
		connection.attachTarget(); 
		
		AbstractGefModel sourceGefModel = connection.getSource();
		TableModel sourceTableModel = (TableModel) sourceGefModel.getDataObject();
		
		sourceTableModel.getLineModelList().add(lineModel);
		
		sourceTableModel.setColumnList(oldChildColumnList);
		
		// 刷新Editor
		DatabaseDiagramModelEditPart.refreshEditor();
	}

}

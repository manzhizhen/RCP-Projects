/* 文件名：     UpdateDefualtColumnCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     wzx
 * 修改时间：2012-11-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.util.List;

import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.models.DefaultColumnsNodeModel;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 更新默认列的命令
 * @author  wzx
 * @version 1.0, 2012-11-26
 * @see 
 * @since 1.0
 */
public class UpdateDefualtColumnCommand extends Command {
	private PhysicalDataModel physicalDataModel;
	private List<ColumnModel> newColumnModels;
	private DatabaseTreeViewPart databaseTreeViewPart;
	private TreeContent defaultColumnsNodeTreeContent;
	
	private Log logger = LogManager.getLogger(UpdateDefualtColumnCommand.class
			.getName());
	
	public UpdateDefualtColumnCommand() {
	}

	@Override
	public void execute() {
		if(defaultColumnsNodeTreeContent == null || !(defaultColumnsNodeTreeContent.getObj() instanceof DefaultColumnsNodeModel) 
				|| newColumnModels == null || databaseTreeViewPart == null) {
			logger.error("传入的数据不完整或为空，UpdateDefualtColumnCommand执行失败！");
			return ;
		}
		
		// 给物理数据模型附上新的默认列的值
		physicalDataModel = ((DefaultColumnsNodeModel)defaultColumnsNodeTreeContent.getObj()).getPhysicalDataModel();
		physicalDataModel.setDefaultColumnList(newColumnModels);
		
		try {
			databaseTreeViewPart.refreshDefaultColumns(defaultColumnsNodeTreeContent);
		} catch (FoundTreeNodeNotUniqueException e) {
			logger.error("更新DatabaseTreeViewPart默认集合节点失败！");
			e.printStackTrace();
		} catch (CanNotFoundNodeIDException e) {
			logger.error("更新DatabaseTreeViewPart默认集合节点失败！");
			e.printStackTrace();
		}
		
		super.execute();
	}
	
	@Override
	public boolean canUndo() {
		return false;
	}

	public void setDatabaseTreeViewPart(DatabaseTreeViewPart databaseTreeViewPart) {
		this.databaseTreeViewPart = databaseTreeViewPart;
	}

	public void setNewColumnModels(List<ColumnModel> newColumnModels) {
		this.newColumnModels = newColumnModels;
	}

	public void setDefaultColumnsNodeTreeContent(
			TreeContent defaultColumnsNodeTreeContent) {
		this.defaultColumnsNodeTreeContent = defaultColumnsNodeTreeContent;
	}
	
}

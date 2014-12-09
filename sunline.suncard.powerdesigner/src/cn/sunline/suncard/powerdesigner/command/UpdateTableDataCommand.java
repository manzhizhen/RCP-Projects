/* 文件名：     UpdateTableDataCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     wzx
 * 修改时间：2012-11-29
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.powerdesigner.model.InitTableDataModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;

/**
 * 
 * @author  wzx
 * @version 1.0, 2012-11-29
 * @see 
 * @since 1.0
 */
public class UpdateTableDataCommand extends Command {
	private TableModel tableModel;
	private InitTableDataModel newInitTableDataModel;
	
	/**
	 * @param tableModel 表格模型对象
	 * @param newTableDataList 新的表格数据
	 */
	public UpdateTableDataCommand(TableModel tableModel, InitTableDataModel newInitTableDataModel) {
		this.tableModel = tableModel;
		this.newInitTableDataModel = newInitTableDataModel;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		tableModel.setInitTableDataModel(newInitTableDataModel);
		super.execute();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	@Override
	public boolean canUndo() {
		// TODO Auto-generated method stub
		return false;
	}
}

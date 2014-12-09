/* 文件名：     TableGefModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.model;

import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.model.DataObjectInterface;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;


/**
 * 表格的Gef模型
 * @author  Manzhizhen
 * @version 1.0, 2012-9-6
 * @see 
 * @since 1.0
 */
public class TableGefModel extends AbstractGefModel{
	private TableModel tableModel;
	public static final String TABLEMODEL_CHANGE = "tablemodel_change";
	public static final String TABLE_SELECT = "table_select";
	public static final String TABLE_NONE_SELECT = "table_none_select";
	
	private Log logger = LogManager.getLogger(TableGefModel.class
			.getName());
	
	public TableGefModel() {
		tableModel = new TableModel();
	}

	@Override
	public TableModel getDataObject() {
		return tableModel;
	}

	@Override
	public void setDataObject(DataObjectInterface object) {
		this.tableModel = (TableModel) object;
		firePropertyListenerChange(TABLEMODEL_CHANGE, null, null);
	}
	
	/**
	 * 设置图元是否被选中状态
	 * @param selected
	 */
	public void setSelected(boolean selected) {
		if(selected) {
			firePropertyListenerChange(TABLE_SELECT, null, null);
		} else {
			firePropertyListenerChange(TABLE_NONE_SELECT, null, null);
		}
	}

	@Override
	public TableGefModel clone() {
		TableGefModel newTableGefModel = new TableGefModel();
		newTableGefModel.setConstraint(getConstraint().getCopy());
		newTableGefModel.setParentGefModel(getParentGefModel());
		try {
			newTableGefModel.setDataObject(tableModel.clone(true));
		} catch (CloneNotSupportedException e) {
			logger.error("克隆TableGefModel时克隆TableModel失败！" + e.getMessage());
			e.printStackTrace();
		}
		
		return newTableGefModel;
	}
}

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
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;


/**
 * 表格的Gef模型
 * @author  Manzhizhen
 * @version 1.0, 2012-9-6
 * @see 
 * @since 1.0
 */
public class TableShortcutGefModel extends AbstractGefModel{
	private TableShortcutModel tableShortcutModel;
	public static final String SHORTCUTMODEL_CHANGE = "tableshortcutmodel_change";
	public static final String SHORTCUTMODEL_SELECT = "tableshortcutmodel_select";
	public static final String SHORTCUTMODEL_NONE_SELECT = "tableshortcutmodel_none_select";
	
	private Log logger = LogManager.getLogger(TableShortcutGefModel.class
			.getName());
	
	public TableShortcutGefModel() {
		tableShortcutModel = new TableShortcutModel();
	}

	@Override
	public TableShortcutModel getDataObject() {
		return tableShortcutModel;
	}

	@Override
	public void setDataObject(DataObjectInterface object) {
		this.tableShortcutModel = (TableShortcutModel) object;
		firePropertyListenerChange(SHORTCUTMODEL_CHANGE, null, null);
	}
	
	/**
	 * 设置图元是否被选中状态
	 * @param selected
	 */
	public void setSelected(boolean selected) {
		if(selected) {
			firePropertyListenerChange(SHORTCUTMODEL_SELECT, null, null);
		} else {
			firePropertyListenerChange(SHORTCUTMODEL_NONE_SELECT, null, null);
		}
	}

	@Override
	public TableShortcutGefModel clone() {
		TableShortcutGefModel newTableShortcutGefModel = new TableShortcutGefModel();
		newTableShortcutGefModel.setConstraint(getConstraint().getCopy());
		newTableShortcutGefModel.setParentGefModel(getParentGefModel());
		try {
			newTableShortcutGefModel.setDataObject(tableShortcutModel.clone());
		} catch (CloneNotSupportedException e) {
			logger.error("克隆TableShortcutGefModel时克隆TableShortcutModel失败！" + e.getMessage());
			e.printStackTrace();
		}
		
		return newTableShortcutGefModel;
	}
}

/* 文件名：     TableDataCellModifier.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     wzx
 * 修改时间：2012-11-29
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.TableDataModel;
import cn.sunline.suncard.powerdesigner.ui.dialog.composite.TableDataComposite;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 
 * @author  wzx
 * @version 1.0, 2012-11-29
 * @see 
 * @since 1.0
 */
public class TableDataCellModifier implements ICellModifier {
	private Log logger = LogManager.getLogger(TableDataCellModifier.class.getName());
	
	private TableViewer tableViewer;
	private TableDataComposite tableDataComposite;
	private List<ColumnModel> tableColumnNames;
	
	/**
	 * @param tableViewer 表格控件
	 * @param tableDataComposite 表格编辑界面
	 * @param tableColumnNames 表格字段名称列表
	 */
	public TableDataCellModifier(TableViewer tableViewer, 
			TableDataComposite tableDataComposite, 
			List<ColumnModel> tableColumnNames) {
		this.tableViewer = tableViewer;
		this.tableDataComposite = tableDataComposite;
		this.tableColumnNames = tableColumnNames;
	}

	@Override
	public boolean canModify(Object element, String property) {
		if (TableDataComposite.COLUMN_INDEX.equalsIgnoreCase(property)) {
			return false;
		}
		return true;
	}

	@Override
	public Object getValue(Object element, String property) {
		TableDataModel lineData = (TableDataModel) element;
		Set<ColumnModel> columnModelSet = lineData.getDataMap().keySet();
		for(ColumnModel columnModel : columnModelSet) {
			if(property.equals(columnModel.getColumnName())) {
				return lineData.getDataMap().get(columnModel);
			}
		}
		
		if(TableDataComposite.OPERA_ATTRIBUTE.equals(property)) {
			if(lineData.isCanModify()) {
				return 0;
			} else {
				return 1;
			}
		}
		
		return "";
	}
	
	@Override
	public void modify(Object element, String property, Object value) {
		TableItem tableItem = (TableItem) element;
		TableDataModel tableDataModel = (TableDataModel) tableItem.getData();
		
		if("@OPERA_ATTRIBUTE@".equals(property)) {
			int select = (Integer)value;
			if(select == 0) {
				tableDataModel.setCanModify(true);
			} else {
				tableDataModel.setCanModify(false);
			}
			
		} else {
			String dataValue = (String) value;
			for(ColumnModel columnModel : tableColumnNames) {
				if(property.equals(columnModel.getColumnName())) {
					tableDataModel.getDataMap().put(columnModel, dataValue);
				}
			}
		}
		
		tableViewer.refresh();
	}

}

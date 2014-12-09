package cn.sunline.suncard.powerdesigner.gef.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.KeyToKeyModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.db.DataTypeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 列属性单元格的Modify
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */
public class ColumnCellModify implements ICellModifier {

	private Log logger = LogManager.getLogger(ColumnCellModify.class
			.getName());
	
	private TableViewer tableViewer;
	private List<ColumnModel> childColumnList; // 子表的ColumnList的克隆版本，注意，里面的ColumnModel并没有克隆
	
	public ColumnCellModify(TableViewer tableViewer, List<ColumnModel> childColumnList) {
		this.tableViewer = tableViewer;
		this.childColumnList = childColumnList;
		
	}
	
	@Override
	public boolean canModify(Object element, String property) {
		if("parent".equalsIgnoreCase(property)) {
			return false;
		}
		
		return true;
	}

	@Override
	public Object getValue(Object element, String property) {
		if(element instanceof KeyToKeyModel) {
			KeyToKeyModel model = (KeyToKeyModel) element;
			
			if("parent".equals(property)) {
				return model.getPrimaryColumnModel() == null ? "" : model.getPrimaryColumnModel().getColumnDesc();
			
			} else if("child".equals(property)) {
				if(model.getForeginColumnModel() == null) {
					return new Integer(0);
				} else {
					int index = childColumnList.indexOf(model.getForeginColumnModel());
					if(index < 0) {
						return new Integer(0);
					} else {
						return new Integer(index + 1);
					}
				}
			}
		}
		
		logger.error("ColumnCellModify获取值失败！");
		
		return "";
	}

	@Override
	public void modify(Object element, String property, Object value) {
		TableItem tableItem = (TableItem) element;
		Object obj = tableItem.getData();
		
		if(obj instanceof KeyToKeyModel) {
			KeyToKeyModel model = (KeyToKeyModel) obj;
			
			if("child".equals(property)) {
				Integer index = (Integer) value;
				if(index != null && index > 0) {
					ColumnModel columnModel = childColumnList.get(index - 1);
					model.setForeginColumnModel(columnModel);
				} else {
					model.setForeginColumnModel(null);
				}
			}
		}
		
		tableViewer.refresh();
	}

	
	
}

package cn.sunline.suncard.powerdesigner.gef.ui.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.db.DataTypeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.ui.dialog.composite.ColumnModelComposite;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */
public class DataTypeCellModify implements ICellModifier {

	private Log logger = LogManager.getLogger(DataTypeCellModify.class
			.getName());
	
	private TableViewer tableViewer;
	private ColumnModelComposite columnModelComposite;
	
	public DataTypeCellModify(TableViewer tableViewer, ColumnModelComposite columnModelComposite) {
		this.tableViewer = tableViewer;
		this.columnModelComposite = columnModelComposite;
	}
	
	@Override
	public boolean canModify(Object element, String property) {
		if("num".equalsIgnoreCase(property) || "f".equalsIgnoreCase(property)) {
			return false;
		}
		
		return true;
	}

	@Override
	public Object getValue(Object element, String property) {
		if(element instanceof ColumnModel) {
			ColumnModel model = (ColumnModel) element;
			
			if("num".equals(property)) {
				if(!columnModelComposite.getColumnModelList().isEmpty() 
						&& columnModelComposite.getColumnModelList().indexOf(model) >= 0) {
					return columnModelComposite.getColumnModelList().indexOf(model) + 1 + "";
				} else {
					return "";
				}
			
			} else if("name".equals(property)) {
				return model.getColumnName() == null ? "" :  model.getColumnName();
				
			} else if("desc".equals(property)) {
				return model.getColumnDesc() == null ? "" :  model.getColumnDesc();
				
			} else if("type".equals(property)) {
				DataTypeModel typeModel = model.getDataTypeModel();
				int index = columnModelComposite.getDataTypeList().indexOf(typeModel);
				if(index < 0 ) {
					return new Integer(0);
				} else {
					return new Integer(index);
				}
				
			} else if("length".equals(property)) {
				// 如果不包含"%"，说明该数据类型不需要参数
				if(!model.getDataTypeModel().getName().contains(DmConstants.DATA_TYPE_PAR)) {
					return "";
				} else {
					return model.getDataTypeModel().getLength() == -1 ? "" 
							:  model.getDataTypeModel().getLength() + "";
				}
				
				
			} else if("precision".equals(property)) {
				// 如果不包含","，说明该数据类型只有长度这个参数，而没有精度这个参数
				if(!model.getDataTypeModel().getName().contains(",")) {
					return "";
				} else {
					return model.getDataTypeModel().getPrecision() == -1 ? "" 
							:  model.getDataTypeModel().getPrecision() + "";
				}
				
			} else if("p".equals(property)) {
				return model.isPrimaryKey();
				
			} else if("f".equals(property)) {
				return model.isForeignKey();
				
			} else if("m".equals(property)) {
				return model.isCanNotNull();
				
			}
		}
		
		return null;
	}

	@Override
	public void modify(Object element, String property, Object value) {
		TableItem tableItem = (TableItem) element;
		Object obj = tableItem.getData();
		
		if(obj instanceof ColumnModel) {
			ColumnModel model = (ColumnModel) obj;
			TableModel tableModel = model.getTableModel();
			
			if("name".equals(property)) {
				String str = (String) value;
				if(str != null && !"".equals(str.trim())) {
					List<ColumnModel> columnModels = tableModel.getColumnList();
					for(ColumnModel columnModel : columnModels) {
						if(!model.equals(columnModel)) {
							if(columnModel.getColumnName().equals(str)) {
								MessageDialog.openWarning(PlatformUI.getWorkbench().
										getActiveWorkbenchWindow().getShell(), I18nUtil.getMessage("MESSAGE"), 
										"列名不能重复！");
								return ;
							}
						}
					}
					
					
					model.setColumnName(str);
				}
				
			} else if("desc".equals(property)) {
				String str = (String) value;
				if(str != null && !"".equals(str.trim())) {
					model.setColumnDesc(str);
				}
				
			} else if("type".equals(property)) {
				if(!isCanModify()) {
					return ;
				}
				
				Integer index = (Integer) value;
				if(index != null && index >= 0) {
					try {
						DataTypeModel typeModel = columnModelComposite.getDataTypeList().get(index).clone();
						model.setDataTypeModel(typeModel);
					} catch (CloneNotSupportedException e) {
						MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
								I18nUtil.getMessage("MESSAGE"), "克隆DataTypeModel失败！");
						e.printStackTrace();
					}
				}
				
			} else if("length".equals(property)) {
				if(!isCanModify()) {
					return ;
				}
				
				String str = (String) value;
				if(str != null && !"".equals(str.trim())) {
					// 如果包含"%"，说明该数据类型有长度这个参数
					if(model.getDataTypeModel().getName().contains("%")) {
						try {
							int selectIndex = Integer.valueOf(str);
							if(selectIndex < 0) {
								MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
										I18nUtil.getMessage("MESSAGE"), "输入的数据必须是非负的！");
							} else {
								model.getDataTypeModel().setLength(selectIndex);
							}
							
						} catch (NumberFormatException e) {
							MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
									I18nUtil.getMessage("MESSAGE"), "输入的数据非法！");
							e.printStackTrace();
						}
					}
					
					
				}
				
			} else if("precision".equals(property)) {
				if(!isCanModify()) {
					return ;
				}
				
				String str = (String) value;
				if(str != null && !"".equals(str.trim())) {
					// 如果包含","，说明该数据类型有精度这个参数
					if(model.getDataTypeModel().getName().contains(",")) {
						model.getDataTypeModel().setPrecision(Integer.valueOf(str));
					}
				}
				
			} else if("p".equals(property)) {
				model.setPrimaryKey((Boolean) value);
				
			} else if("f".equals(property)) {
				
			} else if("m".equals(property)) {
				model.setCanNotNull((Boolean) value);
				
			}
		}
		
		tableViewer.refresh();
	}

	/**
	 * 如果该列字段引用了公共字段，则不能修改数据
	 * @return
	 */
	public boolean isCanModify() {
		IStructuredSelection select = (IStructuredSelection) tableViewer.getSelection();
		if(!select.isEmpty()) {
			Object obj = select.getFirstElement();
			if(obj instanceof ColumnModel) {
				ColumnModel columnModel = (ColumnModel) select.getFirstElement();
				return !columnModel.isRefDomainColumnModel();
			}
		}
		
		return true;
	}
	
	
}

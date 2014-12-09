/**
 * 文件名：ImportDefaultColumnModifier.java
 * 版权：
 * 描述：
 * 修改人： Manzhizhen
 * 修改时间：2013-4-16
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ICellModifier;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;

/**
 * 导入默认列或Domains的表格树的Modifer
 * @author Manzhizhen
 * @version 1.0 2013-4-16
 * @see
 * @since 1.0
 */
public class ImportDefaultColumnModifier implements ICellModifier {
	private List<ColumnModel> importColumnModel = new ArrayList<ColumnModel>();
	private List<String> importColumnStrList = new ArrayList<String>();

	@Override
	public boolean canModify(Object element, String property) {
		if("defaultcolumn".equals(property)) {
			return true;
		}
		
		return false;
	}

	@Override
	public Object getValue(Object element, String property) {
		if(element instanceof ColumnModel) {
			ColumnModel model = (ColumnModel) element;
			
			if("column".equals(property)) {
				return model.getColumnName();
			
			} else if("columndesc".equals(property)) {
				return model.getColumnDesc();
				
			} else if("defaultcolumn".equals(property)) {
				if(model.isUsedDomain()) {
					ColumnModel domainsColumnModel = ColumnModelFactory.getColumnModel(FileModel
							.getFileModelFromObj(model), model.getDomainId());
					int index = importColumnModel.indexOf(domainsColumnModel);
					if(index >= 0) {
						return importColumnStrList.get(index);
					}
				}
			}
			
		} else if(element instanceof PackageModel) {
			PackageModel model = (PackageModel) element;
			return model.getName();
			
		} else if(element instanceof PhysicalDiagramModel) {
			PhysicalDiagramModel model = (PhysicalDiagramModel) element;
			return model.getName();
		}
		
		
		return null;
	}

	@Override
	public void modify(Object element, String property, Object value) {
		
	}

	public void setImportColumnModel(List<ColumnModel> importColumnModel) {
		this.importColumnModel = importColumnModel;
	}
	
	

}

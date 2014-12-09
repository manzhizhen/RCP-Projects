/**
 * 文件名：TableAndColumnShowLabelProvider.java
 * 版权：
 * 描述：
 * 修改人： Manzhizhen
 * 修改时间：2013-4-10
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;

/**
 * 用于展现表格和列信息的标签提供者
 * @author Manzhizhen
 * @version 1.0 2013-4-10
 * @see
 * @since 1.0
 */
public class TableAndColumnShowLabelProvider  extends LabelProvider {
	@Override
	public Image getImage(Object element) {
		if(element instanceof TreeContent) {
			return ((TreeContent)element).getImage();
		}
		
		return super.getImage(element);
	}
	
	@Override
	public String getText(Object element) {
		if(element instanceof TreeContent) {
			TreeContent treeContent = (TreeContent) element;
			Object obj = treeContent.getObj();
			
			if(obj instanceof TableModel) {
				return ((TableModel)obj).getTableName() + " - " 
						+ ((TableModel)obj).getTableDesc();
				
			} else if(obj instanceof ColumnModel) {
				return ((ColumnModel)obj).getColumnName() + " - " 
						+ ((ColumnModel)obj).getColumnDesc();
				
			} else if(obj instanceof PackageModel) {
				return ((PackageModel)obj).getName();
				
			} else if(obj instanceof PhysicalDiagramModel) {
				return ((PhysicalDiagramModel)obj).getName();
				
			}
		}
		
		
		return super.getText(element);
	}
}

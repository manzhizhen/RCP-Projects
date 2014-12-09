/* 文件名：     SynProjectModelLabelProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-9
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.powerdesigner.manager.CompareObjectManager;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.CompareObjectModel;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.resource.CacheImage;

/**
 * 同步项目模型时树上用到的标签提供者
 * @author  Manzhizhen
 * @version 1.0, 2013-1-9
 * @see 
 * @since 1.0
 */
public class SynProjectModelLabelProvider extends LabelProvider{
	@Override
	public Image getImage(Object element) {
		if(element instanceof TreeContent) {
			TreeContent treeContent = (TreeContent) element;
			Object obj = treeContent.getObj();
			if(obj instanceof CompareObjectModel) {
				CompareObjectModel compareObjectModel = (CompareObjectModel)obj;
				Object leftObject = compareObjectModel.getLeftObject();
			
				if(leftObject instanceof ModuleModel) {
					return CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, 
							IDmImageKey.MODULE_LABEL_16);
				} else if(leftObject instanceof TableModel) {
					return CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, 
							IDmImageKey.TABLE_16);
				} else if(leftObject instanceof ColumnModel) {
					return CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID, 
							IDmImageKey.COLUMN_ITEM_16);
				}
				
			}
		}
		
		return super.getImage(element);
	}
	
	@Override
	public String getText(Object element) {
		if(element instanceof TreeContent) {
			TreeContent treeContent = (TreeContent) element;
			Object obj = treeContent.getObj();
			if(obj instanceof CompareObjectModel) {
				CompareObjectModel compareObjectModel = (CompareObjectModel)obj;
				Object leftObject = compareObjectModel.getLeftObject();
				Object rightObject = compareObjectModel.getRightObject();
				
				String label = "";
				String flag = compareObjectModel.getCompareFlag();
				if(CompareObjectManager.COMPARE_MODIFY.equals(flag)) {
					label = "【修改】";
				} else if (CompareObjectManager.COMPARE_SAME.equals(flag)) {
					label = "【相同】";
				} else if (CompareObjectManager.COMPARE_REMOVE.equals(flag)) {
					label = "【移除】";
				} else if (CompareObjectManager.COMPARE_ADD.equals(flag)) {
					label = "【新增】";
				}
				
				if(leftObject instanceof ModuleModel) {
					label += ((ModuleModel)leftObject).getName();
					
				} else if(leftObject instanceof TableModel) {
					label += ((TableModel)leftObject).getTableName() +  "(" + ((TableModel)leftObject).getTableDesc() + ")";
					
				} else if(leftObject instanceof ColumnModel) {
					label += ((ColumnModel)leftObject).getColumnName() +  "(" + ((ColumnModel)leftObject).getColumnDesc() + ")" ;
					
				}
				
				if(rightObject instanceof ModuleModel) {
					label += " - " + ((ModuleModel)rightObject).getName();
					
				} else if(rightObject instanceof TableModel) {
					label += " - " + ((TableModel)rightObject).getTableName() +  "(" + ((TableModel)rightObject).getTableDesc() + ")";
					
				} else if(rightObject instanceof ColumnModel) {
					label += " - " + ((ColumnModel)rightObject).getColumnName() +  "(" + ((ColumnModel)rightObject).getColumnDesc() + ")" ;
					
				}
				
				return label;
			}
			
			
			return null;
		}
		
		return null;
	}
}

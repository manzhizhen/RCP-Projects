/* 文件名：     TreeFilter.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2011-10-28
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import cn.sunline.suncard.powerdesigner.manager.ProductSpaceManager;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.LineModel;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProductSpaceModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;


/**
 * 查找对象的表格的过滤器
 * @author  易振强
 * @version 1.0, 2011-12-15
 * @see 
 * @since 1.0
 */

public class FindObjectTableFilter extends ViewerFilter{
	String nameStr;
	String descStr;
	String moduelIdStr;
	String moduleNameStr;
	private Set<ModuleModel> moduleModelSet;

	public FindObjectTableFilter(String nameStr, String descStr, String moduelIdStr, String moduleNameStr) {
		this.nameStr = nameStr;
		this.descStr = descStr;
		this.moduelIdStr = moduelIdStr;
		this.moduleNameStr = moduleNameStr;
		moduleModelSet = ProductSpaceManager.getAllModuleModels();
	}

	public void setFilter(String nameStr, String descStr, String moduelIdStr, String moduleNameStr) {
		this.nameStr = nameStr;
		this.descStr = descStr;
		this.moduelIdStr = moduelIdStr;
		this.moduleNameStr = moduleNameStr;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		boolean nameFlag = true;
		boolean descFlag = true;
		boolean modelIdFlag = true;
		boolean modelNameFlag = true;
		
		if(element instanceof ColumnModel) {
			ColumnModel model = (ColumnModel) element;
			if(!nameStr.isEmpty()) {
				nameFlag = model.getColumnName().toLowerCase().contains(nameStr
						.toLowerCase());
			}
			
			if(!descStr.isEmpty()) {
				descFlag = model.getColumnDesc().toLowerCase().contains(descStr
						.toLowerCase());
			}
			
			if(!moduelIdStr.isEmpty()) {
				modelIdFlag = false;
			}
			
			if(!moduleNameStr.isEmpty()) {
				modelNameFlag = false;
			}
			
			
			return nameFlag && descFlag && modelIdFlag && modelNameFlag;
			
		} else if(element instanceof TableModel) {
			TableModel model = (TableModel) element;
			if(!nameStr.isEmpty()) {
				nameFlag = model.getTableName().toLowerCase().contains(nameStr
						.toLowerCase());
			}
			
			if(!descStr.isEmpty()) {
				descFlag = model.getTableDesc().toLowerCase().contains(descStr
						.toLowerCase());
			}
			
			if(!moduelIdStr.isEmpty()) {
				modelIdFlag = false;
				
				for(ModuleModel moduleModel : moduleModelSet) {
					if(moduleModel.getTableModelSet().contains(model) 
							&& moduleModel.getId().toLowerCase().contains(moduelIdStr)) {
						modelIdFlag = true;
						break;
					}
				}
			}
			
			if(!moduleNameStr.isEmpty()) {
				modelNameFlag = false;
				
				for(ModuleModel moduleModel : moduleModelSet) {
					if(moduleModel.getTableModelSet().contains(model) 
							&& moduleModel.getName().toLowerCase().contains(moduleNameStr)) {
						modelNameFlag = true;
						break;
					}
				}
			}
			
			
			return nameFlag && descFlag && modelIdFlag && modelNameFlag;
			
		} else if(element instanceof LineModel) {
			LineModel model = (LineModel) element;
			if(!"".equals(nameStr)) {
				nameFlag = model.getName().toLowerCase().contains(nameStr
						.toLowerCase());
			}
			
			if(!"".equals(descStr)) {
				descFlag = model.getDesc().toLowerCase().contains(descStr
						.toLowerCase());
			}
			
			if(!"".equals(moduelIdStr)) {
				modelIdFlag = false;
			}
		} 

		return false;
	}

}

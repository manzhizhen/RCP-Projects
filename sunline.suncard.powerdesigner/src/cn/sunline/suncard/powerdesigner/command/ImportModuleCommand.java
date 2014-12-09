/* 文件名：     ImportModuleCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-3
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.models.ModulesNodeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.powerdesigner.tree.ProjectTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;

/**
 * 在项目树上导入模块的Command
 * @author  Manzhizhen
 * @version 1.0, 2013-1-3
 * @see 
 * @since 1.0
 */
public class ImportModuleCommand extends Command {
	public List<TreeContent> tableTreeContentList; // ImportModuleDialog中被选择的表格树节点
	public ProjectTreeViewPart projectTreeViewPart;
	public TreeContent modulesNodeTreeContent;
	
	private Log logger = LogManager.getLogger(ImportModuleCommand.class
			.getName());

	@Override
	public void execute() {
		if(tableTreeContentList == null || projectTreeViewPart == null || modulesNodeTreeContent == null 
				|| !(modulesNodeTreeContent.getObj() instanceof ModulesNodeModel)) {
			logger.debug("传入的数据不完整，无法执行ImportModuleCommand！");
			return ;
		}
		
		// 先构建Map，以便形成最终的树节点
		modulesNodeTreeContent.getChildrenList().clear();
		Map<String, ModuleModel> moduleMap = new HashMap<String, ModuleModel>();
		Map<ModuleModel, Set<TableModel>> tableMap = new HashMap<ModuleModel, Set<TableModel>>();
		for(TreeContent treeContent : tableTreeContentList) {
			TableModel tableModel = (TableModel) treeContent.getObj();
			ModuleModel moduleModel = (ModuleModel) treeContent.getParent().getObj();
			
			// 如果该模块在tableMap中已经存在
			if(moduleMap.keySet().contains(moduleModel.getId())) {
				// 不能往Set<TableModel>添加表名重复的表格
				Set<TableModel> tableModelSet = tableMap.get(moduleMap.get(moduleModel.getId()));
				boolean isExist = false;
				for(TableModel tempTableModel : tableModelSet) {
					if(tempTableModel.getTableName().equals(tableModel.getTableName())) {
						isExist = true;
						break ; 
					}
				}
				
				if(!isExist) {
					try {
						tableModelSet.add(tableModel.clone(true));
					} catch (CloneNotSupportedException e) {
						logger.error("克隆TableModel失败，ImportModuleCommand执行失败" + e.getMessage());
						e.printStackTrace();
					}
				}
			
			// 如果该模块在tableMap中不存在
			} else {
				try {
					ModuleModel newModuleModel = moduleModel.clone();
					moduleMap.put(newModuleModel.getId(), newModuleModel);
					
					Set<TableModel> tableModelSet = new HashSet<TableModel>();
					tableModelSet.add(tableModel.clone(true));
					tableMap.put(newModuleModel, tableModelSet);
					
				} catch (CloneNotSupportedException e) {
					logger.error("克隆ModuleModel或TableModel失败，ImportModuleCommand执行失败" + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
		// 开始构建树节点
		Set<ModuleModel> moduleModelSet = tableMap.keySet();
		try {
			for(ModuleModel moduleModel : moduleModelSet) {
				//添加模块树节点
				TreeContent moduleModelTreeContent = projectTreeViewPart.getTreeViewComposite().addNode(modulesNodeTreeContent.getId()
						, modulesNodeTreeContent.getId() + moduleModel.getId()
						, moduleModel, CacheImage.getCacheImage().getImage(DmConstants
								.APPLICATION_ID, IDmImageKey.MODULE_LABEL_16));
				
				// 在新的模块模型下面添加所有的表格模型
				moduleModel.getTableModelSet().clear();
				moduleModel.getTableModelSet().addAll(tableMap.get(moduleModel));
				Set<TableModel> tableModelSet = moduleModel.getTableModelSet();
				for(TableModel tableModel : tableModelSet) {
					//添加表格树节点
					projectTreeViewPart.getTreeViewComposite().addNode(moduleModelTreeContent.getId()
							, moduleModelTreeContent.getId() + tableModel.getId()
							, tableModel, CacheImage.getCacheImage().getImage(DmConstants
									.APPLICATION_ID, IDmImageKey.TABLE_16));
				}
			}
		} catch (FoundTreeNodeNotUniqueException e) {
			logger.error("添加模块树节点失败！" + e.getMessage());
			e.printStackTrace();
		} catch (CanNotFoundNodeIDException e) {
			logger.error("添加模块树节点失败！" + e.getMessage());
			e.printStackTrace();
		}
		
		// 将模块模型绑定到项目模型下
		ProjectModel projectModel = (ProjectModel) modulesNodeTreeContent.getParent().getObj();
		projectModel.getModuleModelSet().clear();
		projectModel.getModuleModelSet().addAll(moduleModelSet);
		for(ModuleModel moduleModel : moduleModelSet) {
			moduleModel.setProductModel(projectModel);
		}
		
		projectModel.setAlreadyImport(true);
		
		super.execute();
	}

	public void setTableTreeContentList(List<TreeContent> tableTreeContentList) {
		this.tableTreeContentList = tableTreeContentList;
	}

	public void setProjectTreeViewPart(ProjectTreeViewPart projectTreeViewPart) {
		this.projectTreeViewPart = projectTreeViewPart;
	}

	public void setModulesNodeTreeContent(TreeContent modulesNodeTreeContent) {
		this.modulesNodeTreeContent = modulesNodeTreeContent;
	}
	
	
}

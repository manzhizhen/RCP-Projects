/* 文件名：     SynchronizationProjectModelCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-9
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.util.List;
import java.util.Set;

import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.manager.CompareObjectManager;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.CompareObjectModel;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.models.ModulesNodeModel;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProjectTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 同步项目模块时调用的Command
 * 
 * @author Manzhizhen
 * @version 1.0, 2013-1-9
 * @see
 * @since 1.0
 */
public class SynchronizationProjectModelCommand extends Command {
	private ProjectModel projectModel;
	private Set<TreeContent> moduleCheckedTreeContentSet;
	private Set<TreeContent> tableCheckedTreeContentSet;
	private ProjectTreeViewPart projectTreeViewPart;

	Log logger = LogManager.getLogger(SynchronizationProjectModelCommand.class);

	@Override
	public void execute() {
		if(projectModel == null || projectTreeViewPart == null || (moduleCheckedTreeContentSet == null && tableCheckedTreeContentSet == null)) {
			logger.debug("传入的数据不完整或为空，SynchronizationProjectModelCommand执行失败！");
			return ;
		}
		
		// 先更新表格的ColumnModel
		if(tableCheckedTreeContentSet != null) {
			for(TreeContent treeContent : tableCheckedTreeContentSet) {
				CompareObjectModel compareObjectModel = (CompareObjectModel) treeContent.getObj();
				if(compareObjectModel.getLeftObject() instanceof ColumnModel) {
					String flag = compareObjectModel.getCompareFlag();
					ColumnModel leftColumnModel = (ColumnModel) compareObjectModel.getLeftObject();
					
					if(flag.equals(CompareObjectManager.COMPARE_MODIFY)) {
						ColumnModel productColumnModel = (ColumnModel) compareObjectModel
								.getRightObject();
						try {
							CompareObjectManager.updateColumnModel(leftColumnModel
									, productColumnModel);
						} catch (CloneNotSupportedException e) {
							logger.error("克隆DataTypeModel失败！" + e.getMessage());
							e.printStackTrace();
						}
						
					} else if(flag.equals(CompareObjectManager.COMPARE_REMOVE)) {
						leftColumnModel.getTableModel().getColumnList().remove(leftColumnModel);
						
					} else if(flag.equals(CompareObjectManager.COMPARE_ADD)) {
						TableModel projectTableModel = (TableModel) compareObjectModel.getRightObject();
						ColumnModel newColumnModel;
						try {
							newColumnModel = leftColumnModel.clone();
							newColumnModel.setTableModel(projectTableModel);
							projectTableModel.getColumnList().add(newColumnModel);
						} catch (CloneNotSupportedException e) {
							logger.error("克隆ColumnModel失败！" + e.getMessage());
							e.printStackTrace();
						}
					}
				}
				
			}
		}
		
		// 然后更新模块下的表格
		if(moduleCheckedTreeContentSet != null) {
			for(TreeContent treeContent : moduleCheckedTreeContentSet) {
				CompareObjectModel compareObjectModel = (CompareObjectModel) treeContent.getObj();
				if(compareObjectModel.getLeftObject() instanceof TableModel) {
					TableModel leftTableModel = (TableModel) compareObjectModel.getLeftObject();
					
					String flag = compareObjectModel.getCompareFlag();
					
					if(CompareObjectManager.COMPARE_ADD.equals(flag)) {
						ModuleModel projectModuleModel = (ModuleModel) compareObjectModel.getRightObject();
						try {
							TableModel newTableModel = leftTableModel.clone(false);
							projectModuleModel.getTableModelSet().add(newTableModel);
							
						} catch (CloneNotSupportedException e) {
							logger.error("克隆TableModel失败！" + e.getMessage());
							e.printStackTrace();
						}
						
					} else if(CompareObjectManager.COMPARE_REMOVE.equals(flag)) {
						ModuleModel projectModuleModel = (ModuleModel) compareObjectModel.getRightObject();
						projectModuleModel.getTableModelSet().remove(leftTableModel);
					}
				}
			}
		}
		
		// 刷新项目文件的树结构
		TreeContent projectModelTreeContent = projectTreeViewPart.getProjectTreeContentFromId(projectModel.getId());
		if(projectModelTreeContent == null) {
			logger.error("更新项目树失败，SynchronizationProjectModelCommand不成功！");
		} else {
			for(TreeContent treeContent : projectModelTreeContent.getChildrenList()) {
				if(treeContent.getObj() instanceof ModulesNodeModel) {
					// 先删除功能模块节点下面的所有树节点
					treeContent.getChildrenList().clear();
					// 在功能模块节点下添加模块节点
					Set<ModuleModel> moduleModelSet = projectModel.getModuleModelSet();
					for(ModuleModel moduleModel : moduleModelSet) {
						try {
							ProductTreeViewPart.addModuleModel(moduleModel, treeContent, projectTreeViewPart.getTreeViewComposite());
						} catch (CanNotFoundNodeIDException e) {
							logger.error("更新项目树失败，SynchronizationProjectModelCommand不成功！");
							e.printStackTrace();
						} catch (FoundTreeNodeNotUniqueException e) {
							logger.error("更新项目树失败，SynchronizationProjectModelCommand不成功！");
							e.printStackTrace();
						}
					}
					
				}
			}
		}
		
		super.execute();
	}

	public void setProjectModel(ProjectModel projectModel) {
		this.projectModel = projectModel;
	}

	public void setModuleCheckedTreeContentSet(
			Set<TreeContent> moduleCheckedTreeContentSet) {
		this.moduleCheckedTreeContentSet = moduleCheckedTreeContentSet;
	}

	public void setTableCheckedTreeContentSet(
			Set<TreeContent> tableCheckedTreeContentList) {
		this.tableCheckedTreeContentSet = tableCheckedTreeContentSet;
	}

	public void setProjectTreeViewPart(ProjectTreeViewPart projectTreeViewPart) {
		this.projectTreeViewPart = projectTreeViewPart;
	}

}

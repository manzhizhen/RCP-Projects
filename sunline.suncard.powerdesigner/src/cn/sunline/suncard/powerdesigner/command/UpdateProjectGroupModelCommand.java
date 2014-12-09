/* 文件名：     UpdateProjectGroupModelCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-29
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.io.IOException;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.PartInitException;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.model.ProjectGroupModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.model.ProjectSpaceModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.tree.ProjectTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 更新项目群模型的Command
 * @author  Manzhizhen
 * @version 1.0, 2012-12-29
 * @see 
 * @since 1.0
 */
public class UpdateProjectGroupModelCommand extends Command{
	private String flag;
	private ProjectTreeViewPart projectTreeViewPart;
	private TreeContent projectSpaceTreeContent;
	private TreeContent projectGroupTreeContent;
	private ProjectGroupModel projectGroupModel;
	
	private String newName;
	private String oldName;
	private String newNote;
	private String oldNote;
	
	private Log logger = LogManager.getLogger(UpdateProjectGroupModelCommand.class.getName());

	@Override
	public void execute() {
		if(projectTreeViewPart == null) {
			logger.error("传入的ProjectTreeViewPart为空，无法执行UpdateProjectGroupModelCommand！");
			return ;
		}
		
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			if(projectGroupModel == null || projectSpaceTreeContent == null 
					|| !(projectSpaceTreeContent.getObj() instanceof ProjectSpaceModel)) {
				logger.error("传入的数据不完整，无法添加项目群！");
				return ;
			}
			
			try {
				projectTreeViewPart.addProjectGroupModel(projectGroupModel, projectSpaceTreeContent);
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.error("添加项目群失败！" + e.getMessage());
				e.printStackTrace();
			} catch (CanNotFoundNodeIDException e) {
				logger.error("添加项目群失败！" + e.getMessage());
				e.printStackTrace();
			} catch (PartInitException e) {
				logger.error("添加项目群失败！" + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("添加项目群失败！" + e.getMessage());
				e.printStackTrace();
			}
			
		} else if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			if(projectGroupTreeContent == null || !(projectGroupTreeContent.getObj() instanceof ProjectGroupModel)) {
				logger.error("传入的数据不完整，无法修改项目群！");
				return ;
			}
			
			ProjectGroupModel projectGroupModel = (ProjectGroupModel) projectGroupTreeContent.getObj();
			projectGroupModel.setName(newName);
			projectGroupModel.setNote(newNote);
			
			projectTreeViewPart.refreshTree();
		}
		
		
		super.execute();
	}
	
	
	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setProjectTreeViewPart(ProjectTreeViewPart projectTreeViewPart) {
		this.projectTreeViewPart = projectTreeViewPart;
	}

	public void setProjectSpaceTreeContent(TreeContent projectSpaceTreeContent) {
		this.projectSpaceTreeContent = projectSpaceTreeContent;
	}

	public void setProjectGroupModel(ProjectGroupModel projectGroupModel) {
		this.projectGroupModel = projectGroupModel;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public void setNewNote(String newNote) {
		this.newNote = newNote;
	}

	public void setProjectGroupTreeContent(TreeContent projectGroupTreeContent) {
		this.projectGroupTreeContent = projectGroupTreeContent;
	}
	
	
	
}

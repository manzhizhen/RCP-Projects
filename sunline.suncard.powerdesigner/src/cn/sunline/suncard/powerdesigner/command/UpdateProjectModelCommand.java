/* 文件名：     UpdateProjectModelCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-31
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.tree.ProjectTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 更新项目模型的Command
 * @author  Manzhizhen
 * @version 1.0, 2012-12-31
 * @see 
 * @since 1.0
 */
public class UpdateProjectModelCommand extends Command{
	private String flag;
	private ProjectTreeViewPart projectTreeViewPart;
	private TreeContent projectTreeContent;
	private DatabaseTypeModel databaseTypeModel;
	
	private String newName;
	private String oldName;
	private String newNote;
	private String oldNote;
	
	private boolean isCanModifyTable = false; 	// 是否允许修改表数据
	private boolean isCanModifySql = false;		// 是否允许修改SQL脚本
	private boolean isCanModifyStored = false;	// 是否允许修改存储过程
	private boolean isCanModifyCode = false;	// 是否允许修改程序代码
	private boolean isCanModifyDoc = false;		// 是否允许修改文档
	
	private Log logger = LogManager.getLogger(UpdateProjectModelCommand.class.getName());
	
	@Override
	public void execute() {
		if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			if(projectTreeContent == null || !(projectTreeContent.getObj() instanceof ProjectModel)) {
				logger.error("传入的数据不完整，无法修改项目模型！");
				return ;
			}
			
			ProjectModel projectModel = (ProjectModel) projectTreeContent.getObj();
			projectModel.setName(newName);
			projectModel.setNote(newNote);
			projectModel.setDatabaseTypeModel(databaseTypeModel);
			
			projectModel.setCanModifyCode(isCanModifyCode);
			projectModel.setCanModifyDoc(isCanModifyDoc);
			projectModel.setCanModifySql(isCanModifySql);
			projectModel.setCanModifyStored(isCanModifyStored);
			projectModel.setCanModifyTable(isCanModifyTable);
			
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

	public void setProjectTreeContent(TreeContent projectTreeContent) {
		this.projectTreeContent = projectTreeContent;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public void setNewNote(String newNote) {
		this.newNote = newNote;
	}

	public void setDatabaseTypeModel(DatabaseTypeModel databaseTypeModel) {
		this.databaseTypeModel = databaseTypeModel;
	}
	
	public void setCanModifyTable(boolean isCanModifyTable) {
		this.isCanModifyTable = isCanModifyTable;
	}

	public void setCanModifySql(boolean isCanModifySql) {
		this.isCanModifySql = isCanModifySql;
	}

	public void setCanModifyStored(boolean isCanModifyStored) {
		this.isCanModifyStored = isCanModifyStored;
	}

	public void setCanModifyCode(boolean isCanModifyCode) {
		this.isCanModifyCode = isCanModifyCode;
	}

	public void setCanModifyDoc(boolean isCanModifyDoc) {
		this.isCanModifyDoc = isCanModifyDoc;
	}
}

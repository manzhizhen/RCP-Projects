/* 文件名：     ProDefaultViewPart.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-29
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.tree;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import cn.sunline.suncard.powerdesigner.file.SwitchObjectAndFile;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.manager.ProjectSpaceManager;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 项目树的父类
 * 用于管理命令栈和保存等相关操作
 * @author  Manzhizhen
 * @version 1.0, 2012-12-29
 * @see 
 * @since 1.0
 */
public class ProjectDefaultViewPart extends ViewPart implements ISaveablePart2{
	// 保存每个文件了除Editor以外相关的命令操作
	private static Map<ProjectModel, CommandStack> projectCommandStackMap = 
			new HashMap<ProjectModel, CommandStack>();
	
	private static Log logger = LogManager.getLogger(ProjectDefaultViewPart.class.getName());
	
	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
	}
	
	/**
	 * 添加一个命令堆栈
	 * @param fileModel
	 * @param commandStack
	 */
	public static void addProjectCommandStack(ProjectModel projectModel) {
		if(projectModel == null) {
			logger.error("添加命令堆栈失败，传入的ProjectModel为空！");
			return ;
		}
		
		if(getProjectCommandStackMap().get(projectModel) != null) {
			logger.info("项目模型在projectCommandStackMap已经存在！");
			return ;
		}
		
		getProjectCommandStackMap().put(projectModel, new CommandStack());
		logger.info("在projectCommandStackMap添加了一个项目模型:" + projectModel.getId());
	}
	
	/**
	 * 移除一个命令堆栈
	 * @param fileModel
	 */
	public static void removeProjectCommandStack(ProjectModel projectModel) {
		if(projectModel == null) {
			logger.error("移除命令堆栈失败，传入的ProjectModel非法！");
			return ;
		}
		
		getProjectCommandStackMap().remove(projectModel);
		logger.info("从projectCommandStackMap移除了一个项目模型:" + projectModel.getId());
	}
	
	/**
	 * 返回一个对应的命令堆栈
	 */
	public static CommandStack getCommandStackFromProjectModel(ProjectModel projectModel) {
		if(projectModel == null) {
			logger.error("传入的ProjectModel为空，无法找到对应CommandStack！");
			return null;
		}
		
		CommandStack commandStack = getProjectCommandStackMap().get(projectModel);
		
		if(commandStack == null) {
			logger.error("返回的CommandStack为空！传入用来查找的项目为：" + projectModel.getId());
		}
		
		return commandStack;
	}
	
	
	private static Map<ProjectModel, CommandStack> getProjectCommandStackMap() {
		return projectCommandStackMap;
	}
	
	/**
	 * 保存某个ProjectModel
	 * @param projectModel
	 */
	public static void saveProjectModel(ProjectModel projectModel) {
		if(projectModel == null) {
			logger.error("传入的ProjectModel为空，保存失败！");
			return ;
		}
		
		try {
			SwitchObjectAndFile.SaveProjectModelToFile(projectModel);
//			refreshFileModelTreeContent();
		} catch (IOException e) {
			logger.error("保存文件失败！" + e.getMessage());
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getShell(), I18nUtil.getMessage("MESSAGE"), 
					"保存文件失败！" + e.getMessage());
			e.printStackTrace();
			return ;
		}
	}
	

	@Override
	public void createPartControl(Composite parent) {
	}

	@Override
	public void setFocus() {
		this.setFocus();
	}
	
	/**
	 * 检查一个项目模型是否变脏
	 * @param projectModel
	 * @return
	 */
	public static boolean projectModelIsDirty(ProjectModel projectModel) {
		CommandStack commandStack = getCommandStackFromProjectModel(projectModel);
		if(commandStack == null) {
			return false;
		}
		
		return commandStack.isDirty();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		Set<ProjectModel> projectModels = ProjectSpaceManager.getAllProjectModel();
		for(ProjectModel projectModel : projectModels) {
			saveProjectModel(projectModel);
		}
		
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isDirty() {
		// 只要有一个项目文件被修改，就变脏
		CommandStack commandStack = null;
		Set<ProjectModel> projectModels = ProjectSpaceManager.getAllProjectModel();
		for(ProjectModel projectModel : projectModels) {
			commandStack = getCommandStackFromProjectModel(projectModel);
			if(commandStack != null) {
				if(commandStack.isDirty()) {
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public boolean isSaveOnCloseNeeded() {
		return false;
	}

	@Override
	public int promptToSaveOnClose() {
		return 0;
	}
}

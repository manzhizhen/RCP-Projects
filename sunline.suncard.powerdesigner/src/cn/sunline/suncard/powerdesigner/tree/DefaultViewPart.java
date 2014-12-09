/* 文件名：     EditDomainViewPart.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-21
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.PrintAction;
import org.eclipse.gef.ui.actions.RedoAction;
import org.eclipse.gef.ui.actions.SaveAction;
import org.eclipse.gef.ui.actions.SelectAllAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.properties.UndoablePropertySheetPage;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;

import cn.sunline.suncard.powerdesigner.file.SwitchObjectAndFile;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.WorkSpaceModel;
import cn.sunline.suncard.powerdesigner.xml.SwitchObjAndXml;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 提供整个工作空间的命令栈支持
 * @author  Manzhizhen
 * @version 1.0, 2012-9-21
 * @see 
 * @since 1.0
 */
public class DefaultViewPart extends ViewPart implements ISaveablePart2{
	
	// 保存每个文件了除Editor以外相关的命令操作
	private static Map<FileModel, CommandStack> fileCommandStackMap = 
			new HashMap<FileModel, CommandStack>();
	
	private static Log logger = LogManager.getLogger(DefaultViewPart.class.getName());
	
	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
	}
	
	/**
	 * 添加一个命令堆栈
	 * @param fileModel
	 * @param commandStack
	 */
	public static void addFileCommandStack(FileModel fileModel, CommandStack commandStack) {
		if(fileModel == null || commandStack == null) {
			logger.error("添加命令堆栈失败，传入的数据非法！");
			return ;
		}
		
		getFileCommandStackMap().put(fileModel, commandStack);
		logger.debug("成功向fileCommandStackMap中添加了文件模型:" + fileModel.getFileName());
	}
	
	/**
	 * 移除一个命令堆栈
	 * @param fileModel
	 */
	public static void removeFileCommandStack(FileModel fileModel) {
		if(fileModel == null) {
			logger.error("移除命令堆栈失败，传入的FileModel为空！");
			return ;
		}
		
		getFileCommandStackMap().remove(fileModel);
		logger.debug("成功从fileCommandStackMap中移除了文件模型:" + fileModel.getFileName());
	}
	
	/**
	 * 返回一个对应的命令堆栈
	 */
	public static CommandStack getFileCommandFromObj(Object object) {
		if(object == null) {
			logger.error("传入的对象为空，无法找到对应CommandStack！");
			return null;
		}
		
		CommandStack commandStack = getFileCommandStackMap().get(FileModel.getFileModelFromObj(object));
		
		if(commandStack == null) {
			logger.error("返回的CommandStack为空！传入用来查找的对象类型为：" + object.
					getClass().getSimpleName());
		}
		
		return commandStack;
	}
	
	
	
	/**
	 * 返回命令堆的Map
	 * @return the command stack
	 */
	private static Map<FileModel, CommandStack> getFileCommandStackMap() {
		return fileCommandStackMap;
	}
	

	@Override
	public void createPartControl(Composite parent) {
	}

	@Override
	public void setFocus() {
		this.setFocus();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		Set<FileModel> fileModelSet = WorkSpaceModel.getFileModelSet();
		for(FileModel fileModel : fileModelSet) {
			saveFileModel(fileModel);
		}
		
	}
	
//	/**
//	 * 保存某个FileModel
//	 * @param fileModel
//	 */
//	public void doSaveFileModel(FileModel fileModel) {
//		if(fileModel == null) {
//			logger.error("传入的FileModel为空，保存失败！");
//			return ;
//		}
//		
//		IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().
//				getActivePage().getEditorReferences();
//		
//		for(IEditorReference editorReference : editorReferences) {
//			if(editorReference.getEditor(false) instanceof DatabaseDiagramEditor) {
//				DatabaseDiagramEditor databaseDiagramEditor = (DatabaseDiagramEditor)editorReference.
//						getEditor(false);
//				if(fileModel.equals(FileModel.getFileModelFromObj(databaseDiagramEditor.
//						getPhysicalDiagramModel()))) {
//					databaseDiagramEditor.updateGefDataToPhysicalDiagramModel();
//				}
//			}
//		}
//		try {
//			SwitchObjectAndFile.SaveFileModelToFile(fileModel);
//		} catch (IOException e) {
//			logger.error("保存文件失败！" + e.getMessage());
//			MessageDialog.openError(getSite().getShell(), 
//					I18nUtil.getMessage("MESSAGE"), "保存文件失败！" + e.getMessage());
//			e.printStackTrace();
//			return ;
//		}
//		
//		CommandStack commandStack = getCommandStackMap().get(fileModel);
//		if(commandStack != null) {
//			commandStack.markSaveLocation();
//		} else {
//			logger.error("保存文件：" + fileModel.getFile().getAbsolutePath() + "出错！无法找打对应的CommandStack！");
//			MessageDialog.openError(getSite().getShell(), I18nUtil.getMessage("MESSAGE"), 
//					"保存文件：" + fileModel.getFile().getAbsolutePath() + "出错！无法找打对应的CommandStack！");
//		}
//	}
	
	/**
	 * 保存某个FileModel
	 * @param fileModel
	 */
	public static void saveFileModel(FileModel fileModel) {
		if(fileModel == null) {
			logger.error("传入的FileModel为空，保存失败！");
			return ;
		}
		
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				getActivePage();
		
		if(page == null) {
			logger.error("无法获得活跃的WorkbenchPage，保存失败！");
			return ;
		}
		
		IEditorReference[] editorReferences = page.getEditorReferences();
		
		for(IEditorReference editorReference : editorReferences) {
			if(editorReference.getEditor(false) instanceof DatabaseDiagramEditor) {
				DatabaseDiagramEditor databaseDiagramEditor = (DatabaseDiagramEditor)editorReference.
						getEditor(false);
				if(fileModel.equals(FileModel.getFileModelFromObj(databaseDiagramEditor.
						getPhysicalDiagramModel()))) {
					databaseDiagramEditor.updateGefDataToPhysicalDiagramModel();
				}
			}
		}
		try {
			SwitchObjectAndFile.SaveFileModelToFile(fileModel);
			refreshFileModelTreeContent();
		} catch (IOException e) {
			logger.error("保存文件失败！" + e.getMessage());
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getShell(), I18nUtil.getMessage("MESSAGE"), 
					"保存文件失败！" + e.getMessage());
			e.printStackTrace();
			return ;
		}
	}
	
	/**
	 * 如果一个Command的操作会造成文件的修改，则需要使FileModel变脏，则需要调用该方法来刷新树
	 */
	public static void refreshFileModelTreeContent() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if(page == null) {
			logger.warn("无法获得活跃的WorkbenchPage，refreshFileModelTreeContent()无法执行！");
			return ;
		}
		
		DatabaseTreeViewPart databaseTreeViewPart = (DatabaseTreeViewPart) page
				.findView(DatabaseTreeViewPart.ID);
		if(databaseTreeViewPart == null) {
			logger.warn("找不到DatabaseTreeViewPart，refreshFileModelTreeContent()刷新DatabaseTreeViewPart失败！");
		} else {
			databaseTreeViewPart.refreshTree();
		}
		
		
		ProductTreeViewPart productTreeViewPart = (ProductTreeViewPart) page
				.findView(ProductTreeViewPart.ID);
		if(productTreeViewPart == null) {
			logger.warn("找不到ProductTreeViewPart，refreshFileModelTreeContent()刷新ProductTreeViewPart失败！");
		} else {
			productTreeViewPart.refreshTree();
		}
	}
	
	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isDirty() {
//		Collection<CommandStack> collection = getFileCommandStackMap().values();
//		for(CommandStack commandStack : collection) {
//			if(commandStack.isDirty()) {
//				return true;
//			}
//		}
		
		// 只要有一个文件模型变脏，整棵树就变脏
		Set<FileModel> fileModelSet = WorkSpaceModel.getFileModelSet();
		for(FileModel fileModel : fileModelSet) {
			if(fileModelIsDirty(fileModel)) {
				return true ;
			}
		}
		
		return false;
	}
	
	/**
	 * 检查某个FileModel是否变脏
	 */
	public static boolean fileModelIsDirty(FileModel fileModel) {
		if(fileModel == null) {
			logger.warn("传入的FileModel为空，无法执行fileModelIsDirty()！");
			return false ;
		}
		
		CommandStack commandStack = null;
		
		// 检查文件的CommandStack是否变脏
		commandStack = DefaultViewPart.getFileCommandFromObj(fileModel);
		if(commandStack != null) {
			if(commandStack.isDirty()) {
				return true;
			}
		}
		
		// 检查该文件下是否有相关editor变脏
		Set<PhysicalDataModel> dataSet = fileModel.getPhysicalDataSet();
		for(PhysicalDataModel physicalDataModel : dataSet) {
			Set<PhysicalDiagramModel> diagramSet = physicalDataModel.getAllPhysicalDiagramModels();
			for(PhysicalDiagramModel physicalDiagramModel : diagramSet) {
				commandStack = DatabaseTreeViewPart.getEditorCommandStack(
						physicalDiagramModel);
				if(commandStack != null) {
					if(commandStack.isDirty()) {
						return true;
					}
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
		return true;
	}

	@Override
	public int promptToSaveOnClose() {
		return 3;
	}

}

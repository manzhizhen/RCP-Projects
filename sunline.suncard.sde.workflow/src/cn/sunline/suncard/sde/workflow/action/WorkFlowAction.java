/* 文件名：     WorkFlowAction.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.sde.workflow.Activator;
import cn.sunline.suncard.sde.workflow.common.DmConstants;
import cn.sunline.suncard.sde.workflow.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.sde.workflow.file.FileDeal;
import cn.sunline.suncard.sde.workflow.file.SwitchObjectAndFile;
import cn.sunline.suncard.sde.workflow.gef.command.SaveToImageCommand;
import cn.sunline.suncard.sde.workflow.gef.service.SaveImageToXml;
import cn.sunline.suncard.sde.workflow.gef.service.WorkFlowCoreProcess;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditor;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditorInput;
import cn.sunline.suncard.sde.workflow.gef.xml.GefFigureSwitchXml;
import cn.sunline.suncard.sde.workflow.model.ActionTreeNode;
import cn.sunline.suncard.sde.workflow.model.CommonNode;
import cn.sunline.suncard.sde.workflow.model.WorkFlowTreeNode;
import cn.sunline.suncard.sde.workflow.resource.IDmImageKey;
import cn.sunline.suncard.sde.workflow.tree.ActionTreeViewPart;
import cn.sunline.suncard.sde.workflow.tree.WorkFlowTreeViewPart;
import cn.sunline.suncard.sde.workflow.tree.factory.TreeContent;
import cn.sunline.suncard.sde.workflow.ui.dialog.AddWorkFlowDialog;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 工作流树的Action
 * @author  易振强
 * @version 1.0, 2012-3-26
 * @see 
 * @since 1.0
 */
public class WorkFlowAction extends Action{
	Log logger = LogManager.getLogger(WorkFlowAction.class);
	
	private TreeViewer treeViewer;	// 树视图
	private String editorState;		// 要打开的Editor的状态
	private WorkFlowEditor workFlowEditor;
	
	public WorkFlowAction(String editorState, TreeViewer treeViewer) {
		this.editorState = editorState;
		this.treeViewer = treeViewer;
		
		if(WorkFlowActionGroup.ADD_FLAG.equals(editorState)) {
			setText(I18nUtil.getMessage("ACTION_ADD"));
			
		} else if(WorkFlowActionGroup.DELETE_FLAG.equals(editorState)) {
			setText(I18nUtil.getMessage("ACTION_DELETE"));
			
		} else if(WorkFlowActionGroup.MODIFY_FLAG.equals(editorState)) {
			setText(I18nUtil.getMessage("ACTION_MODIFY"));
			
		} else if(WorkFlowActionGroup.VIEW_FLAG.equals(editorState)) {
			setText(I18nUtil.getMessage("ACTION_VIEW"));
			
		} else if(WorkFlowActionGroup.EXPORT_FLAG.equals(editorState)) {
			setText(I18nUtil.getMessage("ACTION_EXPORT"));
			
		} else if(WorkFlowActionGroup.ATTRI_FLAG.equals(editorState)) {
			setText(I18nUtil.getMessage("ACTION_ATTRI"));
			
		} else if(WorkFlowActionGroup.IMPORT_FLAG.equals(editorState)) {
			setText(I18nUtil.getMessage("ACTION_IMPORT"));
		}
	}
	
	@Override
	public void run() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		if(page == null) {
			logger.error("找不到活跃的WorkbenchPage，无法打开Editor！");
			return ;
		}
		
		if(WorkFlowActionGroup.ADD_FLAG.equals(editorState)) {
			addRun(page);
			
		} else if(WorkFlowActionGroup.DELETE_FLAG.equals(editorState)) {
			deleteRun(page);
			
		} else if(WorkFlowActionGroup.MODIFY_FLAG.equals(editorState)) {
			modifyRun(page);
			
		} else if(WorkFlowActionGroup.VIEW_FLAG.equals(editorState)) {
			viewRun(page);
			
		} else if(WorkFlowActionGroup.EXPORT_FLAG.equals(editorState)) {
			exportRun(page);
			
		} else if(WorkFlowActionGroup.ATTRI_FLAG.equals(editorState)) {
			attriRun(page);
			
		} else if(WorkFlowActionGroup.IMPORT_FLAG.equals(editorState)) {
			importRun(page);
			
		} 
	}
	
	/**
	 * 导入工作流
	 * 复制文件到相应目录，更改ID，复制相应的Action
	 */
	private void importRun(IWorkbenchPage page) {
		boolean isOverride = MessageDialog.openConfirm(page.getActivePart().getSite().getShell(), 
				I18nUtil.getMessage("MESSAGE"), "如果有重复数据，是否覆盖？");;
		
		FileDialog fileDialog = new FileDialog(page.getActivePart().getSite().getShell(), SWT.OPEN);
		fileDialog.setFilterExtensions(new String[]{"*.xml"});
		String filePath = fileDialog.open();
		
		if(filePath == null) {
			return ;
		}
		
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(new File(filePath));
		} catch (DocumentException e) {
			logger.error("导入工作流文件失败，解析XML出错！" + e.getMessage());
			MessageDialog.openError(page.getActivePart().getSite().getShell(), 
					I18nUtil.getMessage("MESSAGE"), "导入工作流文件失败，解析XML出错！"
					+ e.getMessage());
			e.printStackTrace();
			
			return ;
		}
		
		Element rootE = document.getRootElement();
		if(rootE == null) {
			logger.error("Document对象根节点为空！");
			return ;
		}
		
		WorkFlowTreeViewPart part = (WorkFlowTreeViewPart) PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getActivePage().getActivePart();
		
		if(part == null) {
			logger.error("无法找到树的ViewPart，导入数据失败！");
			return ;
		}
		
		rootE.attribute("id").setValue(AddWorkFlowDialog.getNewWorkFlowId(part.getTreeViewComposite()));
		
		// 导入Action
		Element connectionsE = rootE.element("connections");
		List<Element> connectionEList = connectionsE.elements("connection");
		for(Element element : connectionEList)  {
			Element dataE = element.element("dataObject");
			if(dataE == null) {
				logger.error("有一根连接线未绑定数据对象！" + element.element("label").getTextTrim());
				continue ;
			}
			
			Element lineE = dataE.element("lineNode");
			
			CommonNode commonNode = new CommonNode();
			commonNode.getObjectFromElement(lineE);
			
			String actionName = commonNode.getName();
			boolean isExist = false;
			// 遍历，看是否数据库中存在
			for(ActionTreeNode node : WorkFlowTreeViewPart.allActionList) {
				if(node.getName().equals(actionName)) {
					isExist = true;
					break ;
				}
			}
			
			// 如果不存在，需要添加
			if(!isExist && commonNode.getName() != null && !"".equals(commonNode.getName())) {
				ActionTreeNode actionNode = new ActionTreeNode();
				actionNode.setName(commonNode.getName());
				actionNode.setDesc(commonNode.getDescription());
				WorkFlowTreeViewPart.allActionList.add(actionNode);
				
				// 在储存目录下生成相应的action文件
				try {
					SwitchObjectAndFile.SaveActionToFile(actionNode);
				} catch (IOException e1) {
					logger.error("将对象保存为文件失败！" + e1.getMessage());
					e1.printStackTrace();
					
					return ;
				}
			} else if (isExist && isOverride && commonNode.getName() != null && !"".equals(commonNode.getName())) {
				ActionTreeNode actionNode = new ActionTreeNode();
				actionNode.setName(commonNode.getName());
				actionNode.setDesc(commonNode.getDescription());
				WorkFlowTreeViewPart.allActionList.add(actionNode);
				
				// 在储存目录下生成相应的action文件
				try {
					SwitchObjectAndFile.SaveActionToFile(actionNode);
				} catch (IOException e1) {
					logger.error("将对象保存为文件失败！" + e1.getMessage());
					e1.printStackTrace();
					
					return ;
				}
			}
		}
		
		// 更新Action树
		ActionTreeViewPart viewPart = (ActionTreeViewPart) PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getActivePage().findView(ActionTreeViewPart.ID);
		
		if(viewPart != null) {
			viewPart.createContent();
		} else {
			logger.error("导入Action后更新Action树失败！可以尝试关闭Action树后再次打开来修复此问题！");
			MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					I18nUtil.getMessage("MESSAGE"), "导入Action后更新Action树失败！可以尝试关闭Action树后再次打开来修复此问题！");
		}
		
		try {
			File saveFile = new File(DmConstants.WORK_FLOW_DATA_FILE_PATH, 
					rootE.attributeValue("id")+ ".xml");
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(saveFile), "UTF-8");
			out.write(document.asXML());
			out.close();
			
			WorkFlowTreeNode treeNode = GefFigureSwitchXml.getWorkFlowTreeNodeFromXml(saveFile.getAbsolutePath());
			part.getTreeViewComposite().addNode(DmConstants.WORK_FLOW_TREE_ROOT_ID, treeNode.getId(), 
					treeNode.getName() + "-" + treeNode.getDesc(), treeNode);
			
		} catch (IOException e) {
			logger.error("导入文件失败！" + e.getMessage());
			MessageDialog.openError(page.getActivePart().getSite().getShell(), I18nUtil.getMessage("MESSAGE"), 
					"导入文件失败！" + e.getMessage());
			e.printStackTrace();
			return ;
		} catch (DocumentException e) {
			logger.error("解析XML败！" + e.getMessage());
			MessageDialog.openError(page.getActivePart().getSite().getShell(), I18nUtil.getMessage("MESSAGE"), 
					"解析XML失败！" + e.getMessage());
			e.printStackTrace();
		} catch (CanNotFoundNodeIDException e) {
			logger.error("解析XML败！" + e.getMessage());
			MessageDialog.openError(page.getActivePart().getSite().getShell(), I18nUtil.getMessage("MESSAGE"), 
					"解析XML失败！" + e.getMessage());
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * 属性查看对话框
	 */
	private void attriRun(IWorkbenchPage page) {
		WorkFlowTreeNode treeNode = getSelect();
		if(treeNode == null) {
			return ;
		}
		
		AddWorkFlowDialog dialog = new AddWorkFlowDialog(page.getActivePart().getSite().getShell());
		dialog.setFlag(WorkFlowActionGroup.ATTRI_FLAG);
		dialog.setNode(treeNode);
		dialog.open();
	}

	/**
	 * 删除一个工作流
	 */
	private void deleteRun(IWorkbenchPage page) {
		WorkFlowTreeNode treeNode = getSelect();
		if(!MessageDialog.openConfirm(page.getActivePart().getSite().getShell(), 
				I18nUtil.getMessage("MESSAGE"), "是否确认删除工作流" + treeNode.getName())) {
			return ;
		}
		
		try {
			// 关闭相关的Editor
			IEditorReference[] editorParts = page.getEditorReferences();
			
			for(IEditorReference editorPart : editorParts) {
				IEditorInput tempInput = editorPart.getEditorInput();
				if(tempInput instanceof WorkFlowEditorInput) {
					if(treeNode == ((WorkFlowEditorInput)tempInput).getTreeModel()) {
						page.closeEditor(editorPart.getEditor(false), false);
					}
				}
			}
			
			// 更新树
			IWorkbenchPart part = page.getActivePart();
			if (part instanceof WorkFlowTreeViewPart) {
				WorkFlowTreeViewPart treeView = (WorkFlowTreeViewPart) part;
				treeView.getTreeViewComposite().removeNode(treeNode.getId());
			}
			
			// 删除目录下的文件
			String id = treeNode.getId();
			File file = new File(DmConstants.WORK_FLOW_DATA_FILE_PATH);	
			if(file.isDirectory()) {
				File[] files = file.listFiles();
				for(File allFile : files) {
					if(allFile.getName().contains(id)) {
						allFile.delete();
						break;
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("删除工作流失败！" + e.getMessage());
			MessageDialog.openError(page.getActivePart().getSite().getShell(), 
					I18nUtil.getMessage("MESSAGE"), I18nUtil.getMessage("DEL_WORKFLOW_FAIL"));
		}
	}
	
	/**
	 * 查看一个工作流
	 */
	private void viewRun(IWorkbenchPage page) {
		try {
			WorkFlowTreeNode treeNode = getSelect();
			
			if(treeNode == null) {
				return ;
			}
			
			WorkFlowEditorInput input = new WorkFlowEditorInput();
			input.setState(WorkFlowActionGroup.VIEW_FLAG);
			input.setName(treeNode.getName() + "-" + I18nUtil.getMessage("VIEW_EDITOR"));
			input.setTreeModel(treeNode);
			
			WorkFlowEditor workFlowEditor = (WorkFlowEditor) page.openEditor(
					input, WorkFlowEditor.ID);
			
		} catch (PartInitException e) {
			// 写日志
			logger.error("查看工作流失败!");
			
			e.printStackTrace();
		}
	}
	
	/**
	 * 修改一个工作流
	 */
	private void modifyRun(IWorkbenchPage page) {
		try {
			WorkFlowTreeNode treeNode = getSelect();
			
			if(treeNode == null || treeNode.getId() == null) {
				return ;
			}
			
			WorkFlowEditorInput input = new WorkFlowEditorInput();
			input.setState(WorkFlowActionGroup.MODIFY_FLAG);
			input.setName(treeNode.getName() + "-" + I18nUtil.getMessage("MODIFY_EDITOR"));
			input.setTreeModel(treeNode);
			
			workFlowEditor = (WorkFlowEditor) page.openEditor(
					input, WorkFlowEditor.ID);
			
		} catch (PartInitException e) {
			// 写日志
			logger.error("修改工作流失败!");
			
			e.printStackTrace();
		}
	}

	/**
	 * 新增一个工作流
	 */
	public void addRun(IWorkbenchPage page) {
		AddWorkFlowDialog dialog = new AddWorkFlowDialog(page.getActivePart().getSite().getShell());
		dialog.setFlag(WorkFlowActionGroup.ADD_FLAG);
		dialog.open();
	}
	
	/**
	 * 导出为XML文件
	 */
	public void exportRun(IWorkbenchPage page) {
		WorkFlowTreeNode treeNode = getSelect();
		
		if(treeNode == null) {
			return ;
		}
		
		String result = WorkFlowEditor.checkXML(treeNode.getModel());
		if(result != null) {
			if(!MessageDialog.openQuestion(page.getActivePart().getSite().getShell(), I18nUtil.getMessage("MESSAGE"), 
					result + I18nUtil.getMessage("SURE_TO_EXPORT"))) {
				return ;
			}
		}
		
		// 打开editor，便于保存图片
		modifyRun(page);
		
		FileDialog dialog = new FileDialog(page.getActivePart().getSite().getShell(), SWT.SAVE);
		dialog.setFilterExtensions(new String[] {"*.xml"});
		dialog.setText(I18nUtil.getMessage("ACTION_EXPORT"));
		
		String filePath = dialog.open();
		
		if(filePath == null) {
			logger.info("用户取消了导出的操作！");
			return ;
		}
		
		String fileName = dialog.getFileName();
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		
		try {
//			FileWriter fileWriter = new FileWriter(filePath);
//			fileWriter.write(new String(xml.getBytes(), "UTF-8"));
//			fileWriter.close();
			
			ScalableFreeformRootEditPart rootPart = workFlowEditor
					.getRootEditPart();
			IFigure figure = rootPart
					.getLayer(ScalableFreeformRootEditPart.PRINTABLE_LAYERS);
		
			SaveImageToXml saveImageToXml = new SaveImageToXml();
			saveImageToXml.setWorkFlowTreeNode(workFlowEditor.getTreeNode());
			
			byte[] data = SaveToImageCommand.createImage(figure, SWT.IMAGE_JPEG, saveImageToXml);
			
			// 写图片
			FileOutputStream fos = new FileOutputStream(new File(new File(filePath).getParent(), fileName + ".jpg"));
			fos.write(data);
			fos.close();
			
			// 写图片XML文件
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(new File(new File(filePath).getParent(), 
					fileName + I18nUtil.getMessage("PIC_XML") + ".xml")), "UTF-8");
			out.write(saveImageToXml.getPointXmlFormModel());
			out.close();
			
			// 写工作流XML
			String xml = new WorkFlowCoreProcess().saveGef2Xml(treeNode.getModel(), treeNode.getName());
			OutputStreamWriter xmlOut = new OutputStreamWriter(new FileOutputStream(new File(new File(filePath).getParent(), 
					fileName + ".xml")), "UTF-8");
			xmlOut.write(xml.replace("\'", "\""));
			xmlOut.close();
			
			// 导出工作目录下的文件，以便日后导入
			String id = treeNode.getId();
			FileDeal.copyFileToPath(new File(DmConstants.WORK_FLOW_DATA_FILE_PATH, id + ".xml").getAbsolutePath(), 
					new File(new File(filePath).getParent(), treeNode.getName() + "_All.xml").getAbsolutePath());
			
			MessageDialog.openInformation(page.getActiveEditor().getSite().getShell(), I18nUtil.getMessage("MESSAGE"), 
					I18nUtil.getMessage("EXPORT_WORKFLOW_SUCCESS"));
		} catch (IOException e) {
			logger.error("导出工作流出错！" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public WorkFlowTreeNode getSelect() {
		StructuredSelection selection = (StructuredSelection) treeViewer.getSelection();
		TreeContent select = (TreeContent) selection.getFirstElement();
		
		if(select == null) {
			return null;
		}
		
		WorkFlowTreeNode treeNode = (WorkFlowTreeNode) select.getObj();
		
		if(treeNode == null) {
			return null;
		}
		
		return treeNode;
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		ImageDescriptor descriptor = null;
		
		if(editorState.equals(WorkFlowActionGroup.ADD_FLAG)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_NEW_IMAGE);
				
		} else if (editorState.equals(WorkFlowActionGroup.DELETE_FLAG)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_DELETE_IMAGE);
				
		} else if (editorState.equals(WorkFlowActionGroup.MODIFY_FLAG)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_MODIFY_IMAGE);
				
		} else if (editorState.equals(WorkFlowActionGroup.VIEW_FLAG)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_VIEW_IMAGE);
				
		} else if (editorState.equals(WorkFlowActionGroup.EXPORT_FLAG)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_EXPORT_IMAGE);
				
		} else if (editorState.equals(WorkFlowActionGroup.ATTRI_FLAG)) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_ATTRI_IMAGE);
				
		} else if (editorState == WorkFlowActionGroup.IMPORT_FLAG) {
			descriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
							IDmImageKey.A_IMPORT_IMAGE);
				
		} 
		
		return descriptor;
	}
	
}

/* 文件名：     UpdateDocCategoryModelCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-3-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.io.File;
import java.io.IOException;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.PartInitException;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.model.DocumentCategoryModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.models.DocumentsNodeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProjectTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeViewComposite;
import cn.sunline.suncard.powerdesigner.ui.dialog.modelattri.DocCategoryDialog;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2013-3-6
 * @see 
 * @since 1.0
 */
public class UpdateDocCategoryModelCommand extends Command{
	private String flag;
	private DocumentCategoryModel documentCategoryModel;
	private TreeContent documentsNodeTreeContent;
	private String newName;
	
	private Log logger = LogManager.getLogger(UpdateDocCategoryModelCommand.class.getName());
	
	@Override
	public void execute() {
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			if(documentCategoryModel == null || documentsNodeTreeContent == null 
					|| !(documentsNodeTreeContent.getObj() instanceof DocumentsNodeModel)) {
				logger.debug("传入的数据为空或不完整，无法执行UpdateDocCategoryModelCommand！");
				return ;
			}
			
			TreeViewComposite treeViewComposite = null;
			
			// 根据该文档分类对象所属的文档集合节点的父节点是项目模型还是产品模型来分开处理
			ProductModel model = documentCategoryModel.getDocumentsNodeModel().getParentModel();
			try {
				// 如果是在项目树上添加该文档
				if(model instanceof ProjectModel) {
					ProjectTreeViewPart projectTreeViewPart = ProjectTreeViewPart.getInstance();
					treeViewComposite = projectTreeViewPart.getTreeViewComposite();
					
				// 如果是在产品树上添加该文档
				} else {
					ProductTreeViewPart productTreeViewPart = ProductTreeViewPart.getInstance();
					treeViewComposite = productTreeViewPart.getTreeViewComposite();
					
				}
			
				ProductTreeViewPart.addDocCategoryModel(documentCategoryModel, documentsNodeTreeContent);
			} catch (PartInitException e) {
				logger.debug("获得ProductTreeViewPart或ProjectTreeViewPart失败！");
				e.printStackTrace();
			} catch (CanNotFoundNodeIDException e) {
				logger.debug("给ProductTreeViewPart或ProjectTreeViewPart添加文档分类节点失败！" + e.getMessage());
				e.printStackTrace();
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.debug("给ProductTreeViewPart或ProjectTreeViewPart添加文档分类节点失败！" + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				logger.debug("给ProductTreeViewPart或ProjectTreeViewPart添加文档分类节点失败！" + e.getMessage());
				e.printStackTrace();
			}
			
		} else if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			// 改变该文档分类的路径
			ProductModel model = documentCategoryModel.getDocumentsNodeModel().getParentModel();
			try {
				// 如果是在项目树上添加该文档
				if(model instanceof ProjectModel) {
					ProjectModel projectModel = (ProjectModel) model;
					File file = new File(SystemConstants.PROJECTSPACEPATH + File.separator 
							+ projectModel.getFolderName() + File.separator + SystemConstants.ZIP_FILE_DOC 
							+ File.separator + documentCategoryModel.getName() + File.separator);
					if(file.isDirectory()) {
						file.renameTo(new File(file.getParent(), newName + File.separator));
					} else {
						logger.debug("修改文档类别失败！");
						return ;
					}
					documentCategoryModel.setName(newName);

					ProjectTreeViewPart.getInstance().refreshTree();
					
				// 如果是在产品树上添加该文档
				} else {
					FileModel fileModel = FileModel.getFileModelFromObj(documentCategoryModel);
					File file = new File(SystemConstants.WORKSPACEPATH + File.separator 
							+ fileModel.getFolderName() + File.separator + SystemConstants.ZIP_FILE_DOC 
							+ File.separator + documentCategoryModel.getName() + File.separator);
					if(file.isDirectory()) {
						file.renameTo(new File(file.getParent(), newName + File.separator));
					} else {
						logger.debug("修改文档类别失败！");
						return ;
					}
					
					documentCategoryModel.setName(newName);
					ProductTreeViewPart.getInstance().refreshTree();
					
				}
				
			} catch (PartInitException e) {
				logger.debug("刷新树失败！");
				e.printStackTrace();
			}
			
		}
		
		
		super.execute();
	}
	
	public void setNewName(String newName) {
		this.newName = newName;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setDocumentCategoryModel(DocumentCategoryModel documentCategoryModel) {
		this.documentCategoryModel = documentCategoryModel;
	}

	public void setDocumentsNodeTreeContent(TreeContent documentsNodeTreeContent) {
		this.documentsNodeTreeContent = documentsNodeTreeContent;
	}
	
	
	
}

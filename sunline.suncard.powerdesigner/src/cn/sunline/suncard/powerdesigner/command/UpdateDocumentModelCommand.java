package cn.sunline.suncard.powerdesigner.command;

import java.io.File;
import java.io.IOException;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.PartInitException;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.model.DocumentCategoryModel;
import cn.sunline.suncard.powerdesigner.model.DocumentModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.ProjectModel;
import cn.sunline.suncard.powerdesigner.models.DocumentsNodeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.tools.FileDeal;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProjectTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

public class UpdateDocumentModelCommand extends Command{
	private TreeContent docCategoryModelTreeContent;
	private DocumentModel documentModel;
	private String flag;
	private File sourceFile;
	
	private Log logger = LogManager.getLogger(UpdateDocumentModelCommand.class.getName());
	
	@Override
	public void execute() {
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			if(documentModel == null || documentModel.getFileName() == null || docCategoryModelTreeContent == null 
					|| !(docCategoryModelTreeContent.getObj() instanceof DocumentCategoryModel)) {
				logger.debug("传入的数据不完整，UpdateDocumentModelCommand执行失败！");
			}
			
			DocumentCategoryModel documentCategoryModel = (DocumentCategoryModel) docCategoryModelTreeContent.getObj();
			DocumentsNodeModel documentsNodeModel = (DocumentsNodeModel) docCategoryModelTreeContent
					.getParent().getObj();
			
			try {
				// 如果是在项目树上添加该节点
				if(documentsNodeModel.getParentModel() instanceof ProjectModel) {
					File targetFile = new File(SystemConstants.PROJECTSPACEPATH 
							+ File.separator + ((ProjectModel)documentsNodeModel.getParentModel()).getFolderName() +  File.separator 
							+ SystemConstants.ZIP_FILE_DOC + File.separator 
							+ documentCategoryModel.getName() + File.separator);
					
					FileDeal.copyFileToPath(sourceFile.getAbsolutePath(), 
							new File(targetFile.getAbsolutePath(), sourceFile.getName()).getAbsolutePath());
					
					ProjectTreeViewPart projectTreeViewPart = ProjectTreeViewPart.getInstance();
					ProductTreeViewPart.addDocumentModel(documentModel, docCategoryModelTreeContent);
					
					// 如果是在产品树上添加该节点
				} else {
					
					// 拷贝文档到指定目录中去
					FileModel fileModel = FileModel.getFileModelFromObj(documentsNodeModel);
					File targetFile = new File(SystemConstants.WORKSPACEPATH 
							+ File.separator + fileModel.getFolderName() +  File.separator 
							+ SystemConstants.ZIP_FILE_DOC + File.separator 
							+ documentCategoryModel.getName() + File.separator);
					
					FileDeal.copyFileToPath(sourceFile.getAbsolutePath(), 
							new File(targetFile.getAbsolutePath(), sourceFile.getName()).getAbsolutePath());
					
					ProductTreeViewPart productTreeViewPart = ProductTreeViewPart.getInstance();
					ProductTreeViewPart.addDocumentModel(documentModel, docCategoryModelTreeContent);
				}
				
			} catch (IOException e) {
				logger.debug("UpdateDocumentModelCommand执行时拷贝文件失败！");
				e.printStackTrace();
			} catch (PartInitException e) {
				logger.debug("新增文档节点失败！" + e.getMessage());
				e.printStackTrace();
			} catch (CanNotFoundNodeIDException e) {
				logger.debug("新增文档节点失败！" + e.getMessage());
				e.printStackTrace();
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.debug("新增文档节点失败！" + e.getMessage());
				e.printStackTrace();
			}
		}
		
		super.execute();
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setDocCategoryModelTreeContent(
			TreeContent docCategoryModelTreeContent) {
		this.docCategoryModelTreeContent = docCategoryModelTreeContent;
	}

	public void setDocumentModel(DocumentModel documentModel) {
		this.documentModel = documentModel;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}
	
}

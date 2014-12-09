/* 文件名：     UpdateModuleModelCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-7
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 更改模块模型的Command
 * @author  Manzhizhen
 * @version 1.0, 2012-12-7
 * @see 
 * @since 1.0
 */
public class UpdateModuleModelCommand extends Command{
	private String flag;
	private TreeContent modulesNodeModelTreeContent;
	private ModuleModel moduleModel;
	private TreeContent moduleModelTreeContent;
	
	private String newName;
	private String oldName;
	private String newNote;
	private String oldNote;
	
	private Log logger = LogManager.getLogger(UpdateModuleModelCommand.class.getName());
	private ProductTreeViewPart productTreeViewPart;
	
	@Override
	public void execute() {
		if(productTreeViewPart == null) {
			logger.error("ProductTreeViewPart为空，UpdateModuleModelCommand执行失败！");
			return ;
		}
		
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			if(modulesNodeModelTreeContent == null || !(modulesNodeModelTreeContent
					.getParent().getObj() instanceof ProductModel) || moduleModel == null) {
				logger.error("传入的数据为空或错误，无法添加模块模型！");
				return ;
			}
			
			// 在产品模型下添加该模块模型
			moduleModel.getProductModel().getModuleModelSet().add(moduleModel);
			
			try {
				ProductTreeViewPart.addModuleModel(moduleModel, modulesNodeModelTreeContent
						, productTreeViewPart.getTreeViewComposite());
			} catch (CanNotFoundNodeIDException e) {
				logger.error("在树上添加模块模型失败！" + e.getMessage());
				e.printStackTrace();
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.error("在树上添加模块模型失败！" + e.getMessage());
				e.printStackTrace();
			}
		} else if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			oldName = moduleModel.getName();
			oldNote = moduleModel.getNote();
			
			moduleModel.setName(newName);
			moduleModel.setNote(newNote);
			
		} else if(DmConstants.COMMAND_DEL.equals(flag)) {
			if(moduleModelTreeContent == null || !(moduleModelTreeContent
					.getObj() instanceof ModuleModel)) {
				logger.warn("传入的模块模型树节点为空或不正确，无法删除该模块！");
				return ;
			}
			
			try {
				productTreeViewPart.removeModuleModel(moduleModelTreeContent);
			} catch (CanNotFoundNodeIDException e) {
				logger.error("删除该模块模型失败！模块名称:" + ((ModuleModel)moduleModelTreeContent
						.getObj()).getName() + " " + e.getMessage() );
				e.printStackTrace();
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.error("删除该模块模型失败！模块名称:" + ((ModuleModel)moduleModelTreeContent
						.getObj()).getName() + " " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		// 刷新树上的文件节点
		DefaultViewPart.refreshFileModelTreeContent();
		
		super.execute();
	}
	
	@Override
	public boolean canUndo() {
		return false;
	}
	
	@Override
	public void undo() {
		// TODO Auto-generated method stub
		super.undo();
	}
	
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public void setModulesNodeModelTreeContent(TreeContent productModelTreeContent) {
		this.modulesNodeModelTreeContent = productModelTreeContent;
	}

	public void setModuleModel(ModuleModel moduleModel) {
		this.moduleModel = moduleModel;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public void setNewNote(String newNote) {
		this.newNote = newNote;
	}

	public void setProductTreeViewPart(ProductTreeViewPart productTreeViewPart) {
		this.productTreeViewPart = productTreeViewPart;
	}

	public void setModuleModelTreeContent(TreeContent moduleModelTreeContent) {
		this.moduleModelTreeContent = moduleModelTreeContent;
	}
	
	

}

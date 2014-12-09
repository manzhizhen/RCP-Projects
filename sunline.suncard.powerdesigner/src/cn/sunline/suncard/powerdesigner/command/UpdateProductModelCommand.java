/* 文件名：     UpdateProductModelCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-24
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.io.IOException;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.PartInitException;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.DefaultViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 更新产品的Command
 * @author  Manzhizhen
 * @version 1.0, 2012-11-24
 * @see 
 * @since 1.0
 */
public class UpdateProductModelCommand extends Command{
	private String flag; // 标记是新增还是删除还是修改。
	private ProductModel productModel;
	private TreeContent fileModelTreeContent; // 如果是新增，需要传入文件模型树节点
	private TreeContent productModelTreeContent; // 如果是删除，需要传入产品的树节点
	private ProductTreeViewPart productTreeViewPart;
	
	private String oldName;
	private String newName;
	private String oldNote;
	private String newNote;
	
	private Log logger = LogManager.getLogger(UpdateProductModelCommand.class.getName());
	
	@Override
	public void execute() {
		if(productTreeViewPart == null) {
			logger.error("传入的ProductTreeViewPart为空，" +
					"UpdateProductModelCommand无法执行！");
			return ;
		}
		
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			try {
				ProductTreeViewPart.addProductModel(productModel, fileModelTreeContent
						, productTreeViewPart.getTreeViewComposite());
			} catch (CanNotFoundNodeIDException e) {
				logger.error("添加产品模型失败！" + e.getMessage());
				e.printStackTrace();
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.error("添加产品模型失败！" + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				logger.error("添加产品模型失败！" + e.getMessage());
				e.printStackTrace();
			} catch (PartInitException e) {
				logger.error("添加产品模型失败！" + e.getMessage());
				e.printStackTrace();
			}
			
		} else if(DmConstants.COMMAND_MODIFY.equals(flag)) {
			oldName = productModel.getName();
			oldNote = productModel.getNote();
			
			productModel.setName(newName);
			productModel.setNote(newNote);
			
		} else if(DmConstants.COMMAND_DEL.equals(flag)) {
			if(productModelTreeContent == null || !(productModelTreeContent
					.getObj() instanceof ProductModel)) {
				logger.warn("传入的产品模型树节点为空或不正确，无法删除该产品！");
				return ;
			}
			
			try {
				productTreeViewPart.removeProductModel(productModelTreeContent);
			} catch (CanNotFoundNodeIDException e) {
				logger.error("删除该产品模型失败！产品名称:" + ((ProductModel)productModelTreeContent
						.getObj()).getName() + " " + e.getMessage() );
				e.printStackTrace();
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.error("删除该产品模型失败！产品名称:" + ((ProductModel)productModelTreeContent
						.getObj()).getName() + " " + e.getMessage());
				e.printStackTrace();
			}
			
		}
		
		DefaultViewPart.refreshFileModelTreeContent();
		super.execute();
	}
	
	@Override
	public boolean canUndo() {
		return false;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setProductModel(ProductModel productModel) {
		this.productModel = productModel;
	}

	public void setProductModelTreeContent(TreeContent productModelTreeContent) {
		this.productModelTreeContent = productModelTreeContent;
	}

	public void setFileModelTreeContent(TreeContent fileModelTreeContent) {
		this.fileModelTreeContent = fileModelTreeContent;
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
	
	
	
}

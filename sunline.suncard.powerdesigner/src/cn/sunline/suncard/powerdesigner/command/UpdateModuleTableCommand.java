/* 文件名：     UpdateProductTableCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-25
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.util.List;

import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.model.ModuleModel;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.powerdesigner.ui.dialog.ImportTableModelDialog;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 更新一个产品中的TableModel的Command
 * @author  Manzhizhen
 * @version 1.0, 2012-11-25
 * @see 
 * @since 1.0
 */
public class UpdateModuleTableCommand extends Command{
	private List<TableModel> tableModelList;
	private TreeContent moduleModelTreeContent;
	private TreeContent tableModelTreeContent; // 要删除表格节点时需要传入表格的树节点
	private ProductTreeViewPart productTreeViewPart;
	private String flag;
	
	private Log logger = LogManager.getLogger(UpdateModuleTableCommand.class
			.getName());
	
	@Override
	public void execute() {
		if(productTreeViewPart == null) {
			logger.error("传入的ProductTreeViewPart为空，" +
					"UpdateModuleTableCommand无法执行！");
			return ;
		}
		
		
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			if(tableModelList == null) {
				logger.error("传入的TableModel的List为空，无法添加到模块！");
				return ;
			}
			
			if(moduleModelTreeContent == null || !(moduleModelTreeContent.getObj() instanceof ModuleModel)) {
				logger.error("传入的模块模型的树节点为空或不正确，UpdateModuleTableCommand执行失败！");
				return ;
			}
			
			for(TableModel tableModel : tableModelList) {
				try {
					ProductTreeViewPart.addTableModel(tableModel, moduleModelTreeContent
							, productTreeViewPart.getTreeViewComposite());
				} catch (CanNotFoundNodeIDException e) {
					logger.error("在产品树上添加表格失败！" + tableModel.getTableName() + " " + e.getMessage());
					e.printStackTrace();
				} catch (FoundTreeNodeNotUniqueException e) {
					logger.error("在产品树上添加表格失败！" + tableModel.getTableName() + " " + e.getMessage());
					e.printStackTrace();
				}
			}
			
		} else if(DmConstants.COMMAND_DEL.equals(flag)) {
			if(tableModelTreeContent == null || !(tableModelTreeContent.getObj() instanceof TableModel)) {
				logger.warn("传入的表格树节点为空或不正确，无法删除该表格模型！");
				return ;
			}
			
			try {
				productTreeViewPart.removeTableModel(tableModelTreeContent);
			} catch (CanNotFoundNodeIDException e) {
				logger.error("在产品树上删除表格失败！" + e.getMessage());
				e.printStackTrace();
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.error("在产品树上删除表格失败！" + e.getMessage());
				e.printStackTrace();
			}
			
		}
		
		super.execute();
	}
	
	@Override
	public boolean canUndo() {
		return false;
	}

	public void setTableModelList(List<TableModel> tableModelList) {
		this.tableModelList = tableModelList;
	}
	
	public void setModuleModelTreeContent(TreeContent productModelTreeContent) {
		this.moduleModelTreeContent = productModelTreeContent;
	}

	public void setProductTreeViewPart(ProductTreeViewPart productTreeViewPart) {
		this.productTreeViewPart = productTreeViewPart;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setTableModelTreeContent(TreeContent tableModelTreeContent) {
		this.tableModelTreeContent = tableModelTreeContent;
	}
	
}

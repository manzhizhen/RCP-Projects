/* 文件名：     StoredProcedureManagerCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     wzx
 * 修改时间：2012-11-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.model.ProductModel;
import cn.sunline.suncard.powerdesigner.model.SqlScriptModel;
import cn.sunline.suncard.powerdesigner.model.StoredProcedureModel;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 
 * @author  wzx
 * @version 1.0, 2012-11-26
 * @see 
 * @since 1.0
 */
public class StoredProcedureManagerCommand extends Command {
	private TreeContent treeContent;
	private ProductModel productModel;
	private LinkedHashSet<StoredProcedureModel> newSqlList = new LinkedHashSet<StoredProcedureModel>();
	private Log logger = LogManager.getLogger(StoredProcedureManagerCommand.class);
	
	public StoredProcedureManagerCommand(ProductModel productModel, List<StoredProcedureModel> newSqlList, TreeContent treeContent) {
		this.treeContent = treeContent;
		this.productModel = productModel;
		for (StoredProcedureModel storedProcedureModel : newSqlList) {
			this.newSqlList.add(storedProcedureModel);
		}
	}

	@Override
	public void execute() {
		productModel.setStoredProcedureSet(newSqlList);
		
		ProductTreeViewPart viewPart = null;
		IViewReference[] views = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for (IViewReference iViewReference : views) {
			if (ProductTreeViewPart.ID.equalsIgnoreCase(iViewReference.getId())) {
				viewPart = (ProductTreeViewPart) iViewReference.getView(true);
				break;
			}
		}
		
		treeContent.getChildrenList().clear();
		for (StoredProcedureModel storedProcedureModel : newSqlList) {
			try {
				viewPart.addStoredModel(storedProcedureModel, treeContent, viewPart.getTreeViewComposite());
			} catch (CanNotFoundNodeIDException e) {
				logger.error("添加存储过程失败！" + e.getMessage());
				e.printStackTrace();
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.error("添加存储过程失败！" + e.getMessage());
				e.printStackTrace();
			}
		}
		
		super.execute();
	}
	
	@Override
	public boolean canUndo() {
		return false;
	}
}

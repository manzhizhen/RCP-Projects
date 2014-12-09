/* 文件名：     UpdateCommonColumnModelCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.models.DomainsNodeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.ProductTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 更新Domains下面的列对象的Command
 * @author  Manzhizhen
 * @version 1.0, 2012-11-26
 * @see 
 * @since 1.0
 */
public class UpdateCommonColumnModelCommand extends Command{
	private TreeContent domainsTreeContent;
	private ColumnModel columnModel;
	private TreeContent columnModelTreeContent;
	private DatabaseTreeViewPart databaseTreeViewPart;
	private String flag;
	
	Log logger = LogManager.getLogger(UpdateCommonColumnModelCommand.class);
	
	@Override
	public void execute() {
		if(databaseTreeViewPart == null) {
			logger.error("传入的DatabaseTreeViewPart为空，UpdateCommonColumnModelCommand无法执行！");
			return ;
		}
		
		if(DmConstants.COMMAND_ADD.equals(flag)) {
			if(domainsTreeContent == null || !(domainsTreeContent.getObj() 
					instanceof DomainsNodeModel)|| columnModel == null) {
				logger.error("传入的数据为空或不完整,在Domains下添加公共列节点失败！");
				return ;
			}
			
			try {
				databaseTreeViewPart.addCommonColumnModel(columnModel, domainsTreeContent.getId());
				
				PhysicalDataModel physicalDataModel = ((DomainsNodeModel)domainsTreeContent.getObj())
						.getPhysicalDataModel();
				// 添加到物理数据对象中
				physicalDataModel.getDomainList().add(columnModel);
				// 加入列对象工厂
				ColumnModelFactory.addColumnModel(FileModel.getFileModelFromObj(
						columnModel), columnModel);
				
			} catch (FoundTreeNodeNotUniqueException e) {
				MessageDialog.openError(databaseTreeViewPart.getSite().getShell()
						, I18nUtil.getMessage("MESSAGE"), "在Domains下添加公共列节点失败！" + e.getMessage());
				logger.error("在Domains下添加公共列节点失败！" + e.getMessage());
				e.printStackTrace();
			} catch (CanNotFoundNodeIDException e) {
				MessageDialog.openError(databaseTreeViewPart.getSite().getShell()
						, I18nUtil.getMessage("MESSAGE"), "在Domains下添加公共列节点失败！" + e.getMessage());
				logger.error("在Domains下添加公共列节点失败！" + e.getMessage());
				e.printStackTrace();
			}
			
		} else if(DmConstants.COMMAND_MODIFY.equals(flag))	{
			// 获取该物理数据模型下的所有ColumnModel，如果引用该公共列对象，则修改
			PhysicalDataModel physicalDataModel = ((DomainsNodeModel)domainsTreeContent
					.getObj()).getPhysicalDataModel();
			List<ColumnModel> columnModelList = getAllRefCommonColumnModelFromPhysicalDataModel(
					physicalDataModel, columnModel.getId());
			for(ColumnModel oldColumnModel : columnModelList) {
				try {
					oldColumnModel.setDataTypeModel(columnModel.getDataTypeModel().clone());
					oldColumnModel.setDefaultValue(columnModel.getDefaultValue());
					oldColumnModel.setMaxValue(columnModel.getMaxValue());
					oldColumnModel.setMinValue(columnModel.getMinValue());
					oldColumnModel.setCanGetMaxValue(columnModel.isCanGetMaxValue());
					oldColumnModel.setCanGetMinValue(columnModel.isCanGetMinValue());
					oldColumnModel.setCanNotNull(columnModel.isCanNotNull());
				} catch (CloneNotSupportedException e) {
					logger.error("克隆DataTypeModel失败!" + e.getMessage());
					e.printStackTrace();
				}
			}
			
			databaseTreeViewPart.refreshTree();
			
		} else if(DmConstants.COMMAND_DEL.equals(flag))	{
			if(columnModelTreeContent == null || !(columnModelTreeContent.getObj() 
					instanceof ColumnModel)) {
				logger.error("传入的数据为空或不完整,在Domains下删除公共列节点失败！");
				return ;
			}
			
			// 获取该物理数据模型下的所有ColumnModel，如果引用该公共列对象，则去除
			PhysicalDataModel physicalDataModel = ((DomainsNodeModel)domainsTreeContent
					.getObj()).getPhysicalDataModel();
			List<ColumnModel> columnModelList = getAllRefCommonColumnModelFromPhysicalDataModel(
					physicalDataModel, columnModel.getId());
			for(ColumnModel columnModel : columnModelList) {
				// 取消对公共列对象的引用
				columnModel.setDomainId("");
			}
			
			// 从物理数据模型的域列表中删除该公共列对象
			physicalDataModel.getDomainList().remove(columnModelTreeContent.getObj());
			
			try {
				databaseTreeViewPart.removeCommonColumnModel(columnModelTreeContent);
			} catch (FoundTreeNodeNotUniqueException e) {
				logger.error("从产品树上删除公共节点失败:" + ((ColumnModel)columnModelTreeContent
						.getObj()).getColumnDesc());
				e.printStackTrace();
			} catch (CanNotFoundNodeIDException e) {
				logger.error("从产品树上删除公共节点失败:" + ((ColumnModel)columnModelTreeContent
						.getObj()).getColumnDesc());
				e.printStackTrace();
			}
		}
		
		
		super.execute();
	}
	
	/**
	 * 返回物理数据模型的所有使用该公共列ID的列对象
	 * @return
	 */
	public List<ColumnModel> getAllRefCommonColumnModelFromPhysicalDataModel(PhysicalDataModel physicalDataModel, String id) {
		List<ColumnModel> columnModelList = new ArrayList<ColumnModel>();
		
		// 查看是否有物理数据模型下面的默认列是否引用该公共域
		List<ColumnModel> defaultColumnModelList = physicalDataModel.getDefaultColumnList();
		for(ColumnModel columnModel : defaultColumnModelList) {
			if(id.equals(columnModel.getDomainId())) {
				columnModelList.add(columnModel);
			}
		}
		
		Set<PhysicalDiagramModel> physicalDiagramModelSet = physicalDataModel.getAllPhysicalDiagramModels();
		for(PhysicalDiagramModel physicalDiagramModel : physicalDiagramModelSet) {
			Collection<TableModel> tableModels = physicalDiagramModel
					.getTableMap().values();
			for(TableModel tableModel : tableModels) {
				List<ColumnModel> columnModels = tableModel.getColumnList();
				for(ColumnModel columnModel : columnModels) {
					if(id.equals(columnModel.getDomainId())) {
						columnModelList.add(columnModel);
					}
				}
			} 
		}
		
		return columnModelList;
	}
	
	@Override
	public boolean canUndo() {
		return false;
	}
	
	public void setDomainsTreeContent(TreeContent domainsTreeContent) {
		this.domainsTreeContent = domainsTreeContent;
	}

	public void setColumnModel(ColumnModel columnModel) {
		this.columnModel = columnModel;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setDatabaseTreeViewPart(DatabaseTreeViewPart databaseTreeViewPart) {
		this.databaseTreeViewPart = databaseTreeViewPart;
	}

	public void setColumnModelTreeContent(TreeContent columnModelTreeContent) {
		this.columnModelTreeContent = columnModelTreeContent;
		if(columnModelTreeContent != null) {
			columnModel = (ColumnModel) columnModelTreeContent.getObj();
		}
	}
}

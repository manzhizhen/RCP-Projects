/**
 * 文件名：PasteColumnModelCommand.java
 * 版权：
 * 描述：
 * 修改人： Manzhizhen
 * 修改时间：2013-3-15
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.ui.PartInitException;

import cn.sunline.suncard.powerdesigner.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.powerdesigner.exception.FoundTreeNodeNotUniqueException;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDataModel;
import cn.sunline.suncard.powerdesigner.model.PhysicalDiagramModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.db.DatabaseTypeModel;
import cn.sunline.suncard.powerdesigner.model.factory.ColumnModelFactory;
import cn.sunline.suncard.powerdesigner.models.DomainsNodeModel;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 粘贴列模型的Command
 * @author Manzhizhen
 * @version 1.0 2013-3-15
 * @see
 * @since 1.0
 */
public class PasteColumnModelCommand extends Command {
	private List<ColumnModel> copyColumnModelList;
	private TreeContent domainsTreeContent;
	
	public static Log logger = LogManager.getLogger(PasteColumnModelCommand.class
			.getName());


	@Override
	public void execute() {
		if(!canExecute()) {
			return ;
		}
		
		DomainsNodeModel domainsNodeModel = (DomainsNodeModel) domainsTreeContent.getObj();
		PhysicalDataModel physicalDataModel = domainsNodeModel.getPhysicalDataModel();
		DatabaseTypeModel databaseTypeModel = physicalDataModel.getDatabaseTypeModel();
		
		DatabaseTreeViewPart databaseTreeViewPart;
		try {
			databaseTreeViewPart = DatabaseTreeViewPart.getInstance();
		} catch (PartInitException e) {
			logger.debug("无法获取DatabaseTreeViewPart，粘贴Domains失败！" + e.getMessage());
			e.printStackTrace();
			return ;
		}
		
		try {
			for(ColumnModel columnModel : copyColumnModelList) {
				// 只有数据库类型相同才能进行粘贴
				if(columnModel.getTableModel().getPhysicalDiagramModel().getPackageModel()
						.getPhysicalDataModel().getDatabaseTypeModel().equals(databaseTypeModel)) {
					ColumnModel newColumnModel = columnModel.clone();
					// 初始化信息，以便能从该ColumnModel向上遍历到FileModel
					PackageModel packageModel = new PackageModel();
					packageModel.setPhysicalDataModel(domainsNodeModel
							.getPhysicalDataModel());
					PhysicalDiagramModel physicalDiagramModel = new PhysicalDiagramModel();
					physicalDiagramModel.setPackageModel(packageModel);
					TableModel tableModel = new TableModel();
					tableModel.setPhysicalDiagramModel(physicalDiagramModel);
					
					newColumnModel.setTableModel(tableModel);
					domainsNodeModel.getPhysicalDataModel().getDomainList().add(newColumnModel);
					
					
					ColumnModelFactory.addColumnModel(FileModel.getFileModelFromObj(physicalDataModel), newColumnModel);
					databaseTreeViewPart.addCommonColumnModel(newColumnModel, domainsTreeContent.getId());
				}
			}
		} catch (FoundTreeNodeNotUniqueException e) {
			logger.debug("粘贴Domains失败！" + e.getMessage());
			e.printStackTrace();
		} catch (CanNotFoundNodeIDException e) {
			logger.debug("粘贴Domains失败！" + e.getMessage());
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			logger.debug("粘贴Domains失败！" + e.getMessage());
			e.printStackTrace();
		}
		
		
		super.execute();
	}
	
	@Override
	public boolean canExecute() {
		copyColumnModelList = (ArrayList<ColumnModel>) Clipboard.getDefault()
				.getContents();
		if(copyColumnModelList == null || copyColumnModelList.size() == 0) {
			return false;
		}
		
		for(ColumnModel columnModel : copyColumnModelList) {
			if(!columnModel.isDomainColumnModel()) {
				return false;
			}
		}
		
		if(domainsTreeContent == null || !(domainsTreeContent.getObj() instanceof DomainsNodeModel)) {
			return false;
		}
		
		return true;
	}

	public void setDomainsTreeContent(TreeContent domainsTreeContent) {
		this.domainsTreeContent = domainsTreeContent;
	}
	
	
}

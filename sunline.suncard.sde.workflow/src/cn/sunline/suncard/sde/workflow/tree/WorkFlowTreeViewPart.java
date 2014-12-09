/* 文件名：     WorkFlowTreeViewPart.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-24
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.dom4j.DocumentException;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

import cn.sunline.suncard.sde.workflow.action.WorkFlowAction;
import cn.sunline.suncard.sde.workflow.action.WorkFlowActionGroup;
import cn.sunline.suncard.sde.workflow.common.DmConstants;
import cn.sunline.suncard.sde.workflow.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.sde.workflow.file.SwitchObjectAndFile;
import cn.sunline.suncard.sde.workflow.model.ActionTreeNode;
import cn.sunline.suncard.sde.workflow.model.WorkFlowTreeNode;
import cn.sunline.suncard.sde.workflow.resource.IDmAppConstants;
import cn.sunline.suncard.sde.workflow.resource.IDmImageKey;
import cn.sunline.suncard.sde.workflow.tree.factory.TreeContent;
import cn.sunline.suncard.sde.workflow.tree.factory.TreeViewComposite;


/** 
 * 工作流的树
 * @author  易振强
 * @version , 2011-11-1
 * @see     
 * @since   1.0
 */

public class WorkFlowTreeViewPart extends ViewPart{
	public final static String ID = "cn.sunline.suncard.sde.workflow.tree.WorkFlowTreeViewPart";
	private Log logger = LogManager.getLogger(WorkFlowTreeViewPart.class.getName());
	
	private Tree tree;
	private TreeViewer treeViewer;
	
	private TreeViewComposite treeViewComposite;
	
	private List<WorkFlowTreeNode> flowList;
	
	public static List<ActionTreeNode> allActionList;
	
	@Override
	public void createPartControl(Composite parent) {
		
		treeViewComposite = new TreeViewComposite(parent);
		
		tree = treeViewComposite.getTree();
		treeViewer = treeViewComposite.getTreeViewer();
		
		// 创建树的内容
		createContent();
		
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				mouseDoubleEvent();
			}
		});
	}

	/**
	 * 创建树的内容
	 */
	public void createContent() {
		Image image = CacheImage.getCacheImage().getImage(IDmAppConstants.APPLICATION_ID, 
				IDmImageKey.WORK_FLOW);
		
		// 添加一棵树
		treeViewComposite.createTreeRootNode(DmConstants.WORK_FLOW_TREE_ROOT_ID, 
				I18nUtil.getMessage("WORKFLOW"), new WorkFlowTreeNode(), image);
			
		try {
			flowList = SwitchObjectAndFile.getWorkFlowListFromFile();
		} catch (IOException e) {
			logger.error("文件序列化时出错！" + e.getMessage());
			e.printStackTrace();
			return ;
			
		} catch (ClassNotFoundException e) {
			logger.error("文件序列化时出错！" + e.getMessage());
			e.printStackTrace();
			
			return ;
			
		} catch (DocumentException e) {
			logger.error("读取数据的XML文件出错！" + e.getMessage());
			e.printStackTrace();
			
			return ;
		}
		
		try {
			if(flowList != null) {
				for(WorkFlowTreeNode workFlowTreeNode : flowList) {
					treeViewComposite.addNode(DmConstants.WORK_FLOW_TREE_ROOT_ID, workFlowTreeNode.getId(), 
							workFlowTreeNode.getName() + "-" + workFlowTreeNode.getDesc(), workFlowTreeNode);
				}
			}
		} catch(CanNotFoundNodeIDException e) {
			logger.error("添加树节点失败，找不到父节点ID：" + DmConstants.WORK_FLOW_TREE_ROOT_ID);
			e.printStackTrace();
		}
		
		// 创建树的右键菜单
		createTreeMenu();
	}
	
	/** 
	 * 创建菜单
	 */
	private void createTreeMenu() {
		WorkFlowActionGroup workFlowActionGroup = new WorkFlowActionGroup(treeViewer);
		workFlowActionGroup.fillContextMenu(new MenuManager());
	}
	
	/**
	 * 鼠标双击事件
	 */
	public void mouseDoubleEvent() {
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		if(selection.size() == 1) {
			Object obj = selection.getFirstElement();
			if(obj instanceof TreeContent) {
				TreeContent treeContent = (TreeContent) obj;
				Object selObj = treeContent.getObj();
				if(selObj instanceof WorkFlowTreeNode) {
					WorkFlowAction workFlowModifyAction = new WorkFlowAction(WorkFlowActionGroup.MODIFY_FLAG, treeViewer);
					workFlowModifyAction.run();
				}
			}
		}
	}
	
	@Override
	public void setFocus() {
		tree.setFocus();
	}

	public TreeViewComposite getTreeViewComposite() {
		return treeViewComposite;
	}

	public void setTreeViewComposite(TreeViewComposite treeViewComposite) {
		this.treeViewComposite = treeViewComposite;
	}
	
	/**
	 * 为了保证用户修改Action树信息，但未更新工作流信息时，能保证Action的描述一致，
	 * 我们通过再导出工作流的xml时，通过Action的id来获取Action最新的描述信息
	 */
	public static String getDescFromActionId(String id) {
		for(ActionTreeNode actionTreeNode : allActionList) {
			if(actionTreeNode.getName().equals(id)) {
				return actionTreeNode.getDesc();
			}
		}
		
		return null;
	}

}

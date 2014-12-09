/* 文件名：     ActionTreeViewPart.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-9
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
import cn.sunline.suncard.sde.workflow.action.ActionTreeAction;
import cn.sunline.suncard.sde.workflow.action.ActionTreeActionGroup;
import cn.sunline.suncard.sde.workflow.action.WorkFlowAction;
import cn.sunline.suncard.sde.workflow.action.WorkFlowActionGroup;
import cn.sunline.suncard.sde.workflow.common.DmConstants;
import cn.sunline.suncard.sde.workflow.exception.CanNotFoundNodeIDException;
import cn.sunline.suncard.sde.workflow.file.SwitchObjectAndFile;
import cn.sunline.suncard.sde.workflow.model.ActionTreeNode;
import cn.sunline.suncard.sde.workflow.model.ActionTreeNode;
import cn.sunline.suncard.sde.workflow.model.WorkFlowTreeNode;
import cn.sunline.suncard.sde.workflow.resource.IDmAppConstants;
import cn.sunline.suncard.sde.workflow.resource.IDmImageKey;
import cn.sunline.suncard.sde.workflow.tree.factory.TreeContent;
import cn.sunline.suncard.sde.workflow.tree.factory.TreeViewComposite;

/**
 * Action树
 * @author  易振强
 * @version 1.0, 2012-4-9
 * @see 
 * @since 1.0
 */
public class ActionTreeViewPart  extends ViewPart{
	public final static String ID = "cn.sunline.suncard.sde.workflow.tree.ActionTreeViewPart";
	
	private Log logger = LogManager.getLogger(ActionTreeViewPart.class.getName());

	private Tree tree;
	private TreeViewer treeViewer;
	
	private TreeViewComposite treeViewComposite;
	private List<ActionTreeNode> actionList;
	
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
	 * 鼠标双击事件
	 */
	public void mouseDoubleEvent() {
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		if(selection.size() == 1) {
			Object obj = selection.getFirstElement();
			if(obj instanceof TreeContent) {
				TreeContent treeContent = (TreeContent) obj;
				Object selObj = treeContent.getObj();
				if(selObj instanceof ActionTreeNode) {
					ActionTreeAction actionModifyAction = new ActionTreeAction(ActionTreeActionGroup.MODIFY_FLAG, treeViewer);
					actionModifyAction.run();
				}
			}
		}
	}

	/**
	 * 创建树的内容
	 */
	public void createContent() {
		Image image = CacheImage.getCacheImage().getImage(IDmAppConstants.APPLICATION_ID, 
				IDmImageKey.ACTION_PNG);
		
		// 添加一棵树及其数据
		treeViewComposite.removeAll();
		treeViewComposite.createTreeRootNode(DmConstants.ACTION_TREE_ROOT_ID, 
				I18nUtil.getMessage("ACTIONTREE"), new ActionTreeNode(), image);
		
		try {
			WorkFlowTreeViewPart.allActionList = SwitchObjectAndFile.getActionListFromFile();
			actionList = WorkFlowTreeViewPart.allActionList;
		} catch (IOException e) {
			logger.error("文件序列化时出错！" + e.getMessage());
			e.printStackTrace();
			return ;
			
		} catch (ClassNotFoundException e) {
			logger.error("文件序列化时出错！" + e.getMessage());
			e.printStackTrace();
			
			return ;
			
		} catch (DocumentException e) {
			logger.error("将xml解析成对象出错！" + e.getMessage());
			e.printStackTrace();
			
			return ;
		}
		
		
		try {
			if(actionList != null) {
				for(ActionTreeNode actionTreeNode : actionList) {
					treeViewComposite.addNode(DmConstants.ACTION_TREE_ROOT_ID, actionTreeNode.getName(), 
							actionTreeNode.getName() + "-" + actionTreeNode.getDesc(), actionTreeNode);
				}
			}
		} catch(CanNotFoundNodeIDException e) {
			logger.error("找不到父节点ID——" + DmConstants.ACTION_TREE_ROOT_ID);
			e.printStackTrace();
			return ;
		}
		
		// 创建树的右键菜单
		createTreeMenu();
	}
	
	/** 
	 * 创建菜单
	 */
	private void createTreeMenu() {
		ActionTreeActionGroup actionGroup = new ActionTreeActionGroup(treeViewer);
		actionGroup.fillContextMenu(new MenuManager());
	}
	
	@Override
	public void setFocus() {
		treeViewer.getTree().setFocus();
	}

	public TreeViewComposite getTreeViewComposite() {
		return treeViewComposite;
	}

	
	
}

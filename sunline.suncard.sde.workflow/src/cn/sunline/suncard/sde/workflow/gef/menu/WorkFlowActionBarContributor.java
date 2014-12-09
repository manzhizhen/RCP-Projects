/*
 * 文件名：WorkFlowActionBarContributor.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：工具栏
 * 修改人：  易振强
 * 修改时间：2001-02-16
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.workflow.gef.menu;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.AlignmentRetargetAction;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.SelectAllAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;

/**
 * editor的工具栏
 * @author    易振强
 * @version   [1.0, 2011-11-04]
 * @see       
 * @since     1.0
 */
public class WorkFlowActionBarContributor extends ActionBarContributor{
	@Override
	protected void buildActions() {
		
		addRetargetAction(new UndoRetargetAction());	// 撤销
		addRetargetAction(new RedoRetargetAction());	// 重做
		addRetargetAction(new DeleteRetargetAction());	// 删除
//		addAction(new CopyModelAction(this.getPage().getActivePart()));
//		addAction(new PasteModelAction(this.getPage().getActivePart()));
		
		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());
		
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.LEFT));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.CENTER));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.RIGHT));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.TOP));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.MIDDLE));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.BOTTOM));
		
	}

	@Override
	protected void declareGlobalActionKeys() {
		
	}

	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
		toolBarManager.add(getAction(ActionFactory.REDO.getId()));
		toolBarManager.add(getAction(ActionFactory.DELETE.getId()));
//		toolBarManager.add(getAction(ActionFactory.COPY.getId()));
//		toolBarManager.add(getAction(ActionFactory.PASTE.getId()));
		
		toolBarManager.add(new Separator());
		toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));
		toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));
		
		//工具条上加上组合框
		toolBarManager.add(new ZoomComboContributionItem(getPage()));
		
		toolBarManager.add(new Separator());
		//水平方向对齐
		toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_LEFT));
		toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_CENTER));
		toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_RIGHT));
		
		toolBarManager.add(new Separator());
		//垂直方向对齐
		toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_TOP));
		toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_MIDDLE));
		toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_BOTTOM));
		

	}
	
	@Override
	public void setActiveEditor(IEditorPart editor) {
		// TODO Auto-generated method stub
		super.setActiveEditor(editor);
	}
	
	

}

/* 文件名：     WorkFlowXYLayoutEditPolicy.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	模型创建和位置、大小改变的策略
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */

package cn.sunline.suncard.sde.workflow.gef.policy;

import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.Tool;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.internal.ui.palette.editparts.ToolEntryEditPart;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

import cn.sunline.suncard.sde.workflow.gef.command.ChangeConstraintCommand;
import cn.sunline.suncard.sde.workflow.gef.command.CreateModelCommand;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;
import cn.sunline.suncard.sde.workflow.gef.model.WorkFlowModel;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditor;


/**
 * 模型创建和位置、大小改变的策略
 * @author    易振强
 * @version   [1.0, 2012-3-26]
 * @see       
 * @since     1.0 
 */
public class WorkFlowXYLayoutEditPolicy extends XYLayoutEditPolicy {
	private Log logger = LogManager.getLogger(WorkFlowXYLayoutEditPolicy.class.getName());
	
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		CreateModelCommand command = new CreateModelCommand();
		
		//产生创建图形的尺寸和位置
		Rectangle constraint =(Rectangle) getConstraintFor(request);
		
		//获得新创建的模型
		AbstractModel model = (AbstractModel) request.getNewObject();

		//为该图形设置前面获得的位置和尺寸
		model.setConstraint(constraint);
		
		command.setWorkFlowModel((WorkFlowModel) getHost().getModel());
		command.setModel(model);
		
		return command;
	}

	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		
		ChangeConstraintCommand command = new ChangeConstraintCommand();
		command.setModel((AbstractModel) child.getModel());
		command.setConstraint((Rectangle)constraint);
		
		return command;
	}

	@Override
	protected Command createAddCommand(EditPart child, Object constraint) {
		return createAddCommand(child, constraint);
	}

	@Override
	protected Command getDeleteDependantCommand(Request request) {
		return null;
	}

	@Override
	public Command getCommand(Request request) {
		return super.getCommand(request);
	}

}

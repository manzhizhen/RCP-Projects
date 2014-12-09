/*
 * 文件名：.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述： SUNCARD决策引擎系统
 * 修改人： 周兵
 * 修改时间：2001-02-16
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.workflow.gef.outline;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;
import cn.sunline.suncard.sde.workflow.gef.model.WorkFlowModel;


/**
 * outline树工厂
 * @author  易振强
 * @version 1.0, 2012-3-26
 * @see 
 * @since 1.0
 */
public class TreeEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		if (model instanceof StartModel) {
			part = new StartTreeEditPart();
			
		} else if (model instanceof TaskModel) {
			part = new TaskTreeEditPart();
			
		} else if (model instanceof EndModel) {
			part = new EndTreeEditPart();
			
		} else if (model instanceof WorkFlowModel) {
			part = new WorkFlowTreeEditPart();
			
		}

		if (part != null) {
			part.setModel(model);
		}
		
		return part;
	}

}

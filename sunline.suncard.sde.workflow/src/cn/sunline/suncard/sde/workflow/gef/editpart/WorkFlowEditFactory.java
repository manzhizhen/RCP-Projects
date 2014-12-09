/* 文件名：     EditFactory.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.editpart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.gef.model.LabelModel;
import cn.sunline.suncard.sde.workflow.gef.model.LineModel;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;
import cn.sunline.suncard.sde.workflow.gef.model.WorkFlowModel;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditor;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 控制器工厂
 * 
 * @author 易振强
 * @version 1.0, 2012-3-26
 * @see
 * @since 1.0
 */
public class WorkFlowEditFactory implements EditPartFactory {
	private Log logger = LogManager.getLogger(WorkFlowEditFactory.class.getName());
	
	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = getPartForElement(model);
		part.setModel(model);
		return part;
	}

	public EditPart getPartForElement(Object modelElement) {
		if(modelElement instanceof WorkFlowModel) {
			return new WorkFlowModelEditPart();

		} else if (modelElement instanceof StartModel) {
			return new StartModelEditPart();

		} else if (modelElement instanceof EndModel) {
			return new EndModelEditPart();

		} else if (modelElement instanceof TaskModel) {
			return new TaskModelEditPart();

		} else if(modelElement instanceof LabelModel) {
			 return new LabelEditPart();
			 
		} else if(modelElement instanceof LineModel) {
			 return new LineEditPart((LineModel) modelElement);
			 
		} else {
			logger.error("没有找到该模型对应的控制器：" + modelElement.getClass().toString());
			return null;
		}

	}

}

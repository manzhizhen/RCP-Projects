/* 文件名：     CreateCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.command;

import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;
import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;
import cn.sunline.suncard.sde.workflow.gef.model.WorkFlowModel;
import cn.sunline.suncard.sde.workflow.gef.ui.editor.WorkFlowEditor;
import cn.sunline.suncard.sde.workflow.gef.xml.GefFigureSwitchXml;


/**
 * 创建模型的Command
 * @author  易振强
 * @version 1.0, 2012-3-26
 * @see 
 * @since 1.0
 */
public class CreateModelCommand extends Command {
	public static Log logger = LogManager.getLogger(CreateModelCommand.class.getName());
	
	private WorkFlowModel workFlowModel;	// 集合模型
	private AbstractModel model;			// 要添加的图元模型
	
	@Override
	public boolean canExecute() {
		if(model instanceof StartModel) {
			List<AbstractModel> list = workFlowModel.getChildren();
			
			for(AbstractModel object : list) {
				// 开始图元只能有一个
				if(object instanceof StartModel) {
					return false;
				}
			}
		}
		
		if(model instanceof EndModel) {
			List<AbstractModel> list = workFlowModel.getChildren();
			
			for(AbstractModel object : list) {
				// 结束图元只能有一个
				if(object instanceof EndModel) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public void execute() {
		
//		if(dataObject == null) {
//			if(model.getDataObject() != null) {
//				dataObject = model.getDataObject();
//			} 
//		}
		
		workFlowModel.addChild(model);
		
		GefFigureSwitchXml.initNodeProperty(model, workFlowModel); // 设置模型的xml信息
		
		WorkFlowEditor.checkModel(workFlowModel);
	}
	

	@Override
	public void undo() {
		workFlowModel.removeChild(model);
		WorkFlowEditor.checkModel(workFlowModel);
	}
	
	public void setWorkFlowModel(WorkFlowModel workFlowModel) {
		this.workFlowModel = workFlowModel;
	}
	
	public void setModel(AbstractModel model) {
		this.model = model;
	}
	
	
}

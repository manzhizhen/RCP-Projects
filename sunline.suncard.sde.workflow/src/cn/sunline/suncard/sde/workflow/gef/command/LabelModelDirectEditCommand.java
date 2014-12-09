/* 文件名：     LabelModelDirectEditCommand.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	LabelModel的直接编辑策略
 * 修改人：     易振强
 * 修改时间：2011-12-8
 * 修改内容：创     建
 */
package cn.sunline.suncard.sde.workflow.gef.command;

import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;
import cn.sunline.suncard.sde.workflow.gef.model.LabelModel;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;


/**
 *  直接编辑指令
 *  对模型文本直接编辑的指令
 * @author     易振强
 * @version   [1.0, 2011-12-8]
 * @see       
 * @since     1.0
 */
public class LabelModelDirectEditCommand extends Command {
	private AbstractModel model;
	private String oldText;
	private String newText;
	
	@Override
	public void execute() {
		//记录以前的文本
		oldText = model.getLabelName();
		//设置为新文本
		if(model instanceof LabelModel) {
			AbstractModel parentModel = ((LabelModel)model).getParentModel();
			parentModel.setLabelName(newText);
		} else {
			model.setLabelName(newText);
		}
	}
	
	public void setModel(Object model){
		this.model = (AbstractModel) model;
	}
	
	public void  setText(String text){
		newText = text;
	}

	@Override
	public void undo() {
		model.setLabelName(oldText);
	}
	
	

}

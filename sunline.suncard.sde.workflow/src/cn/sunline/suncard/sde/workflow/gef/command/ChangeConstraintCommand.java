/* 文件名：     ChangeConstraintCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.command;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.sde.workflow.gef.model.AbstractModel;

/**
 * 改变模型尺寸和大小的Command
 * @author 易振强
 * @version 1.0, 2012-3-26
 * @see
 * @since 1.0
 */
public class ChangeConstraintCommand extends Command {
	private AbstractModel model;
	private Rectangle constraint;
	private Rectangle oldConstraint;

	@Override
	public void execute() {
		model.setConstraint(constraint);
	}
	
	@Override
	public void undo() {
		model.setConstraint(oldConstraint);
	}
	
	public void setModel(AbstractModel model) {
		this.model = model;
		//记住以前的状态
		oldConstraint = this.model.getConstraint();
	}

	public void setConstraint(Rectangle constraint) {
		this.constraint = constraint;
	}

	public void setOldConstraint(Rectangle oldConstraint) {
		this.oldConstraint = oldConstraint;
	}

}

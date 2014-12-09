/* 文件名：     DelModelEditPolicy.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	删除模型的策略
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.policy;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import cn.sunline.suncard.powerdesigner.gef.command.DelTableModelCommand;
import cn.sunline.suncard.powerdesigner.gef.command.DelTableShortcutModelCommand;
import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableShortcutGefModel;



/**
 * 删除模型的策略
 * @author    易振强
 * @version   [1.0, 2011-11-04]
 * @see       
 * @since     1.0 
 */
public class DelModelEditPolicy extends ComponentEditPolicy {
	//重载createDeleteCommand
	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		AbstractGefModel gefModel = (AbstractGefModel) getHost().getModel();
		if(gefModel instanceof TableGefModel) {
			//调用deleteCommand
			DelTableModelCommand deleteCommand = new DelTableModelCommand();
			//注意这类的getHost方法得到的是ModuleModel的EditPart，这是因为这个
			//ScoreEditPolicy要安装到ModuleModel对应的ModuleModel中
			deleteCommand.setDatabaseDiagramGefModel(getHost().getParent().getModel());
			deleteCommand.setGefModel((TableGefModel) getHost().getModel());
			
			return deleteCommand;
		} else if(gefModel instanceof TableShortcutGefModel) {
			//调用deleteCommand
			DelTableShortcutModelCommand deleteCommand = new DelTableShortcutModelCommand();
			//注意这类的getHost方法得到的是ModuleModel的EditPart，这是因为这个
			//ScoreEditPolicy要安装到ModuleModel对应的ModuleModel中
			deleteCommand.setDatabaseDiagramGefModel(getHost().getParent().getModel());
			deleteCommand.setGefModel((TableShortcutGefModel) gefModel);
			
			return deleteCommand;
		}
		
		return null;
	}

	@Override
	public Command getCommand(Request request) {
		return super.getCommand(request);
	}

	@Override
	protected Command getDeleteCommand(GroupRequest request) {
		return super.getDeleteCommand(request);
	}

	

}

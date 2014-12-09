/**
 * 文件名：CopyColumnModelCommand.java
 * 版权：
 * 描述：
 * 修改人： Manzhizhen
 * 修改时间：2013-3-15
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.command;

import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;

/**
 * 复制Domains列对象的Command
 * @author Manzhizhen
 * @version 1.0 2013-3-15
 * @see
 * @since 1.0
 */
public class CopyColumnModelCommand extends Command {
	private List<ColumnModel> copyColumnModelList;
	
	@Override
	public void execute() {
		if(!canExecute()) {
			return ;
		}
		
		//保存内容到剪贴板
        Clipboard.getDefault().setContents(copyColumnModelList);
		super.execute();
	}
	
	@Override
	public boolean canExecute() {
		if(copyColumnModelList == null || copyColumnModelList.size() == 0) {
			return false;
		}
		
		for(ColumnModel columnModel : copyColumnModelList) {
			if(!columnModel.isDomainColumnModel()) {
				return false;
			}
		}
		
		return true;
	}

	public void setCopyColumnModelList(List<ColumnModel> copyColumnModelList) {
		this.copyColumnModelList = copyColumnModelList;
	}
	
	
	
}

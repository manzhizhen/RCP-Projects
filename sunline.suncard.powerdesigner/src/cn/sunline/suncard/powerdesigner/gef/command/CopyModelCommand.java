/* 文件名：     CopyModelCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	复制模型的Command，把模型复制到剪切板中
 * 修改人：     易振强
 * 修改时间：2011-11-21
 * 修改内容：创     建
 */
package cn.sunline.suncard.powerdesigner.gef.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 复制模型的Command，把模型复制到剪切板中
 * @author 易振强
 * @version [1.0, 2011-11-01]
 * @see
 * @since 1.0
 */
public class CopyModelCommand extends Command {
	//用于保存要复制的对象
    private List<AbstractGefModel> copyList;
    
	private Log logger = LogManager.getLogger(CopyModelCommand.class.getName());

    public CopyModelCommand(List<Object> selectedObjects) {
    	logger.info("public CopyModelCommand(List<Object> selectedObjects) start...");
    	
        if (selectedObjects == null || selectedObjects.isEmpty()) {
            return;
        }

        copyList = new ArrayList<AbstractGefModel>();
        for (Object object : selectedObjects) {
        	if(object instanceof EditPart) {
        		EditPart editPart = (EditPart) object;
        		
        		Object model = editPart.getModel();
        		
        		// 只对模型进行复制，如果只选中线，则不复制
        		if(model instanceof AbstractGefModel) {
        			copyList.add((AbstractGefModel) model);
        		} else {
        			continue;
        		}
        	} else if(object instanceof TableGefModel) {
        		copyList.add((AbstractGefModel) object);
        	}
        }
    }

    @Override
    public boolean canExecute() {
        if (copyList == null || copyList.isEmpty()) {
            return false;
        }
        
        return true;
    }

    @Override
    public void execute() {
    	logger.info(" public void execute() start ...");
    	
        if (!canExecute()) {
            return;
        }

        //保存内容到剪贴板
        Clipboard.getDefault().setContents(copyList);
    }

    @Override
    public boolean canUndo() {
        return false;
    }

}

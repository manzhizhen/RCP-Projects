/* 文件名：     UpdateModuleLabelCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-13
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.command;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.commands.Command;

import cn.sunline.suncard.powerdesigner.model.FileModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.xml.ModuleXmlModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.xml.ModuleDataXmlManager;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 更新模块标签的Command
 * @author  Manzhizhen
 * @version 1.0, 2012-11-13
 * @see 
 * @since 1.0
 */
public class UpdateModuleLabelCommand extends Command{
	private List<ModuleXmlModel> moduleXmlModelList;
	private Log logger = LogManager.getLogger(UpdateModuleLabelCommand.class.getName());

	@Override
	public void execute() {
		try {
			ModuleDataXmlManager.setModuleXmlModel(moduleXmlModelList);
		} catch (UnsupportedEncodingException e) {
			logger.error("写入配置文件失败！" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("写入配置文件失败！" + e.getMessage());
			e.printStackTrace();
		}
		
		super.execute();
	}
	
	@Override
	public boolean canUndo() {
		return false;
	}

	public void setModuleXmlModelList(List<ModuleXmlModel> moduleXmlModelList) {
		this.moduleXmlModelList = moduleXmlModelList;
	}

	
	
}

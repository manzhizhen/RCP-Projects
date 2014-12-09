/* 文件名：     PaletteFactory.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	调色板工厂
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.ui.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cn.sunline.suncard.sde.workflow.Activator;
import cn.sunline.suncard.sde.workflow.gef.editpart.WorkFlowConnectionCreationTool;
import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.gef.model.LineModel;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;
import cn.sunline.suncard.sde.workflow.resource.IDmImageKey;

import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 调色板工厂
 * @author    易振强
 * @version   1.0  2011-12-1
 * @see       
 * @since     WF  1.0 
 */
public class PaletteFactory {

	// 普通模型抽屉
	private PaletteDrawer drawer;
	
	// 空操作的工具箱
	private PaletteDrawer emptyDrawer;
	
	private ToolEntry selectTool;
	private PaletteRoot root;
	
	
	public PaletteRoot createPalette() {
		// 首先创建一个palette的路径
		root = new PaletteRoot();
		
		// 创建一个工具组用于放置常规Tools
		PaletteGroup toolGroup = new PaletteGroup(I18nUtil.getMessage("tool"));

		// 创建一个GEF提供的selection工具并将其放入toolgroup中
		selectTool = new SelectionToolEntry();
		toolGroup.add(selectTool);
		
		ImageDescriptor descriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IDmImageKey.CONNECTION);
		
		String combineConnectionLine = I18nUtil.getMessage("combineConnectionLine");
		CreationToolEntry connectionLine = new ConnectionCreationToolEntry(
				combineConnectionLine, combineConnectionLine, new SimpleFactory(LineModel.class),
				descriptor, descriptor);
		
		connectionLine.setToolClass(WorkFlowConnectionCreationTool.class);
		
		toolGroup.add(connectionLine);
		
		
		// 该选择的工具是缺省被选择的工具
		root.setDefaultEntry(selectTool);

		// 创建一个GEF提供的“Marpuee多选”工具并将其放入toolGroup中去
//		tool = new MarqueeToolEntry();
//		toolGroup.add(tool);

		// 创建一个Drawer（抽屉）放置绘画工具，该抽屉被称为“画图”
		drawer = new PaletteDrawer(I18nUtil.getMessage("model"));
		
		// 创建普通模型工具
		createModelTool();

		root.add(toolGroup);

		// 将创建的工具加到root上

		if(drawer != null) {
			root.add(drawer);
		}
		
		
		if(emptyDrawer != null) {
			root.add(emptyDrawer);
		}
		
		return root;
    }



	/**
	 * 创建各种模型工具
	 */
	private void createModelTool() {
		createStart();
		createTask();
		createEnd();
	}
	
	/**
	 * 创建开始图元
	 */
	private void createStart() {
		// 模型工具所对应的图标
		ImageDescriptor descriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IDmImageKey.MODEL_START);

		ImageDescriptor s_descriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IDmImageKey.S_MODEL_START);

		// 创建“开 始”工具
		// 要实现拖拽，就不能用CreationToolEntry，必须用CombinedTemplateCreationEntry
		CombinedTemplateCreationEntry startToolEntry = new CombinedTemplateCreationEntry(
				I18nUtil.getMessage("START_MODEL"),
				I18nUtil.getMessage("CREATE_START_MODEL"), StartModel.class, new SimpleFactory(
						StartModel.class), s_descriptor, descriptor);
		
		drawer.add(startToolEntry);
	}
	
	/**
	 * 创建结束图元
	 */
	private void createEnd() {
		ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, IDmImageKey.MODEL_END);

		ImageDescriptor s_descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, IDmImageKey.S_MODEL_END);

		// 创建“结 束”工具
		CombinedTemplateCreationEntry endToolEntry = new CombinedTemplateCreationEntry(
				I18nUtil.getMessage("END_MODEL"),
				I18nUtil.getMessage("CREATE_END_MODEL"), EndModel.class, new SimpleFactory(
						EndModel.class), s_descriptor, descriptor);
		
		drawer.add(endToolEntry);
	}
	
	/**
	 * 创建任务图元
	 */
	private void createTask() {
		ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, IDmImageKey.MODEL_TASK);

		ImageDescriptor s_descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, IDmImageKey.S_MODEL_TASK);

		// 创建“任务”工具
		CombinedTemplateCreationEntry taskToolEntry = new CombinedTemplateCreationEntry(
				I18nUtil.getMessage("TASK_MODEL"),
				I18nUtil.getMessage("CREATE_TASK_MODEL"), TaskModel.class, new SimpleFactory(
						TaskModel.class), s_descriptor, descriptor);
		
		drawer.add(taskToolEntry);
	}

	public ToolEntry getSelectTool() {
		return selectTool;
	}
	
	
	
}

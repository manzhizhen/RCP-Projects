/* 文件名：     PaletteFactory.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	调色板工厂
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.ui.editor;

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


import cn.sunline.suncard.powerdesigner.Activator;
import cn.sunline.suncard.powerdesigner.gef.model.LineGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
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
		PaletteGroup toolGroup = new PaletteGroup(I18nUtil.getMessage("TOOL"));

		// 创建一个GEF提供的selection工具并将其放入toolgroup中
		selectTool = new SelectionToolEntry();
		toolGroup.add(selectTool);
		
		ImageDescriptor descriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IDmImageKey.CONNECTION);
		
		String referenceLine = I18nUtil.getMessage("REFERENCE_LINE");
		CreationToolEntry connectionLine = new ConnectionCreationToolEntry(
				referenceLine, referenceLine, new SimpleFactory(LineGefModel.class),
				descriptor, descriptor);
		
		connectionLine.setToolClass(PDConnectionCreationTool.class);
		
		toolGroup.add(connectionLine);
		
		
		// 该选择的工具是缺省被选择的工具
		root.setDefaultEntry(selectTool);

		// 创建一个Drawer（抽屉）放置绘画工具，该抽屉被称为“画图”
		drawer = new PaletteDrawer(I18nUtil.getMessage("MODEL"));
		
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
		createTable();
	}
	
	/**
	 * 创建表格图元
	 */
	private void createTable() {
		// 模型工具所对应的图标
		ImageDescriptor descriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IDmImageKey.TABLE_16);

		ImageDescriptor s_descriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						IDmImageKey.TABLE_16);

		// 创建“表格”工具
		// 要实现拖拽，就不能用CreationToolEntry，必须用CombinedTemplateCreationEntry
		CombinedTemplateCreationEntry tableToolEntry = new CombinedTemplateCreationEntry(
				I18nUtil.getMessage("TABLE"),
				I18nUtil.getMessage("CREATE_TABLE"), TableGefModel.class, new SimpleFactory(
						TableGefModel.class), s_descriptor, descriptor);
//		tableToolEntry.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
		
		drawer.add(tableToolEntry);
	}

	public ToolEntry getSelectTool() {
		return selectTool;
	}
	
	
	
	
}

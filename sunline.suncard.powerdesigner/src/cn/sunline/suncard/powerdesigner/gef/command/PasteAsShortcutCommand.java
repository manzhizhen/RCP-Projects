/* 文件名：     PasteAsShortcutCommand.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-10
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.gef.model.AbstractGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableGefModel;
import cn.sunline.suncard.powerdesigner.gef.model.TableShortcutGefModel;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.model.TableShortcutModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 粘贴为快捷方式的Command
 * @author  Manzhizhen
 * @version 1.0, 2012-12-10
 * @see 
 * @since 1.0
 */
public class PasteAsShortcutCommand extends Command {
	public static String ID = "sunline.suncard.powerdesigner.commands.saveasshortcut";
	
	private List<AbstractGefModel> copyList;
	private DatabaseDiagramEditor databaseDiagramEditor;
	
	public static Log logger = LogManager.getLogger(PasteAsShortcutCommand.class
			.getName());
	
	public PasteAsShortcutCommand() {
		// 从剪贴板获取要复制的内容，保存到copyList中，
		copyList = (ArrayList<AbstractGefModel>) Clipboard.getDefault()
				.getContents();
	}
	
	@Override
	public void execute() {
		databaseDiagramEditor = null;
		IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor();

		if (part instanceof DatabaseDiagramEditor) {
			databaseDiagramEditor = (DatabaseDiagramEditor) part;
		} else {
			logger.error("无法获取当前活跃的DatabaseDiagramEditor，PasteModelCommand执行失败！");
			return;
		}
		
		// 为了实现高度统一，模型创建还是采用本来的创建命令来创建
		for (AbstractGefModel node : copyList) {
			AbstractGefModel cloneNode = node.clone();
			
			Rectangle rect = cloneNode.getConstraint();

			// 设置新模型出现的位置，在旧模型的右上方
			cloneNode.setConstraint(new Rectangle(rect.x + 100, rect.y - 100,
					rect.width, rect.height));
			
			CreateModelCommand createCommand = new CreateModelCommand();

			createCommand
					.setDatabaseDiagramGefModel(((DatabaseDiagramEditor) databaseDiagramEditor)
							.getDatabaseDiagramGefModel());
			
			// 因为是粘贴为快捷方式，所以如果剪切板中的该对象是表格模型，需要转换为对应的快捷方式对象才能粘贴
			if(node instanceof TableGefModel) {
				TableShortcutGefModel shortcutGefModel = new TableShortcutGefModel();
				shortcutGefModel.setConstraint(cloneNode.getConstraint());
				shortcutGefModel.setNodeXmlProperty(cloneNode.getNodeXmlProperty());
				
				// 新建快捷方式对象，并设置其ID为表格对象ID加上前缀一样
				TableShortcutModel shortcutModel = new TableShortcutModel();
				shortcutModel.setTargetTableModel((TableModel) node.getDataObject());
				
				shortcutGefModel.setDataObject(shortcutModel);
				createCommand.setModel(shortcutGefModel);
				
			// 如果是快捷方式对象
			} else {
				createCommand.setModel(cloneNode);
			}
			
			createCommand.setPaste(true); // 标记是粘贴调用的该Command

			((DatabaseDiagramEditor) databaseDiagramEditor).getCommandStack()
					.execute(createCommand);
		}
		
		
		super.execute();
	}
	
	@Override
	public boolean canExecute() {
		if (copyList == null || copyList.isEmpty()) {
			return false;
		}
		return true;
	}
}

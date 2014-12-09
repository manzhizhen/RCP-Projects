package cn.sunline.suncard.powerdesigner.handler;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.powerdesigner.gef.ui.dialog.ColumnPropertiesDialog;
import cn.sunline.suncard.powerdesigner.gef.ui.dialog.ReferencePropertiesDialog;
import cn.sunline.suncard.powerdesigner.gef.ui.dialog.TableGefModelDialog;
import cn.sunline.suncard.powerdesigner.gef.ui.editor.DatabaseDiagramEditor;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

public class RankWindowsHandler extends AbstractHandler{
	private Log logger = LogManager.getLogger(RankWindowsHandler.class.getName());
	
	public static LinkedHashSet<Dialog> getAllDialog() {
		LinkedHashSet<Dialog> allDialog = new LinkedHashSet<Dialog>();
		
		allDialog.addAll(TableGefModelDialog.getTableDialogMap().values());
		allDialog.addAll(ColumnPropertiesDialog.getColumnDialogMap().values());
		allDialog.addAll(ReferencePropertiesDialog.getLineDialogMap().values());
		
		return allDialog;
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		if(window == null){
			logger.error("当前无活跃的WorkbenchWindow！");
			return null;
		}
		
		int y = 60;
		int x = 10;
		
		int i = 0;
		boolean flag = false; // 标记是否已经超过最大排列数量
		
		// 先排列表格对话框
//		Collection<TableGefModelDialog> tableDialogs = TableGefModelDialog
//				.getTableDialogMap().values();
		LinkedHashSet<Dialog> allDialogs = getAllDialog();
		
		for(Dialog dialog : allDialogs) {
			if(flag) {
				dialog.getShell().setMinimized(true);
				continue ;
			}
			
			dialog.getShell().setMinimized(false);
			dialog.getShell().setLocation(x + (i % DmConstants.RANK_WINDOW_ROW_NUM) * DmConstants.RANK_WINDOW_WIDTH
					, y + (i /DmConstants.RANK_WINDOW_ROW_NUM) * DmConstants.RANK_WINDOW_ROW_SIZE);
			dialog.getShell().setSize(new Point(DmConstants.RANK_WINDOW_WIDTH
					, DmConstants.RANK_WINDOW_HEIGH));
			dialog.getShell().setActive();
			
			i++;
			// 超过窗口最大数量，其他的最小化
			if(i >= DmConstants.RANK_WINDOW_NUM) {
				flag = true;
			}
			
		}
		
//		// 再排列列对话框
//		Collection<ColumnPropertiesDialog> columnDialogs = ColumnPropertiesDialog
//				.getColumnDialogMap().values();
//		for(ColumnPropertiesDialog dialog : columnDialogs) {
//			if(flag) {
//				dialog.getShell().setMinimized(true);
//				continue ;
//			}
//			
//			dialog.getShell().setMinimized(false);
//			dialog.getShell().setLocation(x + (i % DmConstants.RANK_WINDOW_ROW_NUM) * DmConstants.RANK_WINDOW_WIDTH
//					, y + (i /DmConstants.RANK_WINDOW_ROW_NUM) * DmConstants.RANK_WINDOW_ROW_SIZE);
//			dialog.getShell().setSize(new Point(DmConstants.RANK_WINDOW_WIDTH
//					, DmConstants.RANK_WINDOW_HEIGH));
//			dialog.getShell().setActive();
//			
//			i++;
//			// 超过窗口最大数量，其他的最小化
//			if(i >= DmConstants.RANK_WINDOW_NUM) {
//				flag = true;
//			}
//		}
//		
//		// 最后排列连接线对话框
//		Collection<ReferencePropertiesDialog> lineDialogs = ReferencePropertiesDialog
//				.getLineDialogMap().values();
//		for(ReferencePropertiesDialog dialog : lineDialogs) {
//			if(flag) {
//				dialog.getShell().setMinimized(true);
//				continue ;
//			}
//			
//			dialog.getShell().setMinimized(false);
//			dialog.getShell().setLocation(x + (i % DmConstants.RANK_WINDOW_ROW_NUM) * DmConstants.RANK_WINDOW_WIDTH
//					, y + (i /DmConstants.RANK_WINDOW_ROW_NUM) * DmConstants.RANK_WINDOW_ROW_SIZE);
//			dialog.getShell().setSize(new Point(DmConstants.RANK_WINDOW_WIDTH
//					, DmConstants.RANK_WINDOW_HEIGH));
//			dialog.getShell().setActive();
//			
//			i++;
//			// 超过窗口最大数量，其他的最小化
//			if(i >= DmConstants.RANK_WINDOW_NUM) {
//				flag = true;
//			}
//		}
		
		return null;
	}
	
}

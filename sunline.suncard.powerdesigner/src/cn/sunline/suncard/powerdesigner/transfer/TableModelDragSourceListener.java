/* 文件名：     TableModelDragSourceListener.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-17
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.transfer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.powerdesigner.tree.DatabaseTreeViewPart;
import cn.sunline.suncard.powerdesigner.tree.factory.TreeContent;

/**
 * 表格模型拖动源监听
 * 
 * @author Manzhizhen
 * @version 1.0, 2013-1-17
 * @see
 * @since 1.0
 */
public class TableModelDragSourceListener implements DragSourceListener {
	private DatabaseTreeViewPart databaseTreeViewPart;

	public TableModelDragSourceListener(
			DatabaseTreeViewPart databaseTreeViewPart) {
		this.databaseTreeViewPart = databaseTreeViewPart;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		if (((DragSource) event.widget).getControl() instanceof Tree) {
			IStructuredSelection selection = (IStructuredSelection) databaseTreeViewPart
					.getTreeViewer().getSelection();
			if (selection.isEmpty()) {
				event.doit = false;

			} else {
				for (Object selectObj : selection.toArray()) {
					if (selectObj instanceof TreeContent) {
						TreeContent treeContent = (TreeContent) selectObj;
						if (treeContent.getObj() instanceof TableModel) {
							return;
						}
					}
				}
			}

			event.doit = false;
		}
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		if (TableModelTransfer.getInstance().isSupportedType(event.dataType)) {
			if (((DragSource) event.widget).getControl() instanceof Tree) {
				IStructuredSelection selection = (IStructuredSelection) databaseTreeViewPart
						.getTreeViewer().getSelection();

				List<TableModel> tableModelList = new ArrayList<TableModel>();
				for (Object obj : selection.toList()) {
					if (obj instanceof TreeContent) {
						if (((TreeContent) obj).getObj() instanceof TableModel) {
							tableModelList.add((TableModel) ((TreeContent) obj)
									.getObj());
						}
					}
				}

				event.data = tableModelList;
			}
		}
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		if (event.detail == DND.DROP_MOVE) {
		}
	}
}

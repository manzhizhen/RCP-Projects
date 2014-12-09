/* 文件名：     ColumnModelDropTargetListener.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-13
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.listener;

import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.widgets.TableItem;

import cn.sunline.suncard.powerdesigner.gef.ui.dialog.TableGefModelDialog;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.ui.dialog.composite.ColumnModelComposite;


/**
 * 拖放ColumnModel时进行的监听器
 * 作为拖拽目标的监听器
 * @author  Manzhizhen
 * @version 1.0, 2012-9-13
 * @see 
 * @since 1.0
 */
public class ColumnModelDropTargetListener implements DropTargetListener{
	private ColumnModelComposite columnModelComposite;
	
	public ColumnModelDropTargetListener(ColumnModelComposite columnModelComposite) {
		this.columnModelComposite = columnModelComposite;
	}
	
	@Override
	public void dragEnter(DropTargetEvent event) {
	}

	@Override
	public void dragLeave(DropTargetEvent event) {
	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {
	}

	@Override
	public void dragOver(DropTargetEvent event) {
	}

	@Override
	public void drop(DropTargetEvent event) {
		
		if(columnModelComposite != null && event.data instanceof ColumnModel[] && event.item instanceof TableItem) {
//			dialog.addCopyColumnModel((ColumnModel[]) event.data);
			columnModelComposite.moveColumnModelListTo((ColumnModel[]) event.data, (TableItem) event.item);
		}
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
	}

}

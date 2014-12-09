/* 文件名：     ColumnModelDrogTargetListener.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-13
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.listener;

import java.util.List;

import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import cn.sunline.suncard.powerdesigner.gef.ui.dialog.TableGefModelDialog;
import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.ui.dialog.composite.ColumnModelComposite;

/**
 * 拖放ColumnModel时进行的监听器
 * 作为拖拽源的监听器
 * @author  Manzhizhen
 * @version 1.0, 2012-9-13
 * @see 
 * @since 1.0
 */
public class ColumnModelDragSourceListener implements DragSourceListener{
	private ColumnModelComposite columnModelComposite;
	
	public ColumnModelDragSourceListener(ColumnModelComposite columnModelComposite) {
		this.columnModelComposite = columnModelComposite;
	}
	
	@Override
	public void dragStart(DragSourceEvent event) {
		event.doit = true;
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		List list = columnModelComposite.getTableSelection();
		if(list != null) {
			event.data = list.toArray(new ColumnModel[]{});
		}
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
	}

}

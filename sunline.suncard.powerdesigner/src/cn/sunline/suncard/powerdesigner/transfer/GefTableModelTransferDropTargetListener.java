/* 文件名：     GefTableModelTransferDropTargetListener.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-17
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.transfer;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;

/**
 * 为GraphicalEditor添加Drop监听
 * @author  Manzhizhen
 * @version 1.0, 2013-1-17
 * @see 
 * @since 1.0
 */
public class GefTableModelTransferDropTargetListener extends 
	AbstractTransferDropTargetListener{

	private TableModelCreationFactory factory = new TableModelCreationFactory();
	
	/**
	 * @param viewer
	 * @param xfer
	 */
	public GefTableModelTransferDropTargetListener(EditPartViewer viewer) {
		super(viewer, TableModelTransfer.getInstance());
	}

	@Override
	protected void updateTargetRequest() {
	}

	@Override
	protected Request createTargetRequest() {
		CreateRequest request = new CreateRequest();
		request.setFactory(factory);
		return request;
	}
	
	@Override
	protected void handleDragOver() {
		getCurrentEvent().detail = DND. DROP_MOVE | DND. DROP_COPY ;
		super.handleDragOver();
	}
	
	@Override
	protected void handleDrop() {
		super.handleDrop();
	}
}

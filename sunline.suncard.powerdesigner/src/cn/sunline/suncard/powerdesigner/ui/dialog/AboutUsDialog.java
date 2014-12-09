/* 文件名：     AboutUsDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-11-13
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.dialogs.AboutDialog;

import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * 关于我们的对话框
 * @author  Manzhizhen
 * @version 1.0, 2012-11-13
 * @see 
 * @since 1.0
 */
public class AboutUsDialog extends Dialog{

	private Log logger = LogManager.getLogger(AboutUsDialog.class.getName());
	private Composite composite;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Canvas canvas;
	
	/**
	 * @param parentShell
	 */
	public AboutUsDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("关于SunlineDesigner");
		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(562, 332);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Control control =   super.createDialogArea(parent);
		
		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new FormLayout());
		
		createControl();
		initControlValue();
		
		return control;
	}
	
	private void initControlValue() {
		canvas.setBackgroundImage(CacheImage.getCacheImage().getImage(DmConstants.APPLICATION_ID
				, IDmImageKey.APPLICATION_IMAGE_128));
	}

	private void createControl() {
		canvas = new Canvas(composite, SWT.NONE);
		FormData fd_canvas = new FormData();
		fd_canvas.top = new FormAttachment(0);
		fd_canvas.left = new FormAttachment(0);
		fd_canvas.bottom = new FormAttachment(0, 128);
		fd_canvas.right = new FormAttachment(0, 128);
		canvas.setLayoutData(fd_canvas);
		
		IFigure imageNameDates = new Figure();
		LightweightSystem lws = new LightweightSystem(canvas);
		lws.setContents(imageNameDates);
		
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 1;
		
		imageNameDates.setLayoutManager(gridLayout);
		imageNameDates.add(new ImageFigure(CacheImage.getCacheImage().getImage(
				DmConstants.APPLICATION_ID, IDmImageKey.APPLICATION_IMAGE_128)));
		
		Label lblNewLabel = formToolkit.createLabel(composite, "SunlineDesigner", SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 13, SWT.BOLD));
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(canvas, 14);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		
		Label lblNewLabel_1 = formToolkit.createLabel(composite, "版本 1.0", SWT.NONE);
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		
		Label lblNewLabel_2 = new Label(composite, SWT.WRAP);
		fd_lblNewLabel_1.bottom = new FormAttachment(lblNewLabel_2, -59);
		FormData fd_lblNewLabel_2 = new FormData();
		fd_lblNewLabel_2.top = new FormAttachment(0, 171);
		fd_lblNewLabel_2.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_lblNewLabel_2.right = new FormAttachment(100, -14);
		fd_lblNewLabel_2.height = 50;
		lblNewLabel_2.setLayoutData(fd_lblNewLabel_2);
		formToolkit.adapt(lblNewLabel_2, true, true);
		lblNewLabel_2.setText("版权所有 © 2002-2012 深圳市长亮科技股份有限公司 V1R3 System Http://oa.sunline.cn ");
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
	}
	
}

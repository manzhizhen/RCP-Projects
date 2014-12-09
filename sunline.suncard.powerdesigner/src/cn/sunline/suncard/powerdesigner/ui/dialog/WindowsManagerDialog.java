/* 文件名：     WindowsManagerDialog.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-13
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cn.sunline.suncard.powerdesigner.action.CloseDialogActionGroup;
import cn.sunline.suncard.powerdesigner.gef.ui.dialog.ColumnPropertiesDialog;
import cn.sunline.suncard.powerdesigner.gef.ui.dialog.ReferencePropertiesDialog;
import cn.sunline.suncard.powerdesigner.gef.ui.dialog.TableGefModelDialog;
import cn.sunline.suncard.powerdesigner.handler.RankWindowsHandler;
import cn.sunline.suncard.powerdesigner.provider.WindowsLableProvider;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.IDmImageKey;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

/**
 * 窗口管理对话框
 * @author  Manzhizhen
 * @version 1.0, 2012-12-13
 * @see 
 * @since 1.0
 */
public class WindowsManagerDialog extends Dialog{
	public static WindowsManagerDialog windowsManagerDialog;
	private Composite composite;

	private Log logger = LogManager.getLogger(WindowsManagerDialog.class
			.getName());

	private ListViewer windowsListViewer;

	private List windowsList;
	
	/**
	 * @param parentShell
	 */
	public WindowsManagerDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.RESIZE);
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("窗口管理");
		newShell.setImage(CacheImage.getCacheImage().getImage(
				DmConstants.PD_APPLICATION_ID, IDmImageKey.WINDOWS_IMAGE_64));
		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(260, 509);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Control control = super.createDialogArea(parent);

		composite = new Composite((Composite) control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		composite.setLayout(new FormLayout());
		
		createControl();
		initControlData();
		createEvent();


		return control;
	}
	
	private void createEvent() {
		windowsListViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection select = (IStructuredSelection) windowsListViewer.getSelection();
				if(!select.isEmpty()) {
					showDialog((Dialog) select.getFirstElement());
				}
			}
		});
		
	}

	private void initControlData() {
		windowsListViewer.setLabelProvider(new WindowsLableProvider());
		windowsListViewer.setContentProvider(new ArrayContentProvider());
		
		windowsListViewer.setInput(RankWindowsHandler.getAllDialog());
	}

	private void createControl() {
		windowsListViewer = new ListViewer(composite, SWT.BORDER | SWT.V_SCROLL);
		windowsList = windowsListViewer.getList();
		FormData fd_windowsList = new FormData();
		fd_windowsList.right = new FormAttachment(100);
		fd_windowsList.bottom = new FormAttachment(100);
		fd_windowsList.top = new FormAttachment(0);
		fd_windowsList.left = new FormAttachment(0);
		windowsList.setLayoutData(fd_windowsList);
		
		// 给List添加右键关闭对话框的菜单
		CloseDialogActionGroup actionGroup = new CloseDialogActionGroup(windowsListViewer);
		actionGroup.fillContextMenu(new MenuManager());
	}
	
	@Override
	public int open() {
		windowsManagerDialog = this;
		return super.open();
	}

	@Override
	public boolean close() {
		windowsManagerDialog = null;
		return super.close();
	}
	
	public static WindowsManagerDialog getWindowsManagerDialog() {
		return windowsManagerDialog;
	}

	public static void setWindowsManagerDialog(
			WindowsManagerDialog windowsManagerDialog) {
		WindowsManagerDialog.windowsManagerDialog = windowsManagerDialog;
	}
	
	
	public ListViewer getWindowsListViewer() {
		return windowsListViewer;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, I18nUtil.getMessage("OK"),
				true);
	}


	public void showDialog(Dialog dialog) {
		try {
			new RankWindowsHandler().execute(null);
			
			// 显示该dialog
			if(dialog != null && dialog.getShell() != null && !dialog.getShell().isDisposed()) {
				dialog.getShell().setMinimized(false);
				dialog.getShell().setSize(600, 500);
				dialog.getShell().setActive();
			}
		} catch (ExecutionException e) {
			logger.error("执行RankWindowsHandler出错！" + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public static void refreshData() {
		if(getWindowsManagerDialog() != null && getWindowsManagerDialog()
				.getShell() != null && !getWindowsManagerDialog().getShell().isDisposed()) {
			WindowsManagerDialog dialog = getWindowsManagerDialog();
			dialog.getWindowsListViewer().setInput(RankWindowsHandler.getAllDialog());
		}
	}
	
	
}

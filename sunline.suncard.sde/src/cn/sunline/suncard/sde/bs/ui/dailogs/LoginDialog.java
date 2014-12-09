package cn.sunline.suncard.sde.bs.ui.dailogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.sunline.suncard.sde.bs.biz.BsUserBiz;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.ui.permission.WidgetPermission;
import cn.sunline.suncard.sde.bs.ui.resource.IAppconstans;
import cn.sunline.suncard.sde.bs.ui.resource.IImageKey;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.bs.util.WidgetPermissionUtil;

public class LoginDialog extends Dialog {

	private Text userNameText;
	
	private Text passwordText;
	
	private BsUserBiz bsUserBiz = new BsUserBiz();
	
	Label labelMsg;
	
	public LoginDialog(Shell parentShell) {
		super(parentShell);
	}
	
	Log logger = LogManager.getLogger(LoginDialog.class.getName());
	
	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(SWT.BORDER | SWT.NO_TRIM | SWT.ON_TOP);
	}

	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginRight = 40;
		gridLayout.marginLeft = 280;
		gridLayout.marginTop = 111;
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 16;
		gridLayout.makeColumnsEqualWidth = true;
		container.setLayout(gridLayout);
		
		GridData data = null;
		Image image = null;
		
		container.setBackgroundImage(CacheImage.getCacheImage().getImage(
				IAppconstans.APPLICATION_ID,
				IImageKey.LOGIN_BACKGROUND_IMAGE));
		container.setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		userNameText = new Text(container,SWT.BORDER);
		data = new GridData(SWT.FILL, SWT.FILL ,true, false, 2, 1);
		data.widthHint = 100;
		userNameText.setLayoutData(data);
		
		passwordText = new Text(container, SWT.BORDER);
		passwordText.setEchoChar('*');
		data = new GridData(SWT.FILL, SWT.FILL ,true, false, 2, 1);
		data.widthHint = 100;
		passwordText.setLayoutData(data);
		
//		userNameText.setText("sysadmin");
//		passwordText.setText("000000");
		
		final Button buttonOK = new Button(container, SWT.CENTER);
		image = CacheImage.getCacheImage().getImage(
				IAppconstans.APPLICATION_ID,
				IImageKey.LOGIN_OK_BUTTON_IMAGE);
		buttonOK.setImage(image);
		data = new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1);
		data.widthHint = image.getBounds().width;
		data.heightHint = image.getBounds().height;
		buttonOK.setLayoutData(data);
		buttonOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				okPressed();
			}
		});
		
		// 按钮鼠标侦听事件
		buttonOK.addMouseTrackListener(new MouseTrackAdapter() {
			
			 // 鼠标进入时显示高亮图片
			@Override
			public void mouseEnter(MouseEvent e) {
				buttonOK.setImage(CacheImage.getCacheImage().getImage(
						IAppconstans.APPLICATION_ID,
						IImageKey.LOGIN_OK_BUTTON_LIGHT_IMAGE));
			}
			
			// 鼠标离开时显示正常图片
			@Override
			public void mouseExit(MouseEvent e) {
				buttonOK.setImage(CacheImage.getCacheImage().getImage(
						IAppconstans.APPLICATION_ID,
						IImageKey.LOGIN_OK_BUTTON_IMAGE));
			}
		});
		
//		new Label(container, SWT.NONE);
		
		final Button buttonCancel = new Button(container, SWT.CENTER | SWT.NO_TRIM);
		image = CacheImage.getCacheImage().getImage(
				IAppconstans.APPLICATION_ID,
				IImageKey.LOGIN_CANCEL_BUTTON_IMAGE);
		buttonCancel.setImage(image);
		data = new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1);
		data.widthHint = image.getBounds().width;
		data.heightHint = image.getBounds().height;
		buttonCancel.setLayoutData(data);
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.exit(0);
			}
		});
		
		// 按钮鼠标侦听事件
		buttonCancel.addMouseTrackListener(new MouseTrackAdapter() {

			// 鼠标进入时显示高亮图片
			@Override
			public void mouseEnter(MouseEvent e) {
				buttonCancel.setImage(CacheImage.getCacheImage().getImage(
						IAppconstans.APPLICATION_ID,
						IImageKey.LOGIN_CANCEL_BUTTON_LIGHT_IMAGE));
			}

			// 鼠标离开时显示正常图片
			@Override
			public void mouseExit(MouseEvent e) {
				buttonCancel.setImage(CacheImage.getCacheImage().getImage(
						IAppconstans.APPLICATION_ID,
						IImageKey.LOGIN_CANCEL_BUTTON_IMAGE));
			}
		});
		
		labelMsg = new Label(container, SWT.RIGHT);
//		labelMsg.setText(I18nUtil.getMessage("welcome"));
		data = new GridData(SWT.RIGHT, SWT.FILL ,true, false, 2, 1);
		data.widthHint = 200;
		labelMsg.setLayoutData(data);
		labelMsg.setForeground(new Color(null, 255, 255, 255));
		
		getShell().setDefaultButton(buttonOK);
		
		return container;
	}
	
	@Override
	protected Control createButtonBar(Composite parent) {
		return null;
	}
	
	protected void okPressed() {
		String userName = userNameText.getText();
		String password = passwordText.getText();
		
//		String userName = "sysadmin";
//		String password = "000000";
		
		if(userName.isEmpty()) {
			MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.OK);
			messageBox.setText(I18nUtil.getMessage("message"));
			messageBox.setMessage(I18nUtil.getMessage("messageInfo"));
			messageBox.open();
			userNameText.setFocus();
			return;
		} else if(!isValidate(userName, password)) {
			return;
		}
		logger.info("登录成功了");
		super.okPressed();
	}
	
//	@Override
//	public int open() {
//		okPressed();
//		return Window.OK;
//	}
	
	/**
	 * 验证用户合法性
	 * @param userId 用户ID
	 * @param password 用户密码
	 * @return
	 */
	private boolean isValidate(final String userId, final String password) {
		String msg = bsUserBiz.login(userId, password);
		System.out.println(msg);
		if("NOT_USER".equals(msg)) {
			labelMsg.setText(I18nUtil.getMessage("notUser"));
			return false;
		} else if("PASSWORD_ERROR".equals(msg)) {
			labelMsg.setText(I18nUtil.getMessage("passwordError"));
			return false;
		} else if("STATE_ERROR".equals(msg)) {
			labelMsg.setText(I18nUtil.getMessage("stateDisabled"));
			return false;
		}
		//登录成功，初始化控件ID信息
		WidgetPermissionUtil.setWidgetPermissionList(new WidgetPermission().initPermissionList());
		return true;
	}
	
	/**
	 * 获取图形界面初始化大小
	 */
	protected Point getInitialSize() {
		return new Point(500,300);
	}
	
	@Override
	protected Point getInitialLocation(Point initialSize) {
		return new Point(433,234);
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(I18nUtil.getMessage("userLogin"));
		newShell.setImage(CacheImage.getCacheImage().getImage(IAppconstans.APPLICATION_ID, IImageKey.LOGIN_ICON));
	}
}

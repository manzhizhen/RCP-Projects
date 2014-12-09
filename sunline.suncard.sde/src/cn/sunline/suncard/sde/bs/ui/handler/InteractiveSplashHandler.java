
package cn.sunline.suncard.sde.bs.ui.handler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.splash.AbstractSplashHandler;

import cn.sunline.suncard.sde.bs.biz.BsUserBiz;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.resource.CacheImage;
import cn.sunline.suncard.sde.bs.ui.permission.WidgetPermission;
import cn.sunline.suncard.sde.bs.ui.resource.IAppconstans;
import cn.sunline.suncard.sde.bs.ui.resource.IImageKey;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.bs.util.WidgetPermissionUtil;

/**
 * @since 3.3
 * 
 */
public class InteractiveSplashHandler extends AbstractSplashHandler {
	
	private final static int F_LABEL_HORIZONTAL_INDENT = 175;

	private final static int F_BUTTON_WIDTH_HINT = 75;
	
	private final static int F_BUTTON_HEIGHT_HINT = 26;
	
	private final static int F_BUTTON_VERTICAL_INDENT = 10;

	private final static int F_TEXT_WIDTH_HINT = 165;
	
	private final static int F_TEXT_HORIZONTAL_INDENT = 10;
	
	private final static int F_TEXT_HEIGHT_HINT = 20;
	
	private final static int F_COLUMN_COUNT = 3;
	
	private Composite fCompositeLogin;
	
	private Text fTextUsername;
	
	private Text fTextPassword;
	
	private Button fButtonOK;
	
	private Button fButtonCancel;
	
	private Label labelMsg;
	
	private boolean fAuthenticated;
	
	private Log logger = LogManager.getLogger(InteractiveSplashHandler.class.getName());
	
	private Image image = null;
	
	/**
	 * 
	 */
	public InteractiveSplashHandler() {
		fCompositeLogin = null;
		fTextUsername = null;
		fTextPassword = null;
		fButtonOK = null;
		fButtonCancel = null;
		fAuthenticated = false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.splash.AbstractSplashHandler#init(org.eclipse.swt.widgets.Shell)
	 */
	public void init(final Shell splash) {
		// Store the shell
		super.init(splash);
		// Configure the shell layout
		configureUISplash();
		// Create UI
		createUI();		
		// Create UI listeners
		createUIListeners();
		// Force the splash screen to layout
		splash.layout(true);
		splash.setDefaultButton(fButtonOK);
		// Keep the splash screen visible and prevent the RCP application from 
		// loading until the close button is clicked.
		doEventLoop();
	}
	
	/**
	 * 
	 */
	private void doEventLoop() {
		Shell splash = getSplash();
		while (fAuthenticated == false) {
			if (splash.getDisplay().readAndDispatch() == false) {
				splash.getDisplay().sleep();
			}
		}
	}

	/**
	 * 
	 */
	private void createUIListeners() {
		// Create the OK button listeners
		createUIListenersButtonOK();
		// Create the cancel button listeners
		createUIListenersButtonCancel();
		// Create the userName text listeners
		createUIListenersTextUserName();
		// Create the password text listeners
		createUIListenersTextPassword();
	}

	/**
	 * 
	 */
	private void createUIListenersButtonCancel() {
		fButtonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleButtonCancelWidgetSelected();
			}
		});
		
		// 按钮鼠标滑动侦听事件
		fButtonCancel.addMouseTrackListener(new MouseTrackAdapter() {
			
			// 鼠标进入时显示高亮图片
			@Override
			public void mouseEnter(MouseEvent e) {
				fButtonCancel.setImage(CacheImage.getCacheImage().getImage(
						IAppconstans.APPLICATION_ID,
						IImageKey.LOGIN_CANCEL_BUTTON_LIGHT_IMAGE));
			}

			// 鼠标离开时显示正常图片
			@Override
			public void mouseExit(MouseEvent e) {
				fButtonCancel.setImage(CacheImage.getCacheImage().getImage(
						IAppconstans.APPLICATION_ID,
						IImageKey.LOGIN_CANCEL_BUTTON_IMAGE));
			}
		});
		
		// 按钮鼠标点击侦听事件
		fButtonCancel.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				fButtonCancel.setImage(CacheImage.getCacheImage().getImage(
						IAppconstans.APPLICATION_ID,
						IImageKey.LOGIN_CANCEL_BUTTON_LIGHT_IMAGE));
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				fButtonCancel.setImage(CacheImage.getCacheImage().getImage(
						IAppconstans.APPLICATION_ID,
						IImageKey.LOGIN_CANCEL_BUTTON_IMAGE));
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				
			}
		});
	}

	/**
	 * 
	 */
	private void handleButtonCancelWidgetSelected() {
		// Abort the loading of the RCP application
		getSplash().getDisplay().close();
		System.exit(0);		
	}
	
	/**
	 * 
	 */
	private void createUIListenersButtonOK() {
		fButtonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleButtonOKWidgetSelected();
			}
		});	
		
		// 按钮鼠标侦听事件
		fButtonOK.addMouseTrackListener(new MouseTrackAdapter() {
					
			// 鼠标进入时显示高亮图片
			@Override
			public void mouseEnter(MouseEvent e) {
				fButtonOK.setImage(CacheImage.getCacheImage().getImage(
						IAppconstans.APPLICATION_ID,
						IImageKey.LOGIN_OK_BUTTON_LIGHT_IMAGE));
			}
			
			// 鼠标离开时显示正常图片
			@Override
			public void mouseExit(MouseEvent e) {
				fButtonOK.setImage(CacheImage.getCacheImage().getImage(
						IAppconstans.APPLICATION_ID,
						IImageKey.LOGIN_OK_BUTTON_IMAGE));
			}
		});
		
		// 按钮鼠标点击侦听事件
		fButtonOK.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				fButtonOK.setImage(CacheImage.getCacheImage().getImage(
						IAppconstans.APPLICATION_ID,
						IImageKey.LOGIN_OK_BUTTON_LIGHT_IMAGE));
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				fButtonOK.setImage(CacheImage.getCacheImage().getImage(
						IAppconstans.APPLICATION_ID,
						IImageKey.LOGIN_OK_BUTTON_IMAGE));
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				
			}
		});
	}

	/**
	 * 
	 */
	private void handleButtonOKWidgetSelected() {
		String userName = fTextUsername.getText();
		String password = fTextPassword.getText();
		// Aunthentication is successful if a user provides any username and
		// any password
//		if ((userName.length() > 0) &&
//				(password.length() > 0)) {
//			fAuthenticated = true;
//		} else {
//			MessageDialog.openError(
//					getSplash(),
//					"Authentication Failed",  //$NON-NLS-1$
//					"A username and password must be specified to login.");  //$NON-NLS-1$
//		}
		
		if(userName.isEmpty()) {
			MessageBox messageBox = new MessageBox(getSplash(), SWT.ICON_ERROR | SWT.OK);
			messageBox.setText(I18nUtil.getMessage("message"));
			messageBox.setMessage(I18nUtil.getMessage("messageInfo"));
			messageBox.open();
			fTextUsername.setFocus();
			return;
		} else if(!isValidate(userName, password)) {
			return;
		}
		
		fAuthenticated = true;
		logger.info("登录成功了");
	}
	
	/**
	 * 创建用户名称输入框侦听事件
	 */
	private void createUIListenersTextUserName() {
		
		// 控件获得焦点及失去焦点事件侦听器
		fTextUsername.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				fTextUsername.selectAll();
			}
		});
	}
	
	/**
	 * 创建用户密码输入框侦听事件
	 */
	private void createUIListenersTextPassword() {
		
		// 控件获得焦点及失去焦点事件侦听器
		fTextPassword.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				fTextPassword.selectAll();
			}
		});
	}
	
	/**
	 * 
	 */
	private void createUI() {
		// Create the login panel
		createUICompositeLogin();
		// Create the blank spanner
		createUICompositeBlank();
		// Create the user name label
		createUILabelUserName();
		// Create the user name text widget
		createUITextUserName();
		// Create the password label
		createUILabelPassword();
		// Create the password text widget
		createUITextPassword();
		// Create the blank label
//		createUILabelBlankMessage();
		// Create the message Label
//		createUILabelMessage();
		// Create the blank label
		createUILabelBlankButtonOK();
		// Create the OK button
		createUIButtonOK();
		// Create the cancel button
		createUIButtonCancel();
		// Create the blank spanner
		createUICompositeBlankBottom();
	}		
	
	/**
	 * 
	 */
	private void createUIButtonCancel() {
		// Create the button
		fButtonCancel = new Button(fCompositeLogin, SWT.CENTER | SWT.NO_TRIM);
		fButtonCancel.setToolTipText(I18nUtil.getMessage("exit"));
//		fButtonCancel.setText("Cancel"); //$NON-NLS-1$
		// Configure layout data
		GridData data = new GridData(SWT.NONE, SWT.NONE, false, false);
		data.widthHint = F_BUTTON_WIDTH_HINT;
		data.heightHint = F_BUTTON_HEIGHT_HINT;
		data.verticalIndent = F_BUTTON_VERTICAL_INDENT;
		fButtonCancel.setLayoutData(data);
		
		image = CacheImage.getCacheImage().getImage(
				IAppconstans.APPLICATION_ID,
				IImageKey.LOGIN_CANCEL_BUTTON_IMAGE);
		fButtonCancel.setImage(image);
	}

	/**
	 * 
	 */
	private void createUIButtonOK() {
		// Create the button
		fButtonOK = new Button(fCompositeLogin, SWT.CENTER | SWT.NO_TRIM);
		fButtonOK.setToolTipText(I18nUtil.getMessage("login"));
//		fButtonOK.setText("OK"); //$NON-NLS-1$
		// Configure layout data
		GridData data = new GridData(SWT.NONE, SWT.NONE, false, false);
		data.widthHint = F_BUTTON_WIDTH_HINT;
		data.heightHint = F_BUTTON_HEIGHT_HINT;
		data.verticalIndent = F_BUTTON_VERTICAL_INDENT;
		fButtonOK.setLayoutData(data);
		
		image = CacheImage.getCacheImage().getImage(
				IAppconstans.APPLICATION_ID,
				IImageKey.LOGIN_OK_BUTTON_IMAGE);
		fButtonOK.setImage(image);
	}

	/**
	 * 
	 */
	private void createUILabelBlankMessage() {
		Label label = new Label(fCompositeLogin, SWT.NONE);
		label.setVisible(false);
	}
	
	/**
	 * 
	 */
	private void createUILabelBlankButtonOK() {
		Label label = new Label(fCompositeLogin, SWT.NONE);
		label.setVisible(false);
	}

	/**
	 * 
	 */
	private void createUITextPassword() {
		// Create the text widget
		int style = SWT.PASSWORD | SWT.BORDER;
		fTextPassword = new Text(fCompositeLogin, style);
		fTextPassword.setText("000000");
		// Configure layout data
		GridData data = new GridData(SWT.NONE, SWT.NONE, false, false);
		data.widthHint = F_TEXT_WIDTH_HINT;
		data.horizontalSpan = 2;
		data.heightHint = F_TEXT_HEIGHT_HINT;
		data.horizontalIndent = F_TEXT_HORIZONTAL_INDENT;
		fTextPassword.setLayoutData(data);		
	}

	/**
	 * 
	 */
	private void createUILabelPassword() {
		// Create the label
		Label label = new Label(fCompositeLogin, SWT.NONE);
		label.setText("         "); //$NON-NLS-1$
//		label.setText("&Password:"); //$NON-NLS-1$
		// Configure layout data
		GridData data = new GridData();
		data.horizontalIndent = F_LABEL_HORIZONTAL_INDENT;
		label.setLayoutData(data);					
	}

	/**
	 * 
	 */
	private void createUITextUserName() {
		// Create the text widget
		fTextUsername = new Text(fCompositeLogin, SWT.BORDER);
		fTextUsername.setText("sysadmin");
		fTextUsername.setFocus();
		// Configure layout data
		GridData data = new GridData(SWT.NONE, SWT.NONE, false, false);
		data.widthHint = F_TEXT_WIDTH_HINT;
		data.horizontalSpan = 2;
		data.heightHint = F_TEXT_HEIGHT_HINT;
		data.horizontalIndent = F_TEXT_HORIZONTAL_INDENT;
		fTextUsername.setLayoutData(data);		
	}

	/**
	 * 
	 */
	private void createUILabelUserName() {
		// Create the label
		Label label = new Label(fCompositeLogin, SWT.NONE);
		label.setText("          "); //$NON-NLS-1$
//		label.setText("&User Name:"); //$NON-NLS-1$
		
		// Configure layout data
		GridData data = new GridData();
		data.horizontalIndent = F_LABEL_HORIZONTAL_INDENT;
		label.setLayoutData(data);		
	}

	/**
	 * 
	 */
	private void createUICompositeBlank() {
		Composite spanner = new Composite(fCompositeLogin, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = F_COLUMN_COUNT;
		data.heightHint = 200;
		spanner.setLayoutData(data);
	}
	
	/**
	 * 
	 */
	private void createUICompositeBlankBottom() {
		Composite spanner = new Composite(fCompositeLogin, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = F_COLUMN_COUNT;
		data.heightHint = 175;
		spanner.setLayoutData(data);
	}

	/**
	 * 
	 */
	private void createUICompositeLogin() {
		// Create the composite
		fCompositeLogin = new Composite(getSplash(), SWT.BORDER);
		GridLayout layout = new GridLayout(F_COLUMN_COUNT, false);
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 30;
		fCompositeLogin.setLayout(layout);		
	}
	
	/**
	 * 创建提示信息标签
	 */
	private void createUILabelMessage() {
		// Create the label
		labelMsg = new Label(fCompositeLogin, SWT.NONE);
		labelMsg.setText("");
		// Configure layout data
		GridData data = new GridData(SWT.NONE, SWT.NONE, false, false);
		data.widthHint = F_TEXT_WIDTH_HINT;
		data.horizontalSpan = 2;
		labelMsg.setLayoutData(data);
		labelMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	}

	/**
	 * 
	 */
	private void configureUISplash() {
		// Configure layout
		FillLayout layout = new FillLayout(); 
		getSplash().setLayout(layout);
		// Force shell to inherit the splash background
		getSplash().setBackgroundMode(SWT.INHERIT_DEFAULT);
	}
	
	/**
	 * 验证用户合法性
	 * @param userId 用户ID
	 * @param password 用户密码
	 * @return
	 */
	private boolean isValidate(final String userId, final String password) {
		BsUserBiz bsUserBiz = new BsUserBiz();
		String msg = bsUserBiz.login(userId, password);
		logger.info(msg);
		
		MessageBox messageBox = new MessageBox(getSplash(), SWT.ICON_ERROR | SWT.OK);
		messageBox.setText(I18nUtil.getMessage("message"));
		
		if("NOT_USER".equals(msg)) {
//			labelMsg.setText(I18nUtil.getMessage("notUser"));
			messageBox.setMessage(I18nUtil.getMessage("notUser"));
			messageBox.open();
			fTextUsername.setFocus();
			fTextUsername.selectAll();
			return false;
		} else if("PASSWORD_ERROR".equals(msg)) {
//			labelMsg.setText(I18nUtil.getMessage("passwordError"));
			messageBox.setMessage(I18nUtil.getMessage("passwordError"));
			messageBox.open();
			fTextPassword.setFocus();
			fTextPassword.selectAll();
			return false;
		} else if("STATE_ERROR".equals(msg)) {
//			labelMsg.setText(I18nUtil.getMessage("stateDisabled"));
			messageBox.setMessage(I18nUtil.getMessage("stateDisabled"));
			messageBox.open();
			fTextUsername.setFocus();
			fTextUsername.selectAll();
			return false;
		}
		//登录成功，初始化控件ID信息
		WidgetPermissionUtil.setWidgetPermissionList(new WidgetPermission().initPermissionList());
		return true;
	}
}

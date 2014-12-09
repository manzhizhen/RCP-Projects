package test;

import java.awt.Toolkit;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.splash.AbstractSplashHandler;

public class MySplashHandler extends AbstractSplashHandler {

    private Button OKButton;
    private Text userText;
    private Text pwdText;
    private Button cancelButton;
	private boolean loginOK;

	public MySplashHandler() {
		OKButton = null;
		cancelButton = null;
		userText = null;
		pwdText = null;
		loginOK = false;
	}

	@Override
	public void init(Shell splash) {
		super.init(splash);
		System.out.println("run init splash");
		
		createUI();
		createUIListeners();
		doEventLoop();
	}

	/**
	 * 界面停留
	 */
	private void doEventLoop() {
		Shell splash = getSplash();
		while (loginOK == false) {
			if (splash.getDisplay().readAndDispatch() == false) {
				splash.getDisplay().sleep();
			}
		}
	}
	
	/**
	 * 创建控件
	 */
	private void createUI(){
        Shell shell=new Shell(getSplash(),SWT.NO_TRIM);
        shell.setText("用户登录"); 
        String temppath= Activator.getDefault().getBundle().getLocation();
        String path= temppath.replace("reference:file:/", "")+"splash.bmp";
        System.out.println(path);
        
        String path1 = "E:/JavaProject/test/icons/loginbg.bmp";
        shell.setBackgroundImage(new Image(getSplash().getDisplay(),path1));
     
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 6;
        gridLayout.marginRight =10;
        gridLayout.marginLeft =100;
        gridLayout.marginTop=180;
        gridLayout.horizontalSpacing=10;
        gridLayout.verticalSpacing=20;
        shell.setLayout(gridLayout);
        shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
        
        final CLabel userLabel = new CLabel(shell,SWT.RIGHT);
        userLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
        userLabel.setText("用 户");
        userText = new Text(shell, SWT.BORDER|SWT.FILL);
        GridData data = new GridData(GridData.FILL,GridData.FILL,true,false,2,1);
        data.widthHint = 100;
        userText.setLayoutData(data);
        
        final CLabel pwdLabel = new CLabel(shell,SWT.RIGHT);
        pwdLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
        pwdLabel.setText("密 码");
        pwdText = new Text(shell, SWT.BORDER);
        pwdText.setEchoChar('*');
        pwdText.setLayoutData(new GridData(GridData.FILL,GridData.FILL,true,false,2,1));
        
//        CLabel label1 = new CLabel(shell,SWT.RIGHT);
//        label1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
        new CLabel(shell, SWT.RIGHT);
        new CLabel(shell, SWT.RIGHT);
        new CLabel(shell, SWT.RIGHT);
        new CLabel(shell, SWT.RIGHT);
        
        OKButton =new Button(shell, SWT.CENTER);
        OKButton.setText(" 登 录 ");
        OKButton.setLayoutData(new GridData(SWT.LEFT,GridData.CENTER,false,false,1,1));
//        String okPath = "E:/JavaProject/test/icons/alt_window_32.gif";
//        OKButton.setBackgroundImage(new Image(null, okPath));
//        OKButton.setImage(new Image(null, okPath));
        
        cancelButton =new Button(shell, SWT.CENTER);
        cancelButton.setText(" 关 闭  ");
        cancelButton.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,false,1,1));
        
//        new CLabel(shell, SWT.RIGHT);
//        CLabel Label = new CLabel(shell,SWT.RIGHT);
//        Label.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 2));
        
        int screenH=Toolkit.getDefaultToolkit().getScreenSize().height;
           int screenW=Toolkit.getDefaultToolkit().getScreenSize().width;
           shell.setLocation(screenW/2-455/2,screenH/2-295/2);
        shell.setSize(new Point(455, 295));
        
        shell.setDefaultButton(OKButton);
        shell.open();  
    }
	
	/**
	 * 创建侦听器
	 */
	private void createUIListeners() {
		// Create the OK button listeners
		createUIListenersButtonOK();
		// Create the cancel button listeners
		createUIListenersButtonCancel();
	}
	    
	/**
	 * 取消按钮监听器
	 */
	private void createUIListenersButtonCancel() {
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getSplash().close();
				System.exit(0);
			}
		});
	}
	    
	/**
	 * 登录按钮监听器
	 */
	private void createUIListenersButtonOK() {
		OKButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				validateLogin();
			}
		});
	}
	
	/**
	 * 登录验证
	 */
	public void validateLogin() {
		String userName = userText.getText().trim();
		String passWrod = pwdText.getText().trim();
		if (userName == "" || passWrod == "") {
			MessageDialog.openError(getSplash(), "出错", "用户名或者密码不能为空！");
			userText.setFocus();
		} else if ("wzx".equals(userName) && "123".equals(passWrod)) {

			// TODO ....
			System.out.println("登录成功！");

			loginOK = true;

		}
	}
}

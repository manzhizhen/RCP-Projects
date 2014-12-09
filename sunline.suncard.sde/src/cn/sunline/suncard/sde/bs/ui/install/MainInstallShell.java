/*
 * 文件名：     ExtVarComposite.java
 * 版权：          Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：          安装程序引导界面
 * 修改人：     wzx
 * 修改时间：2012-1-5
 * 修改内容：创建
 */

package cn.sunline.suncard.sde.bs.ui.install;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import java.util.Map;

import org.dom4j.DocumentException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.sunline.common.util.DateUtil;
import cn.sunline.suncard.sde.bs.biz.BsPcBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.dict.BizDictManager;
import cn.sunline.suncard.sde.bs.entity.BsPc;
import cn.sunline.suncard.sde.bs.entity.BsPcId;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.system.ComputerInfo;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.system.InitPermissionData;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.bs.util.ParseHibernateConfig;
import cn.sunline.suncard.sde.bs.util.SystemParameters;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * 安装程序引导界面
 * 
 * @author  wzx
 * @version , 2012-1-5
 * @see     
 * @since   1.0
 */

public class MainInstallShell extends Shell {
	private static Log logger = LogManager.getLogger(MainInstallShell.class);
	private static final String EMPTY_STRING = "";
	private String processInfo = "";
	private int index = 0;
	private boolean stop = false;
	private Display display;
	
	private CTabFolder tabFolder = null;
	private Composite composite1;
	private Composite composite2;
	private Composite composite3;
	private Composite composite4;
	private Composite composite5;
	
	private CTabItem selectItem;
//	private CTabItem item1;
//	private CTabItem item2;
	private CTabItem item3;
	private CTabItem item4;
	private CTabItem item5;
	
	private Button btnNext;
	private Button btnBack;
	private Button btnClose;
	
	private Label lblPath;
	private Text txtPath;
	private Button btnSelectPath;
	private Combo cbDbType;
	private Label lblDbDialect;
	private Label lblDbType;
	private Text txtDbDialect;
	private Label lblDbDriver;
	private Text txtDbDriver;
	private Label lblDbUrl;
	private Text txtDbUrl;
	private Label lblDbUser;
	private Text txtDbUser;
	private Label lblDbPwd;
	private Text txtDbPwd;
	private Label lblProcess;
	private Text txtProcess;
	private ComboViewer comboViewerDbType;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			MainInstallShell shell = new MainInstallShell(display);
			shell.open();
			shell.initShell();
			shell.layout();
			
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public MainInstallShell(Display display) {
		super(display, SWT.NONE);
		this.display = display;
		createContents();	// 创建控件
		createListener();	// 创建侦听器
	}
	
	/**
	 * 初始化窗口
	 */
	public void initShell() {
		setText("install Wizard");
		int width = 593;
		int heigh = 406;
		int screenWidth = display.getActiveShell().getMonitor().getClientArea().width;
		int screenHeigh = display.getActiveShell().getMonitor().getClientArea().height;
		setSize(width, heigh);
		setLocation((screenWidth - width)/2, (screenHeigh - heigh)/2);
//		setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		display.getActiveShell().setDefaultButton(btnNext);
	}

	/**
	 * 创建控件
	 */
	protected void createContents() {

		tabFolder = new CTabFolder(this, SWT.NONE);
		tabFolder.setBounds(0, 0, 585, 366);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.setSimple(false);
		
		composite1 = new Composite(tabFolder, SWT.NONE);
//		item1 = new CTabItem(tabFolder, SWT.NONE);
//		item1.setText(I18nUtil.getMessage("installTitle"));
//		item1.setControl(composite1);
		
		Label lblNewLabel = new Label(composite1, SWT.NONE);
		lblNewLabel.setBounds(222, 177, 120, 12);
		lblNewLabel.setText(I18nUtil.getMessage("welcomeWords"));
		
		composite2 = new Composite(tabFolder, SWT.NONE);
		composite2.setEnabled(true);
//		item2 = new CTabItem(tabFolder, SWT.NONE);
//		item2.setText(I18nUtil.getMessage("installPath"));
//		item2.setControl(composite2);
		
		lblPath = new Label(composite2, SWT.NONE);
		lblPath.setBounds(10, 52, 54, 12);
		lblPath.setText(I18nUtil.getMessage("labelPath"));
		
		txtPath = new Text(composite2, SWT.BORDER);
		txtPath.setBounds(70, 49, 316, 18);
//		txtPath.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		
		btnSelectPath = new Button(composite2, SWT.NONE);
		btnSelectPath.setBounds(392, 47, 72, 22);
		btnSelectPath.setText(I18nUtil.getMessage("selectPath"));
		
		composite3 = new Composite(tabFolder, SWT.NONE);
		item3 = new CTabItem(tabFolder, SWT.NONE);
		item3.setText(I18nUtil.getMessage("installDb"));
		item3.setControl(composite3);
		
		lblDbType = new Label(composite3, SWT.NONE);
		lblDbType.setBounds(10, 37, 72, 12);
		lblDbType.setText(I18nUtil.getMessage("labelDbType"));
		
//		cbDbType = new Combo(composite3, SWT.NONE);
//		cbDbType.setBounds(88, 34, 295, 20);
//		cbDbType.setItems(new String[]{"derby", "MySQL", "SQLServer"});
		
		comboViewerDbType = new ComboViewer(composite3, SWT.READ_ONLY);
		cbDbType = comboViewerDbType.getCombo();
		cbDbType.setBounds(88, 34, 295, 20);
		
		lblDbDialect = new Label(composite3, SWT.NONE);
		lblDbDialect.setBounds(10, 72, 72, 12);
		lblDbDialect.setText(I18nUtil.getMessage("labelDbDialect"));
		
		txtDbDialect = new Text(composite3, SWT.BORDER);
		txtDbDialect.setBounds(88, 69, 295, 18);
		
		lblDbDriver = new Label(composite3, SWT.NONE);
		lblDbDriver.setBounds(10, 106, 72, 12);
		lblDbDriver.setText(I18nUtil.getMessage("labelDbDriver"));
		
		txtDbDriver = new Text(composite3, SWT.BORDER);
		txtDbDriver.setBounds(88, 103, 295, 18);
		
		lblDbUrl = new Label(composite3, SWT.NONE);
		lblDbUrl.setBounds(10, 142, 54, 12);
		lblDbUrl.setText(I18nUtil.getMessage("labelDbUrl"));
		
		txtDbUrl = new Text(composite3, SWT.BORDER);
		txtDbUrl.setBounds(88, 139, 295, 18);
		
		lblDbUser = new Label(composite3, SWT.NONE);
		lblDbUser.setBounds(10, 177, 54, 12);
		lblDbUser.setText(I18nUtil.getMessage("labelDbUser"));
		
		txtDbUser = new Text(composite3, SWT.BORDER);
		txtDbUser.setBounds(88, 171, 295, 18);
		
		lblDbPwd = new Label(composite3, SWT.NONE);
		lblDbPwd.setBounds(10, 212, 54, 12);
		lblDbPwd.setText(I18nUtil.getMessage("labelDbPwd"));
		
		txtDbPwd = new Text(composite3, SWT.BORDER);
		txtDbPwd.setBounds(88, 206, 295, 18);
		
		composite4 = new Composite(tabFolder, SWT.NONE);
		item4 = new CTabItem(tabFolder, SWT.NONE);
		item4.setText(I18nUtil.getMessage("installInit"));
		item4.setControl(composite4);
		
		composite5 = new Composite(tabFolder, SWT.NONE);
		item5 = new CTabItem(tabFolder, SWT.NONE);
		item5.setText(I18nUtil.getMessage("installProc"));
		item5.setControl(composite5);
		
		lblProcess = new Label(composite5, SWT.NONE);
		lblProcess.setBounds(10, 10, 54, 12);
		lblProcess.setText(I18nUtil.getMessage("labelProc"));
		
		txtProcess = new Text(composite5, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		txtProcess.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		txtProcess.setBounds(10, 28, 561, 307);
		
		tabFolder.setSelection(0);	// 默认选择第一个标签页
		selectItem = tabFolder.getSelection();
		
		btnBack = new Button(this, SWT.NONE);
		btnBack.setBounds(357, 372, 72, 22);
		btnBack.setText(I18nUtil.getMessage("btnBack"));
		btnBack.setEnabled(false);
		
		btnNext = new Button(this, SWT.NONE);
		btnNext.setBounds(435, 372, 72, 22);
		btnNext.setText(I18nUtil.getMessage("btnNext"));
		
		btnClose = new Button(this, SWT.NONE);
		btnClose.setBounds(513, 372, 72, 22);
		btnClose.setText(I18nUtil.getMessage("btnClose"));
		
		initWidgets();
	}
	
	/**
	 * 初始化界面控件
	 */
	private void initWidgets() {
		try {
			Map<String, String> dbTypes = BizDictManager.getDictValue("dbType");
			//System.out.println(dbTypes.values());
			comboViewerDbType.setContentProvider(new ArrayContentProvider());
			comboViewerDbType.setInput(dbTypes.keySet());
//			comboViewerDbType.getCombo().select(0);
		} catch (DocumentException e) {
			e.printStackTrace();
			logger.error("获取数据库类型业务字典出错: "+e.getMessage());
		}
	}
	
	/**
	 * 创建侦听器
	 */
	private void createListener() {
		// 创建tabFolder侦听器
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tabFolder.setSelection(selectItem);
			}
		});
		
		// 创建"上一步"按钮侦听器
		btnBack.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				index--;
//				System.out.println("press back button, index = " + index);
				if (index == 0) {
					btnBack.setEnabled(false);
				}
				btnNext.setText(I18nUtil.getMessage("btnNext"));
				tabFolder.setSelection(index);
				selectItem = tabFolder.getSelection();
			}
		});
		
		// 创建"下一步"按钮侦听器
		btnNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				excuteNext();
			}
		});
		
		// 创建"退出"按钮侦听器
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(display.getActiveShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
				messageBox.setText(I18nUtil.getMessage("titleInfo"));
				messageBox.setMessage(I18nUtil.getMessage("infoQuit"));
				if (messageBox.open() == SWT.YES) {
					display.getActiveShell().close();
					display.dispose();
				}
			}
		});
		
		// 创建"路径"按钮侦听器
		btnSelectPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(display.getActiveShell(), SWT.NONE);
				dialog.setMessage(I18nUtil.getMessage("infoSelectPath"));
				String path = dialog.open();
				String selectPath = path == null ? EMPTY_STRING : path;
				txtPath.setText(selectPath);
				txtPath.setToolTipText(selectPath);
			}
		});
		
		// 创建"数据库类型"下拉框侦听器
//		cbDbType.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				String databaseName = cbDbType.getText();
//				if (databaseName.equalsIgnoreCase("derby")) {
//					
//				}
//			}
//		});
		
		comboViewerDbType.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					String dbType = cbDbType.getText();
					Map<String, String> dbInfoMap = BizDictManager.getDictValue(dbType);
					
					if(dbInfoMap == null || dbInfoMap.isEmpty()) {
						txtDbDialect.setText("");
						txtDbUrl.setText("");
						txtDbUser.setText("");
						txtDbPwd.setText("");
						txtDbDriver.setText("");
					} else {
						txtDbDialect.setText(dbInfoMap.get("dialect"));
						txtDbUrl.setText(dbInfoMap.get("connection.url"));
						txtDbUser.setText(dbInfoMap.get("connection.username"));
						txtDbPwd.setText(dbInfoMap.get("connection.password"));
						txtDbDriver.setText(dbInfoMap.get("connection.driver_class"));
					}
				} catch (DocumentException e) {
					logger.error("获取数据库类型业务字典出错: "+e.getMessage());
				}
			}
		});
	}
	
	/**
	 * 执行下一步操作
	 */
	private void excuteNext() {
		MessageBox messageBox;	// 提示信息对话框
		
		// 安装完成,关闭安装程序
		if (btnNext.getText().equalsIgnoreCase(I18nUtil.getMessage("btnFinish"))) {
			display.getActiveShell().close();
			return;
		}
		
		// 验证输入是否正确
		if (selectItem.getText().equalsIgnoreCase(I18nUtil.getMessage("installPath"))) {
			// 验证路径是否合法
			String path = txtPath.getText().trim();
			if (path.isEmpty() || !new File(path).isDirectory()) {
				messageBox = new MessageBox(display.getActiveShell(), SWT.OK | SWT.ICON_WARNING);
				messageBox.setText(I18nUtil.getMessage("titleWarn"));
				messageBox.setMessage(I18nUtil.getMessage("pathWarnInfo"));
				messageBox.open();
				return;
			}
		} else if (selectItem.getText().equalsIgnoreCase(I18nUtil.getMessage("installDb"))) {
			// 验证数据库输入是否合法
			if (cbDbType.getText().trim().isEmpty()) {
				messageBox = new MessageBox(display.getActiveShell(), SWT.OK | SWT.ICON_WARNING);
				messageBox.setText(I18nUtil.getMessage("titleWarn"));
				messageBox.setMessage(I18nUtil.getMessage("DbWarnInfo"));
				messageBox.open();
				return;
			}
		}
		
		// 执行下一步操作
		index++;
//		System.out.println("press next button, index = " + index);
		btnBack.setEnabled(true);
		tabFolder.setSelection(index);
		selectItem = tabFolder.getSelection();
		
		if (index == (tabFolder.getItemCount() - 2)) {
			btnNext.setText(I18nUtil.getMessage("btnInstall"));
		} else if (index == (tabFolder.getItemCount() - 1)) {
			btnBack.setEnabled(false);
			btnNext.setEnabled(false);
			btnClose.setEnabled(false);
			
			// 此处开始执行安装过程
//			new RefreshThread().start();	// 显示提示信息
			handleInstallProcess();			// 处理安装过程
		}
	}
	
	/**
	 * 处理安装过程
	 */
	private void handleInstallProcess() {
		refreshProcessInfo(I18nUtil.getMessage("procInfoStart"));
		
		/*********** 放入执行安装的方法 **************/
		// 初始化配置文件
		excuteHibernateCfg();
		refreshProcessInfo(I18nUtil.getMessage("procInfoInitConfig"));
		
		InitPermissionData.initData();	// 初始化信息
		createLogsFolder();				// 创建日志文件夹
		
		// 收集系统信息
		try {
			insertPcInfo();	// 插入pc信息
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		refreshProcessInfo(I18nUtil.getMessage("procInfoComputerInfo"));
		refreshProcessInfo(I18nUtil.getMessage("procInfoFinish"));
		
//		InitPermissionData.initData();	// 初始化信息
		
		stop = true;
		btnNext.setText(I18nUtil.getMessage("btnFinish"));
		btnNext.setEnabled(true);
	}
	
	/**
	 * 显示安装进度提示信息
	 * @param info
	 */
	private void refreshProcessInfo(final String info) {
		display.asyncExec(new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				txtProcess.setText("\n" + 
						txtProcess.getText() 
						+ DateUtil.getCurTime().toLocaleString() 
						+ info
						+ "\n");
			}
		});
	}
	
	/**
	 * 将PC信息插入数据库
	 * @throws UnknownHostException 
	 */
	private void insertPcInfo() throws UnknownHostException {
		//初始化hibernate配置文件信息
		ParseHibernateConfig.parseHibernateCfg(SystemParameters.getUserDir()+"/"+SystemParameters.getHibernateXmlPath(), SystemParameters.getUserDir()+"/"+SystemParameters.getHibernateMappingPath());
		
		BsPc bsPc = new BsPc(new BsPcId());
		bsPc.getId().setBankorgId(Long.parseLong(Context.getSessionMap().get(Constants.BANKORG_ID).toString()));
		bsPc.getId().setPcId(ComputerInfo.getHostName());
		bsPc.setPcName(ComputerInfo.getHostName());
		bsPc.setIpAddr(ComputerInfo.getIpAddress());
		BsPcBiz bsPcBiz = new BsPcBiz();
		bsPcBiz.insert(bsPc);
	}
	
	/**
	 * 初始化hibernate配置文件
	 */
	private void excuteHibernateCfg() {
		StringBuffer sb = new StringBuffer();
		String filePath = System.getProperty("user.dir")+"/"+SystemParameters.getHibernateXmlPath();
		File file = new File(filePath);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String str = null;
			while((str=br.readLine()) != null) {
				//str = str+"\n";
				if(str.contains("dialect")) {
					str = str.substring(0,str.indexOf(">")+1) + txtDbDialect.getText() + "</property>";
				}
				if(str.contains("connection.url")) {
					str = str.substring(0,str.indexOf(">")+1) + txtDbUrl.getText() + "</property>";
				}
				if(str.contains("connection.username")) {
					str = str.substring(0,str.indexOf(">")+1) + txtDbUser.getText() + "</property>";
				}
				if(str.contains("connection.password")) {
					str = str.substring(0,str.indexOf(">")+1) + txtDbPwd.getText() + "</property>";
				}
				if(str.contains("connection.driver_class")) {
					str = str.substring(0,str.indexOf(">")+1) + txtDbDriver.getText() + "</property>";
				}
				if(str.contains("myeclipse.connection.profile")) {
					String dbType = cbDbType.getItem(cbDbType.getSelectionIndex());
					str = str.substring(0,str.indexOf(">")+1) + dbType + "</property>";
				}
				str = str+"\n";
				sb.append(str);
			}
			logger.info(sb.toString());
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		writeFile(filePath, sb.toString());
	}
	
	/**
	 * 写文件
	 * 
	 * @param filePath
	 * @param content
	 */
	public static void writeFile(String filePath, String content) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath),"UTF-8"));
			bw.write(content);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("写文件出错："+e.getMessage());
		} finally {
			if(bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("关闭文件流出错："+e.getMessage());
				}
			}
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	class RefreshThread extends Thread {
		private String tempInfo = "";
		
		@Override
		public void run() {
			while (!stop) {
				display.asyncExec(new Runnable() {
					@SuppressWarnings("deprecation")
					public void run() {
						System.out.println("temp equals info = " + tempInfo.equalsIgnoreCase(processInfo));
						if (!tempInfo.equalsIgnoreCase(processInfo)) {
							txtProcess.setText(DateUtil.getCurTime().toLocaleString() 
									+ processInfo
									+ txtProcess.getText());
							tempInfo = processInfo;
						}
					}
				});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			// 安装完成,显示安装成功信息,并退出安装程序
//			MessageBox messageBox = new MessageBox(display.getActiveShell(), SWT.OK | SWT.ICON_INFORMATION);
//			messageBox.setMessage("应用程序安装成功！");
//			messageBox.open();
//			
//			btnNext.setText("完成");
//			btnNext.setEnabled(true);
		}
	}
	
	/**
	 * 创建日志文件夹
	 */
	private void createLogsFolder(){
		File file = new File(System.getProperty("user.dir") + "/logs");
		file.mkdirs();
		logger.info("日志文件夹创建成功");
	}
}

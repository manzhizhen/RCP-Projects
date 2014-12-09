/*
 * 文件名：DBPreferencePage.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：数据库配置类文件
 * 修改人：tpf
 * 修改时间：2011-9-23
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.preferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.dom4j.DocumentException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import cn.sunline.suncard.sde.bs.Activator;
import cn.sunline.suncard.sde.bs.dict.BizDictManager;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;
import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.bs.util.SystemParameters;

/**
 * 数据库配置类
 * 数据库配置类，主要包括hibernate配置文件的操作和IPreferenceStore保存操作
 * @author    tpf
 * @version   1.0  2011-9-23
 * @see       [相关类/方法]
 * @since     [产品/模块版本] 
 */
public class DBPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage,ModifyListener {

	static Log logger = LogManager.getLogger(DBPreferencePage.class.getName());
	
	// 为文本框定义五个键值
	public static final String PROFILE_KEY = "$PROFILE_KEY";	
	public static final String DIALECT_KEY = "$DIALECT_KEY";
	public static final String CLASSNAME_KEY = "$CLASSNAME_KEY";
	public static final String URL_KEY = "$URL_KEY";
	public static final String USERNAME_KEY = "$USERNAME_KEY";
	public static final String PASSWORD_KEY = "$PASSWORD_KEY";
	
	// 为文本框值定义五个默认值
	private static final String PROFILE_DEFAULT = "Derby";
	private static final String DIALECT_DEFAULT = "org.hibernate.dialect.DerbyDialect";
	private static final String CLASSNAME_DEFAULT = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String URL_DEFAULT = "jdbc:derby:"+System.getProperty("user.dir")+"/cardsde";
	private static final String USERNAME_DEFAULT = "root";
	private static final String PASSWORD_DEFAULT = "123456";
	// 定义五个文本框
	private ComboViewer comboViewer;
	private Text dialectText;
	private Text classNameText;
	private Text urlText;
	private Text usernameText;
	private Text passwordText;
	// 定义一个IPreferenceStore对象
	private IPreferenceStore ps;
	
	
	public DBPreferencePage() {
		
	}

	public DBPreferencePage(String title) {
		super(title);
	}

	public DBPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	/**
	 *  接口IWorkbenchPreferencePage的方法，负责初始化。在此方法中设置一个
	 *  PreferenceStore对象，由此对象提供文本框值的读入/写出方法
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	/**
	 * 父类的界面创建方法
	 */
	@Override
	protected Control createContents(Composite parent) {
		ps = getPreferenceStore();// 取得init方法设入的PreferenceStore对象
		Composite topComp = new Composite(parent, SWT.NONE);
		topComp.setLayout(new GridLayout());
		createDbConnectGroup(topComp);
		//createTableRsCountGroup(topComp);
		return topComp;
	}

	// 创建界面里的文本框，顺便把文本框前的标签也创建了
	private Text createText(Composite comp, String label, String saveKey, String defaultValue) {
		new Label(comp, SWT.NONE).setText(label);
		Text text = new Text(comp, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// 用key取出以前保存的值，如果取出值为空或空字串，则返回默认值(defaultValue)
		String value = ps.getString(saveKey);
		text.setText(isEmpty(value) ? defaultValue : value);
		// this代表本类，因为本类实现了ModifyListener接口成为一个监听器
		text.addModifyListener(this);
		return text;
	}
	
	// 判断传入的字符串是否为空值或空字串或空格
	private boolean isEmpty(String str) {
		return str == null || str.trim().equals("");
	}
		
	// 创建数据库连接组
	private void createDbConnectGroup(Composite topComp) {
		Group group = new Group(topComp, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(2, false));
		group.setText(I18nUtil.getMessage("dbConnInfo"));
		
		//创建ComboViewer显示数据库类型
		createComboViewer(group);
		
		//数据库方言
		dialectText = createText(group, I18nUtil.getMessage("dbDialect"), DIALECT_KEY, DIALECT_DEFAULT);
		//数据库驱动
		classNameText = createText(group, I18nUtil.getMessage("dbDriver"), CLASSNAME_KEY, CLASSNAME_DEFAULT);
		//连接URL
		urlText = createText(group, I18nUtil.getMessage("connUrl"), URL_KEY, URL_DEFAULT);
		//用户名
		usernameText = createText(group, I18nUtil.getMessage("userName"), USERNAME_KEY, USERNAME_DEFAULT);
		//密码
		passwordText = createText(group, I18nUtil.getMessage("PASSWORD"), PASSWORD_KEY, PASSWORD_DEFAULT);
		passwordText.setEchoChar('*');
	}
	
	/**
	 * 创建ComboViewer显示数据库类型
	 * @param group
	 */
	private void createComboViewer(Group group) {
		new Label(group, SWT.NONE).setText(I18nUtil.getMessage("dbType"));
		comboViewer = new ComboViewer(group, SWT.NONE);
		Combo combo = comboViewer.getCombo();
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		String profileValue = ps.getString(PROFILE_KEY);
		try {
			Map<String, String> dbTypes = BizDictManager.getDictValue("dbType");
			//System.out.println(dbTypes.values());
			comboViewer.setContentProvider(new ArrayContentProvider());
			comboViewer.setInput(dbTypes.keySet());
		} catch (DocumentException e) {
			e.printStackTrace();
			logger.error("获取数据库类型业务字典出错: "+e.getMessage());
		}
		
		if(!isEmpty(profileValue)) {
			combo.select(combo.indexOf(profileValue));
		} else {
			combo.select(combo.indexOf(PROFILE_DEFAULT));
		}
		//System.out.println(combo.getItem(combo.getSelectionIndex())); //可取到当前选中行的内容
		changeDbSelect(comboViewer);
	}
	
	/**
	 * 改变数据库类型时，连动改变下面数据库信息
	 * @param comboViewer
	 */
	private void changeDbSelect(ComboViewer comboViewer) {
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					String dbType = event.getSelection().toString().substring(event.getSelection().toString().indexOf("[")+1,event.getSelection().toString().indexOf("]"));
					Map<String, String> dbInfoMap = BizDictManager.getDictValue(dbType);
					
					if(dbInfoMap == null || dbInfoMap.isEmpty()) {
						dialectText.setText("");
						urlText.setText("");
						usernameText.setText("");
						passwordText.setText("");
						classNameText.setText("");
					} else {
						dialectText.setText(dbInfoMap.get("dialect"));
						urlText.setText(dbInfoMap.get("connection.url"));
						usernameText.setText(dbInfoMap.get("connection.username"));
						passwordText.setText(dbInfoMap.get("connection.password"));
						classNameText.setText(dbInfoMap.get("connection.driver_class"));
					}
				} catch (DocumentException e) {
					logger.error("获取数据库类型业务字典出错: "+e.getMessage());
				}
				
			}
		});
	}
	
	@Override
	public void modifyText(ModifyEvent e) {
	}

	// 父类方法，单击“复原缺省值”按钮时将执行此方法，取出默认值设置到文本框中
	protected void performDefaults() {
		dialectText.setText(DIALECT_DEFAULT);
		classNameText.setText(CLASSNAME_DEFAULT);
		urlText.setText(URL_DEFAULT);
		usernameText.setText(USERNAME_DEFAULT);
		passwordText.setText(PASSWORD_DEFAULT);
		comboViewer.getCombo().select(comboViewer.getCombo().indexOf(PROFILE_DEFAULT));
		
	}

	// 父类方法，单击“应用”按钮时执行此方法，将文本框值保存并弹出成功的提示信息
	protected void performApply() {
		if(MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), I18nUtil.getMessage("message"), I18nUtil.getMessage("restartMsg"))) {
			doSave(); // 自定义方法，保存设置
		}
	}

	// 父类方法，单击“确定”按钮时执行此方法，将文本框值保存并弹出成功的提示信息。
	// @return true＝成功退出
	public boolean performOk() {
		if(MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), I18nUtil.getMessage("message"), I18nUtil.getMessage("restartMsg"))) {
			doSave();
			return true;
		}
		//这里可写重启代码
		return false;
	}

	/**
	 * 自定义方法，保存文本框的值
	 */
	private void doSave() {
		String profileText = comboViewer.getCombo().getItem(comboViewer.getCombo().getSelectionIndex());
		ps.setValue(PROFILE_KEY, profileText);
		ps.setValue(DIALECT_KEY, dialectText.getText());
		ps.setValue(CLASSNAME_KEY, classNameText.getText());
		ps.setValue(URL_KEY, urlText.getText());
		ps.setValue(USERNAME_KEY, usernameText.getText());
		ps.setValue(PASSWORD_KEY, passwordText.getText());
		
		//保存至hibernate配置文件
		excuteHibernateCfg();
		PlatformUI.getWorkbench().restart();
	}
	
	/**
	 * 初始化hibernate配置文件
	 */
	/*private void createHibernateCfg() {
		SAXReader reader = new SAXReader();
		Document doc = null;
		//String filePath = System.getProperty("user.dir")+"/"+System.getProperty(SystemParameters.getHibernateXmlPath());
		String filePath = "D:/Java/JavaTool/eclipse-modeling-indigo-win32/eclipse/config/hibernate.cfg.xml";
		try {
			doc = reader.read(new FileInputStream(filePath));
			Element root = doc.getRootElement();
			Element secondElement = root.element("session-factory");
			List<Element> es = secondElement.elements();
			String dbType = comboViewer.getCombo().getItem(comboViewer.getCombo().getSelectionIndex());
			Map<String, String> dbInfoMap = BizDictManager.getDictValue(dbType);
			Set<String> keys = dbInfoMap.keySet();
			for(Element e : es) {
				String nameValue = e.attribute("name").getText();
				if(keys.contains(nameValue)) {
					if(nameValue.equals("dialect")) {
						e.setText(dialectText.getText());
					} else if(nameValue.equals("connection.url")) {
						e.setText(urlText.getText());
					} else if(nameValue.equals("connection.username")) {
						e.setText(usernameText.getText());
					} else if(nameValue.equals("connection.password")) {
						e.setText(passwordText.getText());
					} else if(nameValue.equals("connection.driver_class")) {
						e.setText(classNameText.getText());
					} else if(nameValue.equals("myeclipse.connection.profile")) {
						e.setText(dbType);
					}
				}
			}
			writeFile(filePath, doc.asXML());
		} catch (FileNotFoundException e) {
			logger.error("文件未找到！"+e.getMessage());
			throw new RuntimeException("文件未找到！"+e.getMessage());
			//e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
			logger.error("解析hibernate配置文件出错："+e.getMessage());
		}
	}*/

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
					str = str.substring(0,str.indexOf(">")+1) + dialectText.getText() + "</property>";
				}
				if(str.contains("connection.url")) {
					str = str.substring(0,str.indexOf(">")+1) + urlText.getText() + "</property>";
				}
				if(str.contains("connection.username")) {
					str = str.substring(0,str.indexOf(">")+1) + usernameText.getText() + "</property>";
				}
				if(str.contains("connection.password")) {
					str = str.substring(0,str.indexOf(">")+1) + passwordText.getText() + "</property>";
				}
				if(str.contains("connection.driver_class")) {
					str = str.substring(0,str.indexOf(">")+1) + classNameText.getText() + "</property>";
				}
				if(str.contains("myeclipse.connection.profile")) {
					String dbType = comboViewer.getCombo().getItem(comboViewer.getCombo().getSelectionIndex());
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
	
	public static void main(String[] args) {
		//createHibernateCfg();
	}
}

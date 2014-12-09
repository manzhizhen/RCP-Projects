/*
 * 文件名：
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：tpf
 * 修改时间：2011-10-13
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.dict.BizDictManager;
import cn.sunline.suncard.sde.bs.util.ParseHibernateConfig;
import cn.sunline.suncard.sde.bs.util.ResourceUtil;
import cn.sunline.suncard.sde.bs.util.SystemParameters;

public class SystemInit implements ILoginListener {

	Context context = Context.getInstance();
	
	public SystemInit() {
		super();
	}

	public void init() {
		Context.getInstance().addLogonListener(this);
	}
	
	@Override
	public void login() {
		try {
			//初始化系统配置文件信息
			ResourceUtil.setSystemPropertiesFromResource(System.getProperty("user.dir")+"/config/system.properties");
			
			Context.getSessionMap().put(Constants.BANKORG_ID, Long.parseLong(System.getProperty(Constants.BANKORG_ID)));
			Context.getSessionMap().put(Constants.PC_ID, Constants.PC_ID_VALUE);
			Context.getSessionMap().put(Constants.PC_IP, ComputerInfo.getIpAddress());
			
			//初始化国际化信息
			ResourceUtil.initMessageResources();
			
//			File file = new File(SystemParameters.getUserDir()+"/"+SystemParameters.getHibernateXmlPath());
//			
//			List<String> strArray  = new ArrayList<String>();
//			
//			if(file.exists() && file.isFile()) {
//				BufferedReader  fileReader = new BufferedReader (new FileReader(file.getAbsolutePath()));
//				String str = null;
//				while((str = fileReader.readLine()) != null) {
//					
//					if(str.contains("jdbc:derby:")) {
//						str = "<property name="+ "\"" + "connection.url" + "\"" +">jdbc:derby:" + new File(System.getProperty("user.dir"), "/cardsde").getAbsolutePath() + "</property>";
//					}
//					
//					strArray.add(str);
//				}
//				
//				fileReader.close();
//				
//				BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));
//				for(String str1 : strArray) {
//					fileWriter.write(str1 + "\n");
//				}
//				
//				fileWriter.close();
//			}
			
			
			
			
			//初始化hibernate配置文件信息
//			ParseHibernateConfig.parseHibernateCfg(SystemParameters.getUserDir()+"/"+SystemParameters.getHibernateXmlPath(), SystemParameters.getUserDir()+"/"+SystemParameters.getHibernateMappingPath());
			
			//初始化业务字典信息
			BizDictManager.initAndParseDictXmlFile();
		//} catch (IOException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void logout() {
		
	}

}

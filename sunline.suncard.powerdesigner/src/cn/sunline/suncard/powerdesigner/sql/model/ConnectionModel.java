/* 文件名：     JDBCAttributeModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2013-1-11
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.sql.model;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;

import org.dom4j.Element;

import cn.sunline.suncard.powerdesigner.model.DataObjectInterface;

/**
 * 连接数据库需要的模型
 * 
 * @author Agree
 * @version 1.0, 2013-1-11
 * @see
 * @since 1.0
 */
public class ConnectionModel implements DataObjectInterface {
	private String driverTemplate;// 驱动类型
	private String driverURL;// 驱动URL
	private String driverName;// 驱动名
	private String userName;// 用户名
	private String password;// 密码
	private String defaultState;// 存储上次保存的默认的数据库连接，状态0为默认，状态1相反

	private String jarAddress;// 存储JARS物理地址，通过地址找到JAR包，通过JAR包找到className
	private String driverClassName;//存储驱动类名
	
	private Connection connection;
	private LinkedHashMap<String, SqlDatabaseModel> databaseModelMap = new LinkedHashMap<String, SqlDatabaseModel>();

	public String getDriverTemplate() {
		return driverTemplate;
	}

	public void setDriverTemplate(String driverTemplate) {
		this.driverTemplate = driverTemplate;
	}

	public String getDriverURL() {
		return driverURL;
	}

	public void setDriverURL(String driverURL) {
		this.driverURL = driverURL;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDefaultState() {
		return defaultState;
	}

	public void setDefaultState(String defaultState) {
		this.defaultState = defaultState;
	}
	
	public String getJarAddress() {
		return jarAddress;
	}

	public void setJarAddress(String jarAddress) {
		this.jarAddress = jarAddress;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public LinkedHashMap<String, SqlDatabaseModel> getDatabaseModelMap() {
		return databaseModelMap;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Element getElementFromObject(Element parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getObjectFromElement(Element element, Object... obj) {
		// TODO Auto-generated method stub
		return null;
	}
}

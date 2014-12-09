/*
 * 文件名：HibernateUtil.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：创建Hibernate的session通用工具类
 * 修改人： tpf
 * 修改时间：2011-09-20
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.bs.util;

import java.io.File;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * Hibernate通用工具类
 * Hibernate通用工具类，通过此类可以获取SessionFactory，Session等信息
 * @author    tpf
 * @version   1.0, 2011-09-20
 * @see       getSession
 * @see       getSessionFactory
 * @see       closeSession
 * @since     1.0
 */
public class HibernateUtil {
	private static Log logger = LogManager.getLogger(HibernateUtil.class);
	
	static {
//		ParseHibernateConfig.parseHibernateCfg(SystemParameters.getUserDir()+"/"+SystemParameters.getHibernateXmlPath(), SystemParameters.getUserDir()+"/"+SystemParameters.getHibernateMappingPath());
	}
	private static Configuration config = new Configuration().setInterceptor(new HibernateInterceptor()).configure(new File(SystemParameters.getUserDir()+"/"+SystemParameters.getHibernateTempXmlPath()));
	//private static Configuration config = new Configuration().configure(ParseHibernateConfig.getDocument(SystemParameters.getUserDir()+"/"+SystemParameters.getHibernateXmlPath(), SystemParameters.getUserDir()+"/"+SystemParameters.getHibernateMappingPath()).asXML());
	private static SessionFactory sessionFactory = null;
	
	//存放session的容器
	private static final ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();
	
	//控制事务
	private static Transaction transaction = null;
	
	
	//事务传递(嵌套)标识
	private static int i = 1;
	
	//静态块儿初始化sessionFactory
	static {
		try {
			sessionFactory = config.buildSessionFactory();
		} catch (HibernateException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取Configuration的方法
	 * 通过些方法可以获取Hibernate中的Configuration对象
	 * @return config
	 * @see   
	 */
	public static Configuration getConfiguration() {
		return config;
	}
	
	/**
	 * 获取SessionFactory的方法
	 * 通过些方法可以获取Hibernate中的SessionFactory对象
	 * @return sessionFactory
	 * @see   
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	/**
	 * 获取Session的方法
	 * 通过些方法可以获取Hibernate中的session
	 * @return session
	 * @see   
	 */
	public static Session getSession() {
		if(threadLocal.get() != null) {
			return threadLocal.get();
		}
		threadLocal.set(sessionFactory.openSession());
		return threadLocal.get();
	}
	
	/**
	 * 开启session并开启事务
	 * 通过些方法可以获取Hibernate中的session和事务
	 * @return session
	 * @see   
	 */
	public static void openSession() {
		i += 1;
		if(threadLocal.get()!=null && threadLocal.get().isOpen()) {
			Session s = threadLocal.get();
			logger.debug("session已开启时i的值为："+i);
			if(transaction == null) {
				transaction = s.beginTransaction();
			}
		} else {
			logger.debug("session首次开启时i的值为："+i);
			threadLocal.set(sessionFactory.openSession());
			Session s = threadLocal.get();
			transaction = s.beginTransaction();
		}
		
	}
	
//	原来的openSession实现
//	public static void openSession() {
//	if(threadLocal.get()!=null && threadLocal.get().isOpen()) {
//		//return threadLocal.get();
//		threadLocal.get().close();
//	}
//	threadLocal.set(sessionFactory.openSession());
//	Session s = threadLocal.get();
//	transaction = s.beginTransaction();
//}
	
	/**
	 * 关闭Session的方法
	 * 通过些方法可以关闭Hibernate中的session
	 * @see   
	 */
	public static void closeSession() {
		i -= 1;
		logger.debug("执行closeSession时i的值为："+i);
		if(i == 1) {
			try{
				transaction.commit();
				logger.debug("事务提交时i的值为："+i);
			} finally {
				transaction = null;
				if(threadLocal.get() != null) {
					threadLocal.get().close();
					threadLocal.set(null);
				}
			}
		}
	}

	/**
	 * 获取事务的方法
	 * 通过些方法可以获取Hibernate中的事务Transaction
	 * @return transaction
	 * @see   
	 */
	public static Transaction getTransaction() {
		return transaction;
	}

	/**
	 * 设置事务Transaction
	 * 通过些方法可以设置Hibernate中的事务Transaction
	 * @param transaction
	 */
	public static void setTransaction(Transaction transaction) {
		HibernateUtil.transaction = transaction;
	}

	public static Configuration getConfig() {
		return config;
	}
	
}


/*
 * 文件名：HibernateParter.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：获取数据库表结构通用类
 * 修改人： tpf
 * 修改时间：2011-12-20
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.bs.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;

/**
 * Hibernate通用工具类
 * Hibernate通用工具类，通过此类可以获取数据库表结构等信息
 * @author    tpf
 * @version   1.0, 2011-12-20
 * @since     1.0
 */
public class HibernateParterUtil {

	private static Configuration hibernateConf;
	
	private static Configuration getHibernateConf() {
		if (hibernateConf == null) {
			hibernateConf = HibernateUtil.getConfig();
		}
		return hibernateConf;
	}

	private static PersistentClass getPersistentClass(Class clazz) {
		synchronized (HibernateParterUtil.class) {
			PersistentClass pc = getHibernateConf().getClassMapping(clazz.getName());
			if (pc == null) {
				hibernateConf = getHibernateConf().addClass(clazz);
				pc = getHibernateConf().getClassMapping(clazz.getName());
			}
			return pc;
		}
	}

	/**
	  * 获取实体对应的表名
	  * @param clazz 实体类
	  * @return 表名
	  */
	public static String getTableName(Class clazz) {
		return getPersistentClass(clazz).getTable().getName();
	}

	/**
	  * 获取实体对应表的主键字段名称
	  * @param clazz 实体类
	  * @return 主键字段名称
	  */
	public static List<String> getPkColumnName(Class clazz) {
		Table table = getPersistentClass(clazz).getTable();
		List<String> columnNames = new ArrayList<String>();
		List<Column> columns = table.getPrimaryKey().getColumns();
		for (Column column : columns) {
			columnNames.add(column.getName());
		}
		return columnNames;
	}

	/**
	 * 通过实体类和属性，获取实体类属性对应的表字段名称
	 * @param clazz 实体类
	 * @param propertyName 属性名称
	 * @return 字段名称
	 */
	public static String getColumnName(Class clazz, String propertyName) {
		PersistentClass persistentClass = getPersistentClass(clazz);
		Property property = persistentClass.getProperty(propertyName);
		Iterator it = property.getColumnIterator();
		if (it.hasNext()) {   
			Column column= (Column)it.next();   
			return column.getName();
		}
		return null;
	}
	
	/**
	 * 返回表的所有列名
	 * @param clazz
	 * @return
	 */
	public static List<String> getAllColumnName(Class clazz) {
		Table table = getPersistentClass(clazz).getTable();
		List<String> columnNames = new ArrayList<String>();
		Iterator<Column> columns = table.getColumnIterator();
		for (Iterator<Column> iter = columns;iter.hasNext();) {
			Column column = iter.next();
			columnNames.add(column.getName());
		}
		return columnNames;
	}
	
	/**
	 * 返回表的所有列的长度
	 * @param clazz
	 * @return
	 */
	public static List<Integer> getAllColumnLength(Class clazz) {
		Table table = getPersistentClass(clazz).getTable();
		List<Integer> columnLengths = new ArrayList<Integer>();
		Iterator<Column> columns = table.getColumnIterator();
		for (Iterator<Column> iter = columns;iter.hasNext();) {
			Column column = iter.next();
			columnLengths.add(column.getLength());
		}
		return columnLengths;
	}
	
	/**
	 * 返回表的所有列的类型
	 * @param clazz
	 * @return
	 */
	public static List<String> getAllColumnType(Class clazz) {
		Table table = getPersistentClass(clazz).getTable();
		List<String> columnTypes = new ArrayList<String>();
		Iterator<Column> columns = table.getColumnIterator();
		for (Iterator<Column> iter = columns;iter.hasNext();) {
			Column column = iter.next();
			columnTypes.add(column.getSqlType());
		}
		return columnTypes;
	}
}

/*
 * 文件名：BsFunctionDao.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：BsFuncmapping实体的Dao类，主要提供对此类对应表的crud操作
 * 修改人：heyong
 * 修改时间：2011-9-22
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.dao;

import cn.sunline.suncard.sde.bs.db.AbstractEntityDao;
import cn.sunline.suncard.sde.bs.entity.BsFunction;
import cn.sunline.suncard.sde.bs.entity.BsFunctionId;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

/**
 * 
  * BsFunction实体的Dao类，主要提供对此类对应表的crud操作
  * BsFunction实体的Dao类，主要提供对此类对应表的crud操作
  * @author heyong
  * @version 1.0, 2011-9-22
  * @see AbstractEntityDao
  * @since 1.0
 */
public class BsFunctionDao extends AbstractEntityDao<BsFunction> {
	
	public static void main(String[] args) {
		BsFunctionId id = new BsFunctionId();
		id.setBankorgId(new Long(1));
		id.setFunctionId("1");
		id.setPcId("1");
		
		BsFunction function = new BsFunction();
		function.setId(id);
		function.setFunctionName("功能1");
		
		AbstractEntityDao<BsFunction> dao = new BsFunctionDao();
		HibernateUtil.openSession();
		dao.addEntity(function);
		HibernateUtil.closeSession();
	}
}

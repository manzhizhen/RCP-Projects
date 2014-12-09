/*
 * 文件名：BsFuncmappingDao.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：BsFuncmapping实体的Dao类，主要提供对此类对应表的crud操作
 * 修改人：易振强
 * 修改时间：2011-9-22
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.dao;

import java.util.List;

import org.hibernate.Query;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.db.AbstractEntityDao;
import cn.sunline.suncard.sde.bs.entity.BsFuncmapping;
import cn.sunline.suncard.sde.bs.entity.BsFuncmappingId;
import cn.sunline.suncard.sde.bs.entity.BsFunction;
import cn.sunline.suncard.sde.bs.entity.BsWidgetId;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 
  * BsFuncmapping实体的Dao类，主要提供对此类对应表的crud操作
  * BsFuncmapping实体的Dao类，主要提供对此类对应表的crud操作
  * @author 易振强
  * @version 1.0, 2011-9-22
  * @see AbstractEntityDao
  * @since 1.0
 */
public class BsFuncmappingDao extends AbstractEntityDao<BsFuncmapping> {
//	private long bankorgId = ((Long)Context.getSessionMap().get(Constants.BANKORG_ID));
//	private String pcId = (String) Context.getSessionMap().get(Constants.PC_ID);
	
	public void deleteByWidgetId(BsWidgetId widgetId) {
//		String hql = "delete BsFuncmapping bp where bp.id.widgetId = ? and " +
//				"bp.id.bankorgId = ? and bp.id.pcId = ?";
		
		String hql = I18nUtil.getMessage("BsFuncmappingDao_deleteByWidgetId");
		
		Query query = HibernateUtil.getSession().createQuery(hql);
		query.setParameter(0, widgetId.getWidgetId());
		query.setParameter(1, widgetId.getBankorgId());
		query.setParameter(2, widgetId.getPcId());
		query.executeUpdate();
	}
	
	/**
	 * 根据功能得到其对应所有控件
	 * 
	 * @param function 功能
	 * @return List<BsFuncmapping> 功能控件映射集合
	 */
	@SuppressWarnings("unchecked")
	public List<BsFuncmapping> getAllByFunction(BsFunction function){
		String hql = "from BsFuncmapping t where t.id.bankorgId = " +  function.getId().getBankorgId() 
				+ " and t.id.pcId = '" + function.getId().getPcId() + "' and t.id.functionId = '" 
				+ function.getId().getFunctionId() + "'";
		
		return HibernateUtil.getSession().createQuery(hql).list();
	}
	
	/**
	 * 批量添加映射记录
	 * 
	 * @param bsFuncmappings
	 */
	public void batchInsert(List<BsFuncmapping> bsFuncmappings){
		if (bsFuncmappings != null && bsFuncmappings.size() > 0){
			for (int i=0; i<bsFuncmappings.size(); i++){
				addEntity(bsFuncmappings.get(i));
			}
		}
	}
	
	/**
	 * 根据功能删除控件映射关系
	 * 
	 * @param function
	 */
	public void deleteAllByFunction(BsFunction function){
		
		String hql = "delete from BsFuncmapping t where t.id.bankorgId = ? and t.id.pcId = ?"
				+ " and t.id.functionId = ?";
		Query query = HibernateUtil.getSession().createQuery(hql);
		query.setLong(0, function.getId().getBankorgId());
		query.setString(1, function.getId().getPcId());
		query.setString(2, function.getId().getFunctionId());
		
		query.executeUpdate();
	}
	
	public static void main(String[] args) {
		BsFuncmappingId id = new BsFuncmappingId();
		id.setBankorgId(new Long(1));
		id.setFunctionId("1");
		id.setPcId("1");
		id.setWidgetId("1");
		
		BsFuncmapping bsFuncmapping = new BsFuncmapping();
		bsFuncmapping.setId(id);
		bsFuncmapping.setMappingType("2");
		
		AbstractEntityDao<BsFuncmapping> dao = new BsFuncmappingDao();
		HibernateUtil.openSession();
		dao.addEntity(bsFuncmapping);
		HibernateUtil.closeSession();
	}
}

/*
 * 文件名：BsUserBiz.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：用户业务逻辑类所在文件
 * 修改人： tpf
 * 修改时间：2011-09-22
 * 修改内容：新增
 * 
 * 修改人：heyong
 * 修改时间：2011-09-25
 * 修改内容：添加方法permissionSet()
 * 
 * 修改人：heyong
 * 修改时间：2011-09-26
 * 修改内容：添加方法getAllByUser()
*/

package cn.sunline.suncard.sde.bs.biz;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.sunline.suncard.sde.bs.dao.BsUsermappingDao;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.entity.BsUsermapping;
import cn.sunline.suncard.sde.bs.entity.BsUsermappingId;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

/**
 * 用户业务逻辑类
 * 些类中主要是一些用户操作业务逻辑方法
 * @author    tpf
 * @version   1.0, 2011-09-22
 * @see       
 * @since     1.0
 */
public class BsUsermappingBiz {

	//用户Dao类，业务类可以通过此类对象调用其持久化方法
	private BsUsermappingDao usermappingDao = new BsUsermappingDao();
	
	public void delete(BsUsermappingId bsUsermappingId) {
		HibernateUtil.openSession();
		usermappingDao.deleteEntity(bsUsermappingId, BsUsermapping.class);
		HibernateUtil.closeSession();
	}
	
	public void insert(BsUsermapping bsUsermapping) {
		HibernateUtil.openSession();
		usermappingDao.addEntity(bsUsermapping);
		HibernateUtil.closeSession();
	}
	
	public void update(BsUsermapping bsUsermapping){
		HibernateUtil.openSession();
		usermappingDao.updateEntity(bsUsermapping);
		HibernateUtil.closeSession();
	}
	
	public BsUsermapping findByPk(BsUsermappingId id){
		BsUsermapping usermapping = null;
		HibernateUtil.openSession();
		usermapping = usermappingDao.findByPK(id, BsUsermapping.class);
		HibernateUtil.closeSession();
		return usermapping;
	}
	
	public List<BsUsermapping> getAll(){
		List<BsUsermapping> bsUsermappings = new ArrayList<BsUsermapping>();
		HibernateUtil.openSession();
		bsUsermappings = usermappingDao.findAll(BsUsermapping.class);
		HibernateUtil.closeSession();
		return bsUsermappings;
	}
	
	/**
	 * 设置用户的角色
	 * 设置角色时，先删除用户所属角色，然后再添加
	 * @param user 用户
	 * @param bsUsermappings 用户与角色映射
	 */
	public void permissionSet(BsUser user, Set<BsUsermapping> bsUsermappings){
		HibernateUtil.openSession();
		usermappingDao.deleteByUser(user);
		usermappingDao.batchInsert(bsUsermappings);
		HibernateUtil.closeSession();
	}
	
	/**
	 * 根据用户得到其对应的所有角色
	 * @param user 用户实体对象
	 * @return List<BsUsermapping> 用户与角色映射记录集合
	 */
	public List<BsUsermapping> getAllByUser(BsUser user){
		List<BsUsermapping> usermappings = new ArrayList<BsUsermapping>();
		HibernateUtil.openSession();
		usermappings = usermappingDao.getAllByUser(user);
		HibernateUtil.closeSession();
		return usermappings;
	}
}

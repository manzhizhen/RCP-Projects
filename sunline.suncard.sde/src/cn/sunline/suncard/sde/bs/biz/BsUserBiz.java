/*
 * 文件名：BsUserBiz.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：用户业务逻辑类所在文件
 * 修改人： tpf
 * 修改时间：2011-09-22
 * 修改内容：新增
 * 
 * 修改人： tpf
 * 修改时间：2001-09-23 
 * 修改内容：addUser中添加md5校验
 * 
 * 修改人：heyong
 * 修改时间：2011-09-24
 * 修改内容：增加删除多个用户的功能 delete(List<BsUser> users) 
 * 
 * 修改人：heyong
 * 修改时间：2011-10-09
 * 修改内容：增加根据用户id查找用户方法findByUserId(String userid)
 * 
 * 修改人：heyong
 * 修改时间：2011-10-21
 * 修改内容：增加验证输入密码方法，checkPassword(String password)
*/

package cn.sunline.suncard.sde.bs.biz;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.providers.encoding.Md5PasswordEncoder;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.dao.BsUserDao;
import cn.sunline.suncard.sde.bs.dao.BsUsermappingDao;
import cn.sunline.suncard.sde.bs.entity.BsUser;
import cn.sunline.suncard.sde.bs.entity.BsUserId;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.system.GenRandomPasswd;
import cn.sunline.suncard.sde.bs.util.HibernateUtil;

/**
 * 用户业务逻辑类
 * 些类中主要是一些用户操作业务逻辑方法
 * @author    tpf
 * @version   1.0, 2011-09-22
 * @see       
 * @since     1.0
 */
public class BsUserBiz extends AbstractEntityBiz<BsUser>{

	//用户Dao类，业务类可以通过此类对象调用其持久化方法
	private BsUserDao userDao = new BsUserDao();
	private BsUsermappingDao usermappingDao = new BsUsermappingDao();
	public void deleteUserByPK(BsUserId bsUserId) {
		HibernateUtil.openSession();
		userDao.deleteEntity(bsUserId, BsUser.class);
		BsUser user = userDao.findByPK(bsUserId, BsUser.class);
		usermappingDao.deleteByUser(user);
		HibernateUtil.closeSession();
	}
	
	//添加用户
	public void addUser(BsUser bsUser) {
		HibernateUtil.openSession();
		bsUser.setPassword(Constants.INITIAL_USER_PASSWORD);//初始化用户密码
		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		String password = bsUser.getPassword();
		bsUser.setPassword(md5.encodePassword(password, bsUser.getId().getUserId()));
		bsUser.setUserStatus(Constants.INITIAL_USER_STATUS);
		userDao.addEntity(bsUser);
		HibernateUtil.closeSession();
	}
	
	public void updateUser(BsUser bsUser){
		HibernateUtil.openSession();
		userDao.updateEntity(bsUser);
		HibernateUtil.closeSession();
	}
	
	public BsUser findByPk(BsUserId id){
		BsUser user = null;
		HibernateUtil.openSession();
		user = userDao.findByPK(id, BsUser.class);
		HibernateUtil.getSession().evict(user);
		HibernateUtil.closeSession();
		return user;
	}
	
	public List<BsUser> getAll(){
		List<BsUser> bsUsers = new ArrayList<BsUser>();
		HibernateUtil.openSession();
		bsUsers = userDao.findAll(BsUser.class);
		HibernateUtil.closeSession();
		return bsUsers;
	}
	
	public String login(String userId, String password) {
		String msg = "LOGIN_OK";
		HibernateUtil.openSession();
		BsUser user = userDao.findByPK(new BsUserId((Long)Context.getSessionMap().get(Constants.BANKORG_ID), userId), BsUser.class);
		HibernateUtil.getSession().evict(user);
		if(user != null) {
			Md5PasswordEncoder md5 = new Md5PasswordEncoder();
			String uiPassword = md5.encodePassword(password, userId.trim());
			if("N".equals(user.getUserStatus())) {
				if(uiPassword.equals(user.getPassword())) {
					msg = "LOGIN_OK";
					Context.getSessionMap().put(Constants.CURRENT_USER, user);
					Context.getSessionMap().put(Constants.CURRENT_USER_ID, user.getId().getUserId());
				} else {
					msg = "PASSWORD_ERROR";
				}
			} else {
				msg = "STATE_ERROR";
			}
		} else {
			msg = "NOT_USER";
		}
		HibernateUtil.closeSession();
		return msg;
	}
	
	/**
	 * 删除方法
	 * 提供对BsUser实体类的删除方法，根据主键删除，可一次删除多个，同时删除用户对应的角色映射
	 * @param users BsUser列表
	 */
	public void delete(List<BsUser> users) {
		HibernateUtil.openSession();
		if (users != null && users.size() > 0){
			for(int i=0; i<users.size(); i++){
				BsUser user = users.get(i);
				userDao.deleteEntity(user.getId(), BsUser.class);
				usermappingDao.deleteByUser(user);
			}
		}
		HibernateUtil.closeSession();
	}
	
	/**
	 * 对用户进行密码重置
	 * 对用户进行密码重置
	 * @param users 要进行密码重置的用户集合
	 * @return String 返回值，返回重置后的密码
	 */
	public String passwdReset(List<BsUser> users){
		HibernateUtil.openSession();
		//随机生成密码
		String newpasswd = GenRandomPasswd.getPassWord(Constants.GEN_PASSWD_STYLE, Constants.GEN_PASSWD_LENGTH);
		if (users != null && users.size() > 0){
			for (int i=0; i<users.size(); i++){
				BsUser user = users.get(i);
				user.setPassword(new Md5PasswordEncoder().encodePassword(newpasswd, 
						user.getId().getUserId().trim()));
				userDao.updateEntity(user);
			}
		}
		HibernateUtil.closeSession();
		return newpasswd;
	}
	
	/**
	 * 根据用户id查找用户
	 * 根据用户id查找用户，可以用于根据id查找用户和检验用户惟一性
	 * @param userid 查找的用户id
	 * @return BsUser 返回查找到的用户或null
	 */
	public BsUser findByUserId(String userid){
		return userDao.findByUserId(userid);
	}
	
	
	/**
	 * 检查密码输入是否正确
	 * @param password 旧密码
	 * @return boolean 如果密码输入正确返回true
	 */
	public boolean checkPassword(String password){
		//得到当前登陆用户
		BsUser user = (BsUser)Context.getSessionMap().get(Constants.CURRENT_USER);
		//MD5加密
		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		//用当前输入的旧密码和用户Id加密后的加密密码
		String uiPassword = md5.encodePassword(password, user.getId().getUserId());
		//通过主键查找user.getId()
		user = userDao.findByPK(user.getId(), BsUser.class);
		
		return user.getPassword().equals(uiPassword);
	}

	
	/**
	 * 实现抽象类AbstractEntityBiz中的getRecordCount方法
	 */
	@Override
	public int getRecordCount() {
		return userDao.getRecordCount(BsUser.class);
	}
	/**
	 * 实现抽象类AbstractEntityBiz中的getByPage方法
	 */
	@Override
	public List<BsUser> getByPage(int currPage, int pageSize) {
		return userDao.findByPage(currPage, pageSize, BsUser.class);
	}
	
	
}

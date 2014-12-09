package cn.sunline.suncard.sde.bs.system;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.sunline.suncard.sde.bs.common.Constants;

public class Context {
		// 单例模式
		private static Context context = new Context();
		private Context() {}
		public static Context getInstance() {	return context;	}
		
		private static Map<String, Object> sessionMap = new HashMap<String, Object>();
		
		public static Map<String, Object> getSessionMap() {
			return sessionMap;
		}
		public static void setSessionMap(Map<String, Object> sessionMap) {
			Context.sessionMap = sessionMap;
		}
		
		/** ******保存当前登录用户的对象********** */
//		private BsUser currentUser = null;
//		
//		public BsUser getCurrentUser() {
//			return currentUser;
//		}
//		public void setCurrentUser(BsUser currentUser) {
//			this.currentUser = currentUser;
//		}
		
		//判断当前系统是否有用户登录
		public boolean isLogon() {
			return sessionMap.get(Constants.CURRENT_USER) != null;
			//return currentUser != null;
		}
		
		/** ******事件监听的处理********** */
		// 监听器的注册表
		private Set<ILoginListener> loginListeners = new HashSet<ILoginListener>(); 
		
		// 注册监听器
		public void addLogonListener(ILoginListener listener) {
			loginListeners.add(listener);
		}
		
		// 撤销监听器
		public void removeLogonListener(ILoginListener listener) {
			loginListeners.remove(listener);
		}
		
		// 触发所有监听器中的登录事件处理方法
		public void fireLogonEvent() {
			for (ILoginListener listener : new HashSet<ILoginListener>(loginListeners)) {
				listener.login();
			}
		}
		
		// 触发所有监听器中的退出事件处理方法
		public void fireLogoffEvent() {
			for (ILoginListener listener : new HashSet<ILoginListener>(loginListeners)) {
				listener.logout();
			}
			//currentUser = null;// 当前登录用户设为空
			sessionMap.put(Constants.CURRENT_USER, null);
		}
}


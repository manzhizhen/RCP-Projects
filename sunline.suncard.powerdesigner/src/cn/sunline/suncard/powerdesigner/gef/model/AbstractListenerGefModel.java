/* 文件名：     AbstractListenerModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	抽象监听模型
 * 修改人：     易振强
 * 修改时间：2012-3-26
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.gef.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * 抽象监听模型
 * @author  易振强
 * @version 1.0, 2012-3-26
 * @see 
 * @since 1.0
 */
public class AbstractListenerGefModel {
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	// 添加监听
	public void addPropertyChangeLinstener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	// 模型改变通知
	public void firePropertyListenerChange(String propName, Object oldValue,
			Object newValue) {
		listeners.firePropertyChange(propName, oldValue, newValue);
	}

	// 删除监听
	public void removePropertyChangeLinstener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}
}

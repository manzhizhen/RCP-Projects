/* 文件名：     ISubComposite.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-6
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.dialog.composite;

/**
 * 为对话框的子容器定义的接口
 * @author  Manzhizhen
 * @version 1.0, 2013-1-6
 * @see 
 * @since 1.0
 */
public interface ISubComposite {
	/**
	 * 创建控件
	 */
	public void createControl();
	
	/**
	 * 初始化控件的值
	 */
	public void initControlData () throws Throwable;
	
	/**
	 * 创建控件事件
	 */
	public void createControlEvent();
	
	/**
	 * 检查控件的值是否正确
	 * @return 如果正确返回null，否则返回错误信息
	 */
	public String checkData();
}

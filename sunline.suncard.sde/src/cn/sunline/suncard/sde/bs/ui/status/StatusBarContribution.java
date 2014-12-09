/*
 * 文件名：StatusBarContribution.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：状态栏
 * 修改人：tpf
 * 修改时间：2011-9-23
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.status;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * 状态栏类
 * 状态栏类，主要显示用户登录的主要信息
 * @author    tpf
 * @version   1.0  2011-9-23
 * @see       [相关类/方法]
 * @since     [产品/模块版本] 
 */
public class StatusBarContribution extends ContributionItem {

	//状态栏要显示的信息
	private String message;
	
	public StatusBarContribution() {
		super();
	}
	
	public StatusBarContribution(String msg) {
		message = msg;
	}
	
	/**
	 * 添加状态栏信息的方法
	 * 通过些方法可以添加状态栏信息
	 * @see   
	 */
	public void fill(Composite parent) {
		
		//添加一个竖立的分隔线
		Label separator = new Label(parent, SWT.SEPARATOR);
		
		//布局数据类
		StatusLineLayoutData statusLineLayoutData = new StatusLineLayoutData();
		statusLineLayoutData.heightHint = 20;
		separator.setLayoutData(statusLineLayoutData);
		
		//显示文字信息的标签
		CLabel statusCLabel = new CLabel(parent, SWT.SHADOW_NONE);
		statusLineLayoutData = new StatusLineLayoutData();
		statusLineLayoutData.widthHint = 315;
		statusCLabel.setLayoutData(statusLineLayoutData);
		statusCLabel.setText(message);
	}
	
}

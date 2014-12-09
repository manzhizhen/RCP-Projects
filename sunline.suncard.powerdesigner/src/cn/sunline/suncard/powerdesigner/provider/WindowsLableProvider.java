/* 文件名：     WindowsLableProvider.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-13
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.provider;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.LabelProvider;

/**
 * 窗口管理标签提供者
 * @author  Manzhizhen
 * @version 1.0, 2012-12-13
 * @see 
 * @since 1.0
 */
public class WindowsLableProvider extends LabelProvider{
	@Override
	public String getText(Object element) {
		if(element instanceof Dialog) {
			Dialog dialog = (Dialog) element;
			if(dialog.getShell() != null && !dialog.getShell().isDisposed()) {
				return dialog.getShell().getText();
			}
		}
		return super.getText(element);
	}
}

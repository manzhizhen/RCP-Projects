/*
 * 文件名：DBRootPreferencePage.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：tpf
 * 修改时间：2011-9-23
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author    tpf
 * @version   1.0  2011-9-23
 * @see       [相关类/方法]
 * @since     [产品/模块版本] 
 */
public class DBRootPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public DBRootPreferencePage() {
		// TODO Auto-generated constructor stub
	}

	public DBRootPreferencePage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public DBRootPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite topComp = new Composite(parent, SWT.NONE);
		topComp.setLayout(new RowLayout());
		new Label(topComp, SWT.NONE).setText(I18nUtil.getMessage("DB_ROOT_PREFERENCE"));
		return topComp;
	}

}

/*
 * 文件名：
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：tpf
 * 修改时间：2012-1-9
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.dailogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AboutDialog extends Dialog{
	private Text text;
	private Text text_1;
	private Text txtxxxxxxxxx;
	private Text text_2;

	public AboutDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		container.setLayout(null);
		
		text = new Text(container, SWT.BORDER);
		text.setBounds(227, 25, 107, 18);
		text.setText("SUNCARD决策引擎");
		
		text_1 = new Text(container, SWT.BORDER);
		text_1.setText("1.版本：V1.0");
		text_1.setBounds(79, 73, 91, 18);
		
		txtxxxxxxxxx = new Text(container, SWT.BORDER);
		txtxxxxxxxxx.setText("2.xxxxxxxxxxxx");
		txtxxxxxxxxx.setBounds(79, 120, 70, 18);
		
		text_2 = new Text(container, SWT.BORDER | SWT.MULTI);
		text_2.setText("3. 所有权声明：\n" +
				        "   深圳市长亮科技股份有限公司\n "
				         +"  版权所有  不得复制\n"
				        +"   Copyright © 2012 by Shenzhen Sunline Technology Co., Ltd.");
		text_2.setBounds(79, 175, 289, 61);
		return container;
		
	}
	protected Point getInitialSize() {
		return new Point(596, 505);
	}
}

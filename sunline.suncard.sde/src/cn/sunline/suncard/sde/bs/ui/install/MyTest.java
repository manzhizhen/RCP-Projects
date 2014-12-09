/*
 * 文件名：
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：tpf
 * 修改时间：2012-2-7
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.install;

import cn.sunline.suncard.sde.bs.util.VerifyUtil;

public class MyTest {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
//		String XPATH_ID = "\\S+/row(\\[(\\S+='\\S+\\';){0,}\\]){0,1}/field\\[id='(\\S+)'\\]";
//		String XPATH_ID = "\\S+/row(\\[(\\S+='^1$';){0,}\\]){0,1}/field\\[id='(\\S+)'\\]";
//		String str = "wr我们的qr*&^((^&)e1.1-we/row[TYPE_ID='1';]/field[id='a^$%我等东方省B1']";
		String XPATH_ID = "\\d+,";
		String str = "12345,";
		if (VerifyUtil.verifyRegex(XPATH_ID, str)){
			System.out.println("校验成功");
		} else {
			System.out.println("校验失败");
		}
	}

}

/*
 * 文件名：TestMain.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：tpf
 * 修改时间：2011-9-23
 * 修改内容：新增
 */
package testcase;

import org.springframework.security.providers.encoding.Md5PasswordEncoder;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author    tpf
 * @version   1.0  2011-9-23
 * @see       [相关类/方法]
 * @since     [产品/模块版本] 
 */
public class TestMain {

	public static void main(String[] args) {
		Md5PasswordEncoder md5 = new Md5PasswordEncoder();
		String s = "Micheal Jackson";
		//不加”盐“的结果
		System.out.println(md5.encodePassword(s, null));
		//加"盐"的结果
		System.out.println(md5.encodePassword(s, System.currentTimeMillis()));
	}
}

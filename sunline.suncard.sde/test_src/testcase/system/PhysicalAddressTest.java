/*
 * 文件名：
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：yangxs
 * 修改时间：2011-10-8
 * 修改内容：新增
 */
package testcase.system;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import cn.sunline.suncard.sde.bs.system.ComputerInfo;

/**
 * 测试取本地机的Physical Address
 * 
 * @author    yangxs
 * @version   1.0, 2011-10-08
 * @see       testGetPhysicalAddress
 * @since     1.0
 */
public class PhysicalAddressTest extends TestCase {

	@Test
	public void testGetPhysicalAddress() throws IOException{
		
		ComputerInfo mac = new ComputerInfo();
		List<String> macAddr = mac.getPhysicalAddress();
		for(String ip : macAddr)
		System.out.println("TestCase's Mac : "+ip);
	}
	
}

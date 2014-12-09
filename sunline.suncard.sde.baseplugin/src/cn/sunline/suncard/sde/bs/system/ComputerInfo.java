/*
 * 文件名：
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：yangxs
 * 修改时间：2011-10-8
 * 修改内容：新增
 * 修改人：tpf
 * 修改时间：2011-10-13
 * 修改内容：添加获取本地IP地址的方法和主机名的方法
 */
package cn.sunline.suncard.sde.bs.system;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * 取本地机的Physical Address
 * 
 * @author yangxs
 * @version 1.0, 2011-10-08
 * @see getPhysicalAddress
 * @since 1.0
 */
public class ComputerInfo {

	/**
	 * 取本地机的Physical Address，由于一台电脑可能有多个网卡，所以返回一个集合
	 * 
	 * @return typeOf List
	 * @throws IOException 
	 */
	public static List<String> getPhysicalAddress() throws IOException {

		List<String> MACAddr = new ArrayList<String>();
		Process process = Runtime.getRuntime().exec("ipconfig /all");
		InputStreamReader ir = new InputStreamReader(
				process.getInputStream());
		LineNumberReader input = new LineNumberReader(ir);
		String line;
		while ((line = input.readLine()) != null) {
			if (line != null && line.contains("-")) {
				String macIP = line.substring(line.indexOf(":") + 2);
				if (!"".equals(macIP) && macIP.length() == 17) {
					MACAddr.add(macIP);
				}
			}
		}
		return MACAddr;
	}
	
	/**
	 * 获取本机的IP地址
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	public static String getIpAddress() throws UnknownHostException {
		InetAddress localhost = InetAddress.getLocalHost();
		String IP = localhost.getHostAddress();
		return IP;
	}
	
	/**
	 * 获取本机的主机名
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	public static String getHostName() throws UnknownHostException {
		InetAddress localhost = InetAddress.getLocalHost();
		String hostName = localhost.getHostName();
		return hostName;
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("localhost MAC: "+getPhysicalAddress());
		System.out.println("localhost IP: "+getIpAddress());
		
	}
}

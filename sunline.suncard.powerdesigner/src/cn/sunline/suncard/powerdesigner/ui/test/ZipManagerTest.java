/* 文件名：     ZipManagerTest.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-12-18
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.ui.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.tools.zip.ZipFile;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.sunline.suncard.powerdesigner.resource.DmConstants;
import cn.sunline.suncard.powerdesigner.resource.SystemConstants;
import cn.sunline.suncard.powerdesigner.tools.ZipManager;

/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2012-12-18
 * @see 
 * @since 1.0
 */
public class ZipManagerTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link cn.sunline.suncard.powerdesigner.tools.ZipManager#compressFiles(java.io.File, java.io.File)}.
	 */
	@Test
	public void testCompressFilesFileFile() {
//		try {
//			ZipManager.compressFiles(new File("d://123"), new File("C://Users//Manzhizhen//Desktop//SuncardDesigner.zip"));
//			ZipManager.decompressFile(new ZipFile(new File("C://Users//Manzhizhen//Desktop//SuncardDesigner.zip"), SystemConstants.FILE_CODE) , new File("d://1234").getAbsolutePath());
//		} catch (IOException e) {
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//		}
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(new File("d:\\123\\SuncardDesigner.spd"));
			Element root = document.getRootElement();
		} catch (DocumentException e) {
			System.out.println("出错了！");
			e.printStackTrace();
		}

		
		System.out.println("sdf");
	}

}

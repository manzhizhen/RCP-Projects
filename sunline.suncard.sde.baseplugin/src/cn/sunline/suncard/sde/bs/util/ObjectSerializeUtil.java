
/* 文件名：     SwitchObjAndFile.java
 * 版权：          Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	将对象序列化保存到文件和从文件中读取序列化对象
 * 修改人：     易振强
 * 修改时间：2011-11-30
 * 修改内容：创     建
 */
package cn.sunline.suncard.sde.bs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

/**
 * 将对象序列化保存到文件和从文件中读取序列化对象
 * @author  易振强
 * @version 1.0, 2011-11-30
 * @see 
 * @since 1.0
 */
public class ObjectSerializeUtil {

	/**
	 * 将对象保存到文件
	 * @param  Object  需要保存的对象
	 * @param  String  文件路径(含文件名)
	 * @throws IOException 
	 * @exception/throws 
	 */
	public static void saveObjToFile(Object obj, String filePath) throws IOException {
		if(obj == null || filePath == null) {
			return ;
		}
		
		File file = new File(filePath);
		
		// 将对象保存到文件
		saveObjToFile(obj, file);
	}
	
	/**
	 * 将对象保存到文件
	 * @param  Object  需要保存的对象
	 * @param  File  文件
	 * @throws IOException 
	 * @exception/throws 
	 */
	public static void saveObjToFile(Object obj, File file) throws InvalidClassException, NotSerializableException, IOException {
		if(obj == null || file == null) {
			throw new FileNotFoundException();
		}
		
		FileOutputStream fileOutput = null;
		ObjectOutputStream objectOutput = null;
		try {
			fileOutput = new FileOutputStream(file);
			objectOutput = new ObjectOutputStream(fileOutput); 
			
			objectOutput.writeObject(obj);	// 序列化对象
//			objectOutput.flush();
		} finally {
			objectOutput.close();
		}
	}
	
	/**
	 * 从文件中读取对象
	 * @param  File  文件
	 * @return Object 读取到的对象
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @exception/throws 
	 */
	@SuppressWarnings("finally")
	public static Object readObjFromFile(File file) throws  ClassNotFoundException, OptionalDataException, StreamCorruptedException, IOException{
		if(file == null) {
			throw new FileNotFoundException();
		}
		
		if(!file.exists() || !file.isFile()) {
			throw new FileNotFoundException();
		}
		
		FileInputStream fileInput = null;
		ObjectInputStream objectInput = null;
		Object obj = null;
		try {
			fileInput = new FileInputStream(file);
			objectInput = new ObjectInputStream(fileInput); 
			
			obj = objectInput.readObject();	// 对象反序列化
		} finally {
			objectInput.close();
			fileInput.close();
		}
		
		return obj;
	}
	
	/**
	 * 从文件中读取对象
	 * @param  String  文件路径
	 * @return Object 读取到的对象
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @exception/throws 
	 */
	public static Object readObjFromFile(String filePath) throws IOException, ClassNotFoundException {
		if(filePath == null) {
			throw new FileNotFoundException();
		}
		
		File file = new File(filePath);
		
		return readObjFromFile(file);
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String filePath = "e:/12.txt";
		int obj = 10;
		saveObjToFile(obj, filePath);
		
		Integer obj1 = (Integer) readObjFromFile(filePath);
		System.out.println(obj1.intValue());
	}
}


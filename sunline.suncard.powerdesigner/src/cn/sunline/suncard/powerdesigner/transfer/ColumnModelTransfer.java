/* 文件名：     ColumnModelTransfer.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-13
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.transfer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 表格对象的Transfer
 * @author  Manzhizhen
 * @version 1.0, 2012-9-13
 * @see 
 * @since 1.0
 */
public class ColumnModelTransfer extends ByteArrayTransfer {
	private static final String COLUMN_MODEL_NAME = "column_model_name";
	private static final int COLUMN_MODEL_ID = registerType(COLUMN_MODEL_NAME);
	private static ColumnModelTransfer _instance = new ColumnModelTransfer();

	private Log logger = LogManager.getLogger(ColumnModelTransfer.class
			.getName());
	
	public ColumnModelTransfer() {
	}
	
	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		if (object == null || !(object instanceof ColumnModel[])) {
			return;
		}
		
		if (isSupportedType(transferData)) {
			ColumnModel[] columnModels = (ColumnModel[]) object;	
	 		try {
	 			ByteArrayOutputStream out = new ByteArrayOutputStream();
	 			DataOutputStream writeOut = new DataOutputStream(out);
	 			
	 			for (int i = 0, length = columnModels.length; i < length;  i++){
		 			Document doc = DocumentHelper.createDocument();
		 			Element rootElement = doc.addElement("RootElement");
	 				columnModels[i].getElementFromObject(rootElement);
	 				byte[] buffer = doc.asXML().getBytes("UTF-8");
	 				writeOut.writeInt(buffer.length);
	 				writeOut.write(buffer);
	 			}
	 			byte[] buffer = out.toByteArray();
	 			writeOut.close();
	 
	 			super.javaToNative(buffer, transferData);
	 			
	 		} catch (IOException e) {
	 			logger.error("将ColumnModel拷贝到剪贴板失败！" + e.getMessage());
	 		}
	 	}
	}
	
	public Object nativeToJava(TransferData transferData){	
		 
	 	if (isSupportedType(transferData)) {
	 		
	 		byte[] buffer = (byte[])super.nativeToJava(transferData);
	 		if (buffer == null) return null;
	 		
	 		ColumnModel[] columModels = new ColumnModel[0];
	 		try {
	 			SAXReader saxReader = new SAXReader();  
	 			ByteArrayInputStream in = new ByteArrayInputStream(buffer);
	 			DataInputStream readIn = new DataInputStream(in);
	 			
	 			while(readIn.available() > 20) {
	 				ColumnModel columnModel = new ColumnModel();
	 				
	 				int size = readIn.readInt();
	 				byte[] content = new byte[size];
	 				readIn.read(content);
	 				// 把byte数组转换成document
	 				Document doc = saxReader.read(new DataInputStream(new ByteArrayInputStream(content)));
	 				columnModel.getObjectFromElement(doc.getRootElement().element(ColumnModel.getElementName()));
	 				
	 				// 拷贝该数据到原数组
	 				ColumnModel[] newColumnModels = new ColumnModel[columModels.length + 1];
	 				System.arraycopy(columModels, 0, newColumnModels, 0, columModels.length);
	 				newColumnModels[columModels.length] = columnModel;
	 				columModels = newColumnModels;
	 			}
	 			readIn.close();
	 		} catch (IOException ex) {
	 			logger.error("将ColumnModel从剪贴板读取出来失败！" + ex.getMessage());
	 			return null;
	 		} catch (DocumentException e) {
	 			logger.error("将ColumnModel从剪贴板读取出来失败！" + e.getMessage());
				e.printStackTrace();
			}
	 		
	 		return columModels;
	 	}
	 
	 	return null;
	 }

	
	public static ColumnModelTransfer getInstance () {
	 	return _instance;
	 }
	
	@Override
	protected int[] getTypeIds() {
		return new int[] {COLUMN_MODEL_ID};
	}

	@Override
	protected String[] getTypeNames() {
		return new String[]{COLUMN_MODEL_NAME};
	}

}

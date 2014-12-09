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
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import cn.sunline.suncard.powerdesigner.model.ColumnModel;
import cn.sunline.suncard.powerdesigner.model.TableModel;
import cn.sunline.suncard.sde.bs.log.Log;
import cn.sunline.suncard.sde.bs.log.LogManager;

/**
 * 表格对象的Transfer
 * @author  Manzhizhen
 * @version 1.0, 2012-9-13
 * @see 
 * @since 1.0
 */
public class TableModelTransfer extends ByteArrayTransfer {
	private static final String TABLE_MODEL_NAME = "table_model_name";
	private static final int TABLE_MODEL_ID = registerType(TABLE_MODEL_NAME);
	private static TableModelTransfer _instance = new TableModelTransfer();

	private Log logger = LogManager.getLogger(TableModelTransfer.class
			.getName());
	
	private TableModelTransfer() {
	}
	
	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		if (object == null || !(object instanceof ColumnModel[])) {
			return;
		}
		
		if (isSupportedType(transferData)) {
			TableModel[] tableModels = (TableModel[]) object;	
	 		try {
	 			ByteArrayOutputStream out = new ByteArrayOutputStream();
	 			DataOutputStream writeOut = new DataOutputStream(out);
	 			
	 			for (int i = 0, length = tableModels.length; i < length;  i++){
		 			Document doc = DocumentHelper.createDocument();
		 			Element rootElement = doc.addElement("RootElement");
	 				tableModels[i].getElementFromObject(rootElement);
	 				byte[] buffer = doc.asXML().getBytes("UTF-8");
	 				writeOut.writeInt(buffer.length);
	 				writeOut.write(buffer);
	 			}
	 			byte[] buffer = out.toByteArray();
	 			writeOut.close();
	 
	 			super.javaToNative(buffer, transferData);
	 			
	 		} catch (IOException e) {
	 			logger.error("将TableModel拷贝到剪贴板失败！" + e.getMessage());
	 		}
	 	}
	}
	
	public Object nativeToJava(TransferData transferData){	
		 
	 	if (isSupportedType(transferData)) {
	 		
	 		byte[] buffer = (byte[])super.nativeToJava(transferData);
	 		if (buffer == null) return null;
	 		
	 		TableModel[] tableModels = new TableModel[0];
	 		try {
	 			SAXReader saxReader = new SAXReader();  
	 			ByteArrayInputStream in = new ByteArrayInputStream(buffer);
	 			DataInputStream readIn = new DataInputStream(in);
	 			
	 			while(readIn.available() > 20) {
	 				TableModel tableModel = new TableModel();
	 				
	 				int size = readIn.readInt();
	 				byte[] content = new byte[size];
	 				readIn.read(content);
	 				// 把byte数组转换成document
	 				Document doc = saxReader.read(new DataInputStream(new ByteArrayInputStream(content)));
	 				
//	 				tableModel.getObjectFromElement(doc.getRootElement().element(TableModel.getElementName()));
	 				Element tableElement = doc.getRootElement().element(TableModel.getElementName());
	 				tableModel.setId(tableElement.attributeValue("id").trim());
	 				tableModel.setTableName(tableElement.attributeValue("name").trim());
	 				tableModel.setTableDesc(tableElement.attributeValue("desc").trim());
	 				tableModel.setAutoSize(new Boolean(tableElement.attributeValue("autoSize").trim()));
	 				tableModel.setTableType(tableElement.attributeValue("tableType").trim());
	 				tableModel.setTableNote(tableElement.getText());
	 				
	 				List<ColumnModel> columnList = new ArrayList<ColumnModel>();
	 				List<Element> columnElementList = tableElement.elements(ColumnModel.getElementName());
	 				if(columnElementList != null) {
	 					for(Element columnModelElement : columnElementList) {
	 						ColumnModel columnModel = new ColumnModel().getObjectFromElement(columnModelElement);
	 						tableModel.getColumnList().add(columnModel);
	 						columnModel.setTableModel(tableModel);
	 					}
	 				}
	 				
	 				// 拷贝该数据到原数组
	 				TableModel[] newTableModels = new TableModel[tableModels.length + 1];
	 				System.arraycopy(tableModels, 0, newTableModels, 0, tableModels.length);
	 				newTableModels[tableModels.length] = tableModel;
	 				tableModels = newTableModels;
	 			}
	 			
	 			readIn.close();
	 		} catch (IOException ex) {
	 			logger.error("将TableModel从剪贴板读取出来失败！" + ex.getMessage());
	 			return null;
	 		} catch (DocumentException e) {
	 			logger.error("将TableModel从剪贴板读取出来失败！" + e.getMessage());
				e.printStackTrace();
			}
	 		
	 		return tableModels;
	 	}
	 
	 	return null;
	 }

	
	public static TableModelTransfer getInstance () {
	 	return _instance;
	 }
	
	@Override
	protected int[] getTypeIds() {
		return new int[] {TABLE_MODEL_ID};
	}

	@Override
	protected String[] getTypeNames() {
		return new String[]{TABLE_MODEL_NAME};
	}

}

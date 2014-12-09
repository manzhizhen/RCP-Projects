/*
 * 文件名：Properties.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：参数类
 * 修改人： xcc
 * 修改时间：2011-11-22
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.syntax.imp;

/**
 * 参数类
 * 参数结构
 * @author    xcc
 * @version   1.0, 2011-11-22
 * @since     1.0
 */
public class Properties {
	private String valueId;
	private String valueType;
	private int valueSeq;
	private Object value;
	public String getValueId() {
		return valueId;
	}
	public void setValueId(String valueId) {
		this.valueId = valueId;
	}
	public String getValueType() {
		return valueType;
	}
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	public int getValueSeq() {
		return valueSeq;
	}
	public void setValueSeq(int valueSeq) {
		this.valueSeq = valueSeq;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	public Properties clone() {   
		Properties properties = null;
		try {
			properties = (Properties) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}  
		
		return properties;
    }   
}

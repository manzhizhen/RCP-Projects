/* 文件名：     KeyValueModel.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Agree
 * 修改时间：2013-1-4
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.model;

/**
 * 用于存储键值对的对象来传入标签提供者，简化MAP
 * @author Agree
 * @version 1.0, 2013-1-4
 * @see
 * @since 1.0
 */
public class KeyValueModel {
	private Object key; 
  	private Object value;

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}

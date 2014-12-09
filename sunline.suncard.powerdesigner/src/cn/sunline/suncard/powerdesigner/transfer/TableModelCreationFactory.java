/* 文件名：     TableModelCreationFactory.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2013-1-17
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.transfer;

import org.eclipse.gef.requests.CreationFactory;

import cn.sunline.suncard.powerdesigner.model.TableModel;

/**
 * 
 * @author  Manzhizhen
 * @version 1.0, 2013-1-17
 * @see 
 * @since 1.0
 */
public class TableModelCreationFactory implements CreationFactory{

	@Override
	public Object getNewObject() {
		return new TableModel();
	}

	@Override
	public Object getObjectType() {
		return TableModel.class;
	}

}

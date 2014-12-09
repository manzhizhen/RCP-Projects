/*
 * 文件名：Element.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：元素抽象类
 * 修改人： xcc
 * 修改时间：2011-11-02
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.syntax.imp;

import cn.sunline.suncard.sde.syntax.IElement;

/**
 * 元素抽象类
 * 为元素提供必需的操作
 * @author    xcc
 * @version   1.0, 2011-11-02
 * @since     1.0
 */

public abstract class Element implements IElement {
	
	private String id;
	private String name;
	private String description;
	private String expression;
	private String status;
	private Object elementValue;

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getStatus() {
		return this.status;
	}


	@Override
	public String getExpression() {
		return this.expression;
	}

	@Override
	public void setId(String _id) {
		this.id=_id;
	}

	@Override
	public void setName(String _name) {
		this.name=_name;
	}

	@Override
	public void setDescription(String _description) {
		this.description=_description;
	}

	@Override
	public void setStatus(String _status) {
		this.status=_status;
	}

	@Override
	public void setExpression(String _expression) {
		this.expression=_expression;
	}

	@Override
	public Object getValue() {
		return this.elementValue;
	}

	@Override
	public void setValue(Object elementValue) {
		this.elementValue=elementValue;
	}

}

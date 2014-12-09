/*
 * 文件名：FunctionTree.java
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：功能树类
 * 修改人：周兵
 * 修改时间：2011-10-13
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.tree;

import java.util.List;

/**
 * 功能树类
 * @author zhoub
 * @version 1.0, 2011-10-24
 * @see 
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
public class FunctionTree {
	private String name;
	private List children;
	private Class type;

	public FunctionTree(String name, List list, Class type) {
		this.name = name;
		this.children = list;
		this.type = type;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List getChildren() {
		return children;
	}


	public void setChildren(List children) {
		this.children = children;
	}


	public Class getType() {
		return type;
	}


	public void setType(Class type) {
		this.type = type;
	}


	public FunctionTree(String name, Class type) {
		this.name = name;
		this.type = type;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FunctionTree other = (FunctionTree) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (type != other.type)
			return false;
		return true;
	}
	
	
    
}

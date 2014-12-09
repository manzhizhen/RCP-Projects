/*
 * 文件名：SdeComboViewer.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：<描述>
 * 修改人：tpf
 * 修改时间：2011-10-31
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.dict;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author    tpf
 * @version   1.0  2011-10-31
 * @see       [相关类/方法]
 * @since     [产品/模块版本] 
 */
public class DictComboViewer extends ComboViewer {

	private Set<String> set = new HashSet<String>();
	
	private Map<String, String> map = new HashMap<String, String>();
	/**
	 * @param list
	 */
	public DictComboViewer(Combo list) {
		super(list);
		setContentProvider(new ArrayContentProvider());
		// TODO Auto-generated constructor stub
	}

	public DictComboViewer(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

	public DictComboViewer(Composite parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	public void setKey(Set set) {
		this.set = set;
	}
	
	public void setMap(Map map) {
		this.map = map;
		this.setInput(map.values());
		this.setKey(map.keySet());
	}
	
	public Object getSelectKey() {
		int index = this.getCombo().getSelectionIndex();
		if(index < 0) {
			return null;
		} else {
			return set.toArray()[index];
		}
	}
	
	public void setSelect(String key) {
		if(key == null || map.get(key) == null) {
			this.getCombo().select(-1);
			
		} else {
			this.getCombo().select(this.getCombo().indexOf(map.get(key)));
		}
		
	}
}

/**
 * 文件名：     PluginTreeContent.java
 * 版权：          Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	 用来提供插件树列表的数据结构
 * 修改人：     易振强
 * 修改时间：2011-9-23
 * 修改内容：创建
 */
package cn.sunline.suncard.sde.bs.ui.plugin.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;

import cn.sunline.suncard.sde.bs.Activator;
import cn.sunline.suncard.sde.bs.biz.BsPluginBiz;
import cn.sunline.suncard.sde.bs.entity.BsPlugin;

/**
 * 插件树列表的数据结构
 * 可以用来以插件状态来构建插件树
 * @author  易振强
 * @version 1.0, 2011-9-23
 * @see     PluginTreeContentProvider
 * @see     PluginTreeFilter
 * @see		PluginTreeLabelProvider
 * @see		PluginTreePage
 * @since   1.0
 */
public class PluginTreeContent {
	/**
	 * 树节点的显示名称，树的各个顶节点为插件状态。
	 */
	public String name;
	
	/**
	 * 插件，当此节点是树的顶节点时，该值为空。
	 */
	public Bundle bundle;
	
	/**
	 * 数据库中的插件对象
	 */
	public BsPlugin bsPlugin = null;
	
	/**
	 * 父节点，当此节点是树的顶节点时，该值为空。
	 */
	public PluginTreeContent parent = null;
	
	/**
	 * 孩子节点列表，当此节点没有孩子时，该列表Size为空。
	 */
	public List<PluginTreeContent> children = new ArrayList<PluginTreeContent>();
	
	/**
	 * 用来初始化插件的所有状态，为组建父节点做准备。
	 * Integer是各种状态值，String是其对应的名称，用来显示在树节点上。
	 */
	private static Map<Integer, String> bundleStateMap = new HashMap<Integer, String>();
	
	public static BsPluginBiz bsPluginBiz = new BsPluginBiz();
	
	static {
		// 存储所有插件状态
		bundleStateMap.put(new Integer(Bundle.ACTIVE), "ACTIVE");
		bundleStateMap.put(new Integer(Bundle.INSTALLED), "INSTALLED");
		bundleStateMap.put(new Integer(Bundle.RESOLVED), "RESOLVED");
		bundleStateMap.put(new Integer(Bundle.SIGNERS_ALL), "SIGNERS_ALL");
		bundleStateMap.put(new Integer(Bundle.SIGNERS_TRUSTED), "SIGNERS_TRUSTED");
		bundleStateMap.put(new Integer(Bundle.START_ACTIVATION_POLICY), "START_ACTIVATION_POLICY");
		bundleStateMap.put(new Integer(Bundle.START_TRANSIENT), "START_TRANSIENT");
		bundleStateMap.put(new Integer(Bundle.STARTING), "STARTING");
		bundleStateMap.put(new Integer(Bundle.STOP_TRANSIENT), "STOP_TRANSIENT");
		bundleStateMap.put(new Integer(Bundle.STOPPING), "STOPPING");
	}
	
	
	public PluginTreeContent(String name, Bundle bundle) {
		if (bundle != null) {
			this.name = bundle.getHeaders().get("Bundle-Name");
		} else {
			this.name = name;
		}

		this.bundle = bundle;
	}
	
	public PluginTreeContent(String name, Bundle bundle, BsPlugin bsPlugin) {
		if (bundle != null) {
			this.name = bundle.getHeaders().get("Bundle-Name");
		} else {
			this.name = name;
		}

		this.bundle = bundle;
		this.bsPlugin = bsPlugin;
	}

	public Bundle getBundle() {
		return bundle;
	}

	public String getName() {
		return name;
	}

	public PluginTreeContent(String name, Bundle bundle,
			List<PluginTreeContent> children) {
		if (bundle != null) {
			this.name = bundle.getHeaders().get("Bundle-Name");
		} else {
			this.name = name;
		}

		this.bundle = bundle;

		this.children = children;
		for (PluginTreeContent temp : children) {
			temp.parent = this;
		}

	}

	public String toString() {
		return name;
	}

	public BsPlugin getBsPlugin() {
		return bsPlugin;
	}

	public void setBsPlugin(BsPlugin bsPlugin) {
		this.bsPlugin = bsPlugin;
	}

	public boolean hasChild() {
		return children != null && children.size() > 0;

	}

	public List<PluginTreeContent> getChildren() {
		return children;
	}

	/**
	 * 用来作为插件树的数据源
	 * @return List<PluginTreeContent> 返回一个包含当前所有插件的列表
	 */
	public static List<PluginTreeContent> output() {
		Set<Integer> bundleStateSet = bundleStateMap.keySet();
		Iterator<Integer> bundleStateIteraotr = bundleStateSet.iterator();

		List<PluginTreeContent> pluginTreeContentList = new ArrayList<PluginTreeContent>();


		// 获得该启动器下的所有插件。
		Bundle[] bundles = Activator.getBundleContext().getBundles();

		while (bundleStateIteraotr.hasNext()) {
			Integer state = (Integer) bundleStateIteraotr.next();

			List<PluginTreeContent> pluginTreeForState = new ArrayList<PluginTreeContent>();
			for (Bundle bundleTemp : bundles) {
			
				// 如果该插件的状态和该状态集合的状态相等，这加入该状态集合。
				if (state.intValue() == bundleTemp.getState()) {
					String bundleName = bundleTemp.getHeaders().get("Bundle-Name");
					
//					System.out.println(bundleName + ":");
					
					BsPlugin bsPlugin = bsPluginBiz.findByPluginName(bundleName);
					
					// 只有在数据库中有记录的插件才显示（即基础框架以外的插件）
					if(bsPlugin != null) {
//						System.out.println("有" + "\n");
						PluginTreeContent pluginTreeContentNew = new PluginTreeContent(null,
								bundleTemp, bsPlugin);
						pluginTreeForState.add(pluginTreeContentNew);
					}
					
//					System.out.println("无 " + "\n");
				}
			
			}
			
			//如果该状态集合中有插件，则作为往列表里面添加该集合，否则什么也不做（即不再插件树种显示）。
			if(pluginTreeForState.size() > 0) {
				pluginTreeContentList.add(new PluginTreeContent((String) bundleStateMap
						.get(state), null, pluginTreeForState));
			}

		}

		return pluginTreeContentList;
	}

	
	public static void main(String[] args) {
	
	}
}

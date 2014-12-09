/* 文件名：     TreeSorter.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-20
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.tree.oldfactory;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import cn.sunline.suncard.powerdesigner.resource.DmConstants;

/**
 * 自定义排序器
 * @author  Manzhizhen
 * @version 1.0, 2012-9-20
 * @see 
 * @since 1.0
 * @deprecated
 */
public class TreeSorter extends ViewerSorter{
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if(e1 instanceof TreeContent && e2 instanceof TreeContent) {
			TreeContent e1TreeContent = (TreeContent) e1;
			TreeContent e2TreeContent = (TreeContent) e2;	
			
			if(DmConstants.TABLES_NODE_NAME.equals(e1TreeContent.getNodeName()) &&
					e1TreeContent.getObj().getClass() == Object.class) {
				return 1;
			}
			
			if(DmConstants.TABLES_NODE_NAME.equals(e2TreeContent.getNodeName()) &&
					e2TreeContent.getObj().getClass() == Object.class) {
				return -1;
			}
		}
		
		return super.compare(viewer, e1, e2);
	}
}

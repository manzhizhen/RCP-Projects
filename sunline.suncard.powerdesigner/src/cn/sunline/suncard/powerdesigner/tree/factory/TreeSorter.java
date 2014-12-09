/* 文件名：     TreeSorter.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     Manzhizhen
 * 修改时间：2012-9-20
 * 修改内容：
 */
package cn.sunline.suncard.powerdesigner.tree.factory;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import cn.sunline.suncard.powerdesigner.model.PackageModel;
import cn.sunline.suncard.powerdesigner.models.CodesNodeModel;
import cn.sunline.suncard.powerdesigner.models.DefaultColumnsNodeModel;
import cn.sunline.suncard.powerdesigner.models.DocumentsNodeModel;
import cn.sunline.suncard.powerdesigner.models.DomainsNodeModel;
import cn.sunline.suncard.powerdesigner.models.ModulesNodeModel;
import cn.sunline.suncard.powerdesigner.models.SqlsNodeModel;
import cn.sunline.suncard.powerdesigner.models.StoredProceduresNodeModel;
import cn.sunline.suncard.powerdesigner.models.TablesNodeModel;
import cn.sunline.suncard.powerdesigner.resource.DmConstants;

/**
 * 自定义排序器
 * @author  Manzhizhen
 * @version 1.0, 2012-9-20
 * @see 
 * @since 1.0
 */
public class TreeSorter extends ViewerSorter{
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if(e1 instanceof TreeContent && e2 instanceof TreeContent) {
			TreeContent e1TreeContent = (TreeContent) e1;
			TreeContent e2TreeContent = (TreeContent) e2;	
			
			// 要保证Domains节点在最后的位置，默认字段节点在倒数第二的位置，而Tables节点在倒数第三的位置
			// 要保证模块节点下面的功能模块、SQL脚本等节点的固定顺序
			Object obj1 = e1TreeContent.getObj();
			Object obj2 = e2TreeContent.getObj();
			
			if(obj1 instanceof PackageModel && !(obj2 instanceof PackageModel)) {
				return -1;
			} else if(obj2 instanceof PackageModel && !(obj1 instanceof PackageModel)) {
				return 1;
			}
			
			if((obj1 instanceof TablesNodeModel || obj1 instanceof DomainsNodeModel || obj1 instanceof 
					DefaultColumnsNodeModel ) 
					&& (obj2 instanceof TablesNodeModel || obj2 instanceof DomainsNodeModel || obj2 instanceof 
							DefaultColumnsNodeModel )) {
				return e1TreeContent.getId().compareTo(e2TreeContent.getId());
			}
			
			if((obj1 instanceof ModulesNodeModel || obj1 instanceof SqlsNodeModel || obj1 instanceof 
					DocumentsNodeModel || obj1 instanceof CodesNodeModel || obj1 instanceof StoredProceduresNodeModel) 
					&& (obj2 instanceof ModulesNodeModel || obj2 instanceof SqlsNodeModel || obj2 instanceof 
							DocumentsNodeModel || obj2 instanceof CodesNodeModel || obj2 instanceof StoredProceduresNodeModel)) {
				return e1TreeContent.getId().compareTo(e2TreeContent.getId());
			}
		}
		
//		return super.compare(viewer, e1, e2);
		// 为了让其他元素节点按添加顺序，需要返回-1
		return -1;
	}
}

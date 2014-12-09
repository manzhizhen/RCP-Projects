package cn.sunline.suncard.sde.bs.ui.plugin.patch.history;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import cn.sunline.suncard.sde.bs.entity.BsPluginlog;

public class PatchMeessageSorter extends ViewerSorter{
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		PatchMessageContent patchMessageContent1 = (PatchMessageContent)e1;
		PatchMessageContent patchMessageContent2 = (PatchMessageContent)e2;
		
		// 按照操作时间降序排列。
		if(patchMessageContent1 != null && patchMessageContent2 != null) {
			return patchMessageContent2.getBsPluginlog().getProcessDate().compareTo( 
					patchMessageContent1.getBsPluginlog().getProcessDate());
		}
		
		return 0;	
	}
}

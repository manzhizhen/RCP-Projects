package cn.sunline.suncard.sde.bs.ui.plugin.logmanage;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import cn.sunline.suncard.sde.bs.entity.BsPluginlog;

public class PluginLogSorter extends ViewerSorter {
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		// TODO Auto-generated method stub
		BsPluginlog bsPluginlog1 = (BsPluginlog)e1;
		BsPluginlog bsPluginlog2 = (BsPluginlog)e2;
		
		if(bsPluginlog1 != null && bsPluginlog2 != null) {
			return (int)(bsPluginlog1.getId().getLogSeq() 
						- bsPluginlog2.getId().getLogSeq());
		}
		
		return 0;	
	}
}

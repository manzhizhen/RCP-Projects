package cn.sunline.suncard.sde.bs.ui.plugin.patch.history;

import java.sql.Timestamp;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.sde.bs.entity.BsPatch;
import cn.sunline.suncard.sde.bs.entity.BsPluginlog;

public class PatchMessageLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(element == null || !(element instanceof PatchMessageContent)) {
			return null;
		}
		
		PatchMessageContent patchMessageContent = (PatchMessageContent) element;
		BsPatch bsPatch = patchMessageContent.getBsPatch();
		BsPluginlog bsPluginlog = patchMessageContent.getBsPluginlog();
		switch(columnIndex) {
		case 0:
			if(bsPatch == null) {
				return "";
			} else {
				return bsPatch.getId().getPatchId(); 
			}
		case 1:
			if(bsPatch == null) {
				return "";
			} else {
				return bsPatch.getPatchName(); 
			}
		case 2:
			if(bsPatch == null) {
				return "";
			} else {
				return bsPatch.getPatchVer().toString(); 
			}
		case 3:
			if(bsPluginlog == null) {
				return "";
			} else {
				return bsPluginlog.getPluginVer(); 
			}
		case 4:
			if(bsPluginlog == null) {
				return "";
			} else {
				return bsPluginlog.getProcessDate().toString(); 
			}
		default:
			return "";
		}
	}

}

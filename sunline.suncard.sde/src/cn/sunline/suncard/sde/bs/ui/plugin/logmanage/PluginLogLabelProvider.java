package cn.sunline.suncard.sde.bs.ui.plugin.logmanage;

import java.sql.Timestamp;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.sde.bs.entity.BsPluginlog;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

public class PluginLogLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		BsPluginlog bsPluginlog = (BsPluginlog)element;
		switch(columnIndex) {
			case 0:
				return bsPluginlog.getId().getLogSeq().toString();
			case 1:
				return bsPluginlog.getPluginId();
			case 2:
				String type = bsPluginlog.getProcessType();
				if("A".equalsIgnoreCase(type)) {
					return I18nUtil.getMessage("LOG_A");
				} else if("D".equalsIgnoreCase(type)) {
					return I18nUtil.getMessage("LOG_D");
				} else if("P".equalsIgnoreCase(type)) {
					return I18nUtil.getMessage("LOG_P");
				} else if("R".equalsIgnoreCase(type)) {
					return I18nUtil.getMessage("LOG_R");
				}

			case 3:
				return bsPluginlog.getSrcPluginVer();
			case 4:
				return bsPluginlog.getPluginVer();
			case 5:
				return bsPluginlog.getPatchId();
			case 6:
				return bsPluginlog.getReplaceUrl();
			case 7:
				Timestamp timestamp = bsPluginlog.getProcessDate();
				if(timestamp != null){
					return timestamp.toString();
				}else {
					return "";
				}
			case 8:
				return bsPluginlog.getModiUser();
			default:
					return "";
		}
	}

}

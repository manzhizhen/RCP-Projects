package cn.sunline.suncard.sde.bs.ui.plugin.patch.history;

import cn.sunline.suncard.sde.bs.entity.BsPatch;
import cn.sunline.suncard.sde.bs.entity.BsPluginlog;

public class PatchMessageContent {
	private BsPatch bsPatch;
	private BsPluginlog bsPluginlog;
	
	public PatchMessageContent(BsPatch bsPatch, BsPluginlog bsPluginlog) {
		this.bsPatch = bsPatch;
		this.bsPluginlog = bsPluginlog;
	}
	
	public BsPatch getBsPatch() {
		return bsPatch;
	}
	public void setBsPatch(BsPatch bsPatch) {
		this.bsPatch = bsPatch;
	}
	public BsPluginlog getBsPluginlog() {
		return bsPluginlog;
	}
	public void setBsPluginlog(BsPluginlog bsPluginlog) {
		this.bsPluginlog = bsPluginlog;
	}
}

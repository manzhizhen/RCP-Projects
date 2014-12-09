/*
 * 文件名：BsWidgetTvLabelProvider
 * 版权：Copyright 2002-2007 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：控件类标签器
 * 修改人：heyong
 * 修改时间：2011-10-17
 * 修改内容：新增
 */
package cn.sunline.suncard.sde.bs.ui.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.sunline.suncard.sde.bs.biz.BsFuncmappingBiz;
import cn.sunline.suncard.sde.bs.biz.BsPluginBiz;
import cn.sunline.suncard.sde.bs.biz.BsWidgetBiz;
import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.entity.BsFuncmapping;
import cn.sunline.suncard.sde.bs.entity.BsFuncmappingId;
import cn.sunline.suncard.sde.bs.entity.BsPlugin;
import cn.sunline.suncard.sde.bs.entity.BsPluginId;
import cn.sunline.suncard.sde.bs.entity.BsWidget;
import cn.sunline.suncard.sde.bs.entity.BsWidgetId;
import cn.sunline.suncard.sde.bs.system.Context;
import cn.sunline.suncard.sde.bs.util.I18nUtil;

/**
 * 控件类标签器
 * 
 * @author heyong
 * @version 1.0, 2011-10-20
 * @see 
 * @since 1.0
 */
public class BsWidgetTvLabelProvider implements ITableLabelProvider{
	
	private String functionId;

	public BsWidgetTvLabelProvider(String functionId){
		this.functionId = functionId;
	}
	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		BsWidget widget = (BsWidget) element;
		
		if (columnIndex == 0){
			return widget.getId().getBankorgId() == null? "" : widget.getId().getBankorgId().toString();
		}else if (columnIndex == 1){
			return widget.getId().getPcId() == null? "" : widget.getId().getPcId();
		}else if (columnIndex == 2){
			return widget.getId().getWidgetId() == null? "" : widget.getId().getWidgetId();
		}else if (columnIndex == 3){
			return widget.getWidgetName() == null? "" : widget.getWidgetName();
		}else if (columnIndex == 4){
			return widget.getWidgetType() == null? "" : I18nUtil.getMessage(widget.getWidgetType());
		}else if (columnIndex == 5){
			String widgetName = "";
			if (widget.getParWidgetId() != null){
				BsWidget w = new BsWidgetBiz().findByPk(new BsWidgetId(
						(Long)Context.getSessionMap().get(Constants.BANKORG_ID), 
						(String)Context.getSessionMap().get(Constants.PC_ID), 
						widget.getParWidgetId()));
				if (w != null){
					widgetName = w.getWidgetName();
				}
			}
			return widgetName;
		}else if (columnIndex == 6){
			String pluginName = "";
			if (widget.getParWidgetId() != null){
				BsPlugin plugin = new BsPluginBiz().findByPk(new BsPluginId(
						(Long)Context.getSessionMap().get(Constants.BANKORG_ID), 
						(String)Context.getSessionMap().get(Constants.PC_ID), 
						widget.getPluginId()));
				if (plugin != null){
					pluginName = plugin.getPluginName();
				}
			}
			return pluginName;
		}else if (columnIndex == 7){
			BsFuncmappingId id = new BsFuncmappingId();
			id.setBankorgId(widget.getId().getBankorgId());
			id.setFunctionId(this.functionId);
			id.setPcId(widget.getId().getPcId());
			id.setWidgetId(widget.getId().getWidgetId());
			BsFuncmapping funcmapping = new BsFuncmappingBiz().findByPk(id);
			if (funcmapping != null){
				widget.setMappingType(funcmapping.getMappingType());
				return funcmapping.getMappingType() == null? "" : funcmapping.getMappingType();
			}else{
				widget.setMappingType(Constants.WIDGET_MAPPING_TYPE);
				return widget.getMappingType();
			}
		}
		return null;
	}

}

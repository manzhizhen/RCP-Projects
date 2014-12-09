/* 文件名：     StartPropertrySource.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：
 * 修改人：     易振强
 * 修改时间：2012-4-6
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.model.props;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import cn.sunline.suncard.sde.bs.util.I18nUtil;
import cn.sunline.suncard.sde.workflow.gef.model.LineModel;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;

/**
 * 开始模型的属性视图
 * @author  易振强
 * @version 1.0, 2012-4-6
 * @see 
 * @since 1.0
 */
public class StartPropertySource  extends AbstractPropertySource{
	private StartModel startModel;
	
	public StartPropertySource(StartModel startModel) {
		this.startModel = startModel;
	}

	@Override
	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		TextPropertyDescriptor labelDescriptor = new TextPropertyDescriptor(StartModel.PROP_ID_LABEL, I18nUtil.getMessage("DESC"));
		TextPropertyDescriptor nameDescriptor = new TextPropertyDescriptor(StartModel.PROP_ID_NAME, I18nUtil.getMessage("NAME"));
		
		labelDescriptor.setCategory(I18nUtil.getMessage("BASE_ATTRI"));
		nameDescriptor.setCategory(I18nUtil.getMessage("BASE_ATTRI"));
		
		IPropertyDescriptor[] props = new IPropertyDescriptor[]{
				nameDescriptor, labelDescriptor};
		
		return props;
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals(StartModel.PROP_ID_LABEL)) { 
		      // 这里取得 Property view 中文本属性的值 
		      return startModel.getStartNode().getDescription();
		} else if(id.equals(StartModel.PROP_ID_NAME)) {
			 return startModel.getStartName();
		}
		
		return null;
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (id.equals(StartModel.PROP_ID_LABEL) || id.equals(StartModel.PROP_ID_NAME)) { 
		      return true;
		}
		
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (id.equals(StartModel.PROP_ID_LABEL)) { 
		      // 这里取得 Property view 中文本属性的值 
		    startModel.setLabelName((String) value);
		} else if(id.equals(StartModel.PROP_ID_NAME)) {
			startModel.setStartName((String) value);
		}
	}
}

/* 文件名：     PropertySourceAdapter.java
 * 版权：          Copyright 2002-2011 Sunline Tech. Co. Ltd. All Rights Reserved.
 * 描述：	属性试图的适配器
 * 修改人：     易振强
 * 修改时间：2012-4-6
 * 修改内容：
 */
package cn.sunline.suncard.sde.workflow.gef.model.props;

import cn.sunline.suncard.sde.workflow.gef.model.EndModel;
import cn.sunline.suncard.sde.workflow.gef.model.LineModel;
import cn.sunline.suncard.sde.workflow.gef.model.StartModel;
import cn.sunline.suncard.sde.workflow.gef.model.TaskModel;

/**
 * 属性试图的适配器
 * @author  易振强
 * @version 1.0, 2012-4-6
 * @see 
 * @since 1.0
 */
public class PropertySourceAdapter {
	/**
	 * 获取适配器
	 * @param object 模型对象
	 * @return
	 */
	public Object getAdapter(Object object) {
		if(object instanceof LineModel) {
			return new LinePropertySource((LineModel) object);
			
		} else if(object instanceof StartModel) {
			return new StartPropertySource((StartModel) object);
			
		} else if(object instanceof EndModel) {
			return new EndPropertySource((EndModel) object);
			
		} else if(object instanceof TaskModel) {
			return new TaskPropertySource((TaskModel) object);
			
		}
		
		return object;
	}
}

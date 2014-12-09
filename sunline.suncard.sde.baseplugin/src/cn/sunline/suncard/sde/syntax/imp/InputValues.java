/*
 * 文件名：InputValue.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：输入参数
 * 修改人： xcc
 * 修改时间：2011-11-22
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.syntax.imp;

import java.util.ArrayList;



/**
 * 输入参数
 * 格式化输入参数
 * @author    xcc
 * @version   1.0, 2011-11-22
 * @since     1.0
 */
public class InputValues {
	private ArrayList<Properties> inputValues = null;
	
	public InputValues(){
		inputValues = new ArrayList<Properties>();
	}
	
	/**
	 * 添加输入参数，如果重复则覆盖
	 * @param valueId：输入参数ID
	 * @param valueType：输入参数类型
	 * @param valueSeq：输入参数序号
	 * @param val：输入参数值
	 */
	public void addInputValue(int valueSeq,String valueId,String valueType,Object val){
		Properties propertie = new Properties();
		propertie.setValue(val);
		propertie.setValueId(valueId);
		propertie.setValueSeq(valueSeq);
		propertie.setValueType(valueType);
		inputValues.add(valueSeq-1, propertie);
	}
	
	/**
	 * 添加输入参数，如果重复则覆盖
	 * @param valueId：输入参数ID
	 * @param valueType：输入参数类型
	 * @param valueSeq：输入参数序号
	 */
	public void addInputValue(int valueSeq,String valueId,String valueType){
		addInputValue(valueSeq,valueId,valueType,null);
	}
	
	/**
	 * 返回制定输入值的类型
	 * @return 输入参数类型
	 */
	public String getInputValueType(String valueId){
		for(Properties propertie : inputValues){
			if(propertie.getValueId().equals(valueId)){
				return propertie.getValueType();
			}
		}
		return null;
	}
	
	/**
	 * 返回制定输入值的序号
	 * @return 输入参数序号
	 */
	public int getInputValueSeq(String valueId){
		for(Properties propertie : inputValues){
			if(propertie.getValueId().equals(valueId)){
				return propertie.getValueSeq()+1;
			}
		}
		return -1;
	}
	
	/**
	 * 返回制定序号的输入参数和输入参数类型
	 * @return 输入参数序号
	 */
	public Properties getInputValueFromSeq(int seq){
		return inputValues.get(seq-1);
	}
	
	/**
	 * 返回输入参数个数
	 * @return
	 */
	public int getInputValueCounts(){
		return inputValues.size();
	}
	
	/**
	 * 为输入参数组添加参数
	 * @param propertie 输入参数
	 */
	public void addProperties(Properties propertie){
		inputValues.add(propertie.getValueSeq()-1, propertie);
	}
	
	/**
	 * 获取全部输入参数
	 * @return
	 */
	public ArrayList<Properties> getProperties(){
		return inputValues;
	}
	
	/**
	 * 通过序号获取ID
	 * @param seq 序号
	 * @return 参数ID
	 */
	public String getValueIdFromSeq(int seq){
		return inputValues.get(seq-1).getValueId();
	}
	
	
	
}

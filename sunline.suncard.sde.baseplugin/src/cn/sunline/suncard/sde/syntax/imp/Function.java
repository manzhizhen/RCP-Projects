/*
 * 文件名：Function.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：函数类
 * 修改人： xcc
 * 修改时间：2011-11-21
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.syntax.imp;

import java.util.ArrayList;

/**
 * 函数类
 * 所有决策引擎内关于函数的操作
 * @author    xcc
 * @version   1.0, 2011-11-21
 * @since     1.0
 */
public class Function extends Element {

	private final boolean hasExpression=true;
	private InputValues inputValues = new InputValues();
	private String outputType = null;
	
	/* (non-Javadoc)
	 * @see cn.sunline.suncard.sde.syntax.IElement#hasExpression()
	 */
	@Override
	public boolean hasExpression() {
		return hasExpression;
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	public InputValues getInputValues() {
		return inputValues;
	}

	public void setInputValues(InputValues inputValues) {
		this.inputValues = inputValues;
	}
	
	/**
	 * 添加输入参数，如果重复则覆盖
	 * @param valueId：输入参数ID
	 * @param valueType：输入参数类型
	 * @param valueSeq：输入参数序号
	 */
	public void addInputValue(int valueSeq,String valueId,String valueType){
		inputValues.addInputValue(valueSeq,valueId,valueType,null);
	}
	
	/**
	 * 返回制定输入值的类型
	 * @return 输入参数类型
	 */
	public String getInputValueType(String valueId){
		return inputValues.getInputValueType(valueId);
	}
	
	/**
	 * 返回制定序号的输入参数和输入参数类型
	 * @return 输入参数序号
	 */
	public Properties getInputValueFromSeq(int seq){
		return inputValues.getInputValueFromSeq(seq);
	}
	
	/**
	 * 返回输入参数个数
	 * @return
	 */
	public int getInputValueCounts(){
		return inputValues.getInputValueCounts();
	}
	
	/**
	 * 返回制定输入值的序号
	 * @return 输入参数序号
	 */
	public int getInputValueSeq(String valueId){
		return inputValues.getInputValueSeq(valueId);
	}

}


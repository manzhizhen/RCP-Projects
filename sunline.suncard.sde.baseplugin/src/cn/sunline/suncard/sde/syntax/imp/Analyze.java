/*
 * 文件名：Analyze.java
 * 版权：Copyright 2011-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：统计分析
 * 修改人： xcc
 * 修改时间：2011-11-22
 * 修改内容：新增
*/
package cn.sunline.suncard.sde.syntax.imp;

/**
 * 统计分析
 * 提供统计分析所需要的操作
 * @author    xcc
 * @version   1.0, 2011-11-22
 * @since     1.0
 */
public class Analyze extends Element {
	
	private final boolean hasExpression=false;
	private String functionId = null;
	private InputValues inputValues = new InputValues();

	/* (non-Javadoc)
	 * @see cn.sunline.suncard.sde.syntax.IElement#hasExpression()
	 */
	@Override
	public boolean hasExpression() {
		// TODO Auto-generated method stub
		return hasExpression;
	}

	public String getFunctionId() {
		return functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
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
	 * 添加输入参数，如果重复则覆盖
	 * @param valueId：输入参数ID
	 * @param valueType：输入参数类型
	 * @param valueSeq：输入参数序号
	 * @param val：输入参数值
	 */
	public void addInputValue(int valueSeq,String valueId,String valueType,Object val){
		inputValues.addInputValue(valueSeq,valueId,valueType,val);
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

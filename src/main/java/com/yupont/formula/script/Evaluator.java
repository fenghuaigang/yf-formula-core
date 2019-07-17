package com.yupont.formula.script;

import java.math.BigDecimal;

/**
 * 脚本（或表达式）求值器接口，用于实现多种求值器，如：js，parsii，groovy等
 * @author FJW
 *
 */
public interface Evaluator {
	/**
	 * JS表达式中，返回结果的变量名
	 */
	final public static String VAR_RESULT = "result";
	
	/**
	 * 向求值器添加变量和值
	 * @param name 变量名称
	 * @param value 变量值
	 * @return 返回求值器
	 */
	Evaluator addVariable(String name, Object value);
	/**
	 * 求值器更新表达式
	 * @param script
	 * @return
	 */
	Evaluator setScript(String script);
	/**
	 * 执行求知
	 * @param curScript 脚本
	 * @return
	 */
	BigDecimal evaluate()  throws EvaluatorException;
	
	default public BigDecimal toBigDecimal(Object value) {
		BigDecimal v = new BigDecimal("0");		
		if(value!=null){
			v = v.add(new BigDecimal(value+""));
		}
		return v;
	}
}

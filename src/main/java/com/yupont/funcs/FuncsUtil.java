package com.yupont.funcs;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import com.yupont.formula.script.Evaluator;
import com.yupont.formula.script.EvaluatorException;
import com.yupont.formula.script.ParsiiEvaluator;


public class FuncsUtil {
	/**
	 * 高精度表达式计算
	 * @param expression 表达式
	 * @param variables 参数集合
	 * @return
	 * @throws EvaluatorException 
	 */
	public static BigDecimal eval(String expression, Map<String, Object> variables) throws EvaluatorException {
		ParsiiEvaluator evaluator = new ParsiiEvaluator(expression);
		if (Objects.nonNull(variables) && !variables.isEmpty()) {
			variables.forEach((k, v) -> evaluator.addVariable(k, v));
		}
		return evaluator.evaluate();
	}
	
	/**
	 * 自定义求值器处理表达式
	 * @param evaluator
	 * @param expression
	 * @param variables
	 * @return
	 * @throws EvaluatorException
	 */
	public static BigDecimal eval(Evaluator evaluator,String expression, Map<String, Object> variables) throws EvaluatorException{
		if (Objects.nonNull(variables) && !variables.isEmpty()) {
			variables.forEach((k, v) -> evaluator.addVariable(k, v));
		}
		return evaluator.setScript(expression).evaluate();
	}
}

package com.yupont.formula.script;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONObject;

/**
 * 公式求值入口。<br>
 * <br>公式解析类，通过解析返回合适的求值器 {@link Evaluator}
 * <br>自动判断公式是脚本还是表达式，返回合适的求值器
 * @author FJW
 *
 */
public class FormulaParser {
	
	/**
	 * 自动判断公式是脚本还是表达式，返回合适的求值器
	 */
	public static Evaluator Parse(String script) {
		return (script.indexOf(Evaluator.VAR_RESULT)<0) ? 
				new ParsiiEvaluator(script) :  new JsEvaluator(script);
	}

	/**
	 * 带参数的脚本或表达式校验
	 * <p>表达式保存前应该先校验
	 * @param script 脚本或表达式
	 * @param varNames 变量名数组
	 * @return 有效返回true，否则false
	 */
	@SuppressWarnings("unused")
	public static JSONObject valid(String script, String ... varNames) {
		JSONObject ret = new JSONObject();
		
		Evaluator ev = Parse(script);
		if(varNames.length>0) {
			int i = 1;
			for(String v : varNames) {
				ev.addVariable(v, i++);
			}
		}
		
		try {
			BigDecimal v = ev.evaluate();
		} catch (EvaluatorException e) {
			ret.put("success", "false");
			ret.put("msg", e.getLocalizedMessage().toUpperCase());
			ret.put("line", e.getLineNumber());
			ret.put("col", e.getColumnNumber());
			return ret;
		}
		
		ret.put("success", "true");
		return ret;
	}
}

package com.yupont.formula.script;

import java.math.BigDecimal;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;



/**
 * 基于JDK包含的JS引擎实现的脚本（或表达式）求值器
 * @author FJW
 *
 */
public class JsEvaluator implements Evaluator {

	protected ScriptEngine engine;
	protected String curScript;
	
	public JsEvaluator(String script){
		parse(script);
		ScriptEngineManager manager = new ScriptEngineManager();   
		engine = manager.getEngineByName("javascript");
	}

	protected void parse(String script) {
		if(StringUtils.isBlank(script)) return;
		
		if(script.indexOf(VAR_RESULT)<0) script = "var "+ VAR_RESULT + " = " + script;
		this.curScript = script;
	}
	
	@Override
	public JsEvaluator addVariable(String name, Object value) {
		if(engine==null) return null;
		engine.put(name, toBigDecimal(value));
		return this;
	}

	@Override
	public BigDecimal evaluate() throws EvaluatorException {
		// 只能为Double，使用Float和Integer会抛出异常    
		try {
			engine.eval(curScript);
		} catch (ScriptException e) {
			//System.out.println(e.getMessage());
			throw new EvaluatorException(e);
		}
		return toBigDecimal(engine.get(VAR_RESULT));
	}

	@Override
	public Evaluator setScript(String script) {
		parse(script);
		return this;
	}
	
}

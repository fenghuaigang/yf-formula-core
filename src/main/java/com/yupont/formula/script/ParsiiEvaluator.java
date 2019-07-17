package com.yupont.formula.script;


import java.math.BigDecimal;

import com.alibaba.fastjson.JSON;
import com.yupont.util.TextUtil;

import parsiii.eval.Expression;
import parsiii.eval.Parser;
import parsiii.eval.Scope;
import parsiii.eval.Variable;
import parsiii.tokenizer.ParseError;
import parsiii.tokenizer.ParseException;

public class ParsiiEvaluator implements Evaluator {

	private Scope scope;
	private String curScript;
	private Expression expression;
	
	public ParsiiEvaluator(String script) {
		scope = new Scope();
		this.curScript = script;
	}
	@Override
	public Evaluator addVariable(String name, Object value) {
		Variable v = scope.create(name.toLowerCase());
		if(value.toString().startsWith("{") && value.toString().endsWith("}")){
			v.withValue(JSON.parseObject(value.toString()));
		}else if(TextUtil.isNum(value.toString()) && !value.toString().startsWith("0")){
			v.withValue(toBigDecimal(value));
		}else if(!value.toString().contains("@")){//非对象
			v.withValue(value.toString());
		}else{
			v.withValue(value.toString());
		}
		return this;
	}

	@Override
	public BigDecimal evaluate() throws EvaluatorException {
		if(expression == null || !curScript.equals(this.curScript)) {
			this.curScript = curScript.toLowerCase();
			try {
				expression = Parser.parse(this.curScript, scope);
			} catch (ParseException e) {
				ParseError err = e.getErrors().get(0);
				throw new EvaluatorException(err.getMessage(), "", 
						err.getPosition().getLine(),err.getPosition().getPos());
				
			}
		}
			
		return expression.evaluate2();
	}
	
	public String evalStrValue() throws EvaluatorException {
		if(expression == null || !curScript.equals(this.curScript)) {
			this.curScript = curScript.toLowerCase();
			try {
				expression = Parser.parse(this.curScript, scope);
			} catch (ParseException e) {
				ParseError err = e.getErrors().get(0);
				throw new EvaluatorException(err.getMessage(), "", 
						err.getPosition().getLine(),err.getPosition().getPos());
				
			}
		}
		return expression.evaluate3();
	}
	
	@Override
	public Evaluator setScript(String script) {
		this.curScript = script;
		return this;
	}

}

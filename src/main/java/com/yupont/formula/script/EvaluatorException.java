package com.yupont.formula.script;

import javax.script.ScriptException;

public class EvaluatorException extends ScriptException {

	private static final long	serialVersionUID	= -1939798991205509320L;

	int							code				= 0;

	private String message;
	
	public EvaluatorException(ScriptException e) {
		this("无效的表达式", e.getFileName(), e.getLineNumber(), e.getColumnNumber());
	}

	public EvaluatorException(String message, String fileName, int lineNumber, int columnNumber) {
		super(message, fileName, lineNumber, columnNumber);
		this.message = message;
	}

	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getLocalizedMessage() {
		String msg = this.message;
		//本地化
		if(msg.indexOf("无效的符号：'<End Of Input>'")>=0)
			msg = msg.replace("无效的符号：'<End Of Input>'","无效的结尾");
		
		return String.format("%s。位置：行%d 列%d", 
				msg,
				this.getLineNumber(), 
				this.getColumnNumber());
	}
}

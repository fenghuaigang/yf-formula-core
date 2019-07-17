package com.yupont.core.sql.model;

import java.io.Serializable;
/**
 * @author Administrator
 */
public class Routine implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1593113543867424736L;
	protected String name;
	protected String definition;
	
	private StringBuilder stringBuilder=new StringBuilder();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefinition() {
		return stringBuilder.toString();
	}
	
	public StringBuilder appendStr(String str){
		stringBuilder.append(str);
		return stringBuilder;
	}

	@Override
	public String toString() {
		return "Procedure [name=" + name + ", definition=" + getDefinition()+"]";
	}
	
}

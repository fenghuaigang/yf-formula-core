package com.yupont.core.sql.model;

import java.io.Serializable;
/**
 * @author Administrator
 */
public class Trigger extends Routine implements Serializable{

	private static final long serialVersionUID = -1867385892512564099L;
	
	private String tableName;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public String toString() {
		return "Trigger [tableName=" + tableName + ", name=" + name + ", definition=" + getDefinition()  + "]";
	}
	
	

}

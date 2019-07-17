package com.yupont.core.sql.model;

import java.io.Serializable;

/**
 * Represents a table constraint.
 * 
 * @author xumh
 *
 */
public class Constraint implements Serializable{

	private static final long serialVersionUID = 6464975225064851090L;
	
	private String name;
	private TableConstraintType tableConstraintType;
	private boolean deferrable;
	//such as "D1 IS NOT NULL"
	private String definition;
	
	
	public TableConstraintType getTableConstraintType() {
		return tableConstraintType;
	}
	public void setTableConstraintType(TableConstraintType tableConstraintType) {
		this.tableConstraintType = tableConstraintType;
	}
	public boolean isDeferrable() {
		return deferrable;
	}
	public void setDeferrable(boolean deferrable) {
		this.deferrable = deferrable;
	}

	public String getDefinition() {
		return definition;
	}
	@Override
	public String toString() {
		return "Constraint [name=" + name + ", tableConstraintType=" + tableConstraintType + ", deferrable=" + deferrable + ", definition=" + definition + "]";
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}

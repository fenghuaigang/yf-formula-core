package com.yupont.core.sql.model;
/**
 * @author Administrator
 */
public enum TableType {
	/**
	 * unknown("unKnow"), 
	 * system_table("SYSTEM TABLE"),
	 * global_temporary("GLOBAL TEMPORARY"),
	 * local_temporary("LOCAL TEMPORARY"), 
	 * table("TABLE"),
	 * view("VIEW"),
	 * alias("ALIAS"),
	 * synonym("SYNONYM");
	 */
	unknown("unKnow"), 
	system_table("SYSTEM TABLE"),
	global_temporary("GLOBAL TEMPORARY"),
	local_temporary("LOCAL TEMPORARY"), 
	table("TABLE"),
	view("VIEW"),
	alias("ALIAS"),
	synonym("SYNONYM");
	
	private String name;

	private TableType(String s) {
		name = s;
	}

	@Override
	public String toString() {
		return name;
	}

	public static TableType fromString(String text) {
		if (text != null) {
			for (TableType b : TableType.values()) {
				if (text.equalsIgnoreCase(b.name)) {
					return b;
				}
			}
		}
		return null;
	}

}

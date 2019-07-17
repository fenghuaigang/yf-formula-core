package com.yupont.core.sql.ecexption;

/**
 * java.sql.DatabaseMeta can not get database meta exception
 * 
 * @author xumh
 *
 */
@SuppressWarnings("serial")
public class DatabaseMetaGetMetaException extends AbstractDataAccessException{
	public DatabaseMetaGetMetaException(String msg) {
		super(msg);
	}
	
	public DatabaseMetaGetMetaException(String msg, Throwable cause) {
		super(msg, cause);
	}
}

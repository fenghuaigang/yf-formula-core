package com.yupont.core.sql.ecexption;

/**
 * 
 * @author xumh
 * @see com.yupont.core.sql.ecexption.AbstractDataAccessException
 */
@SuppressWarnings("serial")
public abstract class AbstractDataAccessException extends AbstractNestedRuntimeException {

	/**
	 * Constructor for DataAccessException.
	 * @param msg the detail message
	 */
	public AbstractDataAccessException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for DataAccessException.
	 * @param msg the detail message
	 * @param cause the root cause (usually from using a underlying
	 * data access API such as JDBC)
	 */
	public AbstractDataAccessException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
package com.yupont.core.sql.ecexption;

/**
 * Root of the hierarchy of data access exceptions that are considered non-transient -
 * where a retry of the same operation would fail unless the cause of the Exception
 * is corrected.
 *
 * @author Thomas Risberg
 * @see com.yupont.core.sql.ecexption.NonTransientDataAccessException
 * @see java.sql.SQLNonTransientException
 */
@SuppressWarnings("serial")
public class NonTransientDataAccessException extends AbstractDataAccessException {

	/**
	 * Constructor for NonTransientDataAccessException.
	 * @param msg the detail message
	 */
	public NonTransientDataAccessException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for NonTransientDataAccessException.
	 * @param msg the detail message
	 * @param cause the root cause (usually from using a underlying
	 * data access API such as JDBC)
	 */
	public NonTransientDataAccessException(String msg, Throwable cause) {
		super(msg, cause);
	}

}

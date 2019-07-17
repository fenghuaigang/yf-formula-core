package com.yupont.core.sql.ecexption;

import java.sql.SQLException;

/**
 * Fatal exception thrown when we can't connect to an RDBMS using JDBC.
 *
 * @author Xumh
 * @see org.springframework.jdbc.CannotGetJdbcConnectionException
 */
@SuppressWarnings("serial")
public class CannotGetJdbcConnectionException extends AbstractNestedRuntimeException{
	/**
	 * Constructor for CannotGetJdbcConnectionException.
	 * @param msg the detail message
	 * @param ex SQLException root cause
	 */
	public CannotGetJdbcConnectionException(String msg, SQLException ex) {
		super(msg, ex);
	}
}

package com.yupont.core.sql.util;

import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * @author Administrator
 */
public interface ResultSetExtractor<T> {
	
	T extractData(ResultSet rs) throws SQLException;
}

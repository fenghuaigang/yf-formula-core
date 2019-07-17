package com.yupont.core.sql.meta;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.yupont.core.sql.ecexption.NonTransientDataAccessException;
import com.yupont.core.sql.util.JdbcUtils;

/**
 * can be inherit and override the newInstance function
 * 
 * @author xumh
 *
 */
public class DefaultMetaCrawlerFactory implements MetaCrawlerFactory{
	
	public static final int MYSQL = 1;
	public static final int SQL_SERVER = 2;
	public static final int ORACLE = 3;

	@Override
	public MetaCrawler newInstance(Connection con) {
		String product=getProductName(con);
		
		DatabaseMetaData dbm=getDatabaseMetaData(con);
		if ("MySQL".equals(product)) {
			return new MySqlMetaCrawler(dbm);
		} else if ("Oracle".equals(product)) {
			return new OracleMetaCrawler(dbm);
		} else if ("Microsoft SQL Server".equals(product)) {
			return new SqlServerMetaCrawler(dbm);
		} else {
			return null;
		}
	}

	protected String getProductName(Connection conn) {
		String product = null;
		try {
			DatabaseMetaData dbm = conn.getMetaData();
			product = dbm.getDatabaseProductName();
			return product;

		} catch (SQLException e) {
			throw new NonTransientDataAccessException("can not get database product information!",e);
		}
	}
	
	protected DatabaseMetaData getDatabaseMetaData(Connection connection) {
		DatabaseMetaData dbm;
		try {
			dbm = connection.getMetaData();
		} catch (SQLException e) {
			JdbcUtils.closeConnection(connection);
			throw new NonTransientDataAccessException("Could not get DatabaseMeta");
		}
		return dbm;
	}
	
	
}

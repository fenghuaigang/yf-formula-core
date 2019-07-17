package com.yupont.core.sql.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yupont.core.sql.ecexption.CannotGetJdbcConnectionException;
import com.yupont.core.sql.ecexption.AbstractDataAccessException;
import com.yupont.core.sql.ecexption.DatabaseMetaGetMetaException;

public class JdbcUtils {
	private static Log logger=LogFactory.getLog(JdbcUtils.class);
	/**
	 * Close the given JDBC Connection and ignore any thrown exception.
	 * This is useful for typical finally blocks in manual JDBC code.
	 * @param con the JDBC Connection to close (may be {@code null})
	 */
	public static void closeConnection(Connection con) {
		if (con != null) {
			try {
				logger.debug("close connection "+con+" "+con.hashCode());
				con.close();
			}
			catch (SQLException ex) {
				logger.debug("Could not close JDBC Connection", ex);
			}
			catch (Throwable ex) {
				// We don't trust the JDBC driver: It might throw RuntimeException or Error.
				logger.debug("Unexpected exception on closing JDBC Connection", ex);
			}
		}
	}
	
	
	/**
	 * Get a JDBC Connection from datasource
	 * 
	 * @return Connection
	 * @throws AbstractDataAccessException
	 */
	public static Connection getConnection(DataSource dataSource) throws CannotGetJdbcConnectionException {
		Assert.notNull(dataSource, "no datasource can be find!");
		Connection con;
		try {
			con = dataSource.getConnection();
			con.isReadOnly();//Is readOnly
			logger.debug("Get the connection "+con+" "+con.hashCode());
		} catch (SQLException e) {
			throw new CannotGetJdbcConnectionException("Could not get JDBC Connection", e);
		}
		return con;
	}
	
	
	/**
	 * Close the given JDBC ResultSet and ignore any thrown exception.
	 * This is useful for typical finally blocks in manual JDBC code.
	 * @param rs the JDBC Resultset to close (may be {@code null})
	 */
	public static void closeResultSet(ResultSet rs){
		if (rs != null) {
			try {
				logger.debug("close ResultSet "+rs+" "+rs.hashCode());
				rs.close();
			}
			catch (SQLException ex) {
				logger.debug("Could not close JDBC ResultSet", ex);
			}
			catch (Throwable ex) {
				// We don't trust the JDBC driver: It might throw RuntimeException or Error.
				logger.debug("Unexpected exception on closing JDBC ResultSet", ex);
			}
		}
	}
	
	public static void closePreparedStatement(PreparedStatement rs){
		if (rs != null) {
			try {
				logger.debug("close PreparedStatement "+rs+" "+rs.hashCode());
				rs.close();
			}
			catch (SQLException ex) {
				logger.debug("Could not close JDBC PreparedStatement", ex);
			}
			catch (Throwable ex) {
				// We don't trust the JDBC driver: It might throw RuntimeException or Error.
				logger.debug("Unexpected exception on closing JDBC PreparedStatement", ex);
			}
		}
	}
	
	public static <T> T query(DatabaseMetaData dbm,String sql,String exceptionMessage,ResultSetExtractor<T> rsExtractor,Object... args){
		ResultSet rs=null;
		PreparedStatement st=null;
		try{
			Connection con=dbm.getConnection();
			 st=con.prepareStatement(sql);
			for(int i=1;i<=args.length;++i){
				st.setObject(i, args[i-1]);
			}
			rs=st.executeQuery();
			return rsExtractor.extractData(rs);
		}catch(SQLException e){
			throw new DatabaseMetaGetMetaException(exceptionMessage, e);
		}finally{
			JdbcUtils.closePreparedStatement(st);
			JdbcUtils.closeResultSet(rs);
		}
	}


}

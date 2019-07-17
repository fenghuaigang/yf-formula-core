package com.yupont.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONArray;
import com.yupont.core.sql.JsonArray2Handler;
import com.yupont.core.sql.JsonArrayHandler;

/**
 * @author Administrator
 */
public class DbUtil {
	public static final Log logger = LogFactory.getLog(DbUtil.class);


	public static Connection getConnection(String url, String user, String password) {
		try {
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			logger.error(e);
		}
		return null;
	}

	public static Connection getConnection(DataSource ds) {
		try {
			return ds.getConnection();
		} catch (SQLException e) {
			logger.error(e);
		}
		return null;
	}

	public static ResultSet executeQuery2(Connection conn, String sql, Object[] params) {
		ResultSet ret = null;
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					pst.setObject(i + 1, params[i]);
				}
			}
			ret = pst.executeQuery();
		} catch (Exception e) {
			logger.error(e);
		}
		return ret;
	}

	public static ResultSet executeQuery(Connection conn, String sql, Object[] params) {
		ResultSet ret = null;
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					pst.setObject(i + 1, params[i]);
				}
			}
			ret = pst.executeQuery();
			pst.close();
		} catch (Exception e) {
			logger.error(e);
		}
		return ret;
	}

	public static ResultSet executeQuery(Connection conn, String sql) {
		return executeQuery(conn, sql, null);
	}


	/**
	 * @param sql
	 * @return Object[] or null
	 */
	public static Object[] query(Connection conn, String sql) {
		Object[] objs = null;
		try {
			QueryRunner qRunner = new QueryRunner();
			objs = qRunner.query(conn, sql, new ArrayHandler());
		} catch (Exception e) {
			logger.error(e);
		}
		return objs;
	}

	/**
	 * 执行查询，返回指定形式的结果集
	 * 
	 * @param sql
	 * @return Object[] or null
	 */
	public static <T> T query(Connection conn, String sql, ResultSetHandler<T> handler) {
		T objs = null;
		try {
			QueryRunner qRunner = new QueryRunner();
			objs = qRunner.query(conn, sql, handler);
		} catch (Exception e) {
			logger.error(e);
		}
		return objs;
	}

	public static Object[] query(Connection conn, String sql, Object[] params) {
		Object[] objs = null;
		try {
			QueryRunner qRunner = new QueryRunner();
			objs = qRunner.query(conn, sql, new ArrayHandler(), params);
		} catch (Exception e) {
			logger.error(e);
		}
		return objs;
	}


	/** ArrayListHandler将ResultSet中所有的数据转化成List，List中存放的是Object[]* */
	public static List<Object[]> queryList(Connection conn, String sql) {
		List<Object[]> list = null;
		try {
			QueryRunner qRunner = new QueryRunner();
			list = (List<Object[]>) qRunner.query(conn, sql, new ArrayListHandler());
		} catch (Exception e) {
			logger.error(e);
		}
		return list;
	}

	public static List<Object[]> queryList(Connection conn, String sql, Object[] params) {
		List<Object[]> list = null;
		try {
			QueryRunner qRunner = new QueryRunner();
			list = (List<Object[]>) qRunner.query(conn, sql, new ArrayListHandler(), params);
		} catch (Exception e) {
			logger.error(e);
		}
		return list;
	}

	/**
	 * MapHandler ：将ResultSet中第一行的数据存成Map映射
	 * 
	 * @param sql
	 * @return
	 */
	public static Map<String, Object> queryMap(Connection conn, String sql) {
		Map<String, Object> map = null;
		try {
			QueryRunner qRunner = new QueryRunner();
			map = qRunner.query(conn, sql, new MapHandler());
		} catch (Exception e) {
			logger.error(e);
		}
		return map;
	}

	public static List<Map<String, Object>> queryMapList(Connection conn, String sql) {
		List<Map<String, Object>> list =  new ArrayList<>();
		try {
			QueryRunner qRunner = new QueryRunner();
			list = (List<Map<String, Object>>) qRunner.query(conn, sql, new MapListHandler());
		} catch (Exception e) {
			logger.error(e);
		}

		return list;
	}

	/** MapListHandler ：将ResultSet中所有的数据存成List。List中存放的是Map **/
	public static List<Map<String, Object>> queryMapList(Connection conn, String sql, Object[] params) {
		List<Map<String, Object>> list = null;
		try {
			QueryRunner qRunner = new QueryRunner();
			list = qRunner.query(conn, sql, new MapListHandler(), params);
		} catch (Exception e) {
			logger.error(e);
		}
		return list;
	}

	/** MapListHandler ：将ResultSet中所有的数据存成List。List中存放的是Map **/
	public static JSONArray queryJSONArray(Connection conn, String sql, Object[] params) {
		JSONArray list = null;
		try {
			QueryRunner qRunner = new QueryRunner();
			list = (JSONArray) qRunner.query(conn, sql, new JsonArrayHandler(), params);
		} catch (Exception e) {
			logger.error(e);
		}
		return list;
	}

	/** MapListHandler ：将ResultSet中所有的数据存成List。List中存放的是Map **/
	public static JSONArray queryJSONArray2(Connection conn, String sql, Object[] params) {
		JSONArray list = null;
		try {
			QueryRunner qRunner = new QueryRunner();
			list = (JSONArray) qRunner.query(conn, sql, new JsonArray2Handler(), params);
		} catch (Exception e) {
			logger.error(e);
		}
		return list;
	}

	public static int update(Connection conn, String sql) {
		int rows = 0;
		try {
			QueryRunner qRunner = new QueryRunner();
			rows = qRunner.update(conn, sql);
		} catch (Exception e) {
			logger.error(e);
		}
		return rows;
	}

	public static int update(Connection conn, String sql, Object param) {
		int rows = 0;
		try {
			QueryRunner qRunner = new QueryRunner();
			rows = qRunner.update(conn, sql, param);
		} catch (Exception e) {
			logger.error(e);
		}
		return rows;
	}

	public static int[] batch(Connection conn, String sql, Object[][] params) {
		int[] rows = null;
		try {
			QueryRunner qRunner = new QueryRunner();
			rows = qRunner.batch(conn, sql, params);
		} catch (Exception e) {
			logger.error(e);
		}
		return rows;
	}


	public static boolean execute(Connection connection, String sql, Object[] params) {
		boolean execute = false;
		try {
			PreparedStatement pst = connection.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					pst.setObject(i + 1, params[i]);
				}
			}
			execute = pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return execute;
	}

	public static void closeConn(Connection... conn) {
		for (Connection c : conn) {
			DbUtils.closeQuietly(c);
		}
	}

}
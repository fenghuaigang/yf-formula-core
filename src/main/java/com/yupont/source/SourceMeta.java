package com.yupont.source;

import java.sql.Connection;

import javax.sql.DataSource;

import com.yupont.core.sql.meta.MetaLoader;

public interface SourceMeta {
	/**
	 * 数据源ID
	 * @return
	 */
	String getSourceId();
	/**
	 * 返回数据源ID
	 * @param sourceId
	 */
	void setSourceId(String sourceId);
	/**
	 * 数据源名称
	 * @return
	 */
	String getName();
	/**
	 * 设置数据源名称
	 * @param name
	 */
	void setName(String name);
	/**
	 * 数据源类型，{@link DatabaseMeta.TYPE}
	 * @return
	 */
	String getSourceType();
	/**
	 * 设置数据源类型
	 * @param sourceType
	 */
	void setSourceType(String sourceType);
	/**
	 * 获取JDBC类型
	 * @return
	 */
	String getJdbcType();
	/**
	 * 设置JDBC类型
	 * @param jdbcType
	 */
	void setJdbcType(String jdbcType);
	 
	/**
	 * 获取URL
	 * @return
	 */
	String getUrl();
	/**
	 * 设置URL
	 * @param url
	 */
	void setUrl(String url);
	/**
	 * 获取用户名
	 * @return
	 */
	String getUsername();
	/**
	 * 设置用户名
	 * @param username
	 */
	void setUsername(String username);
	/**
	 * 获取密码
	 * @return
	 */
	String getPassword();
	/**
	 * 设置密码
	 * @param password
	 */
	void setPassword(String password);
	 
	/**
	 * 获取state
	 * @return
	 */
	Boolean getState();
	/**
	 * 设置state
	 * @param state
	 */
	void setState(Boolean state);

	/**
	 * 返回元数据加载器，该加载器利用元数据爬虫MetaCrawler获取数据库的元数据
	 * @return
	 */
	MetaLoader getMetaLoader();
	/**
	 * 获取driverClass
	 * @return
	 */
	String getDriverClass();
	/**
	 * 获取数据源
	 * @return
	 */
	DataSource getDataSource();
	
	/**
	 * 设置数据源
	 * @param dataSource
	 */
	void setDataSource(DataSource dataSource);
	
	/**
	 * 获取连接
	 * @return
	 */
	Connection getConnection();
	
	/**
	 * 
	 * @param schama
	 * @param tableName
	 * @return
	 */
	boolean isTableExists(String schama, String tableName);
	
	/**
	 *  获取分页SQL
	 * @param tableName
	 * @param orderColumn
	 * @param lastValue
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	String getPaginationSql(String tableName,String orderColumn,String lastValue,int pageIndex,int pageSize);
	
}

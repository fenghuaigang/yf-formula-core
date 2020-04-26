package com.yupont.source;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.yupont.core.sql.meta.MetaLoader;
import com.yupont.core.sql.meta.MetaLoaderImpl;
import com.yupont.core.sql.model.Database;
import com.yupont.util.DbUtil;

/**
 * 实现数据库信息和方言操作的基类，实现{@link Database}接口
 * @author fjw
 *
 */
public class DatabaseMeta extends ConfigDataSource implements SourceMeta {

	private static final Log logger = LogFactory.getLog(DatabaseMeta.class);

	public static class DataSourceType{
		//分布式数据仓库，使用redis
		public static final String DC_SOURCE 				= "0";
		public static final String LOCAL_FILE 				= "01";
		public static final String FTP_FILE 				= "02";
		public static final String DATA_SOURCE 				= "03";
		public static final String REST_SERVICE				= "04";
	}

	public enum TYPE {
		/**数据源类型枚举：EFILE, JDBC, HBASE, REDIS, KAFKA...*/
		EFILE, JDBC, HBASE, REDIS, KAFKA;
	}
	
	public enum JDBC {
		/**JDBC数据库类型枚举: KINGBASE, MYSQL, ORACLE,SQLSERVER...*/
		KINGBASE, MYSQL, ORACLE, SQLSERVER;
	}

	public static final String	FIELDNAME_PROTECTOR		= "_";
	public static final String	SELECT_COUNT_STATEMENT	= "select count(*) FROM";
	public static final String	DROP_TABLE_STATEMENT	= "DROP TABLE IF EXISTS ";

	protected String 			sourceId;
	/** 数据库中文名称 */
	protected String			name;
	/** efile/oracle/mysql */
	protected String			sourceType;
	protected String			jdbcType;
	protected String			url;
	protected String			username;
	protected String			password;
	protected String			endoding;
	protected Boolean 			state;

	protected DataSource		dataSource;
	protected MetaLoader 		metaLoader;

	protected List<ConfigDataTable>	tables					= new ArrayList<>();
	Map<String, ConfigDataTable>	tableMap				= new HashMap<>();

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getSourceType() {
		return sourceType;
	}

	@Override
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	@Override
	public String getJdbcType() {
		return jdbcType;
	}

	@Override
	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getDriverClass() {
		return "";
	}
	 
	@Override
	public Connection getConnection() {
		try {
			return this.dataSource.getConnection();
		} catch (SQLException e) {
			logger.error(e);
		}
		return null;
	}

	@Override
	public boolean isTableExists(String schama, String tableName) {
		return this.getMetaLoader().getTable( tableName) != null;
	}
	
	public Object execute(String sql) {
		Connection conn = getConnection();
		try {
			return DbUtil.executeQuery(conn,sql);
		}
		catch(Exception e) {
		}finally {
			DbUtil.closeConn(conn);
		}
		return null;
	}

	@Override
	public MetaLoader getMetaLoader() {
		if(dataSource==null){
			return null;
		}
		if(metaLoader==null){
			metaLoader = new MetaLoaderImpl(dataSource);
		}
		return metaLoader;
	}

	/**
	 * 获取分页sql
	 */
	@Override
	public String getPaginationSql(String tableName, String orderColumn,String lastValue, int pageIndex, int pageSize) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getSourceId() {
		return sourceId;
	}

	@Override
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	@Override
	public Boolean getState() {
		return state;
	}

	@Override
	public void setState(Boolean state) {
		this.state = state;
	}

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}

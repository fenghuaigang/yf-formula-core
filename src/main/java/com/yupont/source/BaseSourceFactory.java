package com.yupont.source;

import com.alibaba.druid.pool.DruidDataSource;
import com.yupont.source.meta.KingBaseDatabaseMeta;
import com.yupont.source.meta.MysqlDatabaseMeta;
import com.yupont.source.meta.OracleDatabaseMeta;
import com.yupont.source.meta.SqlServer2005DatabaseMeta;
import com.yupont.util.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * 引擎数据源工厂管理各类数据源连接池
 * 
 * @author feng
 *
 */
public class BaseSourceFactory {

	private static final Logger logger = LoggerFactory.getLogger(BaseSourceFactory.class);

	protected BaseSourceFactory() {
	}

	protected static final class SourceFactory {
		private static final BaseSourceFactory INSTANCE = new BaseSourceFactory();
	}

	public static BaseSourceFactory getInstance() {
		return SourceFactory.INSTANCE;
	}

	/** 系统中数据源配置信息 */
	protected static ConcurrentMap<String, ConfigDataSource> sourceCfgs = new ConcurrentHashMap<>();
	/** 系统中数据表配置信息 */
	protected static ConcurrentMap<String, ConfigDataTable> tableCfgs = new ConcurrentHashMap<>();
	/** 系统中数据字段配置信息 */
	protected static ConcurrentMap<String, ConfigDataField> fieldCfgs = new ConcurrentHashMap<>();
	/** 数据源连接池 */
	protected static ConcurrentMap<String, javax.sql.DataSource> sources = new ConcurrentHashMap<>();
	/** 数据库操作对象 */
	protected static ConcurrentMap<String, SourceMeta> metas = new ConcurrentHashMap<>();

	public void initAll(List<ConfigDataSource> configDataSources) {
		configDataSources.forEach(s -> init(s));
	}

	public void init(ConfigDataSource configDataSource) {
		if (configDataSource != null) {
			try {
				String sourceId = configDataSource.getSourceId();
				sourceCfgs.put(sourceId, configDataSource);
				SourceMeta db = getDatabaseMeta(configDataSource.getJdbcType());
				copy(configDataSource, db);
				metas.put(sourceId, db);
				if (DatabaseMeta.DataSourceType.DATA_SOURCE.equals(configDataSource.getSourceType())) {
					DruidDataSource dataSource = new DruidDataSource();
					// dataSource 配置
					configSource(configDataSource, db, dataSource);
					sources.put(sourceId, dataSource);
					db.setDataSource(dataSource);
				}
				List<ConfigDataTable> tables = configDataSource.getConfigDataTables();
				if (tables != null) {
					for (ConfigDataTable table : tables) {
						tableCfgs.put(table.getTableId(), table);
						List<ConfigDataField> dataFields = table.getConfigDataFields();
						if (dataFields != null) {
							for (ConfigDataField field : dataFields) {
								fieldCfgs.put(field.getId(), field);
							}
						}
					}
				}
			} catch (Exception e) {
				logger.info(e.getMessage(),e);
			}
		}
	}
	public void initDelDataSource() {
		sourceCfgs.clear();
		metas.clear();
		sources.clear();
	}
	
	public void initDataTable(ConfigDataTable configDataTable) {
		if (configDataTable != null) {
			ConfigDataTable table=tableCfgs.get(configDataTable.getTableId());
			if(table!=null){
				tableCfgs.remove(configDataTable.getTableId());
				tableCfgs.put(configDataTable.getTableId(), configDataTable);
			}else{
				tableCfgs.put(configDataTable.getTableId(), configDataTable);
			}
			
		}
	}
	public void initDelDataTable(ConfigDataTable configDataTable) {
		if (configDataTable != null) {
			ConfigDataTable table=tableCfgs.get(configDataTable.getTableId());
			if(table!=null){
				tableCfgs.remove(configDataTable.getTableId());
			}
			
		}
		
	}
	public void initDelDataField(ConfigDataField configDataField) {
		if (configDataField != null) {
			ConfigDataField field=fieldCfgs.get(configDataField.getId());
			if(field!=null){
				fieldCfgs.remove(configDataField.getId());
			}
			
		}
	}
	public void initDataField(ConfigDataField configDataField) {
		if (configDataField != null) {
			ConfigDataField field=fieldCfgs.get(configDataField.getId());
			if(field!=null){
				fieldCfgs.remove(configDataField.getId());
				fieldCfgs.put(configDataField.getId(), configDataField);
			}else{
				fieldCfgs.put(configDataField.getId(), configDataField);
			}
			
		}
	}
	private static void configSource(ConfigDataSource configDataSource, SourceMeta db, DruidDataSource dataSource) {
		// 配置这个属性的意义在于，如果存在多个数据源，监控的时候可以通过名字来区分开来。
		dataSource.setName(configDataSource.getSourceName());
		dataSource.setDriverClassName(db.getDriverClass());
		dataSource.setUrl(configDataSource.getSourceUrl());
		dataSource.setUsername(configDataSource.getSourceUsername());
		dataSource.setPassword(configDataSource.getSourcePassword());
		// 设置连接数相关:初始、最小、最大
		dataSource.setInitialSize(10);
		dataSource.setMinIdle(10);
		dataSource.setMaxActive(30);
		// 配置获取连接等待超时的时间
		dataSource.setMaxWait(60 * 1000);
		// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
		dataSource.setTimeBetweenEvictionRunsMillis(60 * 1000);
		// 配置一个连接在池中最小生存的时间，单位是毫秒
		dataSource.setMinEvictableIdleTimeMillis(30 * 60 * 1000);
		dataSource.setValidationQuery("SELECT 1 FROM DUAL");
		dataSource.setTestWhileIdle(true);
		dataSource.setTestOnBorrow(false);
		dataSource.setTestOnReturn(false);
		// 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
		try {
			dataSource.setFilters("stat");
		} catch (SQLException e) {
			logger.error("druid configuration initialization filter", e);
		}
		// 设置连接租期 60分钟，必须60分钟之内close()
		dataSource.setRemoveAbandoned(true);
		// 单位为秒
		dataSource.setRemoveAbandonedTimeout(60 * 60);
		// 开启psCache
		dataSource.setPoolPreparedStatements(true);
		/**
		 * 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。
		 * 单个connnection独享一个statement
		 * cache，也就是说maxOpenPreparedStatements是针对单个connection链接的
		 */
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(10);
		// 排查错误时启用：关闭abanded连接时输出错误日志
		// dataSource.setLogAbandoned(true);
		// String publicKey = "";
		// 解密
		// dataSource.setConnectionProperties("config.decrypt=true;config.decrypt.key="
		// + publicKey);
	}

	public Connection getConnection(String sourceId) {
		return getDatabase(sourceId).getConnection();
	}


	public SourceMeta getDatabase(String sourceId) {
		return metas.get(sourceId);
	}

	private void copy(ConfigDataSource ds, SourceMeta db) {
		db.setUrl(ds.getSourceUrl());
		db.setUsername(ds.getSourceUsername());
		db.setPassword(ds.getSourcePassword());
		db.setName(ds.getSourceName());
		db.setSourceType(ds.getSourceType());
		db.setJdbcType(ds.getJdbcType());
		db.setSourceId(ds.getSourceId());
	}

	public SourceMeta getDatabaseMeta(String type) {
		if (!TextUtil.isEmpty(type)) {
			if (DatabaseMeta.JDBC.MYSQL.toString().equals(type.toUpperCase())) {
				return new MysqlDatabaseMeta();
			} else if (DatabaseMeta.JDBC.KINGBASE.toString().equals(type.toUpperCase())) {
				return new KingBaseDatabaseMeta();
			} else if (DatabaseMeta.JDBC.ORACLE.toString().equals(type.toUpperCase())) {
				return new OracleDatabaseMeta();
			} else if (DatabaseMeta.JDBC.SQLSERVER.toString().equals(type.toUpperCase())) {
				return new SqlServer2005DatabaseMeta();
			}
		}
		return new DatabaseMeta();
	}

	public ConcurrentMap<String, ConfigDataSource> getSourceCfgs(){
		return sourceCfgs;
	}
	public ConcurrentMap<String, ConfigDataTable> getTableCfgs(){
		return tableCfgs;
	}
	public ConcurrentMap<String, ConfigDataField> getFieldCfgs(){
		return fieldCfgs;
	}

}

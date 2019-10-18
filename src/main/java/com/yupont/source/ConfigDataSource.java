package com.yupont.source;

import java.util.Date;
import java.util.List;

public class ConfigDataSource {

	/** 主键 */
	private String sourceId;

	/** 数据源名 */
	private String sourceName;

	/** 数据源模式 */
	private String sourceSchema;

	/** 业务描述 */
	private String description;

	/** 数据源类型：枚举：file:本地文件，ftp:文件，jdbc:数据库；service服务 */
	private String sourceType;

	/** JDBC类型：KINGBASE、ORACLE、MYSQL、SQLSERVER */
	private String jdbcType;

	/** 数据源地址 */
	private String sourceUrl;

	/** 用户名 */
	private String sourceUsername;

	/** 密码 */
	private String sourcePassword;

	/** 有效性 */
	private String valid;

	/** 创建人ID */
	private String createUserId;

	/** 创建时间 */
	private String createTime;

	/** 修改人ID */
	private String updateUserId;

	/** 修改时间 */
	private String updateTime;

	/** 版本号 */
	private Integer version;

	/** 数据源编码 */
	private String sourceCode;
	
	/** 数据表*/
	private List<ConfigDataTable> configDataTables;
	
	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSourceSchema() {
		return sourceSchema;
	}

	public void setSourceSchema(String sourceSchema) {
		this.sourceSchema = sourceSchema;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public String getSourceUsername() {
		return sourceUsername;
	}

	public void setSourceUsername(String sourceUsername) {
		this.sourceUsername = sourceUsername;
	}

	public String getSourcePassword() {
		return sourcePassword;
	}

	public void setSourcePassword(String sourcePassword) {
		this.sourcePassword = sourcePassword;
	}

	public String getValid() {
		return valid;
	}

	public void setValid(String valid) {
		this.valid = valid;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public String getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public List<ConfigDataTable> getConfigDataTables() {
		return configDataTables;
	}

	public void setConfigDataTables(List<ConfigDataTable> configDataTables) {
		this.configDataTables = configDataTables;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
}

package com.yupont.source;

import java.util.Date;
import java.util.List;

public class ConfigDataTable {
	
	/** 主键 */
    private String tableId;
     
    /** 数据集名称（中文） */
    private String tableDisplayName;
    
    /** 数据集表名称 */
    private String tableName;
    
    /** 数据集类型：枚举结算依据种类 */
    private Integer tableType;
    
    /** 数据源ID */
    private String sourceId;
    
    /** 数据集描述 */
    private String description;
    
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
    
    /** 数据集数据集成频率 */
    private  String dataCycleTime;

    /** 数据项*/
    private List<ConfigDataField> configDataFields;
    
	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getTableDisplayName() {
		return tableDisplayName;
	}

	public void setTableDisplayName(String tableDisplayName) {
		this.tableDisplayName = tableDisplayName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Integer getTableType() {
		return tableType;
	}

	public void setTableType(Integer tableType) {
		this.tableType = tableType;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public List<ConfigDataField> getConfigDataFields() {
		return configDataFields;
	}

	public void setConfigDataFields(List<ConfigDataField> configDataFields) {
		this.configDataFields = configDataFields;
	}

	public String getDataCycleTime() {
		return dataCycleTime;
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

	public void setDataCycleTime(String dataCycleTime) {
		this.dataCycleTime = dataCycleTime;
	}

}

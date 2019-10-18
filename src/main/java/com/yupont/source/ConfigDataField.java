package com.yupont.source;

import java.sql.Timestamp;

public class ConfigDataField {
	  /** 
     * 属性分量ID
     */  
    private String id;    
    /** 
     * 属性名称
     */  
    private String name;    
    /** 
     * 属性数据源ID
     */  
    private String sourceId;    
    /** 
     * 属性数据集ID
     */  
    private String tableId;    
    /** 
     * 属性分量字段名
     */  
    private String fieldName;    
    /** 
     * 属性数据项类型 字符数值布尔时间等
     */  
    private String fieldType;    
    /** 
     * 属性计量单位ID
     */  
    private String unitId;    
    /** 
     * 属性默认值
     */  
    private String defaultValue;    
    /** 
     * 属性同级递增排序
     */  
    private Short sortNum;    
    /** 
     * 属性有效性
     */  
    private String valid;    
    /** 
     * 属性创建人
     */  
    private String createUserId;    
    /** 
     * 属性创建时间
     */  
    private String createTime;
    /** 
     * 属性修改人
     */  
    private String updateUserId;    
    /** 
     * 属性修改时间
     */  
    private String updateTime;

	/**
	 * 数据项编码
	 */
	private String dataItemCode;

    /** 
     * 属性版本
     */  
    private Short version;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getTableId() {
		return tableId;
	}
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getUnitId() {
		return unitId;
	}
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public Short getSortNum() {
		return sortNum;
	}
	public void setSortNum(Short sortNum) {
		this.sortNum = sortNum;
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
	public Short getVersion() {
		return version;
	}
	public void setVersion(Short version) {
		this.version = version;
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

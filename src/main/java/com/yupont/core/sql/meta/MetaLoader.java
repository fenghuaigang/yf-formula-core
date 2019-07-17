package com.yupont.core.sql.meta;

import java.util.Map;
import java.util.Set;

import com.yupont.core.sql.ecexption.AbstractDataAccessException;
import com.yupont.core.sql.model.Database;
import com.yupont.core.sql.model.Function;
import com.yupont.core.sql.model.Procedure;
import com.yupont.core.sql.model.Schema;
import com.yupont.core.sql.model.SchemaInfo;
import com.yupont.core.sql.model.Table;
import com.yupont.core.sql.model.Trigger;

/**
 * 元数据加载器
 * @author fjw
 *
 */
public interface MetaLoader {
	/**
	 * get current datasource own Schema's table names
	 * 
	 * @return Set<String>
	 */
	Set<String> getTableNames() throws AbstractDataAccessException;
	
	/**
	 * get current datasource own schema's table.Default Level Table contaion
	 *  columns、primaryKey、ForeignKey、index
	 *  
	 * 
	 * @param tableName
	 * @return Table
	 */
	Table getTable(String tableName) throws AbstractDataAccessException;
	
	
	/**
	 * get current datasource own schema's table. 
	 * 
	 * @param tableName
	 * @param schemaLevel 
	 * @return
	 */
	Table getTable(String tableName,SchemaInfoLevel schemaLevel) throws AbstractDataAccessException;
	
	
	Table getTable(String tableName,SchemaInfo schemaInfo) throws AbstractDataAccessException;
		
	/**
	 * Gets the database's schema information
	 * 
	 * @return SchemaInfo
	 */
	Set<SchemaInfo> getSchemaInfos() throws AbstractDataAccessException;
	
	/**
	 * get current datasource own Schema
	 * 
	 * @return Schema
	 */
	Schema getSchema() throws AbstractDataAccessException;
	
	Schema getSchema(SchemaInfo schemaInfo) throws AbstractDataAccessException;
	
	/**
	 *  get current datasource own Schema
	 * 
	 * @param level
	 * @return Schema
	 */
	Schema getSchema(SchemaInfoLevel level) throws AbstractDataAccessException;
	
	Schema getSchema(SchemaInfo schemaInfo,SchemaInfoLevel level) throws AbstractDataAccessException;
	
	
	/**
	 * get currrent schema's procedure names.
	 * 
	 * @return Set<String>
	 */
	Set<String> getProcedureNames() throws AbstractDataAccessException;
	
	/**
	 * get procedure (current user can access)
	 * 
	 * @param procedureName the procedure's name(not be null)
	 * @return 
	 */	
	Procedure getProcedure(String procedureName) throws AbstractDataAccessException;
		
	/**
	 * get procedures (current user can access)
	 * 
	 * @return Map<String,Procedure>
	 */
	Map<String,Procedure> getProcedures() throws AbstractDataAccessException;
	
	/**
	 * get currrent schema's access trigger names.
	 * 
	 * @return Set<String>
	 */
	Set<String> getTriggerNames() throws AbstractDataAccessException;
	
	/**
	 * get trigger (current user can access)
	 * 
	 * @param triggerName the trigger's name(not be null)
	 * @return 
	 */	
	Trigger getTrigger(String triggerName) throws AbstractDataAccessException;
	
	/**
	 * get trigger (current user can access)
	 * 
	 * @return Map<String,Trigger>
	 */
	Map<String, Trigger> getTriggers() throws AbstractDataAccessException;
	
	/**
	 * get currrent schema's access function names.
	 * 
	 * @return Set<String>
	 */
	Set<String> getFunctionNames() throws AbstractDataAccessException;
	
	/**
	 * get function (current user can access)
	 * 
	 * @param functionName the trigger's name(not be null)
	 * @return 
	 */	
	Function getFunction(String name) throws AbstractDataAccessException;
	
	
	/**
	 * get Functions (current user can access)
	 * 
	 * @return Map<String,Function>
	 */
	Map<String, Function> getFunctions() throws AbstractDataAccessException;
	
	
	/**
	 * get this database's all the Schema.
	 * 
	 * <p><b>In oracle it is a dangerous function.There are too many system tables</b></p>
	 * 
	 * @return Database
	 */
	@Deprecated
	Database getDatabase() throws AbstractDataAccessException;
	
	/**
	 * get this database's all the Schema
	 * 
	 * <b>In oracle it is a dangerous function.There are too many system tables</b>
	 * 
	 * @param level
	 * @return Database
	 */
	@Deprecated
	Database getDatabase(SchemaInfoLevel level) throws AbstractDataAccessException;
}

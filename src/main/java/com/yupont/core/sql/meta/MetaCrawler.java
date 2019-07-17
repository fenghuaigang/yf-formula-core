package com.yupont.core.sql.meta;

import java.util.Map;
import java.util.Set;

import com.yupont.core.sql.model.Database;
import com.yupont.core.sql.model.DatabaseInfo;
import com.yupont.core.sql.model.Function;
import com.yupont.core.sql.model.Procedure;
import com.yupont.core.sql.model.Schema;
import com.yupont.core.sql.model.SchemaInfo;
import com.yupont.core.sql.model.Table;
import com.yupont.core.sql.model.Trigger;
/**
 * @author Administrator
 */
public interface MetaCrawler {
	Set<String> getTableNames();
	
//	Table getTable(String tableName);
	
	Table getTable(String tableName,SchemaInfoLevel schemaInfoLevel);
	
	Table getTable(String tableName, SchemaInfoLevel level, SchemaInfo schemaInfo);
	
//	Map<String, Column> crawlColumnInfo(String tableName);
//	
//	PrimaryKey crawlPrimaryKey(String tableName);
//	
//	Map<String,ForeignKey> crawlForeignKey(String tableName);
	
	Set<SchemaInfo> getSchemaInfos();
	
	Schema getSchema(SchemaInfoLevel level);
	
	Schema getSchema(SchemaInfo schemaInfo,SchemaInfoLevel level);
	
	DatabaseInfo getDatabaseInfo();
	
	Database getDatabase(SchemaInfoLevel level);
	
	Set<String> getProcedureNames(SchemaInfo schemaInfo);
	
	Procedure getProcedure(String procedureName);
	
	Map<String,Procedure> getProcedures();
	
	Set<String> getTriggerNames();
	
	Trigger getTrigger(String triggerName);
	
	Map<String, Trigger> getTriggers();
	
	Set<String> getFunctionNames();
	
	Function getFunction(String name);
	
	Map<String, Function> getFunctions();
}

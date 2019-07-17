package com.yupont.core.sql.meta;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yupont.core.sql.ecexption.AbstractDataAccessException;
import com.yupont.core.sql.ecexption.DatabaseMetaGetMetaException;
import com.yupont.core.sql.model.Database;
import com.yupont.core.sql.model.Function;
import com.yupont.core.sql.model.Procedure;
import com.yupont.core.sql.model.Schema;
import com.yupont.core.sql.model.SchemaInfo;
import com.yupont.core.sql.model.Table;
import com.yupont.core.sql.model.Trigger;
import com.yupont.core.sql.util.JdbcUtils;

/**
 * 实现异构关系型数据库的元数据加载
 * <b>利用MetaCrawlerFactory工厂，根据数据源生成对应的元数据爬虫MetaCrawler
 * 
 * 
 * @author 
 * 
 */
public class MetaLoaderImpl implements MetaLoader {
	private static Logger logger = LoggerFactory.getLogger(MetaLoaderImpl.class);

//	public static final int MYSQL = 1;
//	public static final int SQL_SERVER = 2;
//	public static final int ORACLE = 3;

	private DataSource dataSource;
	
	private MetaCrawlerFactory factory=new DefaultMetaCrawlerFactory();

	public void setFactory(MetaCrawlerFactory factory) {
		this.factory = factory;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public MetaLoaderImpl() {

	}

	public MetaLoaderImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Set<String> getTableNames() {
		Connection con = JdbcUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getTableNames();
		}catch(AbstractDataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JdbcUtils.closeConnection(con);
		}
	}

	@Override
	public Table getTable(String tableName) {
		return getTable(tableName, SchemaInfoLevel.standard());
	}
	
	@Override
	public Table getTable(String tableName,SchemaInfoLevel level) {
		return getTable(tableName, level, null);
	}
	
	@Override
	public Table getTable(String tableName, SchemaInfo schemaInfo) {
		return getTable(tableName, SchemaInfoLevel.standard(), schemaInfo);
	}
	
	public Table getTable(String tableName, SchemaInfoLevel schemaLevel,SchemaInfo schemaInfo) {
		Connection con = JdbcUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getTable(tableName, schemaLevel,schemaInfo);
		}catch(AbstractDataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JdbcUtils.closeConnection(con);
		}
	}

	@Override
	public Set<SchemaInfo> getSchemaInfos() {
		Connection con = JdbcUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getSchemaInfos();
		}catch(AbstractDataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JdbcUtils.closeConnection(con);
		}
	}

	@Override
	public Schema getSchema(SchemaInfoLevel level) {
		Connection con = JdbcUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getSchema(level);
		}catch(AbstractDataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JdbcUtils.closeConnection(con);
		}
	}
	
	@Override
	public Schema getSchema(SchemaInfo schemaInfo, SchemaInfoLevel level) throws AbstractDataAccessException {
		Connection con = JdbcUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getSchema(schemaInfo,level);
		}catch(AbstractDataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JdbcUtils.closeConnection(con);
		}
	}
	
	@Override
	public Schema getSchema(SchemaInfo schemaInfo) {
		return getSchema(schemaInfo,SchemaInfoLevel.standard());
	}

	@Override
	public Database getDatabase(SchemaInfoLevel level) {
		Connection con = JdbcUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		Database database;
		try{
			metaCrawler=factory.newInstance(con);
			database=metaCrawler.getDatabase(level);
			return database;
		}catch(AbstractDataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JdbcUtils.closeConnection(con);
		}
	}

	@Override
	public Schema getSchema() {
		return getSchema(SchemaInfoLevel.standard());
	}

	@Override
	public Database getDatabase() {
		return getDatabase(SchemaInfoLevel.standard());
	}

	private Set<String> getProcedureNames(SchemaInfo schemaInfo) {
		Connection con = JdbcUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		Set<String> procedureNames;
		try{
			metaCrawler=factory.newInstance(con);
			procedureNames=metaCrawler.getProcedureNames(schemaInfo);
			return procedureNames;
		}catch(AbstractDataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JdbcUtils.closeConnection(con);
		}
	}
	
	@Override
	public Set<String> getProcedureNames(){
		return getProcedureNames(null);
	}
	
	@Override
	public Procedure getProcedure(String procedureName) {
		Connection con = JdbcUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		Procedure p;
		try{
			metaCrawler=factory.newInstance(con);
			p=metaCrawler.getProcedure(procedureName);
			return p;
		}catch(AbstractDataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JdbcUtils.closeConnection(con);
		}
	}

	@Override
	public Map<String,Procedure> getProcedures() {
		Connection con = JdbcUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		Map<String, Procedure> p;
		try{
			metaCrawler=factory.newInstance(con);
			p=metaCrawler.getProcedures();
			return p;
		}catch(AbstractDataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JdbcUtils.closeConnection(con);
		}
	}

	@Override
	public Set<String> getTriggerNames() throws AbstractDataAccessException {
		Connection con = JdbcUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getTriggerNames();
		}catch(AbstractDataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get triggerNames error!", e);
		}finally{
			JdbcUtils.closeConnection(con);
		}
	}

	@Override
	public Trigger getTrigger(String triggerName) throws AbstractDataAccessException {
		Connection con = JdbcUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getTrigger(triggerName);
		}catch(AbstractDataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get trigger error!", e);
		}finally{
			JdbcUtils.closeConnection(con);
		}
	}

	@Override
	public Map<String, Trigger> getTriggers() throws AbstractDataAccessException {
		Connection con = JdbcUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getTriggers();
		}catch(AbstractDataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get triggers error!", e);
		}finally{
			JdbcUtils.closeConnection(con);
		}
	}

	@Override
	public Set<String> getFunctionNames() throws AbstractDataAccessException {
		Connection con = JdbcUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getFunctionNames();
		}catch(AbstractDataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get triggers error!", e);
		}finally{
			JdbcUtils.closeConnection(con);
		}
	}

	@Override
	public Function getFunction(String name) throws AbstractDataAccessException {
		Connection con = JdbcUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getFunction(name);
		}catch(AbstractDataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get triggers error!", e);
		}finally{
			JdbcUtils.closeConnection(con);
		}
	}

	@Override
	public Map<String, Function> getFunctions() throws AbstractDataAccessException {
		Connection con = JdbcUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getFunctions();
		}catch(AbstractDataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get triggers error!", e);
		}finally{
			JdbcUtils.closeConnection(con);
		}
	}
}

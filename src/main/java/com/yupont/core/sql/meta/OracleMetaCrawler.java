package com.yupont.core.sql.meta;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yupont.core.sql.ecexption.DatabaseMetaGetMetaException;
import com.yupont.core.sql.ecexption.NonTransientDataAccessException;
import com.yupont.core.sql.model.Constraint;
import com.yupont.core.sql.model.Function;
import com.yupont.core.sql.model.Procedure;
import com.yupont.core.sql.model.SchemaInfo;
import com.yupont.core.sql.model.Table;
import com.yupont.core.sql.model.TableConstraintType;
import com.yupont.core.sql.model.Trigger;
import com.yupont.core.sql.util.Assert;
import com.yupont.core.sql.util.JdbcUtils;
import com.yupont.core.sql.util.ResultSetExtractor;
/**
 * @author Administrator
 */
public class OracleMetaCrawler extends AbstractMetaCrawler {
	private Logger logger = LoggerFactory.getLogger(OracleMetaCrawler.class);

	public final static String GET_CONSTRAINT_SQL = "select constraint_name name,constraint_type type,search_condition definition,deferrable from All_Constraints where owner=? "
			+ "and TABLE_NAME=? and (Constraint_Type='C' or Constraint_Type='U') ";

	public final static String GET_PROCEDURE_SQL = "select name,text from user_source where type='PROCEDURE' and name=? order by Line";

	public final static String GET_PROCEDURES_SQL = "select name,text from user_source where type='PROCEDURE' order by name,Line";
	
	public final static String GET_TRIGGERNAME_SQL="select Distinct name from user_source where type='TRIGGER'";
	
	public final static String GET_TRIGGER_SQL = "select name,text from user_source where type='TRIGGER' and name=? order by Line";
	
	public final static String GET_TRIGGERS_SQL = "select name,text from user_source where type='TRIGGER' order by name,Line";
	
	public final static String GET_TRIGGERS_BYTABLE_SQL="select trigger_name,Description,Trigger_Body,Table_Name"
			+ " from All_Triggers where Owner=? and table_name= ?";
	
	public final static String GET_FUNCTIONNAME_SQL="select Distinct name from user_source where type='FUNCTION'";
	
	public final static String GET_FUNCTION_SQL="select name,text from user_source where type='FUNCTION' and name=? order by Line";
	
	public final static String GET_FUNCTIONS_SQL = "select name,text from user_source where type='FUNCTION' order by name,Line";

	public OracleMetaCrawler() {

	}

	public OracleMetaCrawler(DatabaseMetaData databaseMetaData) {
		super(databaseMetaData);
	}

	/*
	 * In oracle, every user have it's deafult schema(schema name==userName).
	 * Here we return the tableNames,which this owner has;
	 * 
	 * @see com.cgs.db.meta.retriever.AbstractSqlMetaLoader#getTableNames()
	 */
	@Override
	public Set<String> getTableNames() {
		Set<String> tables = new HashSet<String>();
		try {

			String userName = dbm.getUserName();
			ResultSet rs = dbm.getTables(null, userName, null, new String[] { "TABLE" });

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				if (!isRubbishTable(tableName)) {
					tables.add(tableName);
				}
			}

		} catch (SQLException e) {
			logger.debug(e.getMessage());
			throw new NonTransientDataAccessException(e.getMessage(), e);
		}
		return tables;
	}

	@Override
	public Set<String> getTableNames(SchemaInfo schemaInfo) {
		Set<String> tables = new HashSet<String>();
		ResultSet rs;
		try {
			rs = dbm.getTables(schemaInfo.getCatalogName(), schemaInfo.getSchemaName(), null, new String[] { "TABLE" });

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				if (!isRubbishTable(tableName)) {
					tables.add(tableName);
				}
			}
		} catch (SQLException e) {
			throw new NonTransientDataAccessException(e.getMessage(), e);
		}

		return tables;
	}

	@Override
	public Table invokeCrawlTableInfo(String tableName, SchemaInfoLevel level) {
		logger.trace("Get schema name by username");
		String schemaName = null;
		try {
			schemaName = dbm.getUserName();
		} catch (SQLException e) {
			logger.debug("can not get schema name, so see schema as null");
		}

		Table table = crawlTableInfo(null, schemaName, tableName, level);
		return table;
	}
	
	@Override
	public Set<SchemaInfo> getSchemaInfos() {
		Set<SchemaInfo> schemaInfos = new HashSet<SchemaInfo>();
		try {
			ResultSet rs = dbm.getSchemas();
			while (rs.next()) {
				String schemaName = rs.getString("TABLE_SCHEM");
				SchemaInfo schemaInfo = new SchemaInfo(null, schemaName);
				schemaInfos.add(schemaInfo);
			}
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Get database(Oracle) schema information error!", e);
		}
		return schemaInfos;
	}

	@Override
	protected SchemaInfo getSchemaInfo() {
		String schema = null;
		try {
			schema = dbm.getUserName();
		} catch (SQLException e) {
			throw new NonTransientDataAccessException(e.getMessage(), e);
		}

		return new SchemaInfo(null, schema);
	}

	private boolean isRubbishTable(String tableName) {
		if (tableName == null || tableName.length() > 30) {
			return true;
		}
		String rex = "[a-zA-Z_0-9$#]+";
		return !tableName.matches(rex);
	}

	@Override
	protected Map<String, Constraint> crawlConstraint(String tableName, SchemaInfo schemaInfo) {
		String schema = null;
		if (schemaInfo == null) {
			try {
				schema = dbm.getUserName();
			} catch (SQLException e) {
				throw new DatabaseMetaGetMetaException("Get database(Oracle) user name error!", e);
			}
		} else {
			schema = schemaInfo.getSchemaName();
		}
		String message = "Get database(Oracle) " + tableName + "'s constraint information error!";

		return JdbcUtils.query(dbm, GET_CONSTRAINT_SQL, message, new ResultSetExtractor<Map<String, Constraint>>() {
			@Override
			public Map<String, Constraint> extractData(ResultSet rs) throws SQLException {
				Map<String, Constraint> constraints = new HashMap<String, Constraint>();
				while (rs.next()) {
					String name = rs.getString("name");
					String type = rs.getString("type");
					String definition = rs.getString("definition");
					String deferrable = rs.getString("deferrable");
					Constraint c = new Constraint();
					c.setName(name);
					c.setTableConstraintType(parseConstraintType(type));
					c.setDefinition(definition);
					if (deferrable != null && deferrable.equals("DEFERRABLE")) {
						c.setDeferrable(true);
					} else {
						c.setDeferrable(false);
					}
					constraints.put(name, c);
				}
				return constraints;
			}
		}, schema, tableName);
	}

	private TableConstraintType parseConstraintType(String type) {
		if (type == null) {
			return TableConstraintType.unknown;
		} else if ("C".equals(type)) {
			return TableConstraintType.check;
		} else if ("U".equals(type)) {
			return TableConstraintType.unique;
		} else {
			return TableConstraintType.unknown;
		}
	}

	@Override
	public Set<String> getProcedureNames(SchemaInfo schemaInfo) {
		Set<String> procedures = new HashSet<String>();
		ResultSet rs = null;
		try {
			if (schemaInfo == null) {
				String userName = dbm.getUserName();
				rs = dbm.getProcedures(null, userName, null);
			} else {
				rs = dbm.getProcedures(schemaInfo.getCatalogName(), schemaInfo.getSchemaName(), null);
			}
			while (rs.next()) {
				String tableName = rs.getString("PROCEDURE_NAME");
				procedures.add(tableName);
			}
		} catch (SQLException e) {
			throw new NonTransientDataAccessException(e.getMessage(), e);
		} finally {
			JdbcUtils.closeResultSet(rs);
		}

		return procedures;
	}

	@Override
	public Procedure getProcedure(String procedureName) {
		Assert.notNull(procedureName, "procedure name can not be null");
		String message = "Get database(Oracle) " + procedureName + "'s definition information error!";
		return JdbcUtils.query(dbm, GET_PROCEDURE_SQL, message, new ResultSetExtractor<Procedure>() {
			@Override
			public Procedure extractData(ResultSet rs) throws SQLException {
				Procedure p = null;
				while (rs.next()) {
					if (p == null) {
						p = new Procedure();
						p.setName(rs.getString("name"));
					}
					p.appendStr(rs.getString("text"));
				}
				return p;
			}

		}, procedureName);
	}

	@Override
	public Map<String, Procedure> getProcedures() {
		String message = "Get database(Oracle)  definition information error!";
		return JdbcUtils.query(dbm, GET_PROCEDURES_SQL, message, new ResultSetExtractor<Map<String, Procedure>>() {
			@Override
			public Map<String, Procedure> extractData(ResultSet rs) throws SQLException {
				Map<String, Procedure> procedures = new HashMap<String, Procedure>();
				Procedure p;
				while (rs.next()) {
					String name = rs.getString("name");
					p = procedures.get(name);
					if (p == null) {
						p = new Procedure();
						p.setName(rs.getString("name"));
						procedures.put(name, p);
					}
					p.appendStr(rs.getString("text"));
				}
				return procedures;
			}
		});
	}
	
	@Override
	public Set<String> getTriggerNames(){
		String message="Get database(Oracle) current user's trigger names";
		Set<String> names=JdbcUtils.query(dbm, GET_TRIGGERNAME_SQL, message, new ResultSetExtractor<Set<String>>() {
			@Override
			public Set<String> extractData(ResultSet rs) throws SQLException {
				Set<String> names=new HashSet<String>();
				while(rs.next()){
					String name=rs.getString("name");
					names.add(name);
				}
				return names;
			}
		});
		return names;
	}
	
	@Override
	public Trigger getTrigger(String triggerName) {
		Assert.notNull(triggerName, "triggerName can not be null");
		String message = "Get database(Oracle) " + triggerName + "'s definition information error!";
		return JdbcUtils.query(dbm, GET_TRIGGER_SQL, message, new ResultSetExtractor<Trigger>() {
			@Override
			public Trigger extractData(ResultSet rs) throws SQLException {
				Trigger p = null;
				while (rs.next()) {
					if (p == null) {
						p = new Trigger();
						p.setName(rs.getString("name"));
					}
					p.appendStr(rs.getString("text"));
				}
				return p;
			}

		}, triggerName);
	}
	
	@Override
	public Map<String, Trigger> getTriggers() {
		String message = "Get database(Oracle)  definition information error!";
		return JdbcUtils.query(dbm, GET_TRIGGERS_SQL, message, new ResultSetExtractor<Map<String, Trigger>>() {
			@Override
			public Map<String, Trigger> extractData(ResultSet rs) throws SQLException {
				Map<String, Trigger> triggers = new HashMap<String, Trigger>();
				Trigger p;
				while (rs.next()) {
					String name = rs.getString("name");
					p = triggers.get(name);
					if (p == null) {
						p = new Trigger();
						p.setName(rs.getString("name"));
						triggers.put(name, p);
					}
					p.appendStr(rs.getString("text"));
				}
				return triggers;
			}
		});
	}
	
	@Override
	public Set<String> getFunctionNames(){
		String message="Get database(My sql) current user's function names";
		Set<String> names=JdbcUtils.query(dbm, GET_FUNCTIONNAME_SQL, message, new ResultSetExtractor<Set<String>>() {
			@Override
			public Set<String> extractData(ResultSet rs) throws SQLException {
				Set<String> names=new HashSet<String>();
				while(rs.next()){
					String name=rs.getString("name");
					names.add(name);
				}
				return names;
			}
		});
		return names;
	}
	
	@Override
	public Function getFunction(String name){
		Assert.notNull(name, "procedure name can not be null");
		String message = "Get database(Oracle) " + name + "'s definition information error!";
		return JdbcUtils.query(dbm, GET_FUNCTION_SQL, message, new ResultSetExtractor<Function>() {
			@Override
			public Function extractData(ResultSet rs) throws SQLException {
				Function p = null;
				while (rs.next()) {
					if (p == null) {
						p = new Function();
						p.setName(rs.getString("name"));
					}
					p.appendStr(rs.getString("text"));
				}
				return p;
			}

		}, name);
	}
	
	@Override
	public Map<String, Function> getFunctions(){
		String message = "Get database(Oracle)  definition information error!";
		return JdbcUtils.query(dbm, GET_FUNCTIONS_SQL, message, new ResultSetExtractor<Map<String, Function>>() {
			@Override
			public Map<String, Function> extractData(ResultSet rs) throws SQLException {
				Map<String, Function> functions = new HashMap<String, Function>();
				Function f;
				while (rs.next()) {
					String name = rs.getString("name");
					f = functions.get(name);
					if (f == null) {
						f = new Function();
						f.setName(rs.getString("name"));
						functions.put(name, f);
					}
					f.appendStr(rs.getString("text"));
				}
				return functions;
			}
		});
	}
	
	@Override
	protected Map<String, Trigger> crawleTriggers(String tableName, SchemaInfo schemaInfo) {
		String message = "Get database(My sql)  "+tableName+"'s triggers information error!";
		String schema;
		if(schemaInfo==null||schemaInfo.getSchemaName()==null){
			schema=getSchemaName();
		}else{
			schema=schemaInfo.getSchemaName();
		}
		Map<String, Trigger> triggers=JdbcUtils.query(dbm, GET_TRIGGERS_BYTABLE_SQL, message, new ResultSetExtractor<Map<String, Trigger>>() {
			@Override
			public Map<String, Trigger> extractData(ResultSet rs) throws SQLException {
				Map<String, Trigger> triggers = new HashMap<String, Trigger>();
				while(rs.next()){
					String triggerName=rs.getString("trigger_name");
					String description=rs.getString("Description");
					String triggerBody=rs.getString("Trigger_Body");
					String tableName=rs.getString("Table_Name");
					Trigger trigger=new Trigger();
					trigger.appendStr("create or lineReplace Trigger \n");
					trigger.appendStr(description);
					trigger.appendStr(triggerBody);
					trigger.setTableName(tableName);
					
					triggers.put(triggerName, trigger);
				}
				return triggers;
			}
		}, schema,tableName);
		return triggers;
	}
	
	
	private String getSchemaName(){
		try {
			String schema=dbm.getUserName();
			return schema;
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Get database(Oracle) cataglog name error!", e);
		}
	}

}

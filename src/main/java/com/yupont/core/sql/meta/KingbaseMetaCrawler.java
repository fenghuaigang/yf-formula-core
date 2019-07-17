package com.yupont.core.sql.meta;

import java.util.Map;
import java.util.Set;

import com.yupont.core.sql.model.Constraint;
import com.yupont.core.sql.model.Function;
import com.yupont.core.sql.model.Procedure;
import com.yupont.core.sql.model.SchemaInfo;
import com.yupont.core.sql.model.Table;
import com.yupont.core.sql.model.Trigger;

/**
 * 金仓数据库的元数据爬虫
 * @author fjw
 *
 */
public class KingbaseMetaCrawler extends AbstractMetaCrawler {

	@Override
	public Set<SchemaInfo> getSchemaInfos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Procedure getProcedure(String procedureName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Procedure> getProcedures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getTriggerNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Trigger getTrigger(String triggerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Trigger> getTriggers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getFunctionNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Function getFunction(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Function> getFunctions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Table invokeCrawlTableInfo(String tableName, SchemaInfoLevel level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, Constraint> crawlConstraint(String tableName, SchemaInfo schemaInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, Trigger> crawleTriggers(String tableName, SchemaInfo schemaInfo) {
		// TODO Auto-generated method stub
		return null;
	}

}

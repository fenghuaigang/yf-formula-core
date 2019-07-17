package com.yupont.source.meta;

import org.apache.commons.lang.StringUtils;

import com.yupont.source.DatabaseMeta;
/**
 * MySql
 * @author feng
 *
 */
public class MysqlDatabaseMeta extends DatabaseMeta {

	@Override
	public String getDriverClass() {
		return "com.mysql.cj.jdbc.Driver";
	}

	/**
	 * 获取分页SQL pageIndex从0开始
	 */
	@Override
	public String getPaginationSql(String tableName, String orderColumn,String lastValue, int pageIndex, int pageSize) {
		StringBuilder sql = new StringBuilder();
		int start = pageIndex*pageSize;
		sql.append(" SELECT t.* FROM "+tableName+" t ");
		if(!StringUtils.isEmpty(lastValue)){
			sql.append(" WHERE t."+orderColumn+" > '"+lastValue+"'");
		}
		sql.append(" order by t."+orderColumn+" ASC limit "+start+","+pageSize);
		return sql.toString();
	}
	
}

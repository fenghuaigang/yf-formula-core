package com.yupont.source.meta;

import org.apache.commons.lang.StringUtils;

import com.yupont.source.DatabaseMeta;

/**
 * SQL SERVER2005数据库
 * @author feng
 *
 */
public class SqlServer2005DatabaseMeta extends DatabaseMeta {

	@Override
	public String getDriverClass() {
		return "com.microsoft.jdbc.sqlserver.SQLServerDriver";
	}

	/**
	 * 获取分页SQL pageIndex从0开始
	 */ 
	@Override
	public String getPaginationSql(String tableName, String orderColumn,String lastValue, int pageIndex, int pageSize) {
		int start = pageIndex*pageSize+1;
		int end = (pageIndex+1)*pageSize+1;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ( ")
		   .append("     SELECT *, ROW_NUMBER() OVER(ORDER BY A."+orderColumn+" ASC ) AS ROWNUMBER FROM "+tableName+" AS A ");
		if(!StringUtils.isEmpty(lastValue)){
			sql.append(" WHERE A."+orderColumn+" > '"+lastValue+"'");
		}
		sql.append(") AS B WHERE ROWNUMBER BETWEEN "+start+" AND "+end);
		return sql.toString();
	}
}

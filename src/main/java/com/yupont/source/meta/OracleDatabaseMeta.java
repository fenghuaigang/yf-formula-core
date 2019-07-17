package com.yupont.source.meta;

import org.apache.commons.lang.StringUtils;

import com.yupont.source.DatabaseMeta;
/**
 * Oralce
 * @author feng
 *
 */
public class OracleDatabaseMeta extends DatabaseMeta {

	@Override
	public String getDriverClass() {
		return "oracle.jdbc.driver.OracleDriver";
	}
	
	/**
	 * 获取分页SQL pageIndex从0开始
	 */
	@Override
	public String getPaginationSql(String tableName, String orderColumn,String lastValue, int pageIndex, int pageSize) {
		StringBuilder sql = new StringBuilder();
		int start = pageIndex*pageSize;
		int end = (pageIndex+1)*pageSize;
		sql.append(" SELECT * FROM ( ")
				.append(" SELECT tt.*, ROWNUM AS rowno FROM ( ")
					.append(" SELECT t.* FROM "+tableName+" t ");
		if(!StringUtils.isEmpty(lastValue)){
			     sql.append(" WHERE t."+orderColumn+" > '"+lastValue+"'");
		}
			     sql.append(" order by t."+orderColumn+" ASC ")
		        .append(" ) tt WHERE ROWNUM <= "+end)
		   .append(" ) table_alias WHERE table_alias.rowno > "+start);
		return sql.toString();
	}
	
}

package com.yupont.core.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yupont.util.TextUtil;

public class JsonArray2Handler implements ResultSetHandler<JSONArray> {

	private ResultSetMetaData rsmd;
	protected JSONObject handleRow(ResultSet rs) throws SQLException {
		if(rsmd == null){
			rsmd = rs.getMetaData();
		}
		JSONObject result = new JSONObject();
		int cols = rsmd.getColumnCount();

		for (int i = 1; i <= cols; i++) {
			String col = rsmd.getColumnLabel(i);
			if (TextUtil.isEmpty(col)) {
				col = rsmd.getColumnName(i);
			}
			if(rs.getObject(i) != null && "java.sql.Timestamp".equals(rs.getObject(i).getClass().getName())){
				result.put(col, rs.getString(i));
			}else{
				result.put(col, rs.getObject(i));
			}
		}

		return result;
	}

	@Override
	public JSONArray handle(ResultSet rs) throws SQLException {
		JSONArray rows = new JSONArray();
		while (rs.next()) {
			rows.add(this.handleRow(rs));
		}
		return rows;
	}

}

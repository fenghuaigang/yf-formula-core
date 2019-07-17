/*
 * Copyright 2016 requery.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yupont.core.sql.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.yupont.core.sql.Keywords;

/**
 * 表示 JDBC 标准的SQL字段类型，可用于从 {@link ResultSet}读取值，也可以用于创建SQL
 * {@link PreparedStatement}.
 *
 * @author
 */
public interface FieldType<T>
{

	/**
	 * 从 JDBC {@link ResultSet} 中读取数据类型
	 *
	 * @param results
	 *            查询结果集
	 * @param column
	 *            列索引
	 * @return 读取的值
	 * @throws SQLException
	 *             读取失败
	 */
	T read(ResultSet results, int column) throws SQLException;

	/**
	 * 向 {@link PreparedStatement} SQL中写入参数值
	 *
	 * @param statement
	 *            SQL语句 @param index 参数索引 @param value 字段值 @throws
	 */
	void write(PreparedStatement statement, int index, T value)
			throws SQLException;

	/**
	 * @return 返回 JDBC 类显示的 {@link java.sql.Types}常量
	 */
	int getJdbcTypeCode();

	/**
	 * @return 数据类型是否有长度约束
	 */
	boolean hasLength();

	/**
	 * @return 数据类型默认长度, 如果无长度约束，则返回null
	 */
	Integer getDefaultLength();

	/**
	 * @return 数据类型标识 {@link String} or {@link Keywords}
	 */
	Object getKeyword();

	/**
	 * @return 数据库关键词后的部分，如 VARCHAR(255) 后的： default " "
	 */
	String getKeywordSuffix();

	Class<T> getJavaTypeClass();
}

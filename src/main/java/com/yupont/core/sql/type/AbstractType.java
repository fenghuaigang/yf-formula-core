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
import java.util.Objects;

/**
 * {@link FieldType}接口的基本实现, 提供可覆盖的基本读写操作
 *
 * @param <T>
 *            mapped type
 *
 * @author
 */
public abstract class AbstractType<T> implements FieldType<T>
{

	protected final Class<T>	javaTypeClass;
	protected final int			jdbcTypeCode;

	/**
	 * 初始化实例
	 *
	 * @param type
	 *            java类型
	 * @param sqlTypeCode
	 *            {@link java.sql.Types}JDBC类型
	 */
	protected AbstractType(Class<T> javaTypeClass, int jdbcTypeCode)
	{
		this.javaTypeClass = javaTypeClass;
		this.jdbcTypeCode = jdbcTypeCode;
	}

	@Override
	public T read(ResultSet results, int column) throws SQLException
	{
		T value = javaTypeClass.cast(results.getObject(column));
		if (results.wasNull())
		{
			return null;
		}
		return value;
	}

	@Override
	public void write(PreparedStatement statement, int index, T value)
			throws SQLException
	{
		if (value == null)
		{
			statement.setNull(index, jdbcTypeCode);
		} else
		{
			statement.setObject(index, value, jdbcTypeCode);
		}
	}

	@Override
	public int getJdbcTypeCode()
	{
		return jdbcTypeCode;
	}

	@Override
	public boolean hasLength()
	{
		return false;
	}

	@Override
	public Integer getDefaultLength()
	{
		return null;
	}

	@Override
	public abstract Object getKeyword();

	@Override
	public String getKeywordSuffix()
	{
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof FieldType)
		{
			FieldType other = (FieldType) obj;
			return Objects.equals(getKeyword(), other.getKeyword())
					&& getJdbcTypeCode() == other.getJdbcTypeCode()
					&& hasLength() == other.hasLength()
					&& Objects.equals(getKeywordSuffix(),
							other.getKeywordSuffix())
					&& Objects.equals(getDefaultLength(),
							other.getDefaultLength());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getKeyword(), getJdbcTypeCode(), getDefaultLength(),
				getKeywordSuffix());
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getKeyword());
		if (hasLength())
		{
			sb.append("(");
			sb.append(getDefaultLength());
			sb.append(")");
		}
		if (getKeywordSuffix() != null)
		{
			sb.append(" ");
			sb.append(getKeywordSuffix());
		}
		return sb.toString();
	}

	@Override
	public Class<T> getJavaTypeClass()
	{
		return javaTypeClass;
	}
}

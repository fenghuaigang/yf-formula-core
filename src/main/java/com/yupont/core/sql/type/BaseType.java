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

import java.sql.ResultSet;
import java.sql.SQLException;

import com.yupont.core.sql.Keywords;

/**
 * 使用特定{@link ResultSet }的get方法而不是{@link ResultSet # GetObject(int)}方法获取结果 ,继承自
 * {@link AbstractType}
 * 
 * @see AbstractType
 * @param <T>
 *            java type
 *            @author Administrator
 */
public abstract class BaseType<T> extends AbstractType<T>
{
	protected final boolean checkNull;

	/**
	 * 实例化构造函数
	 *
	 * @param type
	 *            类型映射中的Java数据类型
	 * @param sqlType
	 *            JDBC数据类型{@link java.sql.Types}
	 */
	protected BaseType(Class<T> type, int sqlType)
	{
		super(type, sqlType);
		checkNull = !type.isPrimitive();
	}

	/**
	 * 使用特定{@link ResultSet }的get方法而不是{@link ResultSet # GetObject(int)}方法获取结果
	 *
	 * @param results
	 *            {@link ResultSet} ，通过索引读取记录
	 * @param column
	 *            目标索引
	 * @return {@link ResultSet}中的数据类型
	 * @throws SQLException
	 *             on a failure to read from the result set
	 */
	public abstract T fromResult(ResultSet results, int column)
			throws SQLException;

	@Override
	public T read(ResultSet results, int column) throws SQLException
	{
		T result = fromResult(results, column);
		if (checkNull && results.wasNull())
		{
			return null;
		}
		return result;
	}

	@Override
	public abstract Keywords getKeyword();
}

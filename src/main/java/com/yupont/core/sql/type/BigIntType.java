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

import com.yupont.core.sql.Keywords;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
/**
 * @author Administrator
 */
public class BigIntType extends BaseType<Long> implements PrimitiveLongType
{

	public BigIntType(Class<Long> type)
	{
		super(type, Types.BIGINT);
	}

	@Override
	public Long fromResult(ResultSet results, int column) throws SQLException
	{
		return results.getLong(column);
	}

	@Override
	public Keywords getKeyword()
	{
		return Keywords.BIGINT;
	}

	@Override
	public long readLong(ResultSet results, int column) throws SQLException
	{
		return results.getLong(column);
	}

	@Override
	public void writeLong(PreparedStatement statement, int index, long value)
			throws SQLException
	{
		statement.setLong(index, value);
	}
}

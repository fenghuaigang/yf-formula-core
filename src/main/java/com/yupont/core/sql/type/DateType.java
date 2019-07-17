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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
/**
 * @author Administrator
 */
public class DateType extends BaseType<Date>
{

	public DateType()
	{
		super(Date.class, Types.DATE);
	}

	@Override
	public Date fromResult(ResultSet results, int column) throws SQLException
	{
		return results.getDate(column);
	}

	@Override
	public void write(PreparedStatement statement, int index, Date value)
			throws SQLException
	{
		int sqlType = getJdbcTypeCode();
		if (value == null)
		{
			statement.setNull(index, sqlType);
		} else
		{
			statement.setDate(index, value);
		}
	}

	@Override
	public Keywords getKeyword()
	{
		return Keywords.DATE;
	}

}

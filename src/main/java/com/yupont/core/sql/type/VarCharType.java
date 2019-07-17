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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
/**
 * @author Administrator
 */
public class VarCharType extends BaseType<String>
{

	public VarCharType()
	{
		super(String.class, Types.VARCHAR);
	}

	@Override
	public String fromResult(ResultSet results, int column) throws SQLException
	{
		return results.getString(column);
	}

	@Override
	public String read(ResultSet results, int column) throws SQLException
	{
		return results.getString(column);
	}

	@Override
	public Keywords getKeyword()
	{
		return Keywords.VARCHAR;
	}

	@Override
	public boolean hasLength()
	{
		return true;
	}

	@Override
	public Integer getDefaultLength()
	{
		return 255;
	}
}

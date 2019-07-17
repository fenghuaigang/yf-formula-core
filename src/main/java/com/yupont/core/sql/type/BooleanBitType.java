package com.yupont.core.sql.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.yupont.core.sql.Keywords;
/**
 * @author Administrator
 */
public class BooleanBitType extends BaseType<Boolean>
		implements PrimitiveBooleanType
{
	public BooleanBitType()
	{
		super(Boolean.class, Types.BIT);
	}

	@Override
	public Boolean fromResult(ResultSet results, int column) throws SQLException
	{
		return results.getBoolean(column);
	}

	@Override
	public Keywords getKeyword()
	{
		return Keywords.BOOLEAN;
	}

	@Override
	public boolean readBoolean(ResultSet results, int column)
			throws SQLException
	{
		return results.getBoolean(column);
	}

	@Override
	public void writeBoolean(PreparedStatement statement, int index,
			boolean value) throws SQLException
	{
		statement.setBoolean(index, value);
	}
}

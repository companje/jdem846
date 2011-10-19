/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.dbase;

import java.util.Date;

public class DBaseField
{
	
	private int type;
	private String stringValue = null;
	private float floatValue = 0;
	private int integerValue = 0;
	private Date dateValue = null;
	
	public DBaseField()
	{
		
	}
	
	public Object getValue()
	{
		switch (type) {
		case DBaseConstants.DBASE_TYPE_FLOAT:
			return floatValue;
		case DBaseConstants.DBASE_TYPE_NUMERIC:
			return integerValue;
		case DBaseConstants.DBASE_TYPE_DATE:
			return dateValue;
		case DBaseConstants.DBASE_TYPE_CHAR:
		default:
			return stringValue;
		}
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}



	public String getStringValue()
	{
		return stringValue;
	}

	public void setStringValue(String stringValue)
	{
		this.stringValue = stringValue;
	}

	public float getFloatValue()
	{
		return floatValue;
	}

	public void setFloatValue(float floatValue)
	{
		this.floatValue = floatValue;
	}

	public int getIntegerValue()
	{
		return integerValue;
	}

	public void setIntegerValue(int integerValue)
	{
		this.integerValue = integerValue;
	}

	public Date getDateValue()
	{
		return dateValue;
	}

	public void setDateValue(Date dateValue)
	{
		this.dateValue = dateValue;
	}
	
	
	
	
}

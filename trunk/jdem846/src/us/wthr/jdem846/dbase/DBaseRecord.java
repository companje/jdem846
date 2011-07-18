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

public class DBaseRecord
{
	private DBaseFieldDescriptor[] fieldDescriptors;
	private DBaseField[] fields;
	
	
	public DBaseRecord(int numFields)
	{
		fields = new DBaseField[numFields];
		fieldDescriptors = new DBaseFieldDescriptor[numFields];
	}

	
	public Object getValue(int index)
	{
		DBaseField field = getField(index);
		if (field == null)
			return null;
		else
			return field.getValue();
	}
	
	public Object getValue(String column)
	{
		DBaseField field = getField(column);
		if (field == null)
			return null;
		else
			return field.getValue();
	}
	
	public String getString(int index)
	{
		DBaseField field = getField(index);
		if (field == null)
			return null;
		else
			return field.getStringValue();
	}
	
	public String getString(String column)
	{
		DBaseField field = getField(column);
		if (field == null)
			return null;
		else
			return field.getStringValue();
	}
	
	
	public int getInteger(int index)
	{
		DBaseField field = getField(index);
		if (field == null)
			return 0;
		else
			return field.getIntegerValue();
	}
	
	public int getInteger(String column)
	{
		DBaseField field = getField(column);
		if (field == null)
			return 0;
		else
			return field.getIntegerValue();
	}
	
	public float getFloat(int index)
	{
		DBaseField field = getField(index);
		if (field == null)
			return 0;
		else
			return field.getFloatValue();
	}
	
	public float getFloat(String column)
	{
		DBaseField field = getField(column);
		if (field == null)
			return 0;
		else
			return field.getFloatValue();
	}
	
	public Date getDate(int index)
	{
		DBaseField field = getField(index);
		if (field == null)
			return null;
		else
			return field.getDateValue();
	}
	
	public Date getDate(String column)
	{
		DBaseField field = getField(column);
		if (field == null)
			return null;
		else
			return field.getDateValue();
	}
	
	public void setField(int index, DBaseField field)
	{
		fields[index] = field;
	}
	
	public DBaseField getField(int index)
	{
		return fields[index];
	}
	
	public DBaseField getField(String name)
	{
		for (int i = 0; i < fieldDescriptors.length; i++) {
			if (fieldDescriptors[i].getName().equals(name)) {
				return fields[i];
			}
		}
		return null;
	}
	
	
	public void setFieldDescriptor(int index, DBaseFieldDescriptor fieldDescriptor)
	{
		fieldDescriptors[index] = fieldDescriptor;
	}
	
	public DBaseFieldDescriptor getFieldDescriptor(int index)
	{
		return fieldDescriptors[index];
	}

	public DBaseFieldDescriptor[] getFieldDescriptors()
	{
		return fieldDescriptors;
	}


	public void setFieldDescriptors(DBaseFieldDescriptor[] fieldDescriptors)
	{
		this.fieldDescriptors = fieldDescriptors;
	}


	public DBaseField[] getFields()
	{
		return fields;
	}


	public void setFields(DBaseField[] fields)
	{
		this.fields = fields;
	}
	
	
	
	
	
}

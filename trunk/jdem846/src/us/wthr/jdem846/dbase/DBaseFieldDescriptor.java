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

public class DBaseFieldDescriptor
{
	private String name;
	private int padding;
	
	private byte type;
	private int displacement;
	
	private int length;
	private int decCount;
	
	private int reserved1;
	private int workAreaId;
	private int reserved2;
	private int production;
	
	public DBaseFieldDescriptor()
	{
		
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public int getPadding() 
	{
		return padding;
	}

	public void setPadding(int padding) 
	{
		this.padding = padding;
	}

	public byte getType()
	{
		return type;
	}

	public void setType(byte type) 
	{
		this.type = type;
	}

	public int getDisplacement() 
	{
		return displacement;
	}

	public void setDisplacement(int displacement) 
	{
		this.displacement = displacement;
	}

	public int getLength() 
	{
		return length;
	}

	public void setLength(int length) 
	{
		this.length = length;
	}

	public int getDecCount()
	{
		return decCount;
	}

	public void setDecCount(int decCount) 
	{
		this.decCount = decCount;
	}

	public int getReserved1() 
	{
		return reserved1;
	}

	public void setReserved1(int reserved1) 
	{
		this.reserved1 = reserved1;
	}

	public int getWorkAreaId() 
	{
		return workAreaId;
	}

	public void setWorkAreaId(int workAreaId) 
	{
		this.workAreaId = workAreaId;
	}

	public int getReserved2() 
	{
		return reserved2;
	}

	public void setReserved2(int reserved2) 
	{
		this.reserved2 = reserved2;
	}

	public int getProduction()
	{
		return production;
	}

	public void setProduction(int production) 
	{
		this.production = production;
	}
	
	
	
	
}

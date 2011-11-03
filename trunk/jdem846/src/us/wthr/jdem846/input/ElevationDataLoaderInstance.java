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

package us.wthr.jdem846.input;

@Deprecated
public class ElevationDataLoaderInstance {
	
	private String clazzName;
	private String name;
	private String identifier;
	private String extension;
	
	public ElevationDataLoaderInstance(String clazzName, String name, String identifier, String extension)
	{
		this.clazzName = clazzName;
		this.name = name;
		this.identifier = identifier;
		this.extension = extension;
	}

	public DataSource getImpl()
	{
		return null;
	}
	
	public String getClazzName() 
	{
		return clazzName;
	}

	public void setClazzName(String clazzName) 
	{
		this.clazzName = clazzName;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getIdentifier() 
	{
		return identifier;
	}

	public void setIdentifier(String identifier) 
	{
		this.identifier = identifier;
	}

	public String getExtension() 
	{
		return extension;
	}

	public void setExtension(String extension) 
	{
		this.extension = extension;
	}
	
	
	
	
}

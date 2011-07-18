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

@SuppressWarnings("serial")
public class ClassLoadException extends Exception
{
	private String className;
	
	public ClassLoadException()
	{
		super();
	}
	
	public ClassLoadException(String message)
	{
		super(message);
	}
	
	public ClassLoadException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
	public ClassLoadException(String className, String message)
	{
		super(message);
		this.className = className;
	}
	
	public ClassLoadException(String className, String message, Throwable thrown)
	{
		super(message, thrown);
		this.className = className;
	}
	
	public String getClassName()
	{
		return className;
	}
	
	
	
	
}

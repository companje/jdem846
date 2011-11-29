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

package us.wthr.jdem846.exception;

/** Thrown in the event of an error within a service class method or within the service kernel.
 * 
 * @author Kevin M. Gill
 *
 */
@SuppressWarnings("serial")
public class ServiceException extends Exception
{
	
	/** Fully qualified class name of the service class causing the error.
	 * 
	 */
	private String clazzName;
	
	public ServiceException(String clazzName, String message, Exception ex)
	{
		super(message, ex);
		this.clazzName = clazzName;
	}
	
	public ServiceException(String message, Exception ex)
	{
		super(message, ex);
	}
	
	public ServiceException(String clazzName, String message)
	{
		super(message);
		this.clazzName = clazzName;
	}
	
	/** Fully qualified class name of the service class causing the error.
	 * 
	 * @return
	 */
	public String getClassName()
	{
		return clazzName;
	}
	
}

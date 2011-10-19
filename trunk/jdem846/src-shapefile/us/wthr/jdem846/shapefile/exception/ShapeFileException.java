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

package us.wthr.jdem846.shapefile.exception;

@SuppressWarnings("serial")
public class ShapeFileException extends Exception
{
	private String filePath;
	
	public ShapeFileException()
	{
		
	}
	
	public ShapeFileException(String message)
	{
		super(message);
	}
	
	public ShapeFileException(String message, Throwable thrown)
	{
		super(message, thrown);
	}
	
	
	public ShapeFileException(String filePath, String message, Throwable thrown)
	{
		super(message, thrown);
		this.filePath = filePath;
	}

	public String getFilePath()
	{
		return filePath;
	}
	
	
}

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

import us.wthr.jdem846.exception.DataSourceException;

/** Specifies a class that is to be uses to load and cache binary data.
 * 
 * @author Kevin M. Gill
 *
 */
public abstract class DataCache 
{
	
	/** Gets the length of the data file in bytes.
	 * 
	 * @return The length of the data file in bytes.
	 */
	public abstract long getDataLength();
	public abstract double get(int position) throws DataSourceException;
	public abstract void load(long start) throws DataSourceException;
	public abstract void unload();
	public abstract boolean isLoaded();
	public abstract void setLoaded(boolean isLoaded);
	public abstract void dispose() throws DataSourceException;
	
	public void load(double[] valueBuffer, int start, int length) throws DataSourceException
	{
		throw new DataSourceException("Not implemented");
	}
}

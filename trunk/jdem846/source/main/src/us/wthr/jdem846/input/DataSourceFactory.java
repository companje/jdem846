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

import us.wthr.jdem846.exception.InvalidFileFormatException;
import us.wthr.jdem846.input.bil.BilInt16;
import us.wthr.jdem846.input.edef.ElevationDatasetExchange;
import us.wthr.jdem846.input.esri.GridAscii;
import us.wthr.jdem846.input.gridfloat.GridFloat;
import us.wthr.jdem846.input.netcdf.NetCdf;

/** Utility class for loading a datasource using known file extensions to determine type and correct driver.
 * 
 * @author Kevin M. Gill
 *
 */
@Deprecated
public class DataSourceFactory 
{
	
	private DataSourceFactory()
	{
		
	}
	
	public static DataSource loadDataSource(String filePath) throws InvalidFileFormatException
	{
		DataSource dataSource = null;
		
		String extension = filePath.substring(filePath.lastIndexOf(".")+1);
		if (extension == null) {
			throw new InvalidFileFormatException(null);
		}
		
		if (extension.equalsIgnoreCase("flt")) {
			dataSource = new GridFloat(filePath);
		} else if (extension.equalsIgnoreCase("bil")) {
			dataSource = new BilInt16(filePath);
		} else if (extension.equalsIgnoreCase("edef")) {
			dataSource = new ElevationDatasetExchange(filePath);
		} else if (extension.equalsIgnoreCase("asc")) {
			dataSource = new GridAscii(filePath);
		} else if (extension.equalsIgnoreCase("nc")) {
			dataSource = new NetCdf(filePath);
		} else {
			throw new InvalidFileFormatException(extension);
		}
		
		
		return dataSource;
	}
	
}

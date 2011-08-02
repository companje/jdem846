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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.exception.ResourceLoaderException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.ResourceLoader;

public class GridFloatHeader implements DataSourceHeader 
{
	private static Log log = Logging.getLog(GridFloatHeader.class);
	
	private int columns = 0;
	private int rows = 0;
	private float xLowerLeft = 0;
	private float yLowerLeft = 0;
	private float cellSize = 0;
	private float noData = 0;
	private ByteOrder byteOrder = ByteOrder.LSBFIRST;
	
	
	public GridFloatHeader(String file_path)
	{
		init(file_path);
	}


	private void init(String filePath)
	{
		File headerFile = new File(filePath);
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(headerFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				readHeaderLine(line);
			}
			reader.close();
		} catch (FileNotFoundException ex) {
			log.error("File Not Found error opening GridFloat data file: " + ex.getMessage(), ex);
		} catch (IOException ex) {
			log.error("IO error when opening GridFloat data file: " + ex.getMessage(), ex);
		}
	}


	private void readHeaderLine(String line)
	{
		
		line = line.replaceAll("[ ]+", " ");
		String[] parts = line.split(" ");
		if (parts.length != 2) {
			return;
		}
		String title = parts[0];
		String value = parts[1];
		title = title.trim();
		value = value.trim();
		

		if (title.equalsIgnoreCase("ncols"))
			this.columns = Integer.parseInt(value);
		if (title.equalsIgnoreCase("nrows"))
			this.rows = Integer.parseInt(value);
		if (title.equalsIgnoreCase("xllcorner") || title.equalsIgnoreCase("xllcenter"))
			this.xLowerLeft = Float.parseFloat(value);
		if (title.equalsIgnoreCase("yllcorner") || title.equalsIgnoreCase("yllcenter"))
			this.yLowerLeft = Float.parseFloat(value);
		if (title.equalsIgnoreCase("cellsize"))
			this.cellSize = Float.parseFloat(value);
		if (title.equalsIgnoreCase("NODATA_value"))
			this.noData = Float.parseFloat(value);
		if (title.equalsIgnoreCase("byteorder")) {
			
			if (value.equalsIgnoreCase("LSBFIRST")) 
				byteOrder = ByteOrder.LSBFIRST;
			if (value.equalsIgnoreCase("MSBFIRST"))
				byteOrder = ByteOrder.MSBFIRST;
			if (value.equalsIgnoreCase("INTEL_BYTE_ORDER"))
				byteOrder = ByteOrder.INTEL_BYTE_ORDER;
			if (value.equalsIgnoreCase("INTEL_OR_MOTOROLA"))
				byteOrder = ByteOrder.INTEL_OR_MOTOROLA;
			
		}
		
	}
	
	public int getColumns()
	{
		return columns;
	}


	public void setColumns(int columns)
	{
		this.columns = columns;
	}


	public int getRows() 
	{
		return rows;
	}


	public void setRows(int rows) 
	{
		this.rows = rows;
	}


	public float getxLowerLeft() 
	{
		return xLowerLeft;
	}


	public void setxLowerLeft(float xLowerLeft)
	{
		this.xLowerLeft = xLowerLeft;
	}


	public float getyLowerLeft()
	{
		return yLowerLeft;
	}


	public void setyLowerLeft(float yLowerLeft) 
	{
		this.yLowerLeft = yLowerLeft;
	}


	public float getCellSize() 
	{
		return cellSize;
	}


	public void setCellSize(float cellSize) 
	{
		this.cellSize = cellSize;
	}


	public float getNoData() {
		return noData;
	}


	public void setNoData(float noData) 
	{
		this.noData = noData;
	}


	public ByteOrder getByteOrder()
	{
		return byteOrder;
	}


	public void setByteOrder(ByteOrder byteOrder) 
	{
		this.byteOrder = byteOrder;
	}
	
	
	
	
}

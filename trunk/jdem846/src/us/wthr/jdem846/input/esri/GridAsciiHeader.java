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

package us.wthr.jdem846.input.esri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.input.DataSourceHeader;

public class GridAsciiHeader implements DataSourceHeader 
{
	private int columns = 0;
	private int rows = 0;
	private double xLowerLeft = -999999;
	private double yLowerLeft = -999999;
	private double cellSize = -999999;
	private double noData = 0;
	private int skipBytes = 0;
	private int nbands = 0;
	private int nbits = 0;
	private int bandRowBytes = 0;
	private int totalRowBytes = 0;
	private int pixelType = DemConstants.PIXELTYPE_SIGNED_INT;
	private ByteOrder byteOrder = ByteOrder.LSBFIRST;
	
	
	public GridAsciiHeader(File ascFile)
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(ascFile));
			String line = null;
			
			for (int i = 0; i < 6; i++) {
				line = reader.readLine();
				readHeaderLine(line);
			}
			reader.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	private void readHeaderLine(String line)
	{
		
		line = line.replaceAll("[ ]+", " ");
		String[] parts = line.split(" ");
		if (parts.length < 2) {
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
		if (title.equalsIgnoreCase("xllcorner"))
			this.xLowerLeft = Float.parseFloat(value);
		if (title.equalsIgnoreCase("yllcorner"))
			this.yLowerLeft = Float.parseFloat(value);
		if (title.equalsIgnoreCase("cellsize"))
			this.cellSize = Float.parseFloat(value);
		if (title.equalsIgnoreCase("NODATA_value"))
			this.noData = Float.parseFloat(value);

		
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


	public double getxLowerLeft()
	{
		return xLowerLeft;
	}


	public void setxLowerLeft(double xLowerLeft)
	{
		this.xLowerLeft = xLowerLeft;
	}


	public double getyLowerLeft()
	{
		return yLowerLeft;
	}


	public void setyLowerLeft(double yLowerLeft)
	{
		this.yLowerLeft = yLowerLeft;
	}


	public double getCellSize()
	{
		return cellSize;
	}


	public void setCellSize(double cellSize)
	{
		this.cellSize = cellSize;
	}


	public double getNoData()
	{
		return noData;
	}


	public void setNoData(double noData)
	{
		this.noData = noData;
	}


	public int getSkipBytes()
	{
		return skipBytes;
	}


	public void setSkipBytes(int skipBytes)
	{
		this.skipBytes = skipBytes;
	}


	public int getNbands()
	{
		return nbands;
	}


	public void setNbands(int nbands)
	{
		this.nbands = nbands;
	}


	public int getNbits()
	{
		return nbits;
	}


	public void setNbits(int nbits)
	{
		this.nbits = nbits;
	}


	public int getBandRowBytes()
	{
		return bandRowBytes;
	}


	public void setBandRowBytes(int bandRowBytes)
	{
		this.bandRowBytes = bandRowBytes;
	}


	public int getTotalRowBytes()
	{
		return totalRowBytes;
	}


	public void setTotalRowBytes(int totalRowBytes)
	{
		this.totalRowBytes = totalRowBytes;
	}


	public int getPixelType()
	{
		return pixelType;
	}


	public void setPixelType(int pixelType)
	{
		this.pixelType = pixelType;
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

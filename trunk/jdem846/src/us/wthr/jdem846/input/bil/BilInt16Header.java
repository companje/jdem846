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

package us.wthr.jdem846.input.bil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.input.DataSourceHeader;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class BilInt16Header implements DataSourceHeader 
{
	private static Log log = Logging.getLog(BilInt16Header.class);
	
	private int columns = 0;
	private int rows = 0;
	private float xLowerLeft = -999999;
	private float yLowerLeft = -999999;
	private float cellSize = -999999;
	private float noData = 0;
	private int skipBytes = 0;
	private int nbands = 0;
	private int nbits = 0;
	private int bandRowBytes = 0;
	private int totalRowBytes = 0;
	private int pixelType = DemConstants.PIXELTYPE_SIGNED_INT;
	private ByteOrder byteOrder = ByteOrder.LSBFIRST;
	
	public BilInt16Header(String filePath)
	{
		init(filePath);
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
			correctBounds();
		} catch (FileNotFoundException ex) {
			log.error("File Not Found error opening BIL header file: " + ex.getMessage(), ex);
		} catch (IOException ex) {
			log.error("IO error when reading from BIL header file: " + ex.getMessage(), ex);
		}
	}
	
	private void correctBounds()
	{
		if (xLowerLeft != -999999) {
			xLowerLeft = xLowerLeft - (rows * cellSize);
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
		if (title.equalsIgnoreCase("nbands"))
			this.nbands = Integer.parseInt(value);
		if (title.equalsIgnoreCase("nbits"))
			this.nbits = Integer.parseInt(value);
		if (title.equalsIgnoreCase("skipbytes"))
			this.skipBytes = Integer.parseInt(value);
		if (title.equalsIgnoreCase("bandrowbytes"))
			this.bandRowBytes = Integer.parseInt(value);
		if (title.equalsIgnoreCase("totalrowbytes"))
			this.totalRowBytes = Integer.parseInt(value);
		if (title.equalsIgnoreCase("ulxmap"))
			this.xLowerLeft = Float.parseFloat(value);
		if (title.equalsIgnoreCase("ulymap"))
			this.yLowerLeft = Float.parseFloat(value);
		if (title.equalsIgnoreCase("xdim"))
			this.cellSize = Float.parseFloat(value);
		if (title.equalsIgnoreCase("NODATA"))
			this.noData = Float.parseFloat(value);
		if (title.equalsIgnoreCase("byteorder")) {
			if (value.equalsIgnoreCase("LSBFIRST")) 
				byteOrder = ByteOrder.LSBFIRST;
			if (value.equalsIgnoreCase("MSBFIRST"))
				byteOrder = ByteOrder.MSBFIRST;
			if (value.equalsIgnoreCase("I"))
				byteOrder = ByteOrder.INTEL_BYTE_ORDER;
			if (value.equalsIgnoreCase("M"))
				byteOrder = ByteOrder.INTEL_OR_MOTOROLA;
		}
		if (title.equalsIgnoreCase("pixeltype")) {
			if (value.equalsIgnoreCase("SIGNEDINT"))
				pixelType = DemConstants.PIXELTYPE_SIGNED_INT;
			if (value.equalsIgnoreCase("UNSIGNEDINT"))
				pixelType = DemConstants.PIXELTYPE_UNSIGNED_INT;
		}
		
	}

	

	public int getPixelType() 
	{
		return pixelType;
	}


	public void setPixelType(int pixelType) 
	{
		this.pixelType = pixelType;
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


	public int getSkipBytes() 
	{
		return skipBytes;
	}


	public void setSkipBytes(int skipBytes) 
	{
		this.skipBytes = skipBytes;
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


	public float getNoData()
	{
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

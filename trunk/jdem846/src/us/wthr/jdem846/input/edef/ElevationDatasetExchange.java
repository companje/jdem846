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

package us.wthr.jdem846.input.edef;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.annotations.ElevationDataLoader;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.input.DataSource;
import us.wthr.jdem846.input.bil.BilInt16;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Elevation storage format based on GridFloat with a raster of doubleing-point values and embedded projection
 * and meta data information. The format is designed for simple reading & writing and does not require
 * any additional accompanying files. The specification is free & open.
 * 
 * @author Kevin M. Gill
 *
 */
@ElevationDataLoader(name="us.wthr.jdem846.input.edef.elevationDatasetExchangeFormat.name", identifier="edef", extension="edef")
public class ElevationDatasetExchange extends DataSource
{
	private static Log log = Logging.getLog(ElevationDatasetExchange.class);
	
	private ElevationDatasetExchangeHeader header = null;
	
	private int bits = 32;
	private int totalRowBytes;
	private double maxElevation = -50000;
    private double minElevation = 50000;;
    private double resolution;
    private int maxRow;
    private int maxCol;
    private String filePath;
    
    int cachedRow = -1;
    ElevationDatasetExchangeDataCache cache = null;
	
    private boolean isDisposed = false;
    
	public ElevationDatasetExchange(String filePath)
	{
		this.filePath = filePath;
		
		File edefFile = new File(filePath);
		InputStream fin = null;
		byte[] headerBinData = new byte[ElevationDatasetExchangeHeader.BYTE_SIZE];
		
		try {
			// TODO: Make this a little stronger....

			fin = new BufferedInputStream(new FileInputStream(edefFile));
			fin.read(headerBinData, 0, ElevationDatasetExchangeHeader.BYTE_SIZE);
			fin.close();
		} catch (FileNotFoundException e) {
			log.error("File Not Found error opening EDEF file: " + e.getMessage(), e);
			return;
		} catch (IOException e) {
			log.error("IO error opening EDEF file: " + e.getMessage(), e);
			return;
		}
		
		header = new ElevationDatasetExchangeHeader(headerBinData);
		this.setMaxElevation(header.getMaxElevation());
		this.setMinElevation(header.getMinElevation());
		cache = new ElevationDatasetExchangeDataCache(edefFile, (this.bits * header.getColumns())*2, header.getByteOrder());
		
	}
    
	public long getDataLength()
	{
		return cache.getDataLength();
	}
	
	public void dispose() throws DataSourceException
	{
		if (isDisposed) {
			throw new DataSourceException("Object already disposed of");
		}
		
		cache.dispose();
		cache = null;
		
		isDisposed = true;
	}
	
	@Override
	public void calculateElevationMinMax() throws DataSourceException
	{
		if (minElevation > maxElevation)
			super.calculateElevationMinMax();
	}



	@Override
	public void loadRow(int row) 
	{
		this.cachedRow = row;
		long seekTo = row * (header.getColumns() * 4);
		cache.load(seekTo);
	}

	@Override
	public void initDataCache()
	{
		loadRow(this.cachedRow);
	}

	@Override
	public void unloadDataCache() 
	{
		cache.unload();
	}
	
	private double __get(int row, int col)
	{
		if (col < 0 || col > header.getColumns() || row < 0 || row > header.getRows())
			return DemConstants.ELEV_NO_DATA;
		
		if (row != cachedRow && row != cachedRow+1) {
			this.loadRow(row);
		}
		
		if (row != cachedRow) {
			col = header.getColumns() + col;
		}
	
		double elevation = cache.get(col);
		return elevation;
	}

	public double getElevation(int col)
	{
		return this.getElevation(cachedRow, col);
	}

	public double getElevation(int row, int column)
	{
		double elevation = this.__get(row, column);
		if (elevation == header.getNoData())
			elevation = DemConstants.ELEV_NO_DATA;

		return elevation;
	}

	public ElevationDatasetExchangeHeader getHeader()
	{
		return header;
	}



	public void setHeader(ElevationDatasetExchangeHeader header)
	{
		this.header = header;
	}



	public int getBits()
	{
		return bits;
	}



	public void setBits(int bits)
	{
		this.bits = bits;
	}



	public int getTotalRowBytes()
	{
		return totalRowBytes;
	}



	public void setTotalRowBytes(int totalRowBytes)
	{
		this.totalRowBytes = totalRowBytes;
	}





	public double getMaxElevation()
	{
		return maxElevation;
	}



	public void setMaxElevation(double maxElevation)
	{
		this.maxElevation = maxElevation;
	}



	public double getMinElevation()
	{
		return minElevation;
	}



	public void setMinElevation(double minElevation)
	{
		this.minElevation = minElevation;
	}



	public double getResolution()
	{
		return resolution;
	}



	public void setResolution(double resolution)
	{
		this.resolution = resolution;
	}



	public int getMaxRow()
	{
		return maxRow;
	}



	public void setMaxRow(int maxRow)
	{
		this.maxRow = maxRow;
	}



	public int getMaxCol()
	{
		return maxCol;
	}



	public void setMaxCol(int maxCol)
	{
		this.maxCol = maxCol;
	}



	public String getFilePath()
	{
		return filePath;
	}



	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	
	public DataSource copy()
	{
		ElevationDatasetExchange dataSource = new ElevationDatasetExchange(this.filePath);
		dataSource.bits = this.bits;
		dataSource.totalRowBytes = this.totalRowBytes;
		dataSource.maxElevation = this.maxElevation;
		dataSource.minElevation = this.minElevation;
		dataSource.resolution = this.resolution;
		dataSource.maxRow = this.maxRow;
		dataSource.maxCol = this.maxCol;
		dataSource.filePath = this.filePath;
	    
		dataSource.cachedRow = -1;
		
	    return dataSource;
	}
}

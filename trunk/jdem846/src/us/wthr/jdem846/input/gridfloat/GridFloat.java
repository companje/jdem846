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

package us.wthr.jdem846.input.gridfloat;

import java.io.File;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.annotations.ElevationDataLoader;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.input.DataSource;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.ResourceLoader;

@ElevationDataLoader(name="us.wthr.jdem846.input.gridFloat.name", identifier="gridfloat", extension="flt")
public class GridFloat extends DataSource
{
	private static Log log = Logging.getLog(GridFloat.class);
	
	private GridFloatHeader header = null;
	
	private int bits = 32;
	private int totalRowBytes;
	private double maxDifference;
	private double maxElevation;
    private double minElevation;
    private double resolution;
    private int maxRow;
    private int maxCol;
    private String filePath;
    
    int cachedRow = -1;
    GridFloatDataCache cache = null;
	
    private boolean isDisposed = false;
    
	public GridFloat(String filePath)
	{
		this.filePath = filePath;
		String headerFilePath = filePath.replace(".flt", ".hdr");
		header = new GridFloatHeader(headerFilePath);
		
		File dataFile = new File(filePath);
		cache = new GridFloatDataCache(dataFile, (this.bits * header.getColumns())*2, header.getByteOrder());
		this.calculateDistances();
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
	public void load(double[] valueBuffer, int start, int length) throws DataSourceException
	{
		cache.load(valueBuffer, start, length);
	}
	
	public void loadRow(int row)
	{
		this.cachedRow = row;
		long seekTo = (long) row * (long)(header.getColumns() * 4l);
		cache.load(seekTo);
	}

	public boolean setDataPrecached(boolean precached)
	{
		if (cache != null) {
			return cache.setDataPrecached(precached);
		} else {
			return false;
		}
	}
	
	public void initDataCache()
	{
		loadRow(this.cachedRow);
	}
	
	public void unloadDataCache()
	{
		cache.unload();
	}

	
	private double __get(int row, int col)
	{
		
		//if (col < 0 || col > header.getColumns() || row < 0 || row > header.getRows())
		//	return DemConstants.ELEV_NO_DATA;
		
		if ((row != cachedRow && row != cachedRow+1) || cachedRow == -1 ) {
			this.loadRow(row);
		}
		
		//if (row != cachedRow) {
		//	col = header.getColumns() + col;
		//}
	
		return cache.get(((row != cachedRow) ? header.getColumns() + col : col));
		//if (elevation == Float.NaN || elevation == -Float.NaN) 
		//	elevation = DemConstants.ELEV_NO_DATA;
		
		//return elevation;
		
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
	
	public GridFloatHeader getHeader() 
	{
		return header;
	}

	public void setHeader(GridFloatHeader header)
	{
		this.header = header;
	}

	public String getFilePath()
	{
		return filePath;
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

	public double getMaxDifference() 
	{
		return maxDifference;
	}

	public void setMaxDifference(double maxDifference) 
	{
		this.maxDifference = maxDifference;
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

	protected void setMaxRow(int maxRow) 
	{
		this.maxRow = maxRow;
	}

	public int getMaxCol() 
	{
		return maxCol;
	}

	protected void setMaxCol(int maxCol) 
	{
		this.maxCol = maxCol;
	}
	
	public DataSource copy()
	{
		GridFloat gridFloat = new GridFloat(this.filePath);
		gridFloat.bits = this.bits;
		gridFloat.totalRowBytes = this.totalRowBytes;
		gridFloat.maxDifference = this.maxDifference;
		gridFloat.maxElevation = this.maxElevation;
		gridFloat.minElevation = this.minElevation;
	    gridFloat.resolution = this.resolution;
	    gridFloat.maxRow = this.maxRow;
	    gridFloat.maxCol = this.maxCol;
	    gridFloat.filePath = this.filePath;
	    
	    gridFloat.cachedRow = -1;
		
	    return gridFloat;
	}
	
}

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

import java.io.File;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.annotations.ElevationDataLoader;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@ElevationDataLoader(name="us.wthr.jdem846.input.bilInt16.name", identifier="bil-16int", extension="bil")
public class BilInt16 extends DataSource
{
	private static Log log = Logging.getLog(BilInt16.class);
	
	protected static String[] WORLD_EXTENSIONS = {"bqw", "blw", "dmw", "BQW", "BLW", "DMW"};
	protected static String[] HEADER_EXTENSIONS = {"hdr", "HDR"};
	
	private BilInt16Header header = null;
	private BilInt16World world = null;
	
	private int bits = 16;
	private int totalRowBytes;
	private float maxDifference;
	private float maxElevation;
    private float minElevation;
    private float resolution;
    private int maxRow;
    private int maxCol;
    private String filePath;
    
    int cachedRow = -1;
    private BilInt16DataCache cache = null;
    
    private boolean isDisposed = false;
    
    public BilInt16(String filePath)
    {
    	this.filePath = filePath;
    	for(String extension : HEADER_EXTENSIONS) {
    		String headerFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + "." + extension;
    		File hdrFile = new File(headerFilePath);
    		if (hdrFile.exists()) {
    			header = new BilInt16Header(headerFilePath);
    			break;
    		}
    	}

    	
    	for(String extension : WORLD_EXTENSIONS) {
    		String worldFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + "." + extension;
        	File f = new File(worldFilePath);
        	if (f.exists()) {
        		world = new BilInt16World(worldFilePath);
        		break;
        	}
    	}
    	
    	if (world != null) {
	     	if (header.getCellSize() == -999999) {
	     		header.setCellSize(world.getxDim());
	     	}
	     	
	     	if (header.getxLowerLeft() == -999999) {
	     		header.setxLowerLeft(world.getxUpperLeft());
	     	}
	     	
	     	if (header.getyLowerLeft() == -999999) {
	     		header.setyLowerLeft(world.getyUpperLeft() - (header.getCellSize() * header.getRows()));
	     	}
    	}
     	
     	
     	
     	
    	
    	File dataFile = new File(filePath);
		cache = new BilInt16DataCache(dataFile, (this.bits * header.getColumns())*2, header.getByteOrder(), header.getSkipBytes());
		this.calculateDistances();
    	
    	
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
	
	public void loadRow(int row)
	{
		this.cachedRow = row;
		long seekTo = row * (header.getColumns() * 2);
		cache.load(seekTo);
	}

	public void initDataCache()
	{
		loadRow(this.cachedRow);
	}
	
	public void unloadDataCache()
	{
		cache.unload();
	}

	private float __get(int row, int col)
	{
		if (col < 0 || col > header.getColumns() || row < 0 || row > header.getRows())
			return DemConstants.ELEV_NO_DATA;
		
		if (row != cachedRow && row != cachedRow+1) {
			this.loadRow(row);
		}
		
		if (row != cachedRow) {
			col = header.getColumns() + col;
		}
	
		if (col >= header.getColumns() * 2) {
			log.warn("Column to high!!! -- " + row + "/" + col + " -- " + header.getColumns() + " -- " + col);
		}
		
		float elevation = cache.get(col);
		return elevation;
	}
	
	
	public float getElevation(int col)
	{
		if (col >= header.getColumns())
			return DemConstants.ELEV_NO_DATA;
		
		return this.getElevation(cachedRow, col);
	}

	public float getElevation(int row, int column)
	{
		if (column >= header.getColumns() || row > header.getRows())
			return DemConstants.ELEV_NO_DATA;
		
		
		
		float elevation = this.__get(row, column);
		if (elevation == header.getNoData())
			elevation = DemConstants.ELEV_NO_DATA;

		return elevation;
	}
    
	public DataSourceHeader getHeader() 
	{
		return header;
	}

	public void setHeader(BilInt16Header header) 
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

	public float getMaxDifference()
	{
		return maxDifference;
	}

	public void setMaxDifference(float maxDifference)
	{
		this.maxDifference = maxDifference;
	}

	public float getMaxElevation() 
	{
		return maxElevation;
	}

	public void setMaxElevation(float maxElevation)
	{
		this.maxElevation = maxElevation;
	}

	public float getMinElevation() 
	{
		return minElevation;
	}

	public void setMinElevation(float minElevation) 
	{
		this.minElevation = minElevation;
	}

	public float getResolution()
	{
		return resolution;
	}

	public void setResolution(float resolution) 
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

	public int getCachedRow() 
	{
		return cachedRow;
	}

	protected void setCachedRow(int cachedRow) 
	{
		this.cachedRow = cachedRow;
	}

	public DataSource copy()
	{
		BilInt16 dataSource = new BilInt16(this.filePath);
		dataSource.bits = this.bits;
		dataSource.totalRowBytes = this.totalRowBytes;
		dataSource.maxDifference = this.maxDifference;
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

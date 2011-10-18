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

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;

/** A class that provides the primitives for a specific raster format driver.
 * 
 * @author Kevin M. Gill
 *
 */
public abstract class DataSource 
{
	
	public void calculateDataStats()
	{
		setMaxRow(getHeader().getRows());
		setMaxCol(getHeader().getColumns());
	}
	
	/** Calculate distances (cellsize) of datapoints with default of Earth's mean radius.
	 * 
	 */
	public void calculateDistances()
	{
		calculateDistances(DemConstants.EARTH_MEAN_RADIUS);
	}
	
	public void calculateDistances(double meanRadius)
	{
		double lat1 = getHeader().getyLowerLeft();
		double lon1 = getHeader().getxLowerLeft();
		double lat2 = lat1 + getHeader().getCellSize();
		double lon2 = lon1 + getHeader().getCellSize();
		double R = meanRadius;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		

		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon/2) * Math.sin(dLon/2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = R * c * 1000;
		setResolution(d);
	}
	
	
	public void calculateElevationMinMax() throws DataSourceException
	{
		setMaxElevation(-50000);
		setMinElevation(50000);

		double elevation = 0;
		
		int rows = getHeader().getRows();
		int columns = getHeader().getColumns();
		double nodata = getHeader().getNoData();
		
		double max = getMaxElevation();
		double min = getMinElevation();
		
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				elevation = getElevation(row, col);
					
				if (elevation == nodata || elevation == DemConstants.ELEV_NO_DATA)
					continue;

				if (elevation > max) {
					max = elevation;
				} 
				if (elevation < min) {
					min = elevation;
				}
			}
		}
		
		setMaxElevation(max);
		setMinElevation(min);
	}
	
	/** Gets the length of the data file in bytes.
	 * 
	 * @return The length of the data file in bytes.
	 */
	public abstract long getDataLength();
	
	public boolean setDataPrecached(boolean precached)
	{
		return false;
	}
	
	public void load(double[] valueBuffer, int start, int length) throws DataSourceException
	{
		throw new DataSourceException("Not implemented");
	}
	
	public abstract void loadRow(int row) throws DataSourceException;
	public abstract void initDataCache() throws DataSourceException;
	public abstract void unloadDataCache() throws DataSourceException;
	
	public abstract double getElevation(int column)  throws DataSourceException;
	public abstract double getElevation(int row, int column)  throws DataSourceException;
	
	public abstract String getFilePath();
	public abstract DataSourceHeader getHeader();
	
	protected abstract void setMaxCol(int maxCol);
	public abstract int getMaxCol();
	protected abstract void setMaxRow(int maxRow);
	public abstract int getMaxRow();

	
	protected abstract void setMaxElevation(double maxElevation);
	public abstract double getMaxElevation();
	
	protected abstract void setMinElevation(double minElevation);
	public abstract double getMinElevation();
	
	protected abstract void setResolution(double resolution);
	public abstract double getResolution();

	public abstract DataSource copy();
	public abstract void dispose() throws DataSourceException;
}

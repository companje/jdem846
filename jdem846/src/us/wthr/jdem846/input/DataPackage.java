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

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.shapefile.Box;
import us.wthr.jdem846.shapefile.ShapeBase;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.shapefile.exception.ShapeFileException;

public class DataPackage
{
	private static Log log = Logging.getLog(DataPackage.class);
	
	private float avgXDim;
	private float avgYDim;
	
	private float maxLongitude;
	private float minLongitude;
	private float longitudeWidth;
	private float centerLongitude;
	
	private float maxLatitude;
	private float minLatitude;
	private float latitudeHeight;
	private float centerLatitude;
	
	private float rows;
	private float columns;
	private int halfRows;
	private int halfColumns;
	
	private float maxElevation;
	private float minElevation;
	
	private float noData;
	
	private float averageResolution;
	
	private List<DataSource> dataSources = new LinkedList<DataSource>();
	private List<DataBounds> dataBounds = new LinkedList<DataBounds>();
	
	private List<ShapeFileRequest> shapeFiles = new LinkedList<ShapeFileRequest>();
	
	private PackagedReader[] packagedReaderArray;
	private String prepareFilePath;
	
	private boolean isDisposed = false;
	
	public DataPackage()
	{
		
	}
	
	public DataPackage(String prepareFilePath)
	{
		this.prepareFilePath = prepareFilePath;
		
		
	}
	
	public void addDataSource(DataSource dataSource)
	{
		this.dataSources.add(dataSource);
	}
	
	public DataSource removeDataSource(int index)
	{
		return this.dataSources.remove(index);
	}
	
	public boolean removeDataSource(DataSource dataSource)
	{
		return this.dataSources.remove(dataSource);
	}
	
	public int getDataSourceCount()
	{
		return dataSources.size();
	}
	
	public void reset()
	{
		//this.packagedReaders.clear();
		this.packagedReaderArray = null;
		this.dataBounds.clear();
		this.avgXDim = 0;
		this.avgYDim = 0;
		this.maxLatitude = -91;
		this.minLatitude = 91;
		this.maxLongitude = -361;
		this.minLongitude = 361;
		this.centerLongitude = 0;
		this.centerLatitude = 0;
		this.longitudeWidth = 0;
		this.latitudeHeight = 0;
		this.maxElevation = -50000;
		this.minElevation = 50000;
		this.averageResolution = 0;
	}
	
	public void calculateElevationMinMax(boolean full) throws DataSourceException
	{
		log.info("Calculating elevation minimums & Maximums");
		
		this.maxElevation = -50000;
		this.minElevation = 50000;
		
		for (DataSource dataSource : this.dataSources) {
			
			if (full)
				dataSource.calculateElevationMinMax();
			
			//System.out.println("Min/Max: " + dataSource.getMinElevation() + "/" + dataSource.getMaxElevation());
			if (dataSource.getMaxElevation() > maxElevation)
				maxElevation = dataSource.getMaxElevation();

			if (dataSource.getMinElevation() < minElevation)
				minElevation = dataSource.getMinElevation();
		}
		log.info("Min/Max: " + getMinElevation() + "/" + getMaxElevation());
		
	}
	
	public void prepare()
	{
		
		reset();
		List<PackagedReader> packagedReaders = new LinkedList<PackagedReader>();
		
		if (this.dataSources.size() == 0 && this.shapeFiles.size() > 0) {
			
			minLongitude = 360;
			maxLongitude = -360;
			minLatitude = 90;
			maxLatitude = -90;
			
			for (ShapeFileRequest shapeFileRequest : shapeFiles) {
				
				ShapeBase shapeBase;
				try {
					shapeBase = shapeFileRequest.open();
					Box box = shapeBase.getBounds();
					
					if (box.getxMax() > maxLongitude)
						maxLongitude = (float) box.getxMax();
					if (box.getxMin() < minLongitude)
						minLongitude = (float) box.getxMin();
					if (box.getyMax() > maxLatitude)
						maxLatitude  = (float) box.getyMax();
					if (box.getyMin() < minLatitude)
						minLatitude = (float) box.getyMin();

					shapeBase.close();
				} catch (Exception ex) {
					log.info("Failed to open shape file for preparing data package: " + ex.getMessage(), ex);
					ex.printStackTrace();
				}
				
				columns = 5000;
				rows = 5000;
				halfColumns = 2500;
				halfRows = 2500;

				noData = DemConstants.ELEV_NO_DATA;
				
				this.longitudeWidth = this.maxLongitude - this.minLongitude;
				this.centerLongitude = (this.maxLongitude + this.minLongitude) / 2.0f;
				avgXDim = this.longitudeWidth / columns;

				this.latitudeHeight = this.maxLatitude - this.minLatitude;
				this.centerLatitude = (this.maxLatitude + this.minLatitude) / 2.0f;
				avgYDim = this.latitudeHeight / rows;
	
				averageResolution = (avgXDim + avgYDim) / 2.0f;
				int blah = 0;
			}
			
			
			// Determine bounds based on just shapefiles (if available)
			
		} else if (this.dataSources.size() == 1) {
			DataSource dataSource = dataSources.get(0);

			this.avgXDim = dataSource.getHeader().getCellSize();
			this.avgYDim = dataSource.getHeader().getCellSize();
			
			this.averageResolution = dataSource.getResolution();
			this.columns = dataSource.getHeader().getColumns();
			this.rows = dataSource.getHeader().getRows();
			this.halfColumns = (int) Math.round(columns / 2.0f);
			this.halfRows = (int) Math.round(rows / 2.0f);
			this.noData = dataSource.getHeader().getNoData();
			
			this.minLongitude = dataSource.getHeader().getxLowerLeft();
			this.maxLongitude = dataSource.getHeader().getxLowerLeft() + (dataSource.getHeader().getColumns() * dataSource.getHeader().getCellSize());
			
			this.minLatitude = dataSource.getHeader().getyLowerLeft();
			this.maxLatitude = dataSource.getHeader().getyLowerLeft() + (dataSource.getHeader().getRows() * dataSource.getHeader().getCellSize());
			
			this.longitudeWidth = this.maxLongitude - this.minLongitude;
			this.centerLongitude = (this.maxLongitude + this.minLongitude) / 2.0f;
			this.columns = this.longitudeWidth / this.avgXDim;
			
			this.latitudeHeight = this.maxLatitude - this.minLatitude;
			this.centerLatitude = (this.maxLatitude + this.minLatitude) / 2.0f;
			this.rows = this.latitudeHeight / this.avgYDim;
			
			PackagedReader pack = new PackagedReader(dataSource);
			pack.init(minLongitude, maxLongitude, minLatitude, maxLatitude, columns, rows, avgXDim, avgYDim);
			packagedReaders.add(pack);
			
			DataBounds bounds = new DataBounds((int)this.longitudeToColumn(pack.getLongitude()), (int) this.latitudeToRow(pack.getLatitude()), (int) pack.getColumns(), (int) pack.getRows());
			dataBounds.add(bounds);
		} else if (this.dataSources.size() > 1) {
			
			for (DataSource dataSource : this.dataSources) {
				this.avgXDim += dataSource.getHeader().getCellSize();
				this.avgYDim += dataSource.getHeader().getCellSize();
				this.averageResolution += dataSource.getResolution();
			}
			this.avgXDim = this.avgXDim / this.dataSources.size();
			this.avgYDim = this.avgYDim / this.dataSources.size();
			this.averageResolution = this.averageResolution / this.dataSources.size();
			

			for (DataSource dataSource : this.dataSources) {
				float nrows = (float) dataSource.getHeader().getRows();
				float ncols = (float) dataSource.getHeader().getColumns();

				float lon = dataSource.getHeader().getxLowerLeft();
				float rightLon = lon + (ncols * avgXDim);
				
				float bottomLat = dataSource.getHeader().getyLowerLeft();
				float lat = bottomLat + (nrows * avgYDim);

				if (rightLon > maxLongitude)
					maxLongitude = rightLon;
			
				if (lon < minLongitude)
					minLongitude = lon;

				if (lat > maxLatitude)
					maxLatitude = lat;
				
				if (bottomLat < minLatitude)
					minLatitude = bottomLat;
				
				

			}
		
			this.longitudeWidth = maxLongitude - minLongitude;
			this.centerLongitude = (maxLongitude + minLongitude) / 2.0f;
			this.columns = this.longitudeWidth / this.avgXDim;
			this.halfColumns = (int) Math.round(columns / 2.0f);
			
			this.latitudeHeight = maxLatitude - minLatitude;
			this.centerLatitude = (maxLatitude + minLatitude) / 2.0f;
			this.rows = this.latitudeHeight / this.avgYDim;
			this.halfRows = (int) Math.round(rows / 2.0f);
			
			for (DataSource dataSource : this.dataSources) {
				PackagedReader pack = new PackagedReader(dataSource);
				pack.init(minLongitude, maxLongitude, minLatitude, maxLatitude, columns, rows, avgXDim, avgYDim);
				packagedReaders.add(pack);
				DataBounds bounds = new DataBounds((int)this.longitudeToColumn(pack.getLongitude()), (int) this.latitudeToRow(pack.getLatitude()), (int) pack.getColumns(), (int) pack.getRows());
				dataBounds.add(bounds);
			}
			
			if (dataSources.size() > 0)
				this.noData = DemConstants.ELEV_NO_DATA;
			else
				this.noData = DemConstants.ELEV_NO_DATA;
		}
		
		packagedReaderArray = new PackagedReader[packagedReaders.size()];
		for (int i = 0; i < packagedReaderArray.length; i++) {
			packagedReaderArray[i] = packagedReaders.get(i);
		}
	}
	
	public float getElevation(int row, int col) throws DataSourceException
	{
		int testCol = (col - halfColumns);
		int testRow = (row - halfRows);

		
		int length = packagedReaderArray.length;
		for (int i = 0; i < length; i++) {
			PackagedReader pack = packagedReaderArray[i];
			if (pack.getColumnStart() <= testCol && pack.getColumnEnd() > testCol && pack.getRowStart() <= testRow && pack.getRowEnd() >= testRow) {
				return pack.getElevation(testRow, testCol);
			}
		}
		
		return DemConstants.ELEV_NO_DATA;
		
	}
	
	public float latitudeToRow(float latitude)
	{
		float latMinDiff = latitude - minLatitude;
		float pctDiff = latMinDiff / latitudeHeight;
		float row = Math.round(rows * (1.0 - pctDiff));
		return row;
	}
	
	
	public float longitudeToColumn(float longitude)
	{
		float lonMinDiff = longitude - minLongitude;
		float pctDiff = lonMinDiff / longitudeWidth;
		float col = Math.round(columns * pctDiff);
		return col;
	}
	
	public float rowToLatitude(float row)
	{
		float latAdj = row * avgYDim;
		return (maxLatitude - latAdj);
	}
	
	public float columnToLongitude(float column)
	{
		float lonAdj = column * avgXDim;
		return (minLongitude + lonAdj);

	}

	public boolean dataOverlaps(DataBounds bounds)
	{
		for (DataBounds inputBounds : dataBounds) {
			if (inputBounds.overlaps(bounds))
				return true;
		}
		return false;
	}
	
	public SubsetDataPackage getDataSubset(DataBounds bounds)
	{
		List<PackagedReader> packageReaderSubset = new LinkedList<PackagedReader>();
		
		for (PackagedReader packagedReader : this.packagedReaderArray) {
			DataBounds packageBounds = new DataBounds((int)this.longitudeToColumn(packagedReader.getLongitude()), (int) this.latitudeToRow(packagedReader.getLatitude()), (int) packagedReader.getColumns(), (int) packagedReader.getRows());
			if (packageBounds.overlaps(bounds)) {
				packageReaderSubset.add(packagedReader);
			}
		}
		
		
		BufferedSubsetDataPackage subset = new BufferedSubsetDataPackage(bounds.getTopY(), bounds.getLeftX(), (int)Math.round(this.rows), (int)Math.round(this.columns), bounds.getWidth(), bounds.getHeight());
		//SubsetDataPackage subset = new SubsetDataPackage((int)Math.round(this.rows), (int)Math.round(this.columns));
		subset.setPackagedReaders(packageReaderSubset);
		return subset;
	}
	
	public float getAvgXDim()
	{
		return avgXDim;
	}

	public void setAvgXDim(float avgXDim)
	{
		this.avgXDim = avgXDim;
	}

	public float getAvgYDim() 
	{
		return avgYDim;
	}

	public void setAvgYDim(float avgYDim) 
	{
		this.avgYDim = avgYDim;
	}

	public float getMaxLongitude()
	{
		return maxLongitude;
	}

	public void setMaxLongitude(float maxLongitude) 
	{
		this.maxLongitude = maxLongitude;
	}

	public float getMinLongitude()
	{
		return minLongitude;
	}

	public void setMinLongitude(float minLongitude) 
	{
		this.minLongitude = minLongitude;
	}

	public float getLongitudeWidth() 
	{
		return longitudeWidth;
	}

	public void setLongitudeWidth(float longitudeWidth) 
	{
		this.longitudeWidth = longitudeWidth;
	}

	public float getCenterLongitude()
	{
		return centerLongitude;
	}

	public void setCenterLongitude(float centerLongitude) 
	{
		this.centerLongitude = centerLongitude;
	}

	public float getMaxLatitude() 
	{
		return maxLatitude;
	}

	public void setMaxLatitude(float maxLatitude) 
	{
		this.maxLatitude = maxLatitude;
	}

	public float getMinLatitude() 
	{
		return minLatitude;
	}

	public void setMinLatitude(float minLatitude)
	{
		this.minLatitude = minLatitude;
	}

	public float getLatitudeHeight()
	{
		return latitudeHeight;
	}

	public void setLatitudeHeight(float latitudeHeight) 
	{
		this.latitudeHeight = latitudeHeight;
	}

	public float getCenterLatitude() 
	{
		return centerLatitude;
	}

	public void setCenterLatitude(float centerLatitude)
	{
		this.centerLatitude = centerLatitude;
	}

	public float getRows() 
	{
		return rows;
	}

	public void setRows(float rows) 
	{
		this.rows = rows;
	}

	public float getColumns() 
	{
		return columns;
	}

	public void setColumns(float columns) 
	{
		this.columns = columns;
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

	public float getNoData() 
	{
		return noData;
	}

	public void setNoData(float noData)
	{
		this.noData = noData;
	}

	public float getAverageResolution() 
	{
		return averageResolution;
	}

	public void setAverageResolution(float averageResolution) 
	{
		this.averageResolution = averageResolution;
	}

	public List<DataSource> getDataSources()
	{
		return dataSources;
	}

	public void setDataSources(List<DataSource> dataSources) 
	{
		this.dataSources = dataSources;
	}

	public PackagedReader[] getPackagedReaders() 
	{
		return packagedReaderArray;
	}

	public void setPackagedReaders(PackagedReader[] packagedReaderArray) 
	{
		this.packagedReaderArray = packagedReaderArray;
	}

	public String getPrepareFilePath() 
	{
		return prepareFilePath;
	}

	public void setPrepareFilePath(String prepareFilePath) 
	{
		this.prepareFilePath = prepareFilePath;
	}

	public List<DataBounds> getDataBounds() 
	{
		return dataBounds;
	}

	public void setDataBounds(List<DataBounds> dataBounds) 
	{
		this.dataBounds = dataBounds;
	}

	public void addShapeFile(String path, String shapeDataDefinitionId) throws ShapeFileException
	{
		shapeFiles.add(new ShapeFileRequest(path, shapeDataDefinitionId));
	}
	
	public void addShapeFile(ShapeFileRequest shapeFileRequest)
	{
		shapeFiles.add(shapeFileRequest);
	}
	
	public ShapeFileRequest removeShapeFile(int index)
	{
		return shapeFiles.remove(index);
	}
	
	public boolean removeShapeFile(ShapeFileRequest shapeFileRequest)
	{
		return shapeFiles.remove(shapeFileRequest);
	}
	
	public List<ShapeFileRequest> getShapeFiles()
	{
		return shapeFiles;
	}

	public void setShapeFiles(List<ShapeFileRequest> shapeFiles)
	{
		this.shapeFiles = shapeFiles;
	}
	
	
	
	
	public DataPackage copy()
	{
		DataPackage clone = new DataPackage();
		
		clone.avgXDim = this.avgXDim;
		clone.avgYDim = this.avgYDim;
		
		clone.maxLongitude = this.maxLongitude;
		clone.minLongitude = this.minLongitude;
		clone.longitudeWidth = this.longitudeWidth;
		clone.centerLongitude = this.centerLongitude;
		
		clone.maxLatitude = this.maxLatitude;
		clone.minLatitude = this.minLatitude;
		clone.latitudeHeight = this.latitudeHeight;
		clone.centerLatitude = this.centerLatitude;
		
		clone.rows = this.rows;;
		clone.columns = this.columns;
		clone.halfRows = this.halfRows;
		clone.halfColumns = this.halfColumns;
		
		clone.maxElevation = this.maxElevation;
		clone.minElevation = this.minElevation;
		
		clone.noData = this.noData;
		
		clone.averageResolution = this.averageResolution;
		
		clone.prepareFilePath = prepareFilePath;

		for (DataBounds bounds : this.dataBounds) {
			clone.dataBounds.add(bounds.copy());
		}
		
		clone.packagedReaderArray = new PackagedReader[this.packagedReaderArray.length];
		for (int i = 0; i < this.packagedReaderArray.length; i++) {
			PackagedReader packagedReader = this.packagedReaderArray[i];
			PackagedReader packagedReaderCopy = packagedReader.copy();
			DataSource dataSourceCopy = packagedReaderCopy.getDataSource();
			clone.packagedReaderArray[i] = packagedReaderCopy;
			clone.dataSources.add(dataSourceCopy);
		}

		for (ShapeFileRequest shapeFileRequest : shapeFiles) {
			clone.shapeFiles.add(shapeFileRequest.copy());
		}
		
		return clone;
	}
	
	public void dispose() throws DataSourceException
	{
		if (isDisposed) {
			throw new DataSourceException("Object already disposed of");
		}
		
		for (PackagedReader reader : packagedReaderArray) {
			reader.dispose();
		}
		
		dataSources.clear();
		packagedReaderArray = null;
		dataBounds.clear();
		
		for (ShapeFileRequest shapeFileRequest : shapeFiles) {
			shapeFileRequest.dispose();
		}
		shapeFiles.clear();
		

		isDisposed = true;
	}
}

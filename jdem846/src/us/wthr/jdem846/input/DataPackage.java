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

@Deprecated
public class DataPackage
{
	private static Log log = Logging.getLog(DataPackage.class);
	
	private double avgXDim;
	private double avgYDim;
	
	private double maxLongitude;
	private double minLongitude;
	private double longitudeWidth;
	private double centerLongitude;
	
	private double maxLatitude;
	private double minLatitude;
	private double latitudeHeight;
	private double centerLatitude;
	
	private double rows;
	private double columns;
	private int halfRows;
	private int halfColumns;
	
	private double maxElevation;
	private double minElevation;
	
	private double noData;
	
	private double averageResolution;
	
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
						maxLongitude = (double) box.getxMax();
					if (box.getxMin() < minLongitude)
						minLongitude = (double) box.getxMin();
					if (box.getyMax() > maxLatitude)
						maxLatitude  = (double) box.getyMax();
					if (box.getyMin() < minLatitude)
						minLatitude = (double) box.getyMin();

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
				double nrows = (double) dataSource.getHeader().getRows();
				double ncols = (double) dataSource.getHeader().getColumns();

				double lon = dataSource.getHeader().getxLowerLeft();
				double rightLon = lon + (ncols * avgXDim);
				
				double bottomLat = dataSource.getHeader().getyLowerLeft();
				double lat = bottomLat + (nrows * avgYDim);

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
	
	public double getElevation(int row, int col) throws DataSourceException
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
	
	public double latitudeToRow(double latitude)
	{
		double latMinDiff = latitude - minLatitude;
		double pctDiff = latMinDiff / latitudeHeight;
		double row = Math.round(rows * (1.0 - pctDiff));
		return row;
	}
	
	
	public double longitudeToColumn(double longitude)
	{
		double lonMinDiff = longitude - minLongitude;
		double pctDiff = lonMinDiff / longitudeWidth;
		double col = Math.round(columns * pctDiff);
		return col;
	}
	
	public double rowToLatitude(double row)
	{
		double latAdj = row * avgYDim;
		return (maxLatitude - latAdj);
	}
	
	public double columnToLongitude(double column)
	{
		double lonAdj = column * avgXDim;
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
	
	public double getAvgXDim()
	{
		return avgXDim;
	}

	public void setAvgXDim(double avgXDim)
	{
		this.avgXDim = avgXDim;
	}

	public double getAvgYDim() 
	{
		return avgYDim;
	}

	public void setAvgYDim(double avgYDim) 
	{
		this.avgYDim = avgYDim;
	}

	public double getMaxLongitude()
	{
		return maxLongitude;
	}

	public void setMaxLongitude(double maxLongitude) 
	{
		this.maxLongitude = maxLongitude;
	}

	public double getMinLongitude()
	{
		return minLongitude;
	}

	public void setMinLongitude(double minLongitude) 
	{
		this.minLongitude = minLongitude;
	}

	public double getLongitudeWidth() 
	{
		return longitudeWidth;
	}

	public void setLongitudeWidth(double longitudeWidth) 
	{
		this.longitudeWidth = longitudeWidth;
	}

	public double getCenterLongitude()
	{
		return centerLongitude;
	}

	public void setCenterLongitude(double centerLongitude) 
	{
		this.centerLongitude = centerLongitude;
	}

	public double getMaxLatitude() 
	{
		return maxLatitude;
	}

	public void setMaxLatitude(double maxLatitude) 
	{
		this.maxLatitude = maxLatitude;
	}

	public double getMinLatitude() 
	{
		return minLatitude;
	}

	public void setMinLatitude(double minLatitude)
	{
		this.minLatitude = minLatitude;
	}

	public double getLatitudeHeight()
	{
		return latitudeHeight;
	}

	public void setLatitudeHeight(double latitudeHeight) 
	{
		this.latitudeHeight = latitudeHeight;
	}

	public double getCenterLatitude() 
	{
		return centerLatitude;
	}

	public void setCenterLatitude(double centerLatitude)
	{
		this.centerLatitude = centerLatitude;
	}

	public double getRows() 
	{
		return rows;
	}

	public void setRows(double rows) 
	{
		this.rows = rows;
	}

	public double getColumns() 
	{
		return columns;
	}

	public void setColumns(double columns) 
	{
		this.columns = columns;
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

	public double getNoData() 
	{
		return noData;
	}

	public void setNoData(double noData)
	{
		this.noData = noData;
	}

	public double getAverageResolution() 
	{
		return averageResolution;
	}

	public void setAverageResolution(double averageResolution) 
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
		
		if (packagedReaderArray != null) {
			for (PackagedReader reader : packagedReaderArray) {
				reader.dispose();
			}
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

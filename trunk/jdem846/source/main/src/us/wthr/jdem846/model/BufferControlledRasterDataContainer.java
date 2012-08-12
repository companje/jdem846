package us.wthr.jdem846.model;

import java.util.List;

import us.wthr.jdem846.DataContext;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.exceptions.ContextPrepareException;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.rasterdata.RasterDataRowColumnBox;
import us.wthr.jdem846.scaling.ElevationScaler;

public class BufferControlledRasterDataContainer extends RasterDataContext
{
	private static Log log = Logging.getLog(BufferControlledRasterDataContainer.class);
	
	private RasterDataContext rasterDataContext;
	private String bufferStrategy;
	
	private boolean buffersFilled = false;
	
	protected double north;
	protected double south;
	protected double east;
	protected double west;
	
	protected double latitudeResolution;
	protected double tileHeight;
	protected double cacheHeight;
	protected double nextCachePoint;
	
	private boolean fullBuffering = false;
	private boolean tiledBuffering = false;
	
	
	public BufferControlledRasterDataContainer()
	{
		
		
	}
	
	public BufferControlledRasterDataContainer(RasterDataContext rasterDataContext, String bufferStrategy, double latitudeResolution, double tileHeight)
	{
		this.rasterDataContext = rasterDataContext;
		this.bufferStrategy = bufferStrategy;
		
		this.latitudeResolution = latitudeResolution; 
		this.tileHeight = tileHeight;
		
		this.fullBuffering = bufferStrategy.equalsIgnoreCase("full");
		this.tiledBuffering = bufferStrategy.equalsIgnoreCase("tiled");
		this.north = rasterDataContext.getNorth();
		this.south = rasterDataContext.getSouth();
		this.east = rasterDataContext.getEast();
		this.west = rasterDataContext.getWest();
		
		cacheHeight = latitudeResolution * tileHeight;
		nextCachePoint = north;
	}


	
	
	public void checkBuffer(double latitude, double longitude)
	{
		if (fullBuffering && !buffersFilled) {
			try {
				rasterDataContext.fillBuffers();
				buffersFilled = true;
			} catch (DataSourceException ex) {
				// TODO Add better handling
				log.warn("Error filling raster buffers: " + ex.getMessage(), ex);
			}
			
			
		}
		
		
		if (tiledBuffering) {
			if (latitude <= nextCachePoint) {
				
				double southCache = latitude - cacheHeight - latitudeResolution;
				try {
					clearBuffers();
					rasterDataContext.fillBuffers(latitude, southCache, east, west);
				} catch (Exception ex) {
					// TODO: Add better handling
					log.warn("Error filling raster buffers: " + ex.getMessage(), ex);
				}
				
				nextCachePoint = latitude - cacheHeight;
			}
		}
		
	}
	
	
	
	
	
	@Override
	public boolean dataOverlaps(RasterDataRowColumnBox bounds)
	{
		return rasterDataContext.dataOverlaps(bounds);
	}




	@Override
	public void fillBuffers() throws DataSourceException 
	{
		throw new DataSourceException("Buffers are handled automatically");
	}




	@Override
	public void fillBuffers(double north, double south, double east, double west) throws DataSourceException 
	{
		throw new DataSourceException("Buffers are handled automatically");
	}




	@Override
	public RasterDataContext copy() throws DataSourceException 
	{
		BufferControlledRasterDataContainer clone = new BufferControlledRasterDataContainer();
		super.copy(clone);
		
		
		clone.rasterDataContext = this.rasterDataContext;
		clone.bufferStrategy = this.bufferStrategy;
		
		clone.buffersFilled = this.buffersFilled;
		
		clone.north = this.north;
		clone.south = this.south;
		clone.east = this.east;
		clone.west = this.west;
		
		clone.latitudeResolution = this.latitudeResolution;
		clone.tileHeight = this.tileHeight;
		clone.cacheHeight = this.cacheHeight;
		clone.nextCachePoint = this.nextCachePoint;
		
		clone.fullBuffering = this.fullBuffering;
		clone.tiledBuffering = this.tiledBuffering;
		
		return clone;
	}




	public void clearBuffers() throws DataSourceException 
	{
		rasterDataContext.clearBuffers();
		buffersFilled = false;
	}
	
	
	public void prepare() throws ContextPrepareException 
	{
		rasterDataContext.prepare();
	}



	public void dispose() throws DataSourceException
	{
		rasterDataContext.dispose();
	}


	public boolean isDisposed()
	{
		return rasterDataContext.isDisposed();
	}


	public double getMetersResolution() 
	{
		return rasterDataContext.getMetersResolution();
	}


	public double getMetersResolution(double meanRadius) 
	{
		return rasterDataContext.getMetersResolution(meanRadius);
	}


	public void addRasterData(RasterData rasterData) throws DataSourceException 
	{
		rasterDataContext.addRasterData(rasterData);
	}


	public void removeRasterData(RasterData rasterData)
			throws DataSourceException 
	{
		rasterDataContext.removeRasterData(rasterData);
	}


	public RasterData removeRasterData(int index) throws DataSourceException 
	{
		return rasterDataContext.removeRasterData(index);
	}


	public List<RasterData> getRasterDataList() 
	{
		return rasterDataContext.getRasterDataList();
	}


	public int getRasterDataListSize() 
	{
		return rasterDataContext.getRasterDataListSize();
	}



	
	public List<RasterDataRowColumnBox> getRasterDataRowColumnBoxes() 
	{
		
		return rasterDataContext.getRasterDataRowColumnBoxes();
	}



	
	



	
	public double getData(double latitude, double longitude)
			throws DataSourceException 
	{
		checkBuffer(latitude, longitude);
		
		return rasterDataContext.getData(latitude, longitude);
	}



	
	public double getData(double latitude, double longitude, boolean scaled)
			throws DataSourceException 
	{
		checkBuffer(latitude, longitude);
		
		return rasterDataContext.getData(latitude, longitude, scaled);
	}



	
	public double getData(double latitude, double longitude,
			boolean avgOfAllRasterValues, boolean interpolate)
			throws DataSourceException 
	{
		checkBuffer(latitude, longitude);
		
		return rasterDataContext.getData(latitude, longitude, avgOfAllRasterValues, interpolate);
	}



	
	public double getData(double latitude, double longitude,
			boolean avgOfAllRasterValues, boolean interpolate, boolean scaled)
			throws DataSourceException 
	{
		checkBuffer(latitude, longitude);
		
		return rasterDataContext.getData(latitude, longitude, avgOfAllRasterValues, interpolate,
				scaled);
	}



	
	public double getDataStandardResolution(double latitude, double longitude,
			boolean avgOfAllRasterValues, boolean interpolate)
			throws DataSourceException 
	{
		checkBuffer(latitude, longitude);
		
		return rasterDataContext.getDataStandardResolution(latitude, longitude,
				avgOfAllRasterValues, interpolate);
	}



	
	public double getDataStandardResolution(double latitude, double longitude,
			boolean avgOfAllRasterValues, boolean interpolate, boolean scaled)
			throws DataSourceException 
	{
		checkBuffer(latitude, longitude);
		
		return rasterDataContext.getDataStandardResolution(latitude, longitude,
				avgOfAllRasterValues, interpolate, scaled);
	}



	
	public double getDataAtEffectiveResolution(double latitude,
			double longitude, boolean avgOfAllRasterValues, boolean interpolate)
			throws DataSourceException
	{
		checkBuffer(latitude, longitude);
		
		return rasterDataContext.getDataAtEffectiveResolution(latitude, longitude,
				avgOfAllRasterValues, interpolate);
	}



	
	public double getDataAtEffectiveResolution(double latitude,
			double longitude, boolean avgOfAllRasterValues,
			boolean interpolate, boolean scaled) throws DataSourceException 
	{
		checkBuffer(latitude, longitude);
		
		return rasterDataContext.getDataAtEffectiveResolution(latitude, longitude,
				avgOfAllRasterValues, interpolate, scaled);
	}



	
	public RasterDataContext getSubSet(double north, double south, double east,
			double west) throws DataSourceException 
	{
		
		return rasterDataContext.getSubSet(north, south, east, west);
	}



	
	public int latitudeToRow(double latitude) 
	{
		
		return rasterDataContext.latitudeToRow(latitude);
	}



	
	public double rowToLatitude(int row) 
	{
		
		return rasterDataContext.rowToLatitude(row);
	}



	
	public int longitudeToColumn(double longitude) 
	{
		
		return rasterDataContext.longitudeToColumn(longitude);
	}



	
	public double columnToLongitude(int column) 
	{
		
		return rasterDataContext.columnToLongitude(column);
	}



	
	public double getNorth() 
	{
		
		return rasterDataContext.getNorth();
	}



	
	public double getSouth() 
	{
		
		return rasterDataContext.getSouth();
	}



	
	public double getEast() 
	{
		
		return rasterDataContext.getEast();
	}



	
	public double getWest() 
	{
		
		return rasterDataContext.getWest();
	}



	
	public int getDataRows()
	{
		
		return rasterDataContext.getDataRows();
	}



	
	public int getDataRows(double north, double south)
	{
		
		return rasterDataContext.getDataRows(north, south);
	}



	
	public int getDataColumns() 
	{
		
		return rasterDataContext.getDataColumns();
	}



	
	public int getDataColumns(double east, double west)
	{
		
		return rasterDataContext.getDataColumns(east, west);
	}



	
	public double getLatitudeResolution() 
	{
		
		return rasterDataContext.getLatitudeResolution();
	}



	
	public void setLatitudeResolution(double latitudeResolution)
	{
		
		rasterDataContext.setLatitudeResolution(latitudeResolution);
	}



	
	public double getLongitudeResolution()
	{
		
		return rasterDataContext.getLongitudeResolution();
	}



	
	public void setLongitudeResolution(double longitudeResolution) 
	{
		
		rasterDataContext.setLongitudeResolution(longitudeResolution);
	}



	
	public double getDataMinimumValue() 
	{
		
		return rasterDataContext.getDataMinimumValue();
	}



	
	public void setDataMinimumValue(double dataMinimumValue) 
	{
		
		rasterDataContext.setDataMinimumValue(dataMinimumValue);
	}



	
	public double getDataMaximumValue() 
	{
		
		return rasterDataContext.getDataMaximumValue();
	}



	
	public double getDataMaximumValueTrue() 
	{
		
		return rasterDataContext.getDataMaximumValueTrue();
	}



	
	public void setDataMaximumValue(double dataMaximumValue)
	{
		
		rasterDataContext.setDataMaximumValue(dataMaximumValue);
	}



	
	public double getEffectiveLatitudeResolution()
	{
		
		return rasterDataContext.getEffectiveLatitudeResolution();
	}



	
	public void setEffectiveLatitudeResolution(
			double effectiveLatitudeResolution) 
	{
		
		rasterDataContext.setEffectiveLatitudeResolution(effectiveLatitudeResolution);
	}



	
	public double getEffectiveLongitudeResolution() 
	{
		
		return rasterDataContext.getEffectiveLongitudeResolution();
	}



	
	public void setEffectiveLongitudeResolution(
			double effectiveLongitudeResolution)
	{
		
		rasterDataContext.setEffectiveLongitudeResolution(effectiveLongitudeResolution);
	}



	
	public ElevationScaler getElevationScaler() 
	{
		
		return rasterDataContext.getElevationScaler();
	}



	
	public void setElevationScaler(ElevationScaler elevationScaler) 
	{
		
		rasterDataContext.setElevationScaler(elevationScaler);
	}



	
	public boolean isAvgOfAllRasterValues() 
	{
		
		return rasterDataContext.isAvgOfAllRasterValues();
	}



	
	public void setAvgOfAllRasterValues(boolean avgOfAllRasterValues)
	{
		
		rasterDataContext.setAvgOfAllRasterValues(avgOfAllRasterValues);
	}



	
	public boolean getInterpolate() 
	{
		
		return rasterDataContext.getInterpolate();
	}



	
	public void setInterpolate(boolean interpolate)
	{
		
		rasterDataContext.setInterpolate(interpolate);
	}



	
	public boolean isScaled() 
	{
		
		return rasterDataContext.isScaled();
	}



	
	public void setScaled(boolean scaled) 
	{
		
		rasterDataContext.setScaled(scaled);
	}
	
	
}

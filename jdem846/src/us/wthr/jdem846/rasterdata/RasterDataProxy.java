package us.wthr.jdem846.rasterdata;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class RasterDataProxy 
{
	private static Log log = Logging.getLog(RasterDataProxy.class);
	
	private List<RasterData> rasterDataList = new LinkedList<RasterData>();
	
	private double east = 180.0;
	private double west = -180.0;
	private double north = 90.0;
	private double south = -90.0;
	
	private double latitudeResolution;
	private double longitudeResolution;
	
	private double dataMinimumValue = 0;
	private double dataMaximumValue = 0;
	
	public RasterDataProxy()
	{
		
	}
	
	public void prepare() throws DataSourceException
	{
		
		east = -180.0;
		west = 180.0;
		north = -90.0;
		south = 90.0;
		
		latitudeResolution = Double.MAX_VALUE;
		longitudeResolution = Double.MAX_VALUE;
		
		for (RasterData rasterData : rasterDataList) {
			
			if (rasterData.getNorth() > north)
				north = rasterData.getNorth();
			
			if (rasterData.getSouth() < south)
				south = rasterData.getSouth();
			
			if (rasterData.getEast() > east)
				east = rasterData.getEast();
			
			if (rasterData.getWest() < west)
				west = rasterData.getWest();
			
			if (rasterData.getLatitudeResolution() < latitudeResolution)
				latitudeResolution = rasterData.getLatitudeResolution();
			
			if (rasterData.getLongitudeResolution() < longitudeResolution)
				longitudeResolution = rasterData.getLongitudeResolution();
			
		}
		

		log.info("Prepared RasterDataProxy to region N/S/E/W: " + north + "/" + south + "/" + east + "/" + west);
		log.info("Prepared RasterDataProxy to lat/long resolutions: " + latitudeResolution + "/" + longitudeResolution);
		
	}
	
	public void calculateElevationMinMax() throws DataSourceException
	{
		log.info("Calculating elevation minimums & Maximums");
		
		dataMinimumValue = Double.MAX_VALUE;
		dataMaximumValue = Double.MIN_VALUE;
		
		for (RasterData rasterData : rasterDataList) {
			rasterData.calculateMinAndMax();
			
			if (rasterData.getDataMinimum() < dataMinimumValue)
				dataMinimumValue = rasterData.getDataMinimum();
			
			if (rasterData.getDataMaximum() > dataMaximumValue)
				dataMaximumValue = rasterData.getDataMaximum();
			
		}
		
	}
	
	public void addRasterData(RasterData rasterData) throws DataSourceException
	{
		rasterDataList.add(rasterData);
		prepare();
	}

	public void removeRasterData(RasterData rasterData) throws DataSourceException
	{
		if (rasterDataList.remove(rasterData)) {
			prepare();
		}
	}
	
	public List<RasterData> getRasterDataList()
	{
		return rasterDataList;
	}
	
	public int getRasterDataListSize()
	{
		return rasterDataList.size();
	}
	
	public void fillBuffers(double north, double south, double east, double west) throws DataSourceException
	{
		int fillCount = 0;
		for (RasterData rasterData : rasterDataList) {
			if (rasterData.fillBuffer(north, south, east, west)) {
				fillCount++;
			}
		}

		log.info("" + fillCount + " data rasters matched the caching range");
		
	}
	
	public void clearBuffers() throws DataSourceException
	{
		for (RasterData rasterData : rasterDataList) {
			rasterData.clearBuffer();
		}
	}
	
	

	public double getData(double latitude, double longitude) throws DataSourceException
	{
		double value = DemConstants.ELEV_NO_DATA;
		
		for (RasterData rasterData : rasterDataList) {
			if (rasterData.contains(latitude, longitude)) {
				value = rasterData.getData(latitude, longitude);
				break;
			}
		}
		
		return value;
	}

	
	public RasterDataProxy getSubSet(double north, double south, double east, double west) throws DataSourceException
	{
		RasterDataLatLongBox subsetLatLongBox = new RasterDataLatLongBox(north, south, east, west);
		// TODO: This isn't working properly.

		RasterDataProxy newDataProxy = new RasterDataProxy();
		
		for (RasterData rasterData : rasterDataList) {
			if (rasterData.intersects(subsetLatLongBox)) {
				newDataProxy.getRasterDataList().add(rasterData);
			}
			
		}
		
		newDataProxy.prepare();

		
		return newDataProxy;
	}
	
	
	public int latitudeToRow(double latitude)
	{
		// Nearest neighbor
		return (int) Math.floor((north - latitude) / this.getLatitudeResolution());
	}
	
	public double rowToLatitude(int row)
	{
		return (north - ((double)row * this.getLatitudeResolution()));
	}
	
	public int longitudeToColumn(double longitude)
	{
		// Nearest neighbor
		return (int) Math.floor((longitude - west) / this.getLongitudeResolution());
	}
	
	public double columnToLongitude(int column)
	{
		return west + ((double)column * this.getLongitudeResolution());
	}
	
	
	public double getNorth()
	{
		return north; 
	}
	
	public double getSouth()
	{
		return south;
	}
	
	public double getEast()
	{
		return east;
	}
	
	public double getWest()
	{
		return west;
	}
	
	public int getDataRows()
	{
		return (int) Math.floor((north - south) / this.getLatitudeResolution());
	}
	
	public int getDataColumns()
	{
		return (int) Math.floor((east - west) / this.getLongitudeResolution());
	}
	
	public double getLatitudeResolution() 
	{
		return latitudeResolution;
	}

	public double getLongitudeResolution() 
	{
		return longitudeResolution;
	}

	public double getDataMinimumValue()
	{
		return dataMinimumValue;
	}

	public double getDataMaximumValue()
	{
		return dataMaximumValue;
	}
	
	
}

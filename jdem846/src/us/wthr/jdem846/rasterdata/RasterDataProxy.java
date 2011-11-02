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
	
	public RasterDataProxy()
	{
		
	}
	
	public void prepare() throws DataSourceException
	{
		
		east = Double.MIN_VALUE;
		west = Double.MAX_VALUE;
		north = Double.MIN_VALUE;
		south = Double.MAX_VALUE;
		
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
		
		RasterDataProxy newDataProxy = new RasterDataProxy();
		
		for (RasterData rasterData : rasterDataList) {
			if (rasterData.intersects(subsetLatLongBox)) {
				newDataProxy.getRasterDataList().add(rasterData);
			}
			
		}
		
		newDataProxy.prepare();
		
		return newDataProxy;
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
	
	
	public double getLatitudeResolution() 
	{
		return latitudeResolution;
	}

	public double getLongitudeResolution() 
	{
		return longitudeResolution;
	}
}

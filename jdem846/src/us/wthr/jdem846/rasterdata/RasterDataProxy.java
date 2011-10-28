package us.wthr.jdem846.rasterdata;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class RasterDataProxy 
{
	private static Log log = Logging.getLog(RasterDataProxy.class);
	
	private List<RasterData> rasterDataList = new LinkedList<RasterData>();
	
	
	public RasterDataProxy()
	{
		
	}
	
	public void prepare() throws DataSourceException
	{
		
	}
	
	public double getNorth()
	{
		return -1; // TODO: 
	}
	
	public double getSouth()
	{
		return -1; // TODO: 
	}
	
	public double getEast()
	{
		return -1; // TODO: 
	}
	
	public double getWest()
	{
		return -1; // TODO: 
	}
	
	
	public double getData(double latitude, double longitude) throws DataSourceException
	{
		return -1;
	}
	
	public RasterDataProxy getSubSet(double north, double south, double east, double west)
	{
		return null;
	}
	
}

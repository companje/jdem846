package us.wthr.jdem846.orthoimagery;

import us.wthr.jdem846.DataContext;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class OrthoimageryContext implements DataContext
{
	private static Log log = Logging.getLog(OrthoimageryContext.class);
	
	private double east = 180.0;
	private double west = -180.0;
	private double north = 90.0;
	private double south = -90.0;
	
	private double latitudeResolution;
	private double longitudeResolution;
	
	private boolean isDisposed = false;
	
	public OrthoimageryContext()
	{
		
	}

	@Override
	public void prepare() throws DataSourceException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Orthoimagery data proxy already disposed.");
		}
		
		// TODO: Finish
		isDisposed = true;
	}

	@Override
	public boolean isDisposed()
	{
		return isDisposed;
	}

	@Override
	public DataContext copy() throws DataSourceException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getNorth()
	{
		return north;
	}

	@Override
	public double getSouth()
	{
		return south;
	}

	@Override
	public double getEast()
	{
		return east;
	}

	@Override
	public double getWest()
	{
		return west;
	}
	
	
	
}

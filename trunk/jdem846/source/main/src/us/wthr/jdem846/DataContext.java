package us.wthr.jdem846;

import us.wthr.jdem846.exception.DataSourceException;

/**
 * 
 * @author Kevin M. Gill
 *
 */
public interface DataContext extends Context
{
	
	
	
	public double getNorth();
	public double getSouth();
	public double getEast();
	public double getWest();
	
}

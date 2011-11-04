package us.wthr.jdem846;

import us.wthr.jdem846.exception.DataSourceException;

/**
 * 
 * @author Kevin M. Gill
 *
 */
public interface DataContext
{
	
	public void prepare() throws DataSourceException;
	public void dispose() throws DataSourceException;
	public boolean isDisposed();
	public DataContext copy() throws DataSourceException;
	
	public double getNorth();
	public double getSouth();
	public double getEast();
	public double getWest();
	
}

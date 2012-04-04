package us.wthr.jdem846.input.netcdf;

import ucar.nc2.NetcdfFile;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.input.DataCache;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@Deprecated
public class NetCdfDataCache extends DataCache
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(NetCdfDataCache.class);
	
	@SuppressWarnings("unused")
	private boolean isDisposed = false;
	
	@SuppressWarnings("unused")
	private NetcdfFile ncfile = null;
	
	
	public NetCdfDataCache(NetcdfFile ncfile)
	{
		this.ncfile = ncfile;
	}
	
	@Override
	public long getDataLength()
	{
		
		return 0;
	}

	@Override
	public double get(int position) throws DataSourceException
	{
		
		return 0;
	}

	@Override
	public void load(long start) throws DataSourceException
	{
		
		
	}

	@Override
	public void unload()
	{
		
		
	}

	@Override
	public boolean isLoaded()
	{
		
		return false;
	}

	@Override
	public void setLoaded(boolean isLoaded)
	{
		
		
	}

	@Override
	public void dispose() throws DataSourceException
	{
		
		
	}
	
	
	
	
}

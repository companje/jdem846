package us.wthr.jdem846.input.netcdf;

import ucar.nc2.NetcdfFile;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.input.DataCache;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@Deprecated
public class NetCdfDataCache extends DataCache
{
	private static Log log = Logging.getLog(NetCdfDataCache.class);
	
	private boolean isDisposed = false;
	private NetcdfFile ncfile = null;
	
	
	public NetCdfDataCache(NetcdfFile ncfile)
	{
		this.ncfile = ncfile;
	}
	
	@Override
	public long getDataLength()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double get(int position) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void load(long start) throws DataSourceException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unload()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLoaded()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setLoaded(boolean isLoaded)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() throws DataSourceException
	{
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}

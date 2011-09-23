package us.wthr.jdem846.input.netcdf;

import java.io.IOException;

import ucar.nc2.NetcdfFile;
import us.wthr.jdem846.annotations.ElevationDataLoader;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.input.DataSource;
import us.wthr.jdem846.input.DataSourceHeader;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@ElevationDataLoader(name="NetCDF", identifier="netcdf", extension="nc", enabled=false)
public class NetCdf extends DataSource
{
	private static Log log = Logging.getLog(NetCdf.class);
	
	private String filePath;
	private NetcdfFile ncfile = null;
	
	private NetCdfDataCache cache = null;
	private NetCdfHeader header = null;
	
	private boolean isDisposed = false;
	
	public NetCdf(String filePath)
	{
		this.filePath = filePath;
		
		try {
			ncfile = NetcdfFile.open(filePath);

			header = new NetCdfHeader(ncfile);
			cache = new NetCdfDataCache(ncfile);
		} catch (IOException ex) {
			log.error("Failed to read NetCDF file: " + ex.getMessage(), ex);
		} finally { 
			if (ncfile != null) {
				try {
					ncfile.close();
				} catch (IOException ex) {
					log.error("Failed to close NetCDF file: " + ex.getMessage(), ex);
				}
			}
		}
		
		this.calculateDistances();
	}

	@Override
	public long getDataLength()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void loadRow(int row) throws DataSourceException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initDataCache() throws DataSourceException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unloadDataCache() throws DataSourceException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getElevation(int column) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getElevation(int row, int column) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getFilePath()
	{
		return filePath;
	}

	@Override
	public DataSourceHeader getHeader()
	{
		return header;
	}

	@Override
	protected void setMaxCol(int maxCol)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxCol()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void setMaxRow(int maxRow)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxRow()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void setMaxElevation(float maxElevation)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getMaxElevation()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void setMinElevation(float minElevation)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getMinElevation()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void setResolution(float resolution)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getResolution()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DataSource copy()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() throws DataSourceException
	{
		if (isDisposed) {
			throw new DataSourceException("Object already disposed of");
		}
		
		cache.dispose();
		cache = null;
		
		isDisposed = true;
	}
	
	
	
}

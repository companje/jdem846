package us.wthr.jdem846.input.netcdf;

import java.io.IOException;

import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.input.DataSourceHeader;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@Deprecated
public class NetCdfHeader implements DataSourceHeader 
{
	private static Log log = Logging.getLog(NetCdfHeader.class);
	
	private NetcdfFile ncfile = null;
	
	public NetCdfHeader(NetcdfFile ncfile)
	{
		this.ncfile = ncfile;
	}
	
	protected Object getNcVariable(String name)
	{
		return null;
		/*
		Variable v = ncfile.findVariable(name);
		if (v == null) {
			return null;
		}
		try {
			Array a = v.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
	}
	

	@Override
	public ByteOrder getByteOrder()
	{
		
		return null;
	}

	@Override
	public double getCellSize()
	{
		
		return 0;
	}

	@Override
	public double getNoData()
	{
		
		return 0;
	}

	@Override
	public int getRows()
	{
		
		return 0;
	}

	@Override
	public int getColumns()
	{
		
		return 0;
	}

	@Override
	public double getxLowerLeft()
	{
		
		return 0;
	}

	@Override
	public double getyLowerLeft()
	{
		
		return 0;
	}
	
	
}

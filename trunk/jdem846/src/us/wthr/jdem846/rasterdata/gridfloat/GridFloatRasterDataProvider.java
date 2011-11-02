package us.wthr.jdem846.rasterdata.gridfloat;

import java.io.File;

import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.AbstractRasterDataProvider;

public class GridFloatRasterDataProvider extends AbstractRasterDataProvider
{
	private static Log log = Logging.getLog(GridFloatRasterDataProvider.class);
	
	private GridFloatHeader header;
	private File dataFile;
	
	private boolean isDisposed = false;
	
	public GridFloatRasterDataProvider()
	{
		
	}


	@Override
	public void create(String filePath) throws DataSourceException
	{
		dataFile = new File(filePath);
		if (!dataFile.exists()) {
			throw new DataSourceException("GridFloat data file not found at " + filePath);
		}
		
		String headerFilePath = filePath.replace(".flt", ".hdr");
		header = new GridFloatHeader(headerFilePath);
		
		this.setColumns(header.getColumns());
		this.setRows(header.getRows());
		
		this.setNorth(header.getyLowerLeft() + (header.getCellSize() * header.getRows()));
		this.setSouth(header.getyLowerLeft());
		this.setEast(header.getxLowerLeft() + (header.getCellSize() * header.getColumns()));
		this.setWest(header.getxLowerLeft());
		
		this.setLatitudeResolution(header.getCellSize());
		this.setLongitudeResolution(header.getCellSize());
		
		
	}


	@Override
	public void dispose() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Raster data provider already disposed.");
		}
		
		
		
		// TODO: Finish
	}

	@Override
	public boolean isDisposed()
	{
		return isDisposed;
	}
	

	@Override
	public double getData(double latitude, double longitude)
			throws DataSourceException
	{
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public double getData(int row, int column) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public boolean fillBuffer(double north, double south, double east,
			double west) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void clearBuffer() throws DataSourceException
	{
		// TODO Auto-generated method stub
		
	}
	
	
}

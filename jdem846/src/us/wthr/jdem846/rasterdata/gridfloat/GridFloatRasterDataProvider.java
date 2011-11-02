package us.wthr.jdem846.rasterdata.gridfloat;

import java.io.File;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.AbstractRasterDataProvider;
import us.wthr.jdem846.rasterdata.RasterDataLatLongBox;

public class GridFloatRasterDataProvider extends AbstractRasterDataProvider
{
	private static Log log = Logging.getLog(GridFloatRasterDataProvider.class);
	
	private GridFloatHeader header;
	private CachingGridFloatDataReader dataReader;
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
		
		dataReader = new CachingGridFloatDataReader(dataFile, header.getRows(), header.getColumns(), header.getByteOrder());
	
		log.info("Created GridFloat raster data provider...");
		log.info("    North/South: " + getNorth() + "/" + getSouth());
		log.info("    East/West: " + getEast() + "/" + getWest());
		log.info("    Rows/Columns: " + getRows() + "/" + getColumns());
		log.info("    Latitude Resolution: " + getLatitudeResolution());
		log.info("    Longitude Resolution: " + getLongitudeResolution());
	}

	
	public String getFilePath()
	{
		return dataFile.getAbsolutePath();
	}

	@Override
	public void dispose() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Raster data provider already disposed.");
		}
		
		if (!dataReader.isDisposed()) {
			dataReader.dispose();
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
	public double getData(int row, int column) throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Data raster provider has been disposed.");
		}
		
		double data = dataReader.get(row, column);
		
		if (data == header.getNoData())
			data = DemConstants.ELEV_NO_DATA;
		
		return data;
	}


	@Override
	public boolean fillBuffer(double north, double south, double east,
			double west) throws DataSourceException
	{
		
		RasterDataLatLongBox bufferBox = new RasterDataLatLongBox(north, south, east, west);
		if (!this.intersects(bufferBox))
			return false;
		
		// Adjust the range to fit what this data supports
		
		if (north > this.getNorth())
			north = this.getNorth();
		if (south < this.getSouth())
			south = this.getSouth();
		if (east > this.getEast())
			east = this.getEast();
		if (west < this.getWest())
			west = this.getWest();
		
		int x = this.latitudeToRow(north);
		int y = this.longitudeToColumn(west);

		// TODO: Too simplistic
		int columns = (int) Math.ceil((east - west) / this.getLongitudeResolution()) + 1;
		int rows = (int) Math.ceil((north - south) / this.getLatitudeResolution());
		
		log.info("Filling raster buffer...");
		boolean status = dataReader.fillBuffer(x, y, columns, rows);
		log.info("Filled raster buffer");
		
		return status;
	}


	@Override
	public void clearBuffer() throws DataSourceException
	{
		dataReader.clearBuffer();
	}
	
	
}

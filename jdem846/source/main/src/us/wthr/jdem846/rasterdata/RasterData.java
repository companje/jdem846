package us.wthr.jdem846.rasterdata;

import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.input.InputSourceData;

public interface RasterData extends InputSourceData {

	public void create(String file) throws DataSourceException;
	
	public void dispose() throws DataSourceException;
	public boolean isDisposed();
	
	public RasterData copy() throws DataSourceException;
	
	public String getFilePath();
	
	public double getLatitudeResolution();
	public double getLongitudeResolution();
	
	public double getMetersResolution();
	public double getDataMinimum();
	public double getDataMaximum();
	
	public double getNorth();
	public double getSouth();
	public double getEast();
	public double getWest();
	
	public boolean contains(double latitude, double longitude);
	public boolean intersects(RasterDataLatLongBox otherBox);
	
	public int getRows();
	public int getRows(double north, double south);
	
	public int getColumns();
	public int getColumns(double east, double west);
	
	public void calculateMinAndMax() throws DataSourceException;
	
	public double getData(double latitude, double longitude) throws DataSourceException;
	public double getData(double latitude, double longitude, boolean interpolate) throws DataSourceException;
	public double getData(int row, int column) throws DataSourceException;
	
	public boolean fillBuffer(double north, double south, double east, double west) throws DataSourceException;
	public boolean isBufferFilled();
	public void clearBuffer() throws DataSourceException;
	
}

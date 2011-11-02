package us.wthr.jdem846.rasterdata;

import us.wthr.jdem846.exception.DataSourceException;

public interface RasterData {

	public void create(String file) throws DataSourceException;
	
	public void dispose() throws DataSourceException;
	public boolean isDisposed();
	
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
	public int getColumns();
	
	public void calculatenMinAndMax() throws DataSourceException;
	
	public double getData(double latitude, double longitude) throws DataSourceException;
	public double getData(int row, int column) throws DataSourceException;
	public boolean fillBuffer(double north, double south, double east, double west) throws DataSourceException;
	public void clearBuffer() throws DataSourceException;
	
}

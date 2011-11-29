package us.wthr.jdem846.orthoimagery;

import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataLatLongBox;

public interface OrthoimageryData
{
	public void create(String file) throws DataSourceException;
	
	public void dispose() throws DataSourceException;
	public boolean isDisposed();
	
	public RasterData copy() throws DataSourceException;
	
	public String getFilePath();
	
	public double getLatitudeResolution();
	public double getLongitudeResolution();
	
	public double getNorth();
	public double getSouth();
	public double getEast();
	public double getWest();
	
	public boolean contains(double latitude, double longitude);
	public boolean intersects(RasterDataLatLongBox otherBox);
	
	public int getRows();
	public int getColumns();
	
	/*
	 * getData...
	 */
	
	public boolean fillBuffer(double north, double south, double east, double west) throws DataSourceException;
	public boolean isBufferFilled();
	public void clearBuffer() throws DataSourceException;
}

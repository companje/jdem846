package us.wthr.jdem846.modelgrid;

import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.model.ElevationHistogramModel;
import us.wthr.jdem846.model.ModelPoint;

public interface IModelGrid
{

	public abstract int[] getModelTexture() throws DataSourceException;

	public abstract void dispose();

	public abstract boolean isDisposed();

	public abstract void reset() throws DataSourceException;

	public abstract ModelPoint get(double latitude, double longitude) throws DataSourceException;

	public double getElevation(double latitude, double longitude) throws DataSourceException;

	public double getElevation(double latitude, double longitude, boolean basic) throws DataSourceException;

	public void setElevation(double latitude, double longitude, double elevation) throws DataSourceException;

	public void getRgba(double latitude, double longitude, int[] fill) throws DataSourceException;

	public int getRgba(double latitude, double longitude) throws DataSourceException;

	public void setRgba(double latitude, double longitude, int rgba) throws DataSourceException;

	public void setRgba(double latitude, double longitude, int[] rgba) throws DataSourceException;

	public ElevationHistogramModel getElevationHistogramModel() throws DataSourceException;

	public int getWidth();

	public int getHeight();

	public double getNorth();

	public double getSouth();

	public double getEast();

	public double getWest();

	public double getLatitudeResolution();

	public double getLongitudeResolution();

	public long getGridLength();

	public double getMinimum();

	public void setMinimum(double minimum);

	public double getMaximum();

	public void setMaximum(double maximum);
}

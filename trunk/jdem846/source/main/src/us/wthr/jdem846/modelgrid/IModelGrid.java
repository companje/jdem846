package us.wthr.jdem846.modelgrid;

import us.wthr.jdem846.buffers.IIntBuffer;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.graphics.IColor;
import us.wthr.jdem846.input.InputSourceData;
import us.wthr.jdem846.model.ElevationHistogramModel;

public interface IModelGrid extends InputSourceData
{

	public IIntBuffer getModelTexture() throws DataSourceException;

	public void dispose();

	public boolean isDisposed();

	public void reset() throws DataSourceException;

	public boolean isCompleted();

	public void setCompleted(boolean completed);

	public double getElevationByIndex(int index) throws DataSourceException;

	public void setElevationByIndex(int index, double elevation) throws DataSourceException;

	public double getElevation(double latitude, double longitude) throws DataSourceException;

	public double getElevation(double latitude, double longitude, boolean basic) throws DataSourceException;

	public void setElevation(double latitude, double longitude, double elevation) throws DataSourceException;

	public void getRgbaByIndex(int index, int[] fill) throws DataSourceException;

	public IColor getRgbaByIndex(int index) throws DataSourceException;

	public void setRgbaByIndex(int index, IColor rgba) throws DataSourceException;

	public void setRgbaByIndex(int index, int[] rgba) throws DataSourceException;

	public void getRgba(double latitude, double longitude, int[] fill) throws DataSourceException;

	public IColor getRgba(double latitude, double longitude) throws DataSourceException;
	
	public IColor getRgba(int x, int y) throws DataSourceException;
	
	
	public void setRgba(double latitude, double longitude, IColor rgba) throws DataSourceException;

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

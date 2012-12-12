package us.wthr.jdem846.modelgrid;

import us.wthr.jdem846.model.ElevationHistogramModel;
import us.wthr.jdem846.model.ModelPoint;

public interface IModelGrid
{

	public abstract int[] getModelTexture();

	public abstract void dispose();

	public abstract boolean isDisposed();

	public abstract void reset();

	public abstract ModelPoint get(double latitude, double longitude);

	public double getElevation(double latitude, double longitude);

	public double getElevation(double latitude, double longitude, boolean basic);

	public void setElevation(double latitude, double longitude, double elevation);

	public void getRgba(double latitude, double longitude, int[] fill);

	public int getRgba(double latitude, double longitude);

	public void setRgba(double latitude, double longitude, int rgba);

	public void setRgba(double latitude, double longitude, int[] rgba);

	public ElevationHistogramModel getElevationHistogramModel();

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

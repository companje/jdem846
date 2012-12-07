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

}

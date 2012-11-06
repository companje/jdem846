package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vector;

public class GlobalView extends AbstractView implements View
{
	
	private double dataMaximumValue = DemConstants.ELEV_UNDETERMINED;
	
	protected double elevScaler = -1;
	
	protected double getZoom()
	{
		return globalOptionModel.getViewAngle().getZoom();
	}
	
	protected double getElevationScaler()
	{
		if (elevScaler == -1) {
			elevScaler = radius() / radiusTrue();
		}
		return elevScaler;
	}
	
	protected double scaleElevation(double elevation)
	{
		return (elevation * getElevationScaler());
	}
	
	public void project(double latitude, double longitude, double elevation, Vector point)
	{
		double radius = radius() + scaleElevation(elevation);
		Spheres.getPoint3D(longitude, latitude, radius, point);
	}
	
	
	
	
	public double getZoomDistanceFromCenter()
	{
		//double zoom = globalOptionModel.getViewAngle().getZoom();
		//return (globalOptionModel.getEyeDistance() * (DemConstants.DEFAULT_GLOBAL_RADIUS / radiusTrue())) / zoom;
		return scaleElevation(globalOptionModel.getEyeDistance());
	}
	
	@Override
	public double radiusTrue()
	{
		double radius = 0;
		if (planet != null) {
			radius = planet.getMeanRadius() * 1000.0;
		} else {
			radius = DemConstants.EARTH_MEAN_RADIUS * 1000.0;
		}
		return radius * getZoom();
	}
	
	
	@Override
	public double radius()
	{
		return ((double)this.globalOptionModel.getWidth() / 2.0);
		//return getElevationScaler() * radiusTrue();
	}


	@Override
	public double horizFieldOfView()
	{
		return globalOptionModel.getFieldOfView();
	}

	@Override
	public double elevationFromSurface()
	{
		return getZoomDistanceFromCenter() - radius();
	}

	@Override
	public double nearClipDistance()
	{
		return elevationFromSurface();// - scaleElevation(modelContext.getRasterDataContext().getDataMaximumValue());
	}

	@Override
	public double farClipDistance()
	{
		double r = radius();
		double e = elevationFromSurface();

		double a = MathExt.sec_d(r / (r + e));
		double d = MathExt.cos(a) * r;
		double f = (r - d) + e;
		return f;
	}

	@Override
	public double eyeZ()
	{
		return getZoomDistanceFromCenter();
	}

}

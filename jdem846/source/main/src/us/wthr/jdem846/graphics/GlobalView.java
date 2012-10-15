package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vector;

public class GlobalView extends AbstractView implements View
{
	
	private double dataMaximumValue = DemConstants.ELEV_UNDETERMINED;
	
	public void project(double latitude, double longitude, double elevation, Vector point)
	{
		//this.radius() + elevation;
		
		//elevation = elevation * (0.5 / radiusTrue());
		double radius = DemConstants.DEFAULT_GLOBAL_RADIUS + (elevation * (DemConstants.DEFAULT_GLOBAL_RADIUS / radiusTrue()));
		
		Spheres.getPoint3D(longitude, latitude, radius, point);
		//point.z *= -1.0;
	}
	
	
	public double getZoomDistanceFromCenter()
	{
		double zoom = globalOptionModel.getViewAngle().getZoom();
		return (globalOptionModel.getEyeDistance() * (DemConstants.DEFAULT_GLOBAL_RADIUS / radiusTrue())) / zoom;
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
		return radius;
	}
	
	@Override
	public double radius()
	{
		return DemConstants.DEFAULT_GLOBAL_RADIUS;
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
		return elevationFromSurface();
	}

	@Override
	public double farClipDistance()
	{
		double r = radius();
		double e = elevationFromSurface();
		
		return MathExt.sin(MathExt.sec_d(r / (r + e))) * MathExt.sqrt(e * (e + 2 * r));
	}

	@Override
	public double eyeZ()
	{
		return getZoomDistanceFromCenter();
	}

}

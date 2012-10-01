package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vector;

public class GlobalView extends AbstractView implements View
{

	public void project(double latitude, double longitude, double elevation, Vector point)
	{
		double radius = this.radius() + elevation;
		Spheres.getPoint3D(longitude, latitude, radius, point);
	}
	
	@Override
	public double radius()
	{
		if (planet != null) {
			return planet.getMeanRadius() * 1000.0;
		} else {
			return DemConstants.EARTH_MEAN_RADIUS * 1000.0;
		}
	}

	@Override
	public double horizFieldOfView()
	{
		return DemConstants.DEFAULT_HORIZONTAL_FIELD_OF_VIEW;
	}

	@Override
	public double elevationFromSurface()
	{
		return DemConstants.DEFAULT_EYE_DISTANCE_FROM_EARTH;
	}

	@Override
	public double nearClipDistance()
	{
		double tha = MathExt.tan(MathExt.radians(horizFieldOfView()) * 0.5);
		return elevationFromSurface() / (2.0 * MathExt.sqrt(2.0 * tha * tha + 1.0));
	}

	@Override
	public double farClipDistance()
	{
		double elevationFromSurface = elevationFromSurface();
		return MathExt.sqrt(elevationFromSurface * (2.0 * radius() + elevationFromSurface));
	}

	@Override
	public double eyeZ()
	{
		double tha = MathExt.tan(MathExt.radians(horizFieldOfView()) * 0.5);
		return radius() / tha;
	}

}

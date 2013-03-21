package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Vector;

public class GlobalView extends AbstractView implements View
{
	
	private double dataMaximumValue = DemConstants.ELEV_UNDETERMINED;
	
	
	
	
	protected double getZoom()
	{
		return globalOptionModel.getViewAngle().getZoom();
	}
	

	
	public void project(double latitude, double longitude, double elevation, Vector point)
	{
		double scaledElevation = scaleElevation(elevation);
		this.getEllipsoid().getXyzCoordinates(latitude, longitude, scaledElevation, point);
		//Spheres.getPoint3D(longitude, latitude, radius, point);
		//Spheres.getPointEllipsoid3D(longitude, latitude, radius, planet.getFlattening(), point);
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
			radius = planet.getMeanRadius();
		} else {
			radius = DemConstants.EARTH_MEAN_RADIUS;
		}
		return radius * getZoom();
	}
	

	
		
	@Override
	public double radius()
	{	
		double radius = ((double)this.globalOptionModel.getWidth() / 2.0);
		return radius;
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
		
		double e = elevationFromSurface();
		if (modelContext.getRasterDataContext().getDataMaximumValue() > 0) {
			e -= scaleElevation(modelContext.getRasterDataContext().getDataMaximumValue());
		}
		return e;
		
		//return 1.0;
	}

	@Override
	public double farClipDistance()
	{
		double r = radius();
		double e = elevationFromSurface();
		double f = MathExt.sqrt(e * (2 * r + e));
		return f;
		/*
		double r = radius();
		double e = elevationFromSurface();

		double a = MathExt.sec_d(r / (r + e));
		double d = MathExt.cos(a) * r;
		double f = (r - d) + e;
		return f;
		
		//return 100000.0;
		 */
	}

	@Override
	public double eyeZ()
	{
		return getZoomDistanceFromCenter();
	}

}

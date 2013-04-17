package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Vector;

public class GlobalView extends AbstractView implements View
{
	
	private double dataMaximumValue = DemConstants.ELEV_UNDETERMINED;
	
	protected double getCameraDistanceToCenterOfObject()
	{
		//Vector objPos = globalOptionModel.getViewerPosition().getFocalPoint();
		//Vector camPos = globalOptionModel.getViewerPosition().getPosition();
		//double distance = objPos.getDistanceTo(camPos);
		//return distance;
		return globalOptionModel.getViewerPosition().getDistance();
	}
	
	
	protected double getZoom()
	{
		//return 2.0 / getCameraDistanceToCenterOfObject();
		return globalOptionModel.getViewerPosition().getScale();
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
		return scaleElevation(globalOptionModel.getEyeDistance() * (getCameraDistanceToCenterOfObject() / 2.0));
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
		double radius = 0.5;
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
	}

	@Override
	public double eyeZ()
	{
		return getZoomDistanceFromCenter();
	}

}

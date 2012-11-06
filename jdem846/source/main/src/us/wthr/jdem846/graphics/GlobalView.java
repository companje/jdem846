package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vector;

public class GlobalView extends AbstractView implements View
{
	
	private double dataMaximumValue = DemConstants.ELEV_UNDETERMINED;
	
	protected double elevScaler = -1;
	
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
		double zoom = globalOptionModel.getViewAngle().getZoom();
		return radius * zoom;
	}
	
	
	@Override
	public double radius()
	{
		double zoom = globalOptionModel.getViewAngle().getZoom();
		return 0.5 * zoom;
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
		//return elevationFromSurface();
		double elevationFromSurface = elevationFromSurface();
		double dataMaximumValue = modelContext.getRasterDataContext().getDataMaximumValue();
		double scaledDataMaximumValue = scaleElevation(dataMaximumValue);
		double near = elevationFromSurface - scaledDataMaximumValue;
		return near;
	}

	@Override
	public double farClipDistance()
	{
		//double r = radius();
		//double e = elevationFromSurface();
		
		//return MathExt.sin(MathExt.sec_d(r / (r + e))) * MathExt.sqrt(e * (e + 2 * r));
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

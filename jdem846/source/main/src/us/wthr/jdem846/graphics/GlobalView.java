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
		double radius = 0.5;//this.radius() + elevation;
		Spheres.getPoint3D(longitude, latitude, radius, point);
		point.z *= -1.0;
	}
	
	@Override
	public double radius()
	{
		double radius = 0;
		if (planet != null) {
			radius = planet.getMeanRadius() * 1000.0;
		} else {
			radius = DemConstants.EARTH_MEAN_RADIUS * 1000.0;
		}
		
		if (dataMaximumValue == DemConstants.ELEV_UNDETERMINED) {
			dataMaximumValue = modelContext.getRasterDataContext().getDataMaximumValue();
		}
		
		//return radius + (dataMaximumValue * globalOptionModel.getElevationMultiple());
		return 0.5;
	}

	@Override
	public double horizFieldOfView()
	{
		return DemConstants.DEFAULT_HORIZONTAL_FIELD_OF_VIEW;
	}

	@Override
	public double elevationFromSurface()
	{
		//return DemConstants.DEFAULT_EYE_DISTANCE_FROM_EARTH;
		return 3.0;
	}

	@Override
	public double nearClipDistance()
	{
		//double tha = MathExt.tan(MathExt.radians(horizFieldOfView()) * 0.5);
		//return elevationFromSurface() / (2.0 * MathExt.sqrt(2.0 * tha * tha + 1.0));
		return 0.5;
	}

	@Override
	public double farClipDistance()
	{
		double elevationFromSurface = elevationFromSurface();
		//return MathExt.sqrt(elevationFromSurface * (2.0 * radius() + elevationFromSurface));
		return -0.5;
	}

	@Override
	public double eyeZ()
	{
		//return radius() / MathExt.tan(MathExt.radians(horizFieldOfView()) * 0.5);
		return 2.0;
	}

}

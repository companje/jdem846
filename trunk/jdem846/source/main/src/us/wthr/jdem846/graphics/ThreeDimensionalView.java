package us.wthr.jdem846.graphics;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Vector;

public class ThreeDimensionalView extends AbstractView implements View
{

	public void project(double latitude, double longitude, double elevation, Vector point)
	{

		point.x = -(0.5 - longitudeToColumn(longitude));
		point.z = (0.5 - latitudeToRow(latitude));
		point.y = (0.5 / this.resolution) - ((maxElevation - elevation) / (maxElevation - minElevation) / this.resolution);
	}

	protected double latitudeToRow(double latitude)
	{
		return (((this.north - latitude) / (this.north - this.south)));
	}

	protected double longitudeToColumn(double longitude)
	{
		return (((longitude - this.west) / (this.east - this.west)));
	}

	@Override
	public double radiusTrue()
	{
		return 0.5;
	}

	@Override
	public double radius()
	{
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
		return -1;
	}

	@Override
	public double nearClipDistance()
	{
		return .1;//this.radius();
	}

	@Override
	public double farClipDistance()
	{
		return 1.0;//-this.radius();
	}

	@Override
	public double eyeZ()
	{
		double D = (radius() / 2.0) / MathExt.tan(MathExt.radians(horizFieldOfView() / 2.0));
		return D;
	}

}

package us.wthr.jdem846.render.scaling;

import us.wthr.jdem846.math.MathExt;

public class CubicScaler implements ElevationScaler
{

	@Override
	public double scale(double elevation, double min, double max)
	{
		double elevationScaled = min + (max - min) * (MathExt.cube((elevation - min)) / MathExt.cube(max - min));
		return elevationScaled;
	}

}

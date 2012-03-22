package us.wthr.jdem846.render.scaling;

public class LinearScaler implements ElevationScaler
{

	@Override
	public double scale(double elevation, double min, double max)
	{
		return elevation;
	}

}

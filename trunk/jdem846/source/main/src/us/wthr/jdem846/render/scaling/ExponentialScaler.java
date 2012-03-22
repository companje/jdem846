package us.wthr.jdem846.render.scaling;

import us.wthr.jdem846.math.MathExt;

public class ExponentialScaler implements ElevationScaler
{
	private double exponentDivisor = 10000.0;
	
	
	@Override
	public double scale(double elevation, double min, double max)
	{
		double range = max - min;
		double rangeExp = MathExt.pow(2, range/exponentDivisor);
		
		double elevExp = MathExt.pow(2, (elevation - min)/exponentDivisor);
		
		double elevationScaled = min + (range * elevExp / rangeExp);
		return elevationScaled;
	}

}

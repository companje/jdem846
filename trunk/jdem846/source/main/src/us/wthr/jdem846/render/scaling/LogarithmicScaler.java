package us.wthr.jdem846.render.scaling;

import us.wthr.jdem846.math.MathExt;

public class LogarithmicScaler implements ElevationScaler
{

	@Override
	public double scale(double elevation, double min, double max)
	{
		
		double range = max - min;
		double rangeLog = MathExt.log(range);
		
		double elevAdjusted = elevation - min;
		double elevLog = MathExt.log(elevAdjusted);
		double elevLogRatio = elevLog / rangeLog;
		double scaledElevation = min + (range * elevLogRatio);

		return scaledElevation;
	}

}

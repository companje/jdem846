package us.wthr.jdem846.render.scaling;

import us.wthr.jdem846.math.MathExt;

public class ExponentialScaler extends AbstractElevationScaler
{
	private double exponentDivisor = 10000.0;
	
	
	@Override
	public double scale(double elevation)
	{
		double min = this.getElevationMinimum();
		double maxTrue = this.getElevationMaximum();
		
		
		double maxMulitiplied = maxTrue * getElevationMultiple();
		
		double ratio = (elevation - min) / (maxTrue - min);
		elevation = min + (maxMulitiplied - min) * ratio;
			
		
		double range = maxMulitiplied - min;
		double rangeExp = MathExt.pow(2, range/exponentDivisor) - 1;
		
		double elevExp = MathExt.pow(2, (elevation - min)/exponentDivisor) - 1;
		
		double elevationScaled = min + (range * elevExp / rangeExp);
		return elevationScaled;
	}

}

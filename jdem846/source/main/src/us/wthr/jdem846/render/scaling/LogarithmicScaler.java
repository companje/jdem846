package us.wthr.jdem846.render.scaling;

import us.wthr.jdem846.math.MathExt;

public class LogarithmicScaler extends AbstractElevationScaler
{

	@Override
	public double scale(double elevation)
	{
		double min = this.getElevationMinimum();
		double maxTrue = this.getElevationMaximum();
		
		
		double maxMulitiplied = maxTrue * getElevationMultiple();
		
		//double ratio = (elevation - min) / (maxTrue - min);
		//elevation = min + (maxMulitiplied - min) * ratio;
		
		//=$C$1 + $E$2 * (LOG(D5*10)/LOG($E$2*10))
		double range = maxMulitiplied - min;
		double rangeLog = MathExt.log(range * 10);	
		
		double elevAdjusted = elevation - min;
		double elevLog = MathExt.log(elevAdjusted * 10);
		
		double elevLogRatio = elevLog / rangeLog;
		double scaledElevation = min + (range * elevLogRatio);
		/*
		double range = maxMulitiplied - min;
		double rangeLog = MathExt.log(range);
		
		double elevAdjusted = elevation - min;
		double elevLog = MathExt.log(elevAdjusted);
		double elevLogRatio = elevLog / rangeLog;
		double scaledElevation = min + (range * elevLogRatio);
		*/
		return scaledElevation;
	}

}

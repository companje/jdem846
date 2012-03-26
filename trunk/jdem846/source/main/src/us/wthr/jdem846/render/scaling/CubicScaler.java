package us.wthr.jdem846.render.scaling;

import us.wthr.jdem846.math.MathExt;

public class CubicScaler extends AbstractElevationScaler
{

	@Override
	public double scale(double elevation)
	{
		double min = this.getElevationMinimum();
		double maxTrue = this.getElevationMaximum();
		
		double maxMulitiplied = maxTrue * getElevationMultiple();
		
		double ratio = (elevation - min) / (maxTrue - min);
		double elevationMultiplied = min + (maxMulitiplied - min) * ratio;
		
		double elevationScaled = min + (maxMulitiplied - min) * (MathExt.cube((elevationMultiplied - min)) / MathExt.cube(maxMulitiplied - min));
		return elevationScaled;
	}

}

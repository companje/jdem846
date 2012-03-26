package us.wthr.jdem846.render.scaling;

public class LinearScaler extends AbstractElevationScaler
{

	@Override
	public double scale(double elevation)
	{
		double min = this.getElevationMinimum();
		double maxTrue = this.getElevationMaximum();
		
		double maxMulitiplied = maxTrue * getElevationMultiple();
		
		double ratio = (elevation - min) / (maxTrue - min);
		elevation = min + (maxMulitiplied - min) * ratio;
		
		return elevation;
	}

}

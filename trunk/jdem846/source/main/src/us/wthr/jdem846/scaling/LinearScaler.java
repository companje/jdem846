package us.wthr.jdem846.scaling;

public class LinearScaler extends AbstractElevationScaler
{

	@Override
	public double scale(double elevation)
	{
		double min = this.getElevationMinimum();
		double maxTrue = this.getElevationMaximum();
		
		double maxMulitiplied = maxTrue * getElevationMultiple();
		
		double ratio = 1.0;
		if (maxTrue - min != 0) {
			ratio = (elevation - min) / (maxTrue - min);
		}
		elevation = min + (maxMulitiplied - min) * ratio;
		
		return elevation;
	}
	
	
	public ElevationScaler copy()
	{
		LinearScaler scaler = new LinearScaler();
		super.copyTo(scaler);
		return scaler;
	}
}

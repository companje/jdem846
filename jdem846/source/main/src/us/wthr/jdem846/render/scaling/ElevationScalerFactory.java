package us.wthr.jdem846.render.scaling;

public class ElevationScalerFactory
{
	
	
	public static ElevationScaler createElevationScaler(ElevationScalerEnum elevationScalerEnum) throws Exception
	{
		ElevationScaler scaler = null;

		scaler = (ElevationScaler) elevationScalerEnum.provider().newInstance();

		return scaler;
	}
	
}

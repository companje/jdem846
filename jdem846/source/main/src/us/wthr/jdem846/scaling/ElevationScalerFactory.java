package us.wthr.jdem846.scaling;

public class ElevationScalerFactory
{
	
	/*
	public static ElevationScaler createElevationScaler(ElevationScalerEnum elevationScalerEnum ) throws Exception
	{
		return createElevationScaler(elevationScalerEnum, 1.0);
	}
	*/
	
	public static ElevationScaler createElevationScaler(ElevationScalerEnum elevationScalerEnum, double elevationMultiple, double elevationMinimum, double elevationMaximum) throws Exception
	{
		ElevationScaler scaler = null;

		scaler = (ElevationScaler) elevationScalerEnum.provider().newInstance();
		scaler.setElevationMultiple(elevationMultiple);
		scaler.setElevationMinimum(elevationMinimum);
		scaler.setElevationMaximum(elevationMaximum);
		
		return scaler;
	}
	
}

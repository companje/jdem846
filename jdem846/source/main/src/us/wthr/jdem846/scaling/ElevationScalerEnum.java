package us.wthr.jdem846.scaling;


import us.wthr.jdem846.i18n.I18N;

public enum ElevationScalerEnum
{

	LINEAR("us.wthr.jdem846.render.scaler.linear.name", LinearScaler.class),
	LOGARITHMIC("us.wthr.jdem846.render.scaler.logarithmic.name", LogarithmicScaler.class),
	EXPONENTIAL("us.wthr.jdem846.render.scaler.exponential.name", ExponentialScaler.class),
	CUBIC("us.wthr.jdem846.render.scaler.cubic.name", CubicScaler.class);
	
	
	private final String scalerName;
	private final Class<? extends ElevationScaler> provider;
	
	
	ElevationScalerEnum(String scalerName, Class<? extends ElevationScaler> provider)
	{
		this.scalerName = scalerName;
		this.provider = provider;
	}
	
	
	public String identifier() { return scalerName; }
	public String scalerName() { return I18N.get(scalerName, scalerName); }
	public Class<? extends ElevationScaler> provider() { return provider; }
	
	public static ElevationScalerEnum getElevationScalerEnumFromIdentifier(String identifier)
	{
		for (ElevationScalerEnum value : ElevationScalerEnum.values()) {
			if (value.identifier().equalsIgnoreCase(identifier)) {
				return value;
			}
		}
		
		return null;
	}
	
}

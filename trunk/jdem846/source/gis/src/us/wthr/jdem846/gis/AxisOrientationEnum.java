package us.wthr.jdem846.gis;

public enum AxisOrientationEnum
{
	EASTING("e"),
	WESTING("w"),
	NORTHING("n"),
	SOUTHING("s"),
	UP("u"),
	DOWN("d");
	
	private final String value;
	
	AxisOrientationEnum(String value)
	{
		this.value = value;
	}
	
	public String value() { return value; }
	
	
	public static AxisOrientationEnum getByValue(String value)
	{
		for (AxisOrientationEnum ao : AxisOrientationEnum.values()) {
			if (ao.value.equalsIgnoreCase(value)) {
				return ao;
			}
		}
		return null;
	}
}

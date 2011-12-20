package us.wthr.jdem846.lighting;

public enum LightSourceSpecifyTypeEnum
{
	BY_AZIMUTH_AND_ELEVATION("azimuthAndElevation"),
	BY_DATE_AND_TIME("dateAndTime");
	
	private final String optionValue;
	
	LightSourceSpecifyTypeEnum(String optionValue)
	{
		this.optionValue = optionValue;
	}
	
	public String optionValue() { return optionValue; }
	
	
	public static final LightSourceSpecifyTypeEnum getByOptionValue(String optionValue)
	{
		for (LightSourceSpecifyTypeEnum value : LightSourceSpecifyTypeEnum.values()) {
			if (value.optionValue.equalsIgnoreCase(optionValue)) {
				return value;
			}
		}
		return null;
	}
}

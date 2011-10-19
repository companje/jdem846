package us.wthr.jdem846.kml;

public enum AltitudeModeEnum
{
	CLAMP_TO_GROUND("clampToGround"),
	RELATIVE_TO_GROUND("relativeToGround"),
	ABSOLUTE("absolute"),
	
	// For use with gx:altitudeMode
	CLAMP_TO_SEA_FLOOR("clampToSeaFloor"),
	RELATIVE_TO_SEA_FLOOR("relativeToSeaFloor");
	
	
	private final String text;
	
	AltitudeModeEnum(String text)
	{
		this.text = text;
	}
	
	public String text() { return text; }
}

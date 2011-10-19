package us.wthr.jdem846.kml;

public enum ColorModeEnum
{
	
	NORMAL("normal"),
	RANDOM("random");
	
	private final String text;
	
	ColorModeEnum(String text)
	{
		this.text = text;
	}
	
	public String text() { return text; }
}

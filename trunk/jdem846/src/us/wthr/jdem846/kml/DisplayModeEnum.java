package us.wthr.jdem846.kml;

public enum DisplayModeEnum
{
	DEFAULT_MODE("default"),
	HIDE_MODE("hide");
	
	
	private final String text;
	
	DisplayModeEnum(String text)
	{
		this.text = text;
	}
	
	public String text() { return text; }
	
}

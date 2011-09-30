package us.wthr.jdem846.kml;

public enum GridOriginEnum
{

	LOWER_LEFT("lowerLeft"),
	UPPER_LEFT("upperLeft");
	
	private final String text;
	
	GridOriginEnum(String text)
	{
		this.text = text;
	}
	
	public String text() { return text; }
}

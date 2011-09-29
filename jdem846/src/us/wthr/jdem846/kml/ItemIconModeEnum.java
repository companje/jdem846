package us.wthr.jdem846.kml;

public enum ItemIconModeEnum
{
	
	OPEN("open"),
	CLOSED("closed"),
	ERROR("error"),
	FETCHING0("fetching0"),
	FETCHING1("fetching1"),
	FETCHING2("fetching2");
	
	
	private final String text;
	
	ItemIconModeEnum(String text)
	{
		this.text = text;
	}
	
	public String text() { return text; }
}

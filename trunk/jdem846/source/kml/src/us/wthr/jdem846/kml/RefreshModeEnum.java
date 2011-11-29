package us.wthr.jdem846.kml;

public enum RefreshModeEnum
{
	ON_CHANGE("onChange"),
	ON_INTERVAL("onInterval"),
	ON_EXPIRE("onExpire");
	
	private final String text;
	
	RefreshModeEnum(String text)
	{
		this.text = text;
	}
	
	public String text() { return text; }
}

package us.wthr.jdem846.kml;

public enum ViewRefreshModeEnum
{
	NEVER("never"),
	ON_STOP("onStop"),
	ON_REQUEST("onRequest"),
	ON_REGION("onRegion");
	
	
	private final String text;
	
	ViewRefreshModeEnum(String text)
	{
		this.text = text;
	}
	
	public String text() { return text; }
}

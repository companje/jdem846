package us.wthr.jdem846.kml;

public enum XYUnitsEnum
{
	
	FRACTION("fraction"),
	PIXELS("pixels"),
	INSET_PIXELS("insetPixels");

	private final String text;
	
	XYUnitsEnum(String text)
	{
		this.text = text;
	}
	
	public String text() { return text; }
}

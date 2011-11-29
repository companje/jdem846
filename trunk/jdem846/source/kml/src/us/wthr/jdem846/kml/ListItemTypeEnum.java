package us.wthr.jdem846.kml;

public enum ListItemTypeEnum
{
	CHECK("check"),
	CHECK_OFF_ONLY("checkOffOnly"),
	CHECK_HIDE_CHILDREN("checkHideChildren"),
	RADIO_FOLDER("radioFolder");
	
	
	private final String text;
	
	ListItemTypeEnum(String text)
	{
		this.text = text;
	}
	
	public String text() { return text; }
}

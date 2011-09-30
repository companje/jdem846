package us.wthr.jdem846.kml;

public enum ShapeTypeEnum
{

	RECTANGLE("rectangle"),
	CYLINDER("cylinder"),
	SPHERE("sphere");
	
	private final String text;
	
	ShapeTypeEnum(String text)
	{
		this.text = text;
	}
	
	public String text() { return text; }
}

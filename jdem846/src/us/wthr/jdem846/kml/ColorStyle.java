package us.wthr.jdem846.kml;

import org.dom4j.Element;

public abstract class ColorStyle extends SubStyle
{
	
	private String color = null;
	private ColorModeEnum colorMode = null;
	
	public ColorStyle()
	{
		
	}
	
	public ColorStyle(String color)
	{
		this.color = color;
		this.colorMode = ColorModeEnum.NORMAL;
	}
	
	public ColorStyle(String color, ColorModeEnum colorMode)
	{
		this.color = color;
		this.colorMode = colorMode;
	}
	
	
	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		if (color != null) {
			element.addElement("color").addText(color);
		}
		
		if (colorMode != null) {
			element.addElement("colorMode").addText(colorMode.text());
		}
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("ColorStyle");
		loadKmlChildren(element);
	}

	
	
}

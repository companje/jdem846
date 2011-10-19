package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class BalloonStyle extends SubStyle
{
	
	private String bgColor = null;
	private String textColor = null;
	private String text = null;
	private DisplayModeEnum displayMode = null;
	
	public BalloonStyle()
	{
		
	}
	
	public String getBgColor()
	{
		return bgColor;
	}

	public void setBgColor(String bgColor)
	{
		this.bgColor = bgColor;
	}

	public String getTextColor()
	{
		return textColor;
	}

	public void setTextColor(String textColor)
	{
		this.textColor = textColor;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public DisplayModeEnum getDisplayMode()
	{
		return displayMode;
	}

	public void setDisplayMode(DisplayModeEnum displayMode)
	{
		this.displayMode = displayMode;
	}

	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		if (bgColor != null) {
			element.addElement("bgColor").addText(bgColor);
		}
		
		if (textColor != null) {
			element.addElement("textColor").addText(textColor);
		}
		
		if (text != null) {
			element.addElement("text").addText(text);
		}
		
		if (displayMode != null) {
			element.addElement("displayMode").addText(displayMode.text());
		}

	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("BalloonStyle");
		loadKmlChildren(element);
	}
}

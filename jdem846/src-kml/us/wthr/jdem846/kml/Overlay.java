package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class Overlay extends Feature
{
	
	private String color = null;
	private int drawOrder = -1;
	private Icon icon = null;
	
	
	public Overlay()
	{
		
	}
	
	
	
	public String getColor()
	{
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}

	public int getDrawOrder()
	{
		return drawOrder;
	}

	public void setDrawOrder(int drawOrder)
	{
		this.drawOrder = drawOrder;
	}

	public Icon getIcon()
	{
		return icon;
	}

	public void setIcon(Icon icon)
	{
		this.icon = icon;
	}
	
	
	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		if (icon != null) {
			icon.toKml(element);
		}
		
		if (color != null) {
			element.addElement("color").addText(color);
		}
		
		if (drawOrder != -1) {
			element.addElement("drawOrder").addText(""+drawOrder);
		}

	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("Overlay");
		loadKmlChildren(element);
	}
}

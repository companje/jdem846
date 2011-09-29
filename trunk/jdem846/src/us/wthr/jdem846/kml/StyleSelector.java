package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class StyleSelector extends KmlElement
{
	
	
	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("StyleSelector");
		loadKmlChildren(element);
	}
	

}

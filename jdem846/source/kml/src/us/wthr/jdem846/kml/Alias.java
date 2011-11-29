package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class Alias extends KmlElement
{
	
	private String targetHref;
	private String sourceHref;
	
	public Alias()
	{
		
	}
	
	public Alias(String targetHref, String sourceHref)
	{
		setTargetHref(targetHref);
		setSourceHref(sourceHref);
	}

	public String getTargetHref()
	{
		return targetHref;
	}

	public void setTargetHref(String targetHref)
	{
		this.targetHref = targetHref;
	}



	public String getSourceHref()
	{
		return sourceHref;
	}



	public void setSourceHref(String sourceHref)
	{
		this.sourceHref = sourceHref;
	}



	public void toKml(Element parent)
	{
		Element element = parent.addElement("Alias");
		
		if (targetHref != null) {
			element.addElement("targetHref").addText(targetHref);
		}
		
		if (sourceHref != null) {
			element.addElement("sourceHref").addText(sourceHref);
		}

	}
}

package us.wthr.jdem846.kml;

import java.util.LinkedList;
import java.util.List;

public class KmlDocument extends KmlElement
{
	private String version = "2.1";
	private List<KmlElement> elements = new LinkedList<KmlElement>();
	
	public KmlDocument()
	{
		
	}
	
	
	
	
	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}




	public void addElement(KmlElement element)
	{
		elements.add(element);
	}
	
	public boolean removeElement(KmlElement element)
	{
		return elements.remove(element);
	}

	public List<KmlElement> getElementsList()
	{
		return elements;
	}
	
	
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		buffer.append("<kml xmlns=\"http://earth.google.com/kml/" + version + "\">\r\n");
		
		for (KmlElement element : elements) {
			buffer.append(element.toKml());
		}
		
		buffer.append("</kml>\r\n");
		return buffer.toString();
	}

}

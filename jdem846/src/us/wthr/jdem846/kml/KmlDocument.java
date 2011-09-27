package us.wthr.jdem846.kml;

import java.util.LinkedList;
import java.util.List;

public class KmlDocument extends KmlElement
{
	private String version = "2.1";
	private List<KmlElement> elements = new LinkedList<KmlElement>();
	
	private String name;
	private String description;
	private boolean hideChildren = false;
	
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


	

	public String getName()
	{
		return name;
	}




	public void setName(String name)
	{
		this.name = name;
	}




	public String getDescription()
	{
		return description;
	}




	public void setDescription(String description)
	{
		this.description = description;
	}




	public boolean isHideChildren()
	{
		return hideChildren;
	}




	public void setHideChildren(boolean hideChildren)
	{
		this.hideChildren = hideChildren;
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
	
	/** Constructs the KML markup. Note: Lots of assumptions at the moment!
	 * 
	 */
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		buffer.append("<kml xmlns=\"http://earth.google.com/kml/" + version + "\">\r\n");
		buffer.append("<Document>\r\n");
		
		if (name != null) {
			buffer.append("	<name>" + name + "</name>\r\n");
		}
		
		if (description != null) {
			buffer.append("	<Snippet maxLines=\"2\"></Snippet>\r\n");
			buffer.append("	<styleUrl>#intro-style</styleUrl>\r\n");
			buffer.append("	<Style id=\"intro-style\">\r\n");
			buffer.append("		<BalloonStyle>\r\n");
			buffer.append("			<text>$[description]</text>\r\n");
			buffer.append("		</BalloonStyle>\r\n");
			buffer.append("	</Style>\r\n");
			buffer.append("	<description><![CDATA[" + description + "]]></description>\r\n");
		}
		
		if (hideChildren) {
			buffer.append("	<Style>\r\n");
			buffer.append("		<ListStyle id=\"hideChildren\">\r\n");
			buffer.append("			<listItemType>checkHideChildren</listItemType>\r\n");
			buffer.append("		</ListStyle>\r\n");
			buffer.append("	</Style>\r\n");
		}
		
		for (KmlElement element : elements) {
			buffer.append(element.toKml());
		}
		
		buffer.append("</Document>\r\n");
		buffer.append("</kml>\r\n");
		return buffer.toString();
	}

}

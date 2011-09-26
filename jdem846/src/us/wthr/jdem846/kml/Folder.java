package us.wthr.jdem846.kml;

import java.util.LinkedList;
import java.util.List;

public class Folder extends KmlElement
{
	private String name;
	private String description;
	private List<KmlElement> elements = new LinkedList<KmlElement>();
	
	
	public Folder()
	{
		
	}
	
	public Folder(String name)
	{
		setName(name);
	}
	
	public Folder(String name, String description)
	{
		setName(name);
		setDescription(description);
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
	
	@Override
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<Folder>\r\n");
		buffer.append("	<name>" + name + "</name>\r\n");
		
		if (description != null) {
			buffer.append("	<description>" + description + "</description>\r\n");
		}
		
		for (KmlElement element : elements) {
			buffer.append(element.toKml());
		}
		
		buffer.append("</Folder>\r\n");
		return buffer.toString();
	}

}

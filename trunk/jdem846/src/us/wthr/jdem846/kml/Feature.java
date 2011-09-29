package us.wthr.jdem846.kml;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;

public class Feature extends KmlElement
{
	
	private String name = null;
	private boolean visibility = true;
	private boolean open = false;
	private String description = null;
	private String styleUrl = null;
	private List<StyleSelector> styles = new LinkedList<StyleSelector>();
	private Region region = null;
	
	
	public Feature()
	{
		
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isVisibility()
	{
		return visibility;
	}

	public void setVisibility(boolean visibility)
	{
		this.visibility = visibility;
	}

	public boolean isOpen()
	{
		return open;
	}

	public void setOpen(boolean open)
	{
		this.open = open;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getStyleUrl()
	{
		return styleUrl;
	}

	public void setStyleUrl(String styleUrl)
	{
		this.styleUrl = styleUrl;
	}



	public Region getRegion()
	{
		return region;
	}

	public void setRegion(Region region)
	{
		this.region = region;
	}
	
	public void addStyle(StyleSelector style)
	{
		styles.add(style);
	}
	
	public boolean removeStyle(StyleSelector style)
	{
		return styles.remove(style);
	}
	
	public List<StyleSelector> getStyles()
	{
		return styles;
	}

	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		if (name != null) {
			element.addElement("name").addText(name);
		}
		
		if (!visibility) { // Default is true
			element.addElement("visiblity").addText("0");
		}
		
		if (open) { // Default is false
			element.addElement("open").addText("1");
		}
		
		if (description != null) {
			element.addElement("description").addCDATA(description);
		}
		
		if (styleUrl != null) {
			element.addElement("styleUrl").addText("styleUrl");
		}

		
		if (region != null) {
			region.toKml(element);
		}
		
		for (StyleSelector styleSelector : styles) {
			styleSelector.toKml(element);
		}
		
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("Feature");
		loadKmlChildren(element);
	}
	
}

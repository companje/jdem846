package us.wthr.jdem846.kml;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;

public class ResourceMap extends KmlElement
{
	
	private List<Alias> aliases = new LinkedList<Alias>();
	
	public ResourceMap()
	{
		
	}

	public List<Alias> getAliases()
	{
		return aliases;
	}

	public void setAliases(List<Alias> aliases)
	{
		this.aliases = aliases;
	}

	public void addAlias(Alias alias)
	{
		aliases.add(alias);
	}
	
	public void removeAlias(Alias alias)
	{
		aliases.remove(alias);
	}

	public void toKml(Element parent)
	{
		Element element = parent.addElement("ResourceMap");
		
		for (Alias alias : aliases) {
			alias.toKml(element);
		}
		
	}
}

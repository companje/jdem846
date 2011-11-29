package us.wthr.jdem846.kml;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;

public class Style extends StyleSelector
{	
	
	private List<SubStyle> subStyles = new LinkedList<SubStyle>();
	
	public Style()
	{
		
	}
	
	public void addSubStyle(SubStyle subStyle)
	{
		subStyles.add(subStyle);
	}
	
	public boolean removeSubStyle(SubStyle subStyle)
	{
		return subStyles.remove(subStyle);
	}
	
	public List<SubStyle> getSubStyles()
	{
		return subStyles;
	}
	
	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		for (SubStyle subStyle : subStyles)
		{
			subStyle.toKml(element);
		}
		
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("Style");
		loadKmlChildren(element);
	}
}

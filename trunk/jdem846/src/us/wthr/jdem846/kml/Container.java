package us.wthr.jdem846.kml;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;


public abstract class Container extends Feature
{
	
	private List<Feature> features = new LinkedList<Feature>();
	
	public Container()
	{
		
	}
	
	

	public void addFeature(Feature feature)
	{
		features.add(feature);
	}
	
	public boolean removeFeature(Feature feature)
	{
		return features.remove(feature);
	}

	public List<Feature> getFeaturesList()
	{
		return features;
	}
	
	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		for (Feature feature : features) {
			feature.toKml(element);
		}
		
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("Folder");
		loadKmlChildren(element);
	}
}

package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class Model extends Geometry
{
	
	private AltitudeModeEnum altitudeMode = AltitudeModeEnum.CLAMP_TO_GROUND;
	private Location location;
	private Orientation orientation;
	private Scale scale;
	private ResourceMap resourceMap;
	
	public Model()
	{
		
	}
	
	
	public AltitudeModeEnum getAltitudeMode()
	{
		return altitudeMode;
	}


	public void setAltitudeMode(AltitudeModeEnum altitudeMode)
	{
		this.altitudeMode = altitudeMode;
	}


	public Location getLocation()
	{
		return location;
	}


	public void setLocation(Location location)
	{
		this.location = location;
	}


	public Orientation getOrientation()
	{
		return orientation;
	}


	public void setOrientation(Orientation orientation)
	{
		this.orientation = orientation;
	}


	public Scale getScale()
	{
		return scale;
	}


	public void setScale(Scale scale)
	{
		this.scale = scale;
	}


	public ResourceMap getResourceMap()
	{
		return resourceMap;
	}


	public void setResourceMap(ResourceMap resourceMap)
	{
		this.resourceMap = resourceMap;
	}


	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		if (altitudeMode != null) {
			element.addElement("altitudeMode").addText(altitudeMode.text());
		}
		
		if (location != null) {
			location.toKml(element);
		}
		
		if (orientation != null) {
			orientation.toKml(element);
		}
		
		if (scale != null) {
			scale.toKml(element);
		}
		
		if (resourceMap != null) {
			resourceMap.toKml(element);
		}
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("Model");
		loadKmlChildren(element);
	}
	
	

	
}

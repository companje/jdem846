package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class Point extends Geometry
{

	private boolean extrude = false;
	private AltitudeModeEnum altitudeMode = null;
	private Coordinate coordinates = null;
	
	public Point()
	{
		
	}
	
	public Point(Coordinate coordinates, AltitudeModeEnum altitudeMode)
	{
		setCoordinates(coordinates);
		setAltitudeMode(altitudeMode);
	}
	
	

	public boolean isExtrude()
	{
		return extrude;
	}

	public void setExtrude(boolean extrude)
	{
		this.extrude = extrude;
	}

	public AltitudeModeEnum getAltitudeMode()
	{
		return altitudeMode;
	}

	public void setAltitudeMode(AltitudeModeEnum altitudeMode)
	{
		this.altitudeMode = altitudeMode;
	}

	public Coordinate getCoordinates()
	{
		return coordinates;
	}

	public void setCoordinates(Coordinate coordinates)
	{
		this.coordinates = coordinates;
	}

	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);
		
		element.addElement("extrude").addText((extrude ? "1" : "0"));
		
		if (altitudeMode != null) {
			element.addElement("altitudeMode").addText(altitudeMode.text());
		}
		
		if (coordinates != null) {
			element.addElement("coordinates").addText(coordinates.toString());
		}
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("Point");
		loadKmlChildren(element);
	}
	
}

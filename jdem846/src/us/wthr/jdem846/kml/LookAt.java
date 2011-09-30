package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class LookAt extends AbstractView
{

	private double longitude = 0; // angle180
	private double latitude = 0; // angle180
	private double altitude = 0;
	private double heading = 0; // angle360
	private double tilt = 0; // anglepos90
	private double range = 0;
	private AltitudeModeEnum altitudeMode;
	
	
	public LookAt()
	{
		
	}
	
	
	public double getLongitude()
	{
		return longitude;
	}


	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}


	public double getLatitude()
	{
		return latitude;
	}


	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}


	public double getAltitude()
	{
		return altitude;
	}


	public void setAltitude(double altitude)
	{
		this.altitude = altitude;
	}


	public double getHeading()
	{
		return heading;
	}


	public void setHeading(double heading)
	{
		this.heading = heading;
	}


	public double getTilt()
	{
		return tilt;
	}


	public void setTilt(double tilt)
	{
		this.tilt = tilt;
	}


	public double getRange()
	{
		return range;
	}


	public void setRange(double range)
	{
		this.range = range;
	}


	public AltitudeModeEnum getAltitudeMode()
	{
		return altitudeMode;
	}


	public void setAltitudeMode(AltitudeModeEnum altitudeMode)
	{
		this.altitudeMode = altitudeMode;
	}


	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);

		element.addElement("longitude").addText(""+longitude);
		element.addElement("latitude").addText(""+latitude);
		element.addElement("altitude").addText(""+altitude);
		element.addElement("heading").addText(""+heading);
		element.addElement("tilt").addText(""+tilt);
		element.addElement("range").addText(""+range);
		
		if (altitudeMode != null) {
			element.addElement("altitudeMode").addText(altitudeMode.text());
		}

	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("LookAt");
		loadKmlChildren(element);
	}
}

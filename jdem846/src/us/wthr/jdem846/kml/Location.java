package us.wthr.jdem846.kml;

import org.dom4j.Element;

public class Location extends KmlElement
{
	/*
	<Location>
  <longitude>39.55375305703105</longitude>  
  <latitude>-118.9813220168456</latitude> 
  <altitude>1223</altitude> 
</Location> 
	*/
	
	// angle180Type
	private double longitude;
	
	// angle180Type
	private double latitude;
	
	private double altitude;
	
	public Location()
	{
		
	}
	
	public Location(double latitude, double longitude)
	{
		setLatitude(latitude);
		setLongitude(longitude);
	}
	
	public Location(double latitude, double longitude, double altitude)
	{
		setLatitude(latitude);
		setLongitude(longitude);
		setAltitude(altitude);
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



	protected void loadKmlChildren(Element element)
	{
		super.loadKmlChildren(element);

		element.addElement("latitude").addText(""+latitude);
		element.addElement("longitude").addText(""+longitude);
		element.addElement("altitude").addText(""+altitude);
		
	}
	
	public void toKml(Element parent)
	{
		Element element = parent.addElement("Location");
		loadKmlChildren(element);
	}
}

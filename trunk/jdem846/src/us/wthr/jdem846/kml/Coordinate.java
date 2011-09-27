package us.wthr.jdem846.kml;

public class Coordinate extends KmlElement
{
	private double latitude;
	private double longitude;
	private double altitude;
	
	public Coordinate(double latitude, double longitude)
	{
		setLatitude(latitude);
		setLongitude(longitude);
		setAltitude(-9999);
	}
	
	public Coordinate(double latitude, double longitude, double altitude)
	{
		setLatitude(latitude);
		setLongitude(longitude);
		setAltitude(altitude);
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public double getAltitude()
	{
		return altitude;
	}

	public void setAltitude(double altitude)
	{
		this.altitude = altitude;
	}
	
	@Override
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(""+getLongitude()+","+getLatitude());
		if (altitude != -9999) {
			buffer.append(","+altitude);
		}
		
		return buffer.toString();
	}
}

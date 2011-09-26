package us.wthr.jdem846.kml;

public abstract class KmlElement
{
	
	public String toKml()
	{
		return toKml(null);
	}
	
	public abstract String toKml(String id);
}

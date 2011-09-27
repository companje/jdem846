package us.wthr.jdem846.kml;

public class Placemark extends KmlElement
{
	
	private String name;
	private Geometry geometry;
	
	
	public Placemark(String name, Geometry geometry)
	{
		setName(name);
		setGeometry(geometry);
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Geometry getGeometry()
	{
		return geometry;
	}

	public void setGeometry(Geometry geometry)
	{
		this.geometry = geometry;
	}

	@Override
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<Placemark>\r\n");
		
		if (name != null) {
			buffer.append("<name>" + name + "</name>\r\n");
		}
		
		if (geometry != null) {
			buffer.append(geometry.toKml());
		}
		
		buffer.append("</Placemark>\r\n");
		
		return buffer.toString();
	}
}

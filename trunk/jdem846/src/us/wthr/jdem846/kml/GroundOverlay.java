package us.wthr.jdem846.kml;

public class GroundOverlay extends KmlElement
{
	
	private String name;
	private Icon icon;
	private LatLonBox latLonBox;
	
	
	public GroundOverlay()
	{
		
	}
	
	public GroundOverlay(String name, String iconHref, LatLonBox latLonBox)
	{
		setName(name);
		setIcon(new Icon(iconHref));
		setLatLonBox(latLonBox);
	}
	
	public GroundOverlay(String name, Icon icon, LatLonBox latLonBox)
	{
		setName(name);
		setIcon(icon);
		setLatLonBox(latLonBox);
	}
	
	
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Icon getIcon()
	{
		return icon;
	}

	public void setIcon(Icon icon)
	{
		this.icon = icon;
	}

	public LatLonBox getLatLonBox()
	{
		return latLonBox;
	}

	public void setLatLonBox(LatLonBox latLonBox)
	{
		this.latLonBox = latLonBox;
	}

	@Override
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("		<GroundOverlay>\r\n");
		buffer.append("		<name>" + name + "</name>\r\n");
		
		if (icon != null) {
			buffer.append(icon.toKml());
		}
		
		if (latLonBox != null) {
			buffer.append(latLonBox.toKml());
		}
		
		buffer.append("		</GroundOverlay>\r\n");
		
		return buffer.toString();
	}
}

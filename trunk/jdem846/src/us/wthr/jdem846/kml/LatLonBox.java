package us.wthr.jdem846.kml;

public class LatLonBox extends KmlElement
{
	
	// Longitude
	private double east;
	private double west;
	
	// Latitude
	private double north;
	private double south;
	
	
	public LatLonBox()
	{
		
	}
	
	public LatLonBox(double east, double west, double north, double south)
	{
		setEast(east);
		setWest(west);
		setNorth(north);
		setSouth(south);
	}

	public LatLonBox(double west, double south, double cellsize, int rows, int columns)
	{
		setWest(west);
		setSouth(south);
		
		setEast(west + (cellsize * (double)columns));
		setNorth(south + (cellsize * (double)rows));
	}
			
	
	
	public double getEast()
	{
		return east;
	}

	public void setEast(double east)
	{
		this.east = east;
	}

	public double getWest()
	{
		return west;
	}

	public void setWest(double west)
	{
		this.west = west;
	}

	public double getNorth()
	{
		return north;
	}

	public void setNorth(double north)
	{
		this.north = north;
	}

	public double getSouth()
	{
		return south;
	}

	public void setSouth(double south)
	{
		this.south = south;
	}
	
	@Override
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		
		if (id == null) {
			buffer.append("		<LatLonBox>\r\n");
		} else {
			buffer.append("		<LatLonBox id=\"" + id + "\">\r\n");
		}
		
		buffer.append("			<north>" + north +"</north>\r\n");
		buffer.append("			<south>" + south +"</south>\r\n");
		buffer.append("			<east>" + east +"</east>\r\n");
		buffer.append("			<west>" + west +"</west>\r\n");
		
		buffer.append("		</LatLonBox>\r\n");
		return buffer.toString();
	}
	
}

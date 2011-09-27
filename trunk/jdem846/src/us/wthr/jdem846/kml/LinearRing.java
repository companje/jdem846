package us.wthr.jdem846.kml;

import java.util.LinkedList;
import java.util.List;

public class LinearRing extends Geometry
{
	
	private List<Coordinate> coordinates = new LinkedList<Coordinate>();
	
	public LinearRing()
	{
		
	}
	
	public void addCoordinate(Coordinate coordinate)
	{
		coordinates.add(coordinate);
	}
	
	public List<Coordinate> getCoordinates()
	{
		return coordinates;
	}
	
	
	@Override
	public String toKml(String id)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<LinearRing>\r\n");
		buffer.append("	<coordinates>\r\n");
		
		for (Coordinate coordinate : coordinates) {
			buffer.append(coordinate.toKml() + "\r\n");
		}
		
		buffer.append("	</coordinates>\r\n");
		buffer.append("</LinearRing>\r\n");
		return buffer.toString();
	}
}

package us.wthr.jdem846.gis;

import junit.framework.TestCase;

public class CoordinateTest extends TestCase
{
	
	//Coordinate(int hour, int minute, int second, CardinalDirectionEnum direction, CoordinateTypeEnum coordinateType)
	
	
	public void testWgs84LatitudeNorthCoordinate()
	{
		//42 45' 27" N, 71 27' 52" W
		//42.7575, -71.464444
		Coordinate c = new Coordinate(42, 45, 27, CardinalDirectionEnum.NORTH, CoordinateTypeEnum.LATITUDE);
		assertEquals(42.7575, c.toDecimal(), 0.0001);
	}
	
	public void testWgs84LongitudeWestCoordinate()
	{
		//42 45' 27" N, 71 27' 52" W
		//42.7575, 
		Coordinate c = new Coordinate(71, 27, 52, CardinalDirectionEnum.WEST, CoordinateTypeEnum.LONGITUDE);
		assertEquals(-71.464444, c.toDecimal(), 0.0001);
	}
	
	
	//Coordinate(double decimal, CoordinateTypeEnum coordinateType)
	public void testWgs84LatitudeNorthDecimal()
	{
		//42 45' 27" N, 71 27' 52" W
		//42.7575, -71.464444
		
		Coordinate c = new Coordinate(42.7575, CoordinateTypeEnum.LATITUDE);
		assertEquals(42, c.getHour());
		assertEquals(45, c.getMinute());
		assertEquals(27, c.getSecond(), 0.0001);
		assertEquals(CardinalDirectionEnum.NORTH, c.getDirection());
	}
	
	public void testWgs84LongitudeWestDecimal()
	{
		//42 45' 27" N, 71 27' 52" W
		//42.7575, -71.464444
		Coordinate c = new Coordinate(-71.464444, CoordinateTypeEnum.LONGITUDE);
		assertEquals(71, c.getHour());
		assertEquals(27, c.getMinute());
		assertEquals(51.9984, c.getSecond(), 0.0001);
		assertEquals(CardinalDirectionEnum.WEST, c.getDirection());
	}
	
	
	
	
	public void testWgs84LatitudeSouthCoordinate()
	{
		//33 55' 31" S, 18 25' 26" E
		//-33.925278, 18.423889
		Coordinate c = new Coordinate(33, 55, 31, CardinalDirectionEnum.SOUTH, CoordinateTypeEnum.LATITUDE);
		assertEquals(-33.925278, c.toDecimal(), 0.0001);
	}
	
	public void testWgs84LongitudeSouthCoordinate()
	{
		//33 55' 31" S, 18 25' 26" E
		//-33.925278, 18.423889
		Coordinate c = new Coordinate(18, 25, 26, CardinalDirectionEnum.EAST, CoordinateTypeEnum.LONGITUDE);
		assertEquals(18.423889, c.toDecimal(), 0.0001);
	}
	
	
	//Coordinate(double decimal, CoordinateTypeEnum coordinateType)
	public void testWgs84LatitudeEastDecimal()
	{
		//33 55' 31" S, 18 25' 26" E
		//-33.925278, 18.423889
		
		Coordinate c = new Coordinate(-33.925278, CoordinateTypeEnum.LATITUDE);
		assertEquals(33, c.getHour());
		assertEquals(55, c.getMinute());
		assertEquals(31.0008, c.getSecond(), 0.0001);
		assertEquals(CardinalDirectionEnum.SOUTH, c.getDirection());
	}
	
	public void testWgs84LongitudeEastDecimal()
	{
		//33 55' 31" S, 18 25' 26" E
		//-33.925278, 18.423889
		Coordinate c = new Coordinate(18.423889, CoordinateTypeEnum.LONGITUDE);
		assertEquals(18, c.getHour());
		assertEquals(25, c.getMinute());
		assertEquals(26.0004, c.getSecond(), 0.0001);
		assertEquals(CardinalDirectionEnum.EAST, c.getDirection());
	}
	
	
}

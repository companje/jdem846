package us.wthr.jdem846.gis.datetime;

import us.wthr.jdem846.gis.CardinalDirectionEnum;
import us.wthr.jdem846.gis.Coordinate;
import us.wthr.jdem846.gis.CoordinateTypeEnum;
import us.wthr.jdem846.gis.Location;
import junit.framework.TestCase;

public class SolarCalculatorTest extends TestCase
{
	SolarCalculator solCalc = null;
	EarthDateTime datetime = null;
	Location location = null;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Coordinate latitude = new Coordinate(42, 45, 27, CardinalDirectionEnum.NORTH, CoordinateTypeEnum.LATITUDE);
		Coordinate longitude = new Coordinate(71, 27, 52, CardinalDirectionEnum.WEST, CoordinateTypeEnum.LONGITUDE);
		
		location = new Location("Boire Field, Nashua NH", latitude, longitude, -5, false);
		
		
		datetime = new EarthDateTime(2011, 12, 16, 12, 30, 0, -5, false);
		solCalc = new SolarCalculator(datetime, location);
	
	}

	public void testJulianCentury()
	{
		assertEquals(0.11956251426, solCalc.getJulianCentury(), 0.0001);
	}
	
	public void testEquationOfTime()
	{
		assertEquals(4.41, solCalc.equationOfTime(), 0.001);
	}
	
	public void testDeclinationOfSun()
	{
		assertEquals(-23.32, solCalc.declinationOfSun(), 0.001);
	}
	
	public void testApparentSunrise()
	{
		// Apparent sunrise: 07:10
		EarthDateTime result = solCalc.sunriseSet(true);
		assertEquals(7, result.getHour());
		assertEquals(10, result.getMinute());
	}
	
	public void testSolarNoon()
	{
		// Solar noon: 11:41:26
		EarthDateTime result = solCalc.solarNoon();
		assertEquals(11, result.getHour());
		assertEquals(41, result.getMinute());
		assertEquals(26, result.getSecond());
	}
	
	public void testApparentSunset()
	{
		// Apparent sunset: 16:13
		EarthDateTime result = solCalc.sunriseSet(false);
		assertEquals(16, result.getHour());
		assertEquals(13, result.getMinute());
	}
	
	public void testSolarAzimuth()
	{
		// Solar Azimuth: 192.11
		assertEquals(192.11, solCalc.solarAzimuthAngle(), 0.001);
	}
	
	public void testSolarElevation()
	{
		// Solar Elevation: 23.02
		assertEquals(23.02, solCalc.solarElevationAngle(), 0.001);
	}
	
}

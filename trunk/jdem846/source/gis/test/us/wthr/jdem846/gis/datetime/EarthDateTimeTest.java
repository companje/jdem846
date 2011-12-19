package us.wthr.jdem846.gis.datetime;

import junit.framework.TestCase;

public class EarthDateTimeTest extends TestCase
{
	
	EarthDateTime datetime = null;
	
	protected void setUp() throws Exception
	{
		super.setUp();
		datetime = new EarthDateTime(2011, 12, 16, 12, 30, 0, -5, false);
	}
	

	
	public void testTimeLocal()
	{
		assertEquals(750.0, datetime.timeLocal());
	}
	
	public void testJulianDay()
	{
		assertEquals(2455912.020833, datetime.julianDay(), 0.1);
	}
	
	public void testJulianCentury()
	{	
		assertEquals(0.11956251426, datetime.julianCentury(), 0.0001);
	}
	
	public void testYear()
	{
		assertEquals(2011, datetime.getYear());
	}
	
	public void testMonth()
	{
		assertEquals(12, datetime.getMonth());
	}
	
	public void testDayOfMonth()
	{
		assertEquals(16, datetime.getDay());
	}
	
	public void testHour()
	{
		assertEquals(12, datetime.getHour());
	}
	
	public void testMinute()
	{
		assertEquals(30, datetime.getMinute());
	}
	
	public void testTimezone()
	{
		assertEquals(-5, datetime.getTimezone());
	}
	
	public void testDst()
	{
		assertEquals(false, datetime.isDst());
	}
	
	public void testLeapYear()
	{
		assertEquals(false, datetime.isLeapYear());	
	}
}

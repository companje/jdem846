package us.wthr.jdem846.geo;

public class Unit
{
	
	private String name;
	private double conversionFactor;
	
	public Unit(String name, double conversionFactor)
	{
		this.name = name;
		this.conversionFactor = conversionFactor;
	}
	
	public String getName()
	{
		return name;
	}
	
	public double getConversionFactor()
	{
		return conversionFactor;
	}
	
	
	
	public static final Unit METER = new Unit("Meter", 1.0);
	public static final Unit FOOT_INTERNATIONAL = new Unit("Foot (International)", 0.3048);
	public static final Unit FOOT_US = new Unit("U.S. Foot", 12/39.37);
	
	/*
	
	*/
}

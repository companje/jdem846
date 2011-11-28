package us.wthr.jdem846.geo;

public class Unit
{
	
	private String name;
	private double conversionFactor;
	
	private boolean isLinear = false;
	private boolean isAngular = false;
	
	private double latitudeRangeMinimum = 0;
	private double latitudeRangeMaximum = 0;
	private double longitudeRangeMinimum = 0;
	private double longitudeRangeMaximum = 0;
	
	public Unit(String name, double conversionFactor)
	{
		this.name = name;
		this.conversionFactor = conversionFactor;
		isLinear = true;
		isAngular = false;
	}
	
	public Unit(String name, double latitudeRangeMinimum, double latitudeRangeMaximum, double longitudeRangeMinimum, double longitudeRangeMaximum, double conversionFactor)
	{
		this.name = name;
		this.latitudeRangeMinimum = latitudeRangeMinimum;
		this.latitudeRangeMaximum = latitudeRangeMaximum;
		this.longitudeRangeMinimum = longitudeRangeMinimum;
		this.longitudeRangeMaximum = longitudeRangeMaximum;
		this.conversionFactor = conversionFactor;
		isLinear = false;
		isAngular = true;
	}
	
	
	
	
	public String getName()
	{
		return name;
	}
	
	public double getConversionFactor()
	{
		return conversionFactor;
	}

	public boolean isLinear()
	{
		return isLinear;
	}

	public boolean isAngular()
	{
		return isAngular;
	}

	public double getLatitudeRangeMinimum()
	{
		return latitudeRangeMinimum;
	}

	public double getLatitudeRangeMaximum()
	{
		return latitudeRangeMaximum;
	}

	public double getLongitudeRangeMinimum()
	{
		return longitudeRangeMinimum;
	}

	public double getLongitudeRangeMaximum()
	{
		return longitudeRangeMaximum;
	}
	
	
	
	public boolean isWithinLatitudeRange(double value)
	{
		if (isLinear())
			return true; // Don't care in this condition.
		
		if (getLatitudeRangeMinimum() <= value && value <= getLatitudeRangeMaximum()) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public boolean isWithinLongitudeRange(double value)
	{
		if (isLinear())
			return true; // Don't care in this condition.
		
		if (getLongitudeRangeMinimum() <= value && value <= getLongitudeRangeMaximum()) {
			return true;
		} else {
			return false;
		}
		
	}
	
	
	
	
	
}

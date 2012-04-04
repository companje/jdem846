package us.wthr.jdem846.gis;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Unit implements Cloneable, Serializable
{
	
	private String name;
	private double conversionFactor;
	
	private boolean isLinear = false;
	private boolean isAngular = false;
	
	private double latitudeRangeMinimum = 0;
	private double latitudeRangeMaximum = 0;
	private double longitudeRangeMinimum = 0;
	private double longitudeRangeMaximum = 0;
	

	
	public Unit(String name, double conversionFactor, String type, double[] latitudeRange, double[] longitudeRange)
	{
		this.name = name;
		this.conversionFactor = conversionFactor;
		
		if (type.equalsIgnoreCase("linear")) {
			isLinear = true;
		} else if (type.equalsIgnoreCase("angular")) {
			isAngular = true;
		}
		
		if (latitudeRange != null && latitudeRange.length == 2) {
			latitudeRangeMinimum = latitudeRange[0];
			latitudeRangeMaximum = latitudeRange[1];
		}
		
		if (longitudeRange != null && longitudeRange.length == 2) {
			longitudeRangeMinimum = longitudeRange[0];
			longitudeRangeMaximum = longitudeRange[1];
		}
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
	
	
	public Object clone() 
	{
		try {
			Unit e = (Unit) super.clone();
			return e;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}
	
	
}

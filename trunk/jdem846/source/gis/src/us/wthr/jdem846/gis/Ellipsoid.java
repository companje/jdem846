package us.wthr.jdem846.gis;

import java.io.Serializable;

public class Ellipsoid implements Cloneable, Serializable
{

	private String name;
	private String shortName;
	private double reciprocalFlattening = 0;
	private double equatorRadius = 1.0;
	private double poleRadius = 1.0;
	private double eccentricity = 1.0;
	private double eccentricity2 = 1.0;
	
	public Ellipsoid(String name, String shortName, double equatorRadius, double poleRadius, double reciprocalFlattening, double eccentricity, double eccentricity2)
	{
		this.name = name;
		this.shortName = shortName;
		this.equatorRadius = equatorRadius;
		this.poleRadius = poleRadius;
		this.reciprocalFlattening = reciprocalFlattening;
		this.eccentricity = eccentricity;
		this.eccentricity2 = eccentricity2;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getShortName()
	{
		return shortName;
	}

	public double getReciprocalFlattening()
	{
		return reciprocalFlattening;
	}

	public double getEquatorRadius()
	{
		return equatorRadius;
	}

	public double getPoleRadius()
	{
		return poleRadius;
	}

	public double getEccentricity()
	{
		return eccentricity;
	}

	public double getEccentricity2()
	{
		return eccentricity2;
	}

	
	public Object clone() 
	{
		try {
			Ellipsoid e = (Ellipsoid) super.clone();
			return e;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}
}

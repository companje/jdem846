package us.wthr.jdem846.geo;

public class Spheriod
{
	private String name;
	private double semiMajorAxis = 0;
	private double inverseFlattening = 0;
	
	public Spheriod(String name, double semiMajorAxis, double inverseFlattening)
	{
		this.name = name;
		this.semiMajorAxis = semiMajorAxis;
		this.inverseFlattening = inverseFlattening;
	}

	protected String getName()
	{
		return name;
	}

	protected double getSemiMajorAxis()
	{
		return semiMajorAxis;
	}

	protected double getInverseFlattening()
	{
		return inverseFlattening;
	}
	
	
	
}

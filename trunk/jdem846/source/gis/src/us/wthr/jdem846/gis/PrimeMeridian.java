package us.wthr.jdem846.gis;

import java.io.Serializable;

public class PrimeMeridian implements Cloneable, Serializable
{

	private String name;
	private String definition;
	private double offset;
	
	public PrimeMeridian(String name, String definition, double offset)
	{
		this.name = name;
		this.definition = definition;
		this.offset = offset;
	}

	public String getName()
	{
		return name;
	}

	public String getDefinition()
	{
		return definition;
	}

	public double getOffset()
	{
		return offset;
	}
	
	public Object clone() 
	{
		try {
			PrimeMeridian e = (PrimeMeridian) super.clone();
			return e;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}
	
}

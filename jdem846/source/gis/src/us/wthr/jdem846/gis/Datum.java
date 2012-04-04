package us.wthr.jdem846.gis;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Datum implements Cloneable, Serializable
{

	private String id;
	private double[] toWGS84;
	private String definition;
	private Ellipsoid ellipse;
	private String datumName;
	
	public Datum(String id, String datumName, String definition, double[] toWGS84, Ellipsoid ellipse)
	{
		this.id = id;
		this.datumName = datumName;
		this.definition = definition;
		this.toWGS84 = toWGS84;
		this.ellipse = ellipse;
	}

	public String getId()
	{
		return id;
	}

	public double[] getToWGS84()
	{
		return toWGS84;
	}

	public String getDefinition()
	{
		return definition;
	}

	public Ellipsoid getEllipse()
	{
		return ellipse;
	}

	public String getDatumName()
	{
		return datumName;
	}
	
	public Object clone() 
	{
		try {
			Datum e = (Datum) super.clone();
			return e;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}
	
}

package us.wthr.jdem846.geom;

import java.util.Comparator;

public class Vertex implements Comparable<Vertex>
{
	public double x;
	public double y;
	public double z;
	
	public Vertex(Vertex copy)
	{
		this.x = copy.x;
		this.y = copy.y;
		this.z = copy.z;
	}
	
	public Vertex(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Vertex(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public int compareTo(Vertex other)
	{

		Double y0 = (Double) this.y;
		Double y1 = (Double) other.y;
		return y0.compareTo(y1);
		
	}

	
}

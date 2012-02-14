package us.wthr.jdem846.geom.util;

import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.geom.Vertex;

public abstract class PointTest
{
	protected static Perspectives perspectives = new Perspectives();
	
	public abstract boolean contains(Vertex p0, Vertex p1, Vertex p2, double x, double y, double z);
	
	
	protected void fill(double x, double y, double z, double[] v)
	{
		v[0] = x;
		v[1] = y;
		v[2] = z;
	}
	
}

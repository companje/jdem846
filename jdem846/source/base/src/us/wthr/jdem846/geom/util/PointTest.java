package us.wthr.jdem846.geom.util;

import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.math.Vector;

public abstract class PointTest
{

	public abstract boolean contains(Vertex p0, Vertex p1, Vertex p2, double x, double y, double z);
	
	
	protected void fill(double x, double y, double z, double[] v)
	{
		v[0] = x;
		v[1] = y;
		v[2] = z;
	}
	
	protected void fill(double x, double y, double z, Vector v)
	{
		v.x = x;
		v.y = y;
		v.z = z;
	}
	
}

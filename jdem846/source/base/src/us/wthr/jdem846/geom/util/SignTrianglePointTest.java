package us.wthr.jdem846.geom.util;

import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.math.Vector;

public class SignTrianglePointTest extends PointTest
{

	//private double[] P = new double[3];
	
	private static Vector P = new Vector();
	
	public SignTrianglePointTest()
	{
		
	}
	
	public boolean contains(Vertex p0, Vertex p1, Vertex p2, double x, double y, double z)
	{
		return contains(p0.vector, p1.vector, p2.vector, x, y, z);
		
	}
	
	public static boolean contains(Vector p0, Vector p1, Vector p2, double x, double y, double z)
	{
		//fill(x, y, z, P);
		P.x = x;
		P.y = y;
		P.z = z;
		
		boolean b0 = (sign(P, p0, p1) <= 0.0);
		boolean b1 = (sign(P, p1, p2) <= 0.0);
		boolean b2 = (sign(P, p2, p0) <= 0.0);

		return ((b0 == b1) && (b1 == b2));
		
	}
	
	protected static double sign(Vector v0, Vector v1, Vector v2)
	{
		return (v0.x - v2.x) * (v1.y - v2.y) - (v1.x - v2.x) * (v0.y - v2.y);
	}
	
	
}

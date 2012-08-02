package us.wthr.jdem846.geom.util;

import us.wthr.jdem846.geom.Vertex;

public class SignTrianglePointTest extends PointTest
{

	private double[] P = new double[3];
	
	public SignTrianglePointTest()
	{
		
	}
	
	
	public boolean contains(Vertex p0, Vertex p1, Vertex p2, double x, double y, double z)
	{
		fill(x, y, z, P);
		
		boolean b0 = (sign(P, p0.xyz, p1.xyz) < 0.0);
		boolean b1 = (sign(P, p1.xyz, p2.xyz) < 0.0);
		boolean b2 = (sign(P, p2.xyz, p0.xyz) < 0.0);

		return ((b0 == b1) && (b1 == b2));
		
	}
	
	protected double sign(double[] v0, double[] v1, double[] v2)
	{
		
		return (v0[0] - v2[0]) * (v1[1] - v2[1]) - (v1[0] - v2[0]) * (v0[1] - v2[1]);
		
		
	}
	
	
}

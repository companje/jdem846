package us.wthr.jdem846.geom.util;

import us.wthr.jdem846.geom.Vertex;

/** Apologies to http://www.blackpawn.com/texts/pointinpoly/default.html
 * 
 * @author Kevin M. Gill
 *
 */
public class BarycentricTrianglePointTest extends PointTest
{

	double[] A;
	double[] B;
	double[] C;
	double[] P;
	
	double[] v0;
	double[] v1;
	double[] v2;
	
	public BarycentricTrianglePointTest()
	{
		A = new double[3];
		B = new double[3];
		C = new double[3];
		P = new double[3];
	
		v0 = new double[3];
		v1 = new double[3];
		v2 = new double[3];
	}
	
	public boolean contains(Vertex p0, Vertex p1, Vertex p2, double x, double y, double z)
	{
		fill(p0.x, p0.y, p0.z, A);
		fill(p1.x, p1.y, p1.z, B);
		fill(p2.x, p2.y, p2.z, C);
		fill(x, y, z, P);
		
		perspectives.subtract(C, A, v0);
		perspectives.subtract(B, A, v1);
		perspectives.subtract(P, A, v2);
		
		double dot00 = perspectives.dotProduct(v0, v0);
		double dot01 = perspectives.dotProduct(v0, v1);
		double dot02 = perspectives.dotProduct(v0, v2);
		double dot11 = perspectives.dotProduct(v1, v1);
		double dot12 = perspectives.dotProduct(v1, v2);
		
		double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
		double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		return (u >= 0) && (v >= 0) && (u + v <= 1.0);
	}
	
	
	
}

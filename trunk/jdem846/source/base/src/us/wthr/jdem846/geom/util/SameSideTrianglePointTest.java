package us.wthr.jdem846.geom.util;

import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.math.Vectors;


/** Apologies to http://www.blackpawn.com/texts/pointinpoly/default.html
 * 
 * @author Kevin M. Gill
 *
 */
public class SameSideTrianglePointTest extends PointTest
{

	double[] A;
	double[] B;
	double[] C;
	double[] P;
	
	double[] v0;
	double[] v1;
	
	double[] cp0;
	double[] cp1;
	
	public SameSideTrianglePointTest()
	{
		A = new double[3];
		B = new double[3];
		C = new double[3];
		P = new double[3];
	
		v0 = new double[3];
		v1 = new double[3];

		cp0 = new double[3];
		cp1 = new double[3];
	}
	
	public boolean contains(Vertex p0, Vertex p1, Vertex p2, double x, double y, double z)
	{
		
		fill(x, y, z, P);
		
		fill(p0.x, p0.y, p0.z, A);
		fill(p1.x, p1.y, p1.z, B);
		fill(p2.x, p2.y, p2.z, C);
		
		if (sameSide(P, A, B, C) && sameSide(P, B, A, C) && sameSide(P, C, A, B)) {
			return true;
		} else {
			return false;
		}
		
	}
	
	protected boolean sameSide(double[] p0, double[] p1, double[] a, double[] b)
	{
		
		Vectors.subtract(b, a, v0);
		Vectors.subtract(p0, a, v1);
		Vectors.crossProduct(v0, v1, cp0);
		
		Vectors.subtract(p1, a, v1);
		Vectors.crossProduct(v0, v1, cp1);
		
		if (Vectors.dotProduct(cp0, cp1) >= 0.0) {
			return true;
		} else {
			return false;
		}
		
		
	}
	
	
}

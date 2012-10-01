package us.wthr.jdem846.geom.util;

import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.math.Vectors;


/** Apologies to http://www.blackpawn.com/texts/pointinpoly/default.html
 * 
 * @author Kevin M. Gill
 *
 */
public class SameSideTrianglePointTest extends PointTest
{

	Vector P = new Vector();
	
	Vector v0 = new Vector();
	Vector v1 = new Vector();
	
	Vector cp0 = new Vector();
	Vector cp1 = new Vector();
	
	public SameSideTrianglePointTest()
	{
		/*
		P = new double[3];
	
		v0 = new double[3];
		v1 = new double[3];

		cp0 = new double[3];
		cp1 = new double[3];
		*/
	}
	
	public boolean contains(Vertex p0, Vertex p1, Vertex p2, double x, double y, double z)
	{
		
		fill(x, y, z, P);
		
		if (sameSide(P, p0.vector, p1.vector, p2.vector) && sameSide(P, p1.vector, p0.vector, p2.vector) && sameSide(P, p2.vector, p0.vector, p1.vector)) {
			return true;
		} else {
			return false;
		}
		
	}
	
	protected boolean sameSide(Vector p0, Vector p1, Vector a, Vector b)
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

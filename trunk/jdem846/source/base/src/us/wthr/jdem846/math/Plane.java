package us.wthr.jdem846.math;

public class Plane
{
	private Vector plane = new Vector();
	
	
	public Plane(Vector plane)
	{
		this.plane = plane.getCopy();
	}
	
	public Plane(Vector p0, Vector p1, Vector p2)
	{
		this.plane = Plane.findPlane(p0, p1, p2);
	}
	
	public Vector getPlaneVector()
	{
		return plane;
	}
	
	
	
	
	public static Vector findPlane(Vector pt0, Vector pt1, Vector pt2)
	{
		Vector v0 = pt1.subtract(pt0);
		Vector v1 = pt2.subtract(pt0);
		Vector n = v0.crossProduct(v1);
		double d = -n.dotProduct(pt0);
		return new Vector(n.x, n.y, n.z, d);
		
	}
	
}

package us.wthr.jdem846.model.processing.util;


import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.math.Vectors;

public class Aspect
{
	
	private static Vector NORTH = new Vector(0, 0, -1);
	
	private static Vector normalBufferA = new Vector();
	private static Vector normalBufferB = new Vector();
	

	
	
	public static double aspectInDegrees(Vector normal)
	{
		normal.copyTo(normalBufferA);
		normalBufferA.y = 0;
		
		double dot = Vectors.dotProduct(NORTH, normalBufferA);
		double degrees = MathExt.degrees(MathExt.acos(dot));
		
		if (normalBufferA.x < 0) {
			degrees += 180.0;
		}
		
		return degrees;
	}
	

	
	
	public static double aspectInDegrees(double x, double z)
	{
		normalBufferB.x = x;
		normalBufferB.y = 0.0;
		normalBufferB.z = z;
		double degrees = aspectInDegrees(normalBufferB);
		return degrees;
	}

	
}

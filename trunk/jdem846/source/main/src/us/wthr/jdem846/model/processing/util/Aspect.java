package us.wthr.jdem846.model.processing.util;

import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.ModelPoint;

public class Aspect
{
	
	private static double[] NORTH = new double[3];
	
	private static double[] normalBufferA = new double[3];
	private static double[] normalBufferB = new double[3];
	
	private static Perspectives perspectives = new Perspectives();
	
	static {
		NORTH[0] = 0;
		NORTH[1] = 0;
		NORTH[2] = -1;
	}
	
	
	public static double aspectInDegrees(double[] normal)
	{
		fill(normalBufferA, normal);
		normalBufferA[1] = 0;
		
		double dot = perspectives.dotProduct(NORTH, normalBufferA);
		double degrees = MathExt.degrees(MathExt.acos(dot));
		
		if (normalBufferA[0] < 0) {
			degrees += 180.0;
		}
		
		return degrees;
	}
	

	
	
	public static double aspectInDegrees(double x, double z)
	{
		normalBufferB[0] = x;
		normalBufferB[1] = 0.0;
		normalBufferB[2] = z;
		double degrees = aspectInDegrees(normalBufferB);
		return degrees;
	}
	
	protected static void fill(double[] fill, double with[])
	{
		fill[0] = with[0];
		fill[1] = with[1];
		fill[2] = with[2];
	}
	
}

package us.wthr.jdem846.math;

public class Vectors
{
	public static final int X_AXIS = 0;
	public static final int Y_AXIS = 1;
	public static final int Z_AXIS = 2;
	
	
	@SuppressWarnings("unused")
	private static double[] tmpPoints1 = {0, 0, 0};
	
	
	@SuppressWarnings("unused")
	private static double matrix3x3[][] = {
			{0, 0, 0},
			{0, 1, 0},
			{0, 0, 0}
	};
	
	private static double[][] translateMatrix = {
			{1, 0, 0, 0},
			{0, 1, 0, 0},
			{0, 0, 1, 0},
			{0, 0, 0, 1}
	};
	
	public static void translate(double x, double y, double z, double[] xyz)
	{
		translateMatrix[0][3] = x;
		translateMatrix[1][3] = y;
		translateMatrix[2][3] = z;
		
		double tX = (translateMatrix[0][0] * xyz[0]) + (translateMatrix[0][1] * xyz[1]) + (translateMatrix[0][2] * xyz[2]) + (translateMatrix[0][3] * 1);
		double tY = (translateMatrix[1][0] * xyz[0]) + (translateMatrix[1][1] * xyz[1]) + (translateMatrix[1][2] * xyz[2]) + (translateMatrix[1][3] * 1);
		double tZ = (translateMatrix[2][0] * xyz[0]) + (translateMatrix[2][1] * xyz[1]) + (translateMatrix[2][2] * xyz[2]) + (translateMatrix[2][3] * 1);
		
		xyz[0] = tX;
		xyz[1] = tY;
		xyz[2] = tZ;
	}
	
	
	
	public static void scale(double x, double y, double z, double[] xyz)
	{
		xyz[0] = xyz[0] * x;
		xyz[1] = xyz[1] * y;
		xyz[2] = xyz[2] * z;
	}
	
	
	
	public static void rotate(double x, double y, double z, double[] xyz) 
	{
		
		double _x = MathExt.radians(x);
		double _y = MathExt.radians(y);
		double _z = MathExt.radians(z);
		
		double sinAX = MathExt.sin(-_x);
		double sinAY = MathExt.sin(-_y);
		double sinAZ = MathExt.sin(-_z);
		
		double cosAX = MathExt.cos(-_x);
		double cosAY = MathExt.cos(-_y);
		double cosAZ = MathExt.cos(-_z);
		
		double rX = ((cosAY * cosAZ) * xyz[0]) + ((-sinAX*-sinAY*cosAZ+cosAX*sinAZ) * xyz[1]) + ((cosAX*-sinAY*cosAZ+sinAX*sinAZ) * xyz[2]);
		double rY = ((cosAY * -sinAZ) * xyz[0]) + ((-sinAX*-sinAY*-sinAZ+cosAX*cosAZ) * xyz[1]) + ((cosAX*-sinAY*-sinAZ+sinAX*cosAZ) * xyz[2]);
		double rZ = (sinAY * xyz[0]) + ((-sinAX*cosAY) * xyz[1]) + ((cosAX*cosAY) * xyz[2]);
		
		xyz[0] = rX;
		xyz[1] = rY;
		xyz[2] = rZ;
		
	}
}

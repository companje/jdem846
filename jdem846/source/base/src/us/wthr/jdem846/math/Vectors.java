package us.wthr.jdem846.math;

public class Vectors
{
	public static final int X_AXIS = 0;
	public static final int Y_AXIS = 1;
	public static final int Z_AXIS = 2;
	
	public static final int XYZ = 0;
	public static final int XZY = 1;
	
	public static final int YXZ = 2;
	public static final int YZX = 3;
	
	public static final int ZXY = 4;
	public static final int ZYX = 5;
	
	
	private static double[] buffer0 = new double[3];
	private static double[] buffer1 = new double[3];
	private static double[] buffer2 = new double[3];
	
	
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
	
	//private static double buffer0[] = new double[3];
	//private static double buffer1[] = new double[3];
	//private static double buffer2[] = new double[3];
	
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
		
		
		double _x = (x != 0) ? MathExt.radians(x) : 0;
		double _y = (y != 0) ? MathExt.radians(y) : 0;
		double _z = (z != 0) ? MathExt.radians(z) : 0;
		
		double sinAX = (x != 0) ? MathExt.sin(-_x) : 0;
		double sinAY = (y != 0) ? MathExt.sin(-_y) : 0;
		double sinAZ = (z != 0) ? MathExt.sin(-_z) : 0;

		double cosAX = (x != 0) ? MathExt.cos(-_x) : 1;
		double cosAY = (y != 0) ? MathExt.cos(-_y) : 1;
		double cosAZ = (z != 0) ? MathExt.cos(-_z) : 1;
		
		double rx = ((cosAY * cosAZ) * xyz[0]) + ((-sinAX*-sinAY*cosAZ+cosAX*sinAZ) * xyz[1]) + ((cosAX*-sinAY*cosAZ+sinAX*sinAZ) * xyz[2]);
		double ry = ((cosAY * -sinAZ) * xyz[0]) + ((-sinAX*-sinAY*-sinAZ+cosAX*cosAZ) * xyz[1]) + ((cosAX*-sinAY*-sinAZ+sinAX*cosAZ) * xyz[2]);
		double rz = (sinAY * xyz[0]) + ((-sinAX*cosAY) * xyz[1]) + ((cosAX*cosAY) * xyz[2]);
		
		xyz[0] = rx;
		xyz[1] = ry;
		xyz[2] = rz;
	}
	
	
	public static void rotate(double x, double y, double z, double[] xyz, int order) 
	{
		
		switch(order) {
		case XYZ:
			rotateX(x, xyz);
			rotateY(y, xyz);
			rotateZ(z, xyz);
			break;
		case XZY:
			rotateX(x, xyz);
			rotateZ(z, xyz);
			rotateY(y, xyz);
			break;
		case YXZ:
			rotateY(y, xyz);
			rotateX(x, xyz);
			rotateZ(z, xyz);
			break;
		case YZX:
			rotateY(y, xyz);
			rotateZ(z, xyz);
			rotateX(x, xyz);
			break;
		case ZXY:
			rotateZ(z, xyz);
			rotateX(x, xyz);
			rotateY(y, xyz);
			break;
		case ZYX:
			rotateZ(z, xyz);
			rotateY(y, xyz);
			rotateX(x, xyz);
		}
		
	}
	
	public static void rotateX(double x, double[] xyz) 
	{
		if (x != 0.0) {
			
			x = MathExt.radians(x);

			double cosX = MathExt.cos(x);
			double sinX = MathExt.sin(x);
			
			//xyz[0] = xyz[0] * 1.0;
			//xyz[1] = xyz[1] * cosX + xyz[1] * sinX;
			//xyz[2] = xyz[2] * -sinX + xyz[2] * cosX;
			
			//xyz[0] = xyz[0] * 1.0;
			//xyz[1] = cosX * xyz[1] + -sinX * xyz[1];
			//xyz[2] = sinX * xyz[2] + cosX * xyz[2];
			
			
			//xyz[0] = xyz[0] * 1.0;
			xyz[1] = cosX * xyz[1] + -sinX * xyz[2];
			xyz[2] = sinX * xyz[1] + cosX * xyz[2];
		}
	}
	
	
	
	
	public static void rotateY(double y, double[] xyz) 
	{
		if (y != 0.0) {
			
			y = MathExt.radians(y);
			
			double cosY = MathExt.cos(y);
			double sinY = MathExt.sin(y);
			
			//xyz[0] = xyz[0] * cosY + xyz[0] * -sinY;
			//xyz[1] = xyz[1];
			//xyz[2] = xyz[2] * sinY + xyz[2] * cosY;
			
			//xyz[0] = xyz[0] * cosY + xyz[0] * sinY;
			//xyz[1] = xyz[1] * 1.0;
			//xyz[2] = xyz[2] * -sinY + xyz[2] * cosY;
			
			xyz[0] = cosY * xyz[0] + sinY * xyz[2];
			//xyz[1] = xyz[1] * 1.0;
			xyz[2] = -sinY * xyz[0] + cosY * xyz[2];
			
		}
	}
	
	public static void rotateZ(double z, double[] xyz) 
	{
		if (z != 0.0) {
			
			z = MathExt.radians(z);
			
			double cosZ = MathExt.cos(z);
			double sinZ = MathExt.sin(z);
			
			//xyz[0] = xyz[0] * cosZ + xyz[0] * sinZ;
			//xyz[1] = xyz[1] * -sinZ + xyz[1] * cosZ;
			//xyz[2] = xyz[2];
			
			//xyz[0] = cosZ * xyz[0] + -sinZ * xyz[0];
			//xyz[1] = sinZ * xyz[1] + cosZ * xyz[1];
			//xyz[2] = xyz[2] * 1.0;
			
			
			xyz[0] = cosZ * xyz[0] + -sinZ * xyz[1];
			xyz[1] = sinZ * xyz[0] + cosZ * xyz[1];
			//xyz[2] = xyz[2] * 1.0;
			
		}
	}
	
	
	public static void copy(double[] src, double[] dst)
	{
		dst[0] = src[0];
		dst[1] = src[1];
		dst[2] = src[2];
	}
	
	
	public static void subtract(double[] pt0, double[] pt1, double[] usr)
	{
		usr[0] = pt0[0] - pt1[0];
		usr[1] = pt0[1] - pt1[1];
		usr[2] = pt0[2] - pt1[2];
	}
	
	public static void add(double[] pt0, double[] pt1, double[] usr)
	{
		usr[0] = pt0[0] + pt1[0];
		usr[1] = pt0[1] + pt1[1];
		usr[2] = pt0[2] + pt1[2];
	}

	public static void crossProduct(double[] pt0, double[] pt1, double[] usr)
	{
		usr[0] = pt0[1] * pt1[2] - pt1[1] * pt0[2];
		usr[1] = pt0[2] * pt1[0] - pt1[2] * pt0[0];
		usr[2] = pt0[0] * pt1[1] - pt1[0] * pt0[1];
	}

	public static double dotProduct(double[] pt0, double[] pt1)
	{
		//double[] buffer0 = new double[3];
		//double[] buffer1 = new double[3];
		
		normalize(pt0, buffer0);
		normalize(pt1, buffer1);

		return  buffer0[0] * buffer1[0] + buffer0[1] * buffer1[1] + buffer0[2] * buffer1[2];
	}

	
	public static void inverse(double [] v, double[] usr)
	{
        for (int i = 0; i<3; i++) {
        	usr[i] = -v[i];
        }
	}
	
	public static void normalize(double[] pt0, double[] no)
	{
		double len = MathExt.sqrt((pt0[0] * pt0[0]) + (pt0[1] * pt0[1]) + (pt0[2] * pt0[2]));
		if (len == 0.0) 
			len = 1.0;

		no[0] = pt0[0] / len;
		no[1] = pt0[1] / len;
		no[2] = pt0[2] / len;
	}

	public static void calcNormal(double[] pt0, double[] pt1, double[] pt2, double[] no)
	{
		if (pt2 == null) {
			no[0] = pt0[1] * pt1[2] - pt1[2] * pt0[2];
	        no[1] = pt0[0] * pt1[2] - pt1[0] * pt0[2];
	        no[2] = pt0[0] * pt1[1] - pt1[0] * pt0[1];
		} else {
			
			//double[] buffer0 = new double[3];
			//double[] buffer1 = new double[3];
			//double[] buffer2 = new double[3];
			
			subtract(pt0, pt1, buffer0);
			subtract(pt1, pt2, buffer1);
			crossProduct(buffer0, buffer1, buffer2);
			normalize(buffer2, no);

		}
		
	}
}

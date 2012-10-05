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
	
	
	//private static double[] buffer0 = new double[3];
	//private static double[] buffer1 = new double[3];
	//private static double[] buffer2 = new double[3];
	
	//private static Vector vector0 = new Vector();
	//private static Vector vector1 = new Vector();
	//private static Vector vector2 = new Vector();
	
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
	
	public static void translate(double x, double y, double z, Vector v)
	{
		v.x += x;
		v.y += y;
		v.z += z;
	}
	
	
	public static void scale(double x, double y, double z, double[] xyz)
	{
		xyz[0] = xyz[0] * x;
		xyz[1] = xyz[1] * y;
		xyz[2] = xyz[2] * z;
	}
	
	public static void scale(double x, double y, double z, Vector v)
	{
		v.x *= x;
		v.y *= y;
		v.z *= z;
	}
	
	
	public static void rotate(double x, double y, double z, double[] xyz) 
	{
		rotate(x, y, z, xyz, XYZ); 
	}
	
	public static void rotate(double x, double y, double z, Vector v)
	{
		rotate(x, y, z, v, XYZ);
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
	
	public static void rotate(double x, double y, double z, Vector v, int order) 
	{
		
		switch(order) {
		case XYZ:
			rotateX(x, v);
			rotateY(y, v);
			rotateZ(z, v);
			break;
		case XZY:
			rotateX(x, v);
			rotateZ(z, v);
			rotateY(y, v);
			break;
		case YXZ:
			rotateY(y, v);
			rotateX(x, v);
			rotateZ(z, v);
			break;
		case YZX:
			rotateY(y, v);
			rotateZ(z, v);
			rotateX(x, v);
			break;
		case ZXY:
			rotateZ(z, v);
			rotateX(x, v);
			rotateY(y, v);
			break;
		case ZYX:
			rotateZ(z, v);
			rotateY(y, v);
			rotateX(x, v);
		}
	}
	
	public static void rotateX(double x, double[] xyz) 
	{
		if (x != 0.0) {
			
			x = MathExt.radians(x);

			double cosX = MathExt.cos(x);
			double sinX = MathExt.sin(x);
			
			//xyz[0] = xyz[0] * 1.0;
			double ry = cosX * xyz[1] + -sinX * xyz[2];
			double rz = sinX * xyz[1] + cosX * xyz[2];
			
			xyz[1] = ry;
			xyz[2] = rz;
		}
	}
	
	public static void rotateX(double x, Vector v) 
	{
		if (x != 0.0) {
			
			x = MathExt.radians(x);

			double cosX = MathExt.cos(x);
			double sinX = MathExt.sin(x);
			
			//xyz[0] = xyz[0] * 1.0;
			double ry = cosX * v.y + -sinX * v.z;
			double rz = sinX * v.y + cosX * v.z;
			
			v.y = ry;
			v.z = rz;
		}
	}
	
	
	
	public static void rotateY(double y, double[] xyz) 
	{
		if (y != 0.0) {
			
			y = MathExt.radians(y);
			
			double cosY = MathExt.cos(y);
			double sinY = MathExt.sin(y);
			
			double rx = cosY * xyz[0] + sinY * xyz[2];
			//xyz[1] = xyz[1] * 1.0;
			double rz = -sinY * xyz[0] + cosY * xyz[2];
			
			xyz[0] = rx;
			xyz[2] = rz;
		}
	}
	
	public static void rotateY(double y, Vector v) 
	{
		if (y != 0.0) {
			
			y = MathExt.radians(y);
			
			double cosY = MathExt.cos(y);
			double sinY = MathExt.sin(y);
			
			double rx = cosY * v.x + sinY * v.z;
			//xyz[1] = xyz[1] * 1.0;
			double rz = -sinY * v.x + cosY * v.z;
			
			v.x = rx;
			v.z = rz;
		}
	}
	
	public static void rotateZ(double z, double[] xyz) 
	{
		if (z != 0.0) {
			
			z = MathExt.radians(z);
			
			double cosZ = MathExt.cos(z);
			double sinZ = MathExt.sin(z);
			
			double rx = cosZ * xyz[0] + -sinZ * xyz[1];
			double ry = sinZ * xyz[0] + cosZ * xyz[1];
			//xyz[2] = xyz[2] * 1.0;
			
			xyz[0] = rx;
			xyz[1] = ry;
		}
	}
	
	public static void rotateZ(double z, Vector v) 
	{
		if (z != 0.0) {
			
			z = MathExt.radians(z);
			
			double cosZ = MathExt.cos(z);
			double sinZ = MathExt.sin(z);
			
			double rx = cosZ * v.x + -sinZ * v.y;
			double ry = sinZ * v.x + cosZ * v.y;
			//xyz[2] = xyz[2] * 1.0;
			
			v.x = rx;
			v.y = ry;
		}
	}
	
	public static void copy(double[] src, double[] dst)
	{
		dst[0] = src[0];
		dst[1] = src[1];
		dst[2] = src[2];
	}
	
	public static void copy(Vector from, Vector to)
	{
		to.x = from.x;
		to.y = from.y;
		to.z = from.z;
		to.w = from.w;
	}
	
	
	public static void subtract(double[] pt0, double[] pt1, double[] usr)
	{
		usr[0] = pt0[0] - pt1[0];
		usr[1] = pt0[1] - pt1[1];
		usr[2] = pt0[2] - pt1[2];
	}
	
	public static void subtract(Vector a, Vector b, Vector to)
	{
		to.x = a.x - b.x;
		to.y = a.y - b.y;
		to.z = a.z - b.z;
		to.w = a.w - b.w;
	}
	
	public static void add(double[] pt0, double[] pt1, double[] usr)
	{
		usr[0] = pt0[0] + pt1[0];
		usr[1] = pt0[1] + pt1[1];
		usr[2] = pt0[2] + pt1[2];
	}
	
	public static void add(Vector a, Vector b, Vector to)
	{
		to.x = a.x + b.x;
		to.y = a.y + b.y;
		to.z = a.z + b.z;
		to.w = a.w + b.w;
	}

	public static void crossProduct(double[] pt0, double[] pt1, double[] usr)
	{
		usr[0] = pt0[1] * pt1[2] - pt1[1] * pt0[2];
		usr[1] = pt0[2] * pt1[0] - pt1[2] * pt0[0];
		usr[2] = pt0[0] * pt1[1] - pt1[0] * pt0[1];
	}
	
	public static void crossProduct(Vector a, Vector b, Vector to)
	{
		to.x = a.y * b.z - b.y * a.z;
		to.y = a.z * b.x - b.z * a.x;
		to.z = a.x * b.y - b.x * a.y;
	}
	

	public static double dotProduct(double[] pt0, double[] pt1)
	{
		double[] buffer0 = {0.0, 0.0, 0.0};//new double[3];
		double[] buffer1 = {0.0, 0.0, 0.0};//new double[3];
		
		
		
		normalize(pt0, buffer0);
		normalize(pt1, buffer1);

		return  buffer0[0] * buffer1[0] + buffer0[1] * buffer1[1] + buffer0[2] * buffer1[2];
	}
	
	public static double dotProduct(Vector a, Vector b)
	{
		Vector vector0 = new Vector();
		Vector vector1 = new Vector();
		
		normalize(a, vector0);
		normalize(b, vector1);
		
		return vector0.x * vector1.x + vector0.y * vector1.y + vector0.z * vector1.z;
	}

	
	public static void inverse(double [] v, double[] usr)
	{
        for (int i = 0; i<3; i++) {
        	usr[i] = -v[i];
        }
	}
	
	public static void inverse(double [] v)
	{
		inverse(v, v);
	}
	
	public static void inverse(Vector v, Vector into)
	{
		into.x = v.x * -1;
		into.y = v.y * -1;
		into.z = v.z * -1;
	}
	
	public static void inverse(Vector v)
	{
		inverse(v, v);
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

	public static void normalize(Vector v, Vector into)
	{
		double len = MathExt.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
		if (len == 0.0)
			len = 1.0;
		
		into.x = v.x / len;
		into.y = v.y / len;
		into.z = v.z / len;
	}
	
	public static void normalize(Vector v)
	{
		normalize(v, v);
	}
	
	public static void calcNormal(double[] pt0, double[] pt1, double[] pt2, double[] no)
	{
		if (pt2 == null) {
			no[0] = pt0[1] * pt1[2] - pt1[2] * pt0[2];
	        no[1] = pt0[0] * pt1[2] - pt1[0] * pt0[2];
	        no[2] = pt0[0] * pt1[1] - pt1[0] * pt0[1];
		} else {
			
			double[] buffer0 = {0.0, 0.0, 0.0};//new double[3];
			double[] buffer1 = {0.0, 0.0, 0.0};//new double[3];
			double[] buffer2 = {0.0, 0.0, 0.0};//new double[3];
			
			subtract(pt0, pt1, buffer0);
			subtract(pt1, pt2, buffer1);
			crossProduct(buffer0, buffer1, buffer2);
			normalize(buffer2, no);

		}
		
	}
	
	
	public static void calcNormal(Vector a, Vector b, Vector norm)
	{
		norm.x = a.y * b.z - b.z * a.z;
		norm.y = a.x * b.z - b.x * a.z;
		norm.z = a.x * b.y - b.x * a.y;
	}
	
	public static void calcNormal(Vector a, Vector b, Vector c, Vector norm)
	{
		Vector vector0 = new Vector();
		Vector vector1 = new Vector();
		Vector vector2 = new Vector();
		
		subtract(a, b, vector0);
		subtract(b, c, vector1);
		
		crossProduct(vector0, vector1, vector2);
		normalize(vector2, norm);
		
	}
}

package us.wthr.jdem846.math;



public class Spheres
{
	
	private static final double RAD_90 = MathExt.radians(90.0);
	private static final double RAD_180 = MathExt.radians(180.0);
	private static final double RAD_270 = MathExt.radians(270.0);
	
	
	public static void getPoint3D(double theta, // Longitude, in degrees
									double phi, // Latitude, in degrees
									double radius, 
									double[] points)
	{
		if (theta < 0)
			theta += 360;
		if (theta >= 360)
			theta -= 360;
		
		
		theta = MathExt.radians(theta);
		phi = MathExt.radians(phi);
		
		double _y = MathExt.sqrt(MathExt.sqr(radius) - MathExt.sqr(radius * MathExt.cos(phi)));
        double r0 = MathExt.sqrt(MathExt.sqr(radius) - MathExt.sqr(_y));

        double _b = r0 * MathExt.cos(theta );
        double _z = MathExt.sqrt(MathExt.sqr(r0) - MathExt.sqr(_b));
        double _x = MathExt.sqrt(MathExt.sqr(r0) - MathExt.sqr(_z));

		
        if (theta <= RAD_90) {
            _z *= -1.0;
	    } else if (theta  <= RAD_180) {
	    	_x *= -1.0;
	    	_z *= -1.0;
	    } else if (theta  <= RAD_270) {
	    	_x *= -1.0;
	    }
	
	    if (phi >= 0) { 
	    	_y = MathExt.abs(_y);
	    } else {
	    	_y = MathExt.abs(_y) * -1;
	    }

        points[0] = _x;
        points[1] = _y;
        points[2] = _z;

	}
	
	
	public static void getPoints2D(double angle, double radius, double[] points)
	{
		double b = radius * MathExt.cos(MathExt.radians(angle));
        double x = MathExt.sqrt(MathExt.sqr(radius) - MathExt.sqr(b));
        double y = MathExt.sqrt(MathExt.sqr(radius) - MathExt.sqr(x));
        
        if (angle <= 90.0) {
        	y = radius - y;
        } else if (angle  <= 180.0) {
        	y = y + radius;
        } else if (angle  <= 270.0) {
        	x = -1 * x;
        	y = y + radius;
        } else if (angle <= 360.0) {
        	x = -1 * x;
        	y = radius - y;
        }
        points[0] = x;
        points[1] = y;

	}
	
}

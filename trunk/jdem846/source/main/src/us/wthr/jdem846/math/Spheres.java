package us.wthr.jdem846.math;



public class Spheres
{
	
	private static final double RAD_90 = Math.toRadians(90.0);
	private static final double RAD_180 = Math.toRadians(180.0);
	private static final double RAD_270 = Math.toRadians(270.0);
	
	
	public static void getPoint3D(double theta, 
									double phi, 
									double radius, 
									double[] points)
	{
		theta = Math.toRadians(theta);
		phi = Math.toRadians(phi);
		
		double _y = Math.sqrt(Math.pow(radius, 2) - Math.pow(radius * Math.cos(phi), 2));
        double r0 = Math.sqrt(Math.pow(radius, 2) - Math.pow(_y, 2));

        double _b = r0 * Math.cos(theta );
        double _z = Math.sqrt(Math.pow(r0, 2) - Math.pow(_b, 2));
        double _x = Math.sqrt(Math.pow(r0, 2) - Math.pow(_z, 2));

		
        if (theta <= RAD_90) {
            _z *= -1.0;
	    } else if (theta  <= RAD_180) {
	            _x *= -1.0;
	            _z *= -1.0;
	    } else if (theta  <= RAD_270) {
	            _x *= -1.0;
	    }
	
	    if (phi >= 0) { 
	            _y = Math.abs(_y);
	    } else {
	            _y = Math.abs(_y) * -1;
	    }

        points[0] = _x;
        points[1] = _y;
        points[2] = _z;

	}
	
}

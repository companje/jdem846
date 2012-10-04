package us.wthr.jdem846.math;



public class Spheres
{
	
	private static final double RAD_90 = MathExt.radians(90.0);
	private static final double RAD_180 = MathExt.radians(180.0);
	private static final double RAD_270 = MathExt.radians(270.0);
	
	
	
	protected static double fixThetaDegrees(double degrees)
	{
		
		while (degrees < 0.0) {
			degrees += 360.0;
		}
		
		while (degrees >= 360.0) {
			degrees -= 360.0;
		}
		
		return degrees;
		
		/*
		double limited;
		degrees /= 360.0;
		limited = 360.0 * (degrees - MathExt.floor(degrees));
		if (limited < 0)
			limited += 360.0;
		return limited;
		*/
	}
	
	protected static double fixPhiDegrees(double phi)
	{
		
		/*
		while (phi < -90.0) {
			phi += 180.0;
		}
		
		while (phi >= 90.0) {
			phi -= 180.0;
		}
		*/
		while (phi < -90.0 || phi > 90.0) {
            if (phi > 90.0) {
                phi = phi - 90.0;
            }
            if (phi < -90.0) {
                phi = phi + 90.0;
            }
        }
		return phi;
	}
	
	//private static Vector vec = new Vector();
	public static void getPoint3D(double theta, // Longitude, in degrees
									double phi, // Latitude, in degrees
									double radius, 
									double[] points)
	{
		Vector vec = new Vector();
		vec.x = points[0];
		vec.y = points[1];
		vec.z = points[2];
		
		getPoint3D(theta, phi, radius, vec);
		
		points[0] = vec.x;
		points[1] = vec.y;
		points[2] = vec.z;
		
		
	}
	
	public static void getPoint3D(double theta, // Longitude, in degrees
			double phi, // Latitude, in degrees
			double radius, 
			Vector v)
	{
		
		theta = MathExt.radians(fixThetaDegrees(theta));
		phi = MathExt.radians(fixPhiDegrees(phi));

		
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

        v.x = _x;
        v.y = _y;
        v.z = _z;
        
        
        v.x = MathExt.min(radius, v.x);
        v.x = MathExt.max(-radius, v.x);

        v.y = MathExt.min(radius, v.y);
        v.y = MathExt.max(-radius, v.y);
        
        v.z = MathExt.min(radius, v.z);
        v.z = MathExt.max(-radius, v.z);
        
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

package us.wthr.jdem846.math;



public class Spheres
{
	
	private static final double RAD_90 = MathExt.radians(90.0);
	private static final double RAD_180 = MathExt.radians(180.0);
	private static final double RAD_270 = MathExt.radians(270.0);
	
	
	
	public static double fixThetaDegrees(double degrees)
	{
		double limited;
		degrees /= 360.0;
		limited = 360.0 * (degrees - MathExt.floor(degrees));
		if (limited < 0)
			limited += 360.0;
		return limited;
		
	}
	
	public static double fixPhiDegrees(double degrees)
	{
		degrees += 90.0;
		
		double limited;
		degrees /= 180.0;
		limited = 180.0 * (degrees - MathExt.floor(degrees));
		if (limited < 0)
			limited += 180.0;
		return limited - 90.0;

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
								Vector vec)
	{
		Vector v = getPoint3D(theta, phi, radius);
		vec.x = v.x;
		vec.y = v.y;
		vec.z = v.z;
	}
	
	public static Vector getPoint3D(double theta, // Longitude, in degrees
								double phi, // Latitude, in degrees
								double radius)
	{

		theta += 90.0;
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

	    Vector v = new Vector(_x, _y, _z);
        
        v.x = MathExt.min(radius, v.x);
        v.x = MathExt.max(-radius, v.x);

        v.y = MathExt.min(radius, v.y);
        v.y = MathExt.max(-radius, v.y);
        
        v.z = MathExt.min(radius, v.z);
        v.z = MathExt.max(-radius, v.z);
        
        return v;
        
	}
	
	
	public static void getPointEllipsoid3D(double theta, // Longitude, in degrees
										double phi, // Latitude, in degrees
										double equitorialRadius, 
										double flattening,
										Vector vec)
	{
		Vector v = getPointEllipsoid3D(theta, phi, equitorialRadius, flattening);
		vec.x = v.x;
		vec.y = v.y;
		vec.z = v.z;
	}
		
	public static Vector getPointEllipsoid3D(double theta, // Longitude, in degrees
										double phi, // Latitude, in degrees
										double equitorialRadius, 
										double flattening)
	{
		phi = fixPhiDegrees(phi);
		double radius = sphericalToEllipsoidRadius(equitorialRadius, flattening, phi);
		
		return getPoint3D(theta, phi, radius);
	}
	
	
	public static double sphericalToEllipsoidRadius(double equitorialRadius, double flattening, double latitude)
	{
		return sphericalToEllipsoidRadiusGeocentric(equitorialRadius, flattening, latitude);
	}
	
	public static double sphericalToEllipsoidRadiusGeodetic(double equitorialRadius, double flattening, double latitude)
	{
		double polarRadius = equitorialRadius * (1.0 - flattening);
		
		//r = 1/sqrt(cos^2(x)/a^2 + sin^2(x)/b^2)		
		double ellipsoidRadius = 1.0 / MathExt.sqrt(MathExt.sqr(MathExt.cos_d(latitude)) / MathExt.sqr(equitorialRadius) + MathExt.sqr(MathExt.sin_d(latitude)) / MathExt.sqr(polarRadius));
		return ellipsoidRadius;
	}
	
	public static double sphericalToEllipsoidRadiusGeocentric(double equitorialRadius, double flattening, double latitude)
	{
		double polarRadius = equitorialRadius * (1.0 - flattening);

		double tanlat2 = MathExt.sqr(MathExt.tan(MathExt.radians(latitude)));
		double ellipsoidRadius = polarRadius*MathExt.pow((1+tanlat2), 0.5) /  MathExt.pow(( (MathExt.sqr(polarRadius) / MathExt.sqr(equitorialRadius))+tanlat2), 0.5);
		
		return ellipsoidRadius;
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

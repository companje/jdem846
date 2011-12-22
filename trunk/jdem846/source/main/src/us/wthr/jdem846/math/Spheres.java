package us.wthr.jdem846.math;



public class Spheres extends MathExt
{
	
	public static void getPoint3D(double theta, double phi, double radius, double[] points)
	{
		theta = radians(theta);
		phi = radians(phi);
		
		/*
		double _y = sqrt(sqr(radius) - sqr(radius * cos(phi)));
		double r0 = sqrt(sqr(radius) - sqr(_y));

		double _b = r0 * cos(theta );
        double _z = sqrt(sqr(r0) - sqr(_b));
        double _x = sqrt(sqr(r0) - sqr(_z));
        */
		double _y = sqrt(pow(radius, 2) - pow(radius * cos(phi), 2));
        double r0 = sqrt(pow(radius, 2) - pow(_y, 2));

        double _b = r0 * cos(theta );
        double _z = sqrt(pow(r0, 2) - pow(_b, 2));
        double _x = sqrt(pow(r0, 2) - pow(_z, 2));

		
        if (theta <= radians(90.0)) {
            _z *= -1.0;
	    } else if (theta  <= radians(180.0)) {
	            _x *= -1.0;
	            _z *= -1.0;
	    } else if (theta  <= radians(270.0)) {
	            _x *= -1.0;
	    }
	
	    if (phi >= 0) { 
	            _y = abs(_y);
	    } else {
	            _y = abs(_y) * -1;
	    }

        /*
        if (theta <= 180.0) {
        	_z *= -1.0;
        }
        
        if (theta > 90.0 && theta <= 270.0) {
        	_x *= -1.0;
        }

        if (phi >= 0) { 
                _y = abs(_y);
        } else {
                _y = abs(_y) * -1;
        }
		*/

        points[0] = _x;
        points[1] = _y;
        points[2] = _z;

       // double mag = sqrt(sqr(_x)+sqr(_y)+sqr(_z));   
       // _x /= mag;   
        //_y /= mag;   
       // _z /= mag; 
        
        //points[3] = (atan2(_x, _z)/(Math.PI*2)) + 0.5f;   
       // points[4] =  (asin(_y) / Math.PI) + 0.5f;
	}
	
}

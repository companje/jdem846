package us.wthr.jdem846.geotrans;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Testing WGS84 calculations based on http://home.hiwaay.net/~taylorc/toolbox/geography/geoutm.html
 * 
 * @author Kevin M. Gill
 * 
 */
public class GeoTransTesting extends AbstractTestMain
{
	
	private static Log log = null;
	public static void main(String[] args)
	{
		try {
			initialize(false);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		log = Logging.getLog(GeoTransTesting.class);
				
				
		GeoTransTesting testing = new GeoTransTesting();
		testing.doTesting(args);
	}
	
	
	public void doTesting(String[] args)
	{
		log.info("Hello");
		
		boolean southernHemi = false;
		double A = 0.9999198070000000;
		double D = 0.0000000000000000;
		double B = 0.0000000000000000;
		double E = -0.9999788620000000;
		double C = 321739.9889599030000000; // X
		double F = 4778143.4400105600000000; // Y
		
		double utmScaleFactor = 0.9996000000000000;
		double centralMeridian = -69.0000000000000000;
		double falseEasting = 500000.0000000000000000;
		double falseNorthing = 0.0;

		double x = (C - falseEasting) / utmScaleFactor;
		double y = ((southernHemi) ? F - 10000000.0 : F) / utmScaleFactor;
		
		log.info("Map X/Y: " + x + "/" + y);

	}
	
	/*
	function MapXYToLatLon (x, y, lambda0, philambda)

    {

        var phif, Nf, Nfpow, nuf2, ep2, tf, tf2, tf4, cf;

        var x1frac, x2frac, x3frac, x4frac, x5frac, x6frac, x7frac, x8frac;

        var x2poly, x3poly, x4poly, x5poly, x6poly, x7poly, x8poly;

    	

        // Get the value of phif, the footpoint latitude.

        phif = FootpointLatitude (y);

        	

        // Precalculate ep2

        ep2 = (Math.pow (sm_a, 2.0) - Math.pow (sm_b, 2.0))

              / Math.pow (sm_b, 2.0);

        	

        // Precalculate cos (phif) 

        cf = Math.cos (phif);

        	

        // Precalculate nuf2 

        nuf2 = ep2 * Math.pow (cf, 2.0);

        	

        // Precalculate Nf and initialize Nfpow 

        Nf = Math.pow (sm_a, 2.0) / (sm_b * Math.sqrt (1 + nuf2));

        Nfpow = Nf;

        	

        // Precalculate tf 

        tf = Math.tan (phif);

        tf2 = tf * tf;

        tf4 = tf2 * tf2;

        

        // Precalculate fractional coefficients for x**n in the equations

        // below to simplify the expressions for latitude and longitude. 

        x1frac = 1.0 / (Nfpow * cf);

        

        Nfpow *= Nf;   // now equals Nf**2) 

        x2frac = tf / (2.0 * Nfpow);

        

        Nfpow *= Nf;   // now equals Nf**3) 

        x3frac = 1.0 / (6.0 * Nfpow * cf);

        

        Nfpow *= Nf;   // now equals Nf**4) 

        x4frac = tf / (24.0 * Nfpow);

        

        Nfpow *= Nf;   // now equals Nf**5) 

        x5frac = 1.0 / (120.0 * Nfpow * cf);

        

        Nfpow *= Nf;   // now equals Nf**6) 

        x6frac = tf / (720.0 * Nfpow);

        

        Nfpow *= Nf;   // now equals Nf**7) 

        x7frac = 1.0 / (5040.0 * Nfpow * cf);

        

        Nfpow *= Nf;   // now equals Nf**8) 

        x8frac = tf / (40320.0 * Nfpow);

        

        // Precalculate polynomial coefficients for x**n.
        //-- x**1 does not have a polynomial coefficient. 

        x2poly = -1.0 - nuf2;

        

        x3poly = -1.0 - 2 * tf2 - nuf2;

        

        x4poly = 5.0 + 3.0 * tf2 + 6.0 * nuf2 - 6.0 * tf2 * nuf2

        	- 3.0 * (nuf2 *nuf2) - 9.0 * tf2 * (nuf2 * nuf2);

        

        x5poly = 5.0 + 28.0 * tf2 + 24.0 * tf4 + 6.0 * nuf2 + 8.0 * tf2 * nuf2;

        

        x6poly = -61.0 - 90.0 * tf2 - 45.0 * tf4 - 107.0 * nuf2

        	+ 162.0 * tf2 * nuf2;

        

        x7poly = -61.0 - 662.0 * tf2 - 1320.0 * tf4 - 720.0 * (tf4 * tf2);

        

        x8poly = 1385.0 + 3633.0 * tf2 + 4095.0 * tf4 + 1575 * (tf4 * tf2);

        	

        // Calculate latitude 

        philambda[0] = phif + x2frac * x2poly * (x * x)

        	+ x4frac * x4poly * Math.pow (x, 4.0)

        	+ x6frac * x6poly * Math.pow (x, 6.0)

        	+ x8frac * x8poly * Math.pow (x, 8.0);

        	

        // Calculate longitude 

        philambda[1] = lambda0 + x1frac * x

        	+ x3frac * x3poly * Math.pow (x, 3.0)

        	+ x5frac * x5poly * Math.pow (x, 5.0)

        	+ x7frac * x7poly * Math.pow (x, 7.0);

        	

        return;

    }

	*/
}

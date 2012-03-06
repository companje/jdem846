package us.wthr.jdem846.math;

public class MathExt
{
	public static final double PI = Math.PI;
	
	
	public static double abs(double a)
	{
		return Math.abs(a);
	}
	
	public static double tan(double a)
    {
    	return Math.tan(a);
    }
    
    
    public static double sin(double a)
    {
    	return Math.sin(a);
    }
    
    public static double cos(double a)
    {
    	return Math.cos(a);
    }
    
    public static double radians(double a)
    {
    	return Math.toRadians(a);
    }
    
    public static double degrees(double a)
    {
    	return Math.toDegrees(a);
    }
    
    public static double atan2(double y, double x)
    {
    	return Math.atan2(y, x);
    }
    
    public static double pow(double a, double b)
    {
    	return Math.pow(a, b);
    }
    
    /** Compute the square of a value. (a^2)
     * 
     * @param a
     * @return
     */
    public static double sqr(double a)
    {
    	return MathExt.pow(a, 2);
    }
    
    /** Compute the cube of a value. (a^3)
     * 
     * @param a
     * @return
     */
    public static double cube(double a)
    {
    	return MathExt.pow(a, 3);
    }
    
    public static double sqrt(double a)
    {
    	return Math.sqrt(a);
    }
    
    public static double cbrt(double a)
    {
    	return Math.cbrt(a);
    }
    
    public static double asin(double a)
    {
    	return Math.asin(a);
    }
    
    public static double acos(double a)
    {
    	return Math.acos(a);
    }
    
    public static double floor(double a)
    {
    	return Math.floor(a);
    }
    
    public static double ceil(double a)
    {
    	return Math.ceil(a);
    }
    
    public static double round(double a)
    {
    	return Math.round(a);
    }
	/*
	public static double min(double a, double b)
	{
		return Math.min(a, b);
	}
	
	public static double max(double a, double b)
	{
		return Math.max(a, b);
	}
	*/
	
	public static double min(double...values)
	{
		double m = Double.NaN;
		for (int i = 0; i < values.length; i++) {
			
			if (Double.isNaN(m)) {
				m = values[i];
			} else {
				m = Math.min(m, values[i]);
			}
		}
		return m;
	}
	
	public static double max(double...values)
	{
		double m = Double.NaN;
		for (int i = 0; i < values.length; i++) {
			
			if (Double.isNaN(m)) {
				m = values[i];
			} else {
				m = Math.max(m, values[i]);
			}

		}
		return m;
	}
	
	public static int interpolate(int i00, int i01, int i10, int i11, double xFrac, double yFrac)
	{
		double s00 = (double) i00;
		double s01 = (double) i01;
		double s10 = (double) i10;
		double s11 = (double) i11;

        return (int) Math.round(interpolate(s00, s01, s10, s11, xFrac, yFrac));
	}
	
	public static double interpolate(double s00, double s01, double s10, double s11, double xFrac, double yFrac)
	{
		double s0 = (s01 - s00)*xFrac + s00;
        double s1 = (s11 - s10)*xFrac + s10;
        return (s1 - s0)*yFrac + s0;
	}
	
	
	
}

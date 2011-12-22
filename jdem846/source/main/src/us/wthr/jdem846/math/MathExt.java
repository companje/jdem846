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
	
	public static double min(double a, double b)
	{
		return Math.min(a, b);
	}
	
	public static double max(double a, double b)
	{
		return Math.max(a, b);
	}
}

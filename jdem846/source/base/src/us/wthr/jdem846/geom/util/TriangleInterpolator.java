package us.wthr.jdem846.geom.util;

public class TriangleInterpolator
{
	
	private double a = 0;
	private double b = 0;
	private double c = 0;
	
	public TriangleInterpolator()
	{
		
	}
	
	
	public TriangleInterpolator(double x00, double y00, double m00,
								double x01, double y01, double m01,
								double x10, double y10, double m10)
	{
		set(x00, y00, m00,
			x01, y01, m01,
			x10, y10, m10);
		
	}
	
	
	public void set(double x00, double y00, double m00,
								double x01, double y01, double m01,
								double x10, double y10, double m10)
	{
		double det = x00*y01-x01*y00+x01*y10-x10*y01+x10*y00-x00*y10;
		
		a = ((y01-y10)*m00+(y10-y00)*m01+(y00-y01)*m10) / det;
		b = ((x10-x01)*m00+(x00-x10)*m01+(x01-x00)*m10) / det;
		c = ((x01*y10-x10*y01)*m00+(x10*y00-x00*y10)*m01+(x00*y01-x01*y00)*m10) / det;
	}
	
	
	public double getInterpolatedValue(double x, double y)
	{
		return a*x+b*y+c;
	}
	
}

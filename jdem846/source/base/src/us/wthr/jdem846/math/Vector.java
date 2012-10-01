package us.wthr.jdem846.math;

public class Vector 
{
	public double x = 0;
	public double y = 0;
	public double z = 0;
	public double w = 1.0;
	
	public Vector()
	{
		
	}
	
	public Vector(Vector copy)
	{
		this.x = copy.x;
		this.y = copy.y;
		this.z = copy.z;
		this.w = copy.w;
	}
	
	public Vector(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector(double x, double y, double z, double w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
}

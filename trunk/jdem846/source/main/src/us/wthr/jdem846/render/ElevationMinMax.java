package us.wthr.jdem846.render;

public class ElevationMinMax
{
	public double minimum;
	public double maximum;
	public double mean;
	public double median;
	
	public ElevationMinMax(double minimum, double maximum)
	{
		this(minimum, maximum, (minimum + maximum) / 2.0, 0.0);
	}
	
	public ElevationMinMax(double minimum, double maximum, double mean, double median)
	{
		this.minimum = minimum;
		this.maximum = maximum;
		this.mean = mean;
		this.median = median;
	}
	
}

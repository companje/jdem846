package us.wthr.jdem846.gis.elevation;

public class ElevationMinMax
{
	private double minimumElevation;
	private double maximumElevation;
	private double meanElevation;
	private double medianElevation;
	
	public ElevationMinMax()
	{
		
	}
	
	public ElevationMinMax(double minimum, double maximum)
	{
		this(minimum, maximum, (minimum + maximum) / 2.0, 0.0);
	}
	
	public ElevationMinMax(double minimum, double maximum, double mean, double median)
	{
		this.minimumElevation = minimum;
		this.maximumElevation = maximum;
		this.meanElevation = mean;
		this.medianElevation = median;
	}

	public double getMinimumElevation()
	{
		return minimumElevation;
	}

	public void setMinimumElevation(double minimumElevation)
	{
		this.minimumElevation = minimumElevation;
	}

	public double getMaximumElevation()
	{
		return maximumElevation;
	}

	public void setMaximumElevation(double maximumElevation)
	{
		this.maximumElevation = maximumElevation;
	}

	public double getMeanElevation()
	{
		return meanElevation;
	}

	public void setMeanElevation(double meanElevation)
	{
		this.meanElevation = meanElevation;
	}

	public double getMedianElevation()
	{
		return medianElevation;
	}

	public void setMedianElevation(double medianElevation)
	{
		this.medianElevation = medianElevation;
	}
	
	
	
}

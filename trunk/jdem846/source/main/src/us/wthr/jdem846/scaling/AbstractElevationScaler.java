package us.wthr.jdem846.scaling;

public abstract class AbstractElevationScaler implements ElevationScaler
{

	
	private double elevationMultiple;
	private double elevationMinimum;
	private double elevationMaximum;
	
	@Override
	public void setElevationMultiple(double elevationMultiple)
	{
		this.elevationMultiple = elevationMultiple;
	}
	
	protected double getElevationMultiple()
	{
		return this.elevationMultiple;
	}

	
	public void setElevationMinimum(double elevationMinimum)
	{
		this.elevationMinimum = elevationMinimum;
	}
	
	protected double getElevationMinimum()
	{
		return elevationMinimum;
	}
	
	public void setElevationMaximum(double elevationMaximum)
	{
		this.elevationMaximum = elevationMaximum;
	}
	
	protected double getElevationMaximum()
	{
		return elevationMaximum;
	}
	
	@Override
	public abstract double scale(double elevation);

}

package us.wthr.jdem846.color;

import us.wthr.jdem846.graphics.IColor;
import us.wthr.jdem846.scaling.ElevationScaler;

public abstract class SingleColorTinting implements ModelColoring
{
	private final IColor defaultColor;
	
	public SingleColorTinting(IColor color)
	{
		this.defaultColor = color;
	}
	
	@Override
	public void reset()
	{
		// Nothing to reset...
	}
	
	
	@Override
	public GradientLoader getGradientLoader()
	{
		return null;
	}
	
	@Override
	public IColor getColorByMeters(double ratio) 
	{
		return defaultColor;
	}
	
	@Override
	public IColor getColorByPercent(double ratio) 
	{
		return defaultColor;
	}

	@Override
	public IColor getGradientColor(double elevation, double minElevation, double maxElevation) 
	{
		return defaultColor;
	}
	
	public double getMinimumSupported()
	{
		return 0;
	}
	
	public double getMaximumSupported()
	{
		return 1.0;
	}
	
	@Override
	public void setElevationScaler(ElevationScaler elevationScaler) 
	{
		
	}
	
	public abstract ModelColoring copy() throws Exception;
}
